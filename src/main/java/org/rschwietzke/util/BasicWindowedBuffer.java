package org.rschwietzke.util;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.Arrays;

public class BasicWindowedBuffer implements Closeable
{
    private final RandomAccessFile file;
    private final byte[] buffer;

    public final long from;
    public final long to;

    public long bufferViewFrom;
    public long bufferViewTo;

    public long fileSize;

    public int bufferPos = 0;
    public int bufferEnd = -1;

    public int mark1 = -1;
    public int mark2 = -1;

    public BasicWindowedBuffer(final RandomAccessFile file, final int bufferSize) throws IOException
    {
        this.buffer = new byte[bufferSize];
        this.file = file;

        this.bufferViewFrom = 0;
        this.bufferViewTo = 0;

        this.fileSize = this.file.length();
        this.from = 0;
        this.to = this.fileSize - 1;

    }

    public BasicWindowedBuffer(final Path path, final long from, final long to, final int bufferSize)
            throws IOException
    {
        this.from = from;
        this.to = to;

        this.buffer = new byte[bufferSize];
        this.file = new RandomAccessFile(path.toFile(), "r");

        // ok, forward to our desired start
        this.bufferViewFrom = from;
        this.bufferViewTo = from;

        this.fileSize = this.file.length();

        this.file.seek(from);
    }

    /**
     * Remember this pos. Typically a start pos
     */
    public void mark1()
    {
        this.mark1 = this.bufferPos;
    }

    /**
     * Our second position and you can apply an offset for later
     * access to data.
     *
     * @param offset
     */
    public void mark2(final int offset)
    {
        this.mark2 = this.bufferPos + offset;
    }

    /**
     * Check the mark area against our other array. No safety checks!
     * @return
     */
    public boolean matchMarkedArea(final byte[] data)
    {
        // check length first to bail out early
        final int length = data.length;
        if (data.length != this.mark2 - this.mark1)
        {
            return false;
        }

        // content check
        // this could be also an array compare... gotta try that too
        boolean same = true;
        for (int i = 0; i < length; i++)
        {
            // we could bail out early but we rather expect
            // that it matches, so we skip the if
            same &= data[i] == this.buffer[mark1 + i];
        }

        return same;
    }

    /**
     * Returns a copy of the marked area
     */
    public byte[] copyMarkedArea()
    {
        return Arrays.copyOfRange(this.buffer, mark1, mark2 + 1);
    }

    /**
     * Returns the buffersize, mainly for testing
     * @return buffer size
     */
    public int bufferSize()
    {
        return buffer.length;
    }

    /**
     * Ensure that we close all things opened
     */
    @Override
    public void close() throws IOException
    {
        file.close();
    }

    /**
     * Fills the buffer
     */
    private void fillBuffer() throws IOException, EOFException
    {
        // ok, we have to copy a remainder in case we have an open mark1
        // because that means we want to later go for the content
        if (mark1 >= 0)
        {
            // ok, we have an active mark and so we have to preserve things
            // copy stuff
            final int fillFrom = this.bufferEnd - mark1;
            System.arraycopy(this.buffer, mark1, this.buffer, 0, this.bufferEnd - mark1);

            // read
            final int read = this.file.read(buffer, fillFrom + 1, this.buffer.length - fillFrom);

            // the marking moved
            this.bufferPos = this.bufferPos - this.mark1;
            this.bufferEnd = fillFrom + read;
            this.mark2 = this.mark2 - this.mark1;
            this.mark1 = 0;


        }
        else
        {
            // nothing to preserve, buffer is all ours
            // we just read as much as possible
            final int read = this.file.read(buffer);

            if (read == -1)
            {
                throw new EOFException("EndOfFileReached");
            }

            this.bufferPos = 0;
            this.bufferEnd = this.bufferPos + read - 1;

            this.bufferViewFrom = this.bufferViewTo;
            this.bufferViewTo += read;
        }
    }

    /**
     * Indicate that we have no markers open
     */
    public void resetMark()
    {
        this.mark1 = this.mark2 = -1;
    }

    /**
     * Ok, read a single byte, make this as small as possible for perfect inlining
     */
    public byte read() throws IOException, EOFException
    {
        // safe to read the next?
        if (this.bufferPos > this.bufferEnd)
        {
            fillBuffer();
        }

        if (this.bufferViewFrom + this.bufferPos <= this.to)
        {
            // get us the next byte
            return this.buffer[this.bufferPos++];
        }
        else
        {
            throw new IndexOutOfBoundsException(
                    String.format("Tried to read over the end, read %,d with limit %,d",
                            this.bufferViewFrom + this.bufferPos, this.to));
        }
    }

    /**
     * Skip a byte
     */
    public void skip()
    {
        this.bufferPos++;
    }

    /**
     * Because it is about performance and we know that our datasource is complete
     * in the sense of always full data, we can often come here and safe us the trouble
     * of checking the buffer
     */
    public byte readUnsafe()
    {
        // get us the next byte
        return this.buffer[this.bufferPos++];
    }

    /**
     * How much data is left in the buffer? This helps us to make the call if
     * we can read unsafe.
     */
    public int remainingBufferedBytes()
    {
        return this.bufferEnd - this.bufferPos + 1;
    }

    /**
     * How much data is left in the file without taking the
     * planned read limit, the to param, into account?
     */
    public long remainingFileBytes()
    {
        // when we read the last byte and have not tried to read again,
        // we are over the end
        return this.fileSize - (this.bufferViewFrom + this.bufferPos);
    }

    /**
     * How much is left when taking the set limit into account?
     */
    public long remainingBytesToRead()
    {
        // when we read the last byte and have not tried to read again,
        // we are over the end
        return this.to - (this.bufferViewFrom + this.bufferPos) + 1;
    }
}
