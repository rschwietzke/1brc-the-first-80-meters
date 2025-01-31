package org.rschwietzke.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

public class BasicWindowedBufferTest
{
    static Path create(final Function<Long, Byte> provider, final long size)
    {
        try
        {
            final Path p = Files.createTempFile(UUID.randomUUID().toString(), ".test");
            p.toFile().deleteOnExit();

            // fill, not super efficient
            try (var c = Files.newByteChannel(p, StandardOpenOption.WRITE))
            {
                ByteBuffer b = ByteBuffer.allocate(512);

                for (long i = 0; i < size; i++)
                {
                    b.put(provider.apply(i));

                    if (!b.hasRemaining())
                    {
                        c.write(b.flip());
                        b.clear();
                    }
                }
                // write rest if any
                c.write(b.flip());
            }

            return p;
        }
        catch(IOException io)
        {
            io.printStackTrace();
            fail();
        }

        return null;
    }

    static void compare(final Path path, final byte[] readData, final long from, final long to)
    {
        final byte[] fileData;
        boolean equals = true;
        try
        {
            fileData = Files.readAllBytes(path);

            assertTrue(readData.length == to - from + 1);

            for (int i = (int) from; i <= to; i++)
            {
                equals &= fileData[i] == readData[(int) (i - from)];
            }
        }
        catch (IOException e)
        {
            fail("Could not read source file");
        }
        assertTrue(equals);
    }

    /**
     * Basic cons
     * @throws IOException
     */
    @Test
    public void ctr() throws IOException
    {
        var l = 1024L;
        var p = create(i -> 1, l);
        var b = new BasicWindowedBuffer(p, 0l, l - 1, 1024);
        assertEquals(1024, b.bufferSize());
    }

    /**
     * Utility for testing states without repeating code
     */
    private void testReading(
            final long fileSize, final int bufferSize) throws IOException
    {
        testReading(fileSize, bufferSize, 0, fileSize - 1, 0, 0, 0);
    }

    /**
     * Utility for testing states without repeating code
     */
    private void testReading(
            final long fileSize, final int bufferSize,
            final long from, final long to,
            final long expectedBufferBytesRemaining,
            final long expectedFileBytesRemaining,
            final long expectedTotalFileBytesRemaining
            ) throws IOException
    {
        testReading(i -> i.byteValue(),
                fileSize, bufferSize,
                from, to,
                expectedBufferBytesRemaining, expectedFileBytesRemaining, expectedTotalFileBytesRemaining);
    }

    /**
     * Utility for testing states without repeating code
     */
    private void testReading(
            final Function<Long, Byte> supplier,
            final long fileSize, final int bufferSize,
            final long from, final long to,
            final long expectedBufferBytesRemaining,
            final long expectedFileBytesRemaining,
            final long expectedTotalFileBytesRemaining
            ) throws IOException
    {
        final Path p = create(supplier, fileSize);
        // we won't test with extremely large files, so that is safe now

        final int viewFileSize = (int) (to - from + 1);
        final int byteFromFromToFileEnd = (int) (fileSize - from);
        final byte[] readData = new byte[viewFileSize];

        try (var buffer = new BasicWindowedBuffer(p, from, to, bufferSize))
        {
            int readDataPos = 0;

            System.out.printf("%nfrom=%d, to=%d, fileSize=%d, viewFileSize=%d %n",
                    from, to, fileSize, viewFileSize);
            System.out.printf("buffered=%d / toRead=%d / toFileEnd=%d%n",
                    buffer.remainingBufferedBytes(),
                    buffer.remainingBytesToRead(),
                    buffer.remainingFileBytes()
                    );

            try
            {
                assertEquals(0, buffer.remainingBufferedBytes());
                assertEquals(byteFromFromToFileEnd, buffer.remainingFileBytes());
                assertEquals(viewFileSize, buffer.remainingBytesToRead());

                byte b = 0;
                while (buffer.remainingBytesToRead() > 0)
                {
                    readData[readDataPos++] = b = buffer.read();
                    System.out.printf("pos=%d / data=%d / rbuffer=%d / rread=%d / rfile=%d%n",
                            readDataPos + from - 1,
                            b,
                            buffer.remainingBufferedBytes(),
                            buffer.remainingBytesToRead(),
                            buffer.remainingFileBytes()
                            );
                }
            }
            catch (EOFException e)
            {
                fail();
            }

            compare(p, readData, from, to);
            assertEquals(expectedBufferBytesRemaining, buffer.remainingBufferedBytes());
            assertEquals(expectedFileBytesRemaining, buffer.remainingBytesToRead());
            assertEquals(expectedTotalFileBytesRemaining, buffer.remainingFileBytes());
        }
    }

    /**
     * Simplest read, buffer and file are the same and no reload needed
     */
    @Test
    public void readAll_BufferSizeEqualsFileSize() throws IOException
    {
        testReading(500L, 500);
        testReading(1024L, 1024);
        testReading(8L, 8);
        testReading(23328L, 23328);
    }

    /**
     * File size is a multiple of the buffer, always full reloads
     */
    @Test
    public void readAll_BufferSizeMultipleOfFileSize() throws IOException
    {
        testReading(8L, 4);
        testReading(2716L, 1);
        testReading(14L, 7);
        testReading(1024L, 512);
        testReading(9 * 511L, 511);
        testReading(10240L, 512);
    }

    /**
     * Buffer is larger than filesize
     */
    @Test
    public void readAll_BufferLargerThanFile() throws IOException
    {
        testReading(4L, 8);
        testReading(4L, 1000);
        testReading(250L, 1000);
        testReading(500L, 1000);
        testReading(999L, 1000);
        testReading(9876L, 11118);
    }

    /**
     */
    @Test
    public void readWindow_BufferSmallerThanFile() throws IOException
    {
        // not to the end
        testReading(20L, 10, 0, 18, 1, 0, 1);
        // not from the start
        testReading(20L, 10, 1, 19, 0, 0, 0);
        // fully
        testReading(20L, 10, 0, 19, 0, 0, 0);
        // smaller on both ends
        testReading(20L, 10, 1, 18, 1, 0, 1);
        // just a byte, we will have a lot of buffer left
        testReading(20L, 10, 3, 3, 9, 0, 16);
        // just four byte, we will have a lot of buffer left
        testReading(20L, 10, 2, 7, 4, 0, 12);
    }

    /**
     */
    @Test
    public void readWindow_BufferLargerThanFile() throws IOException
    {
        // fully
        testReading(20L, 30, 0, 19, 0, 0, 0);
        // not to the end
        testReading(20L, 30, 0, 18, 1, 0, 1);
        // not from the start
        testReading(20L, 30, 1, 19, 0, 0, 0);
        // smaller on both ends
        testReading(20L, 30, 1, 18, 1, 0, 1);
        // just a byte, we will have a lot of buffer left
        testReading(20L, 30, 3, 3, 16, 0, 16);
        // just four byte, we will have a lot of buffer left
        testReading(20L, 30, 2, 7, 12, 0, 12);
    }

    /**
     * Ok, play with markers and the different loading strategy in that case
     * Marker no crossing a reload
     */
    @Test
    public void markersWithinBuffer_Loading()
    {
//        final Path p = create(i -> i, 100);
//        try (var buffer = new BasicWindowedBuffer(p, 0, 20))
//        {
//
//        }
    }

    /**
     * Ok, play with markers and the different loading strategy in that case
     * Marker crossing a reload
     */
    @Test
    public void markersCrossingBuffer_Loading()
    {

    }

    /**
     * See if get data back from marked areas
     */
    @Test
    public void getMarkerData_NotAcrossReloads()
    {

    }

    /**
     * Get data back with a reload in between
     */
    @Test
    public void getMarkerData_AcrossReloads()
    {

    }

    /**
     * Match data that is not with a reload
     */
    @Test
    public void matchMarkerData_NotAcrossReloads()
    {
        // not matching length

        // matching length but not content

        // matching
    }

    /**
     * Match data that is with a reload
     */
    @Test
    public void matchMarkerData_AcrossReloads()
    {
        // not matching length

        // matching length but not content

        // matching

    }


}
