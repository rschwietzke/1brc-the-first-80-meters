package org.onebrc.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class PositionableByteReader implements Closeable
{
    private final long to;
    private RandomAccessFile raf;
    private final Line line = new Line();
    
    // Buffering fields
    private static final int BUFFER_SIZE = 81920;
    private final byte[] buffer = new byte[BUFFER_SIZE];
    private int bufferPos = 0;
    private int bufferLimit = 0;
    private long currentPos; // Tracks our logical position in the file

    public PositionableByteReader(String filePath, long from, long to) throws IOException
    {
        this.to = to;
        this.raf = new RandomAccessFile(filePath, "r");

        // position
        this.raf.seek(from);
        this.currentPos = from;
        
        // if we are not from 0, we read the first "half line" and throw it away
        if (from > 0)
        {
            // We use our own buffered read method here instead of raf.readLine()
            // so our buffer and logical pointer (currentPos) stay perfectly synchronized.
            skipLine();
        }
    }

    /**
     * Reads a single byte from the internal buffer, refilling it if necessary.
     */
    private byte readByte() throws IOException
    {
        // If we have consumed all bytes in our buffer, fetch the next chunk
        if (bufferPos >= bufferLimit)
        {
            bufferLimit = raf.read(buffer);
            bufferPos = 0;
            
            if (bufferLimit == -1)
            {
                return -1; // EOF reached
            }
        }
        
        currentPos++; // Advance our logical file pointer
        
        return buffer[bufferPos++]; 
    }

    /**
     * Read the next line and returns it. In case of end of file, we simply
     * return null.
     * @return Read string or null when EOF
     * @throws IOException
     */
    public Line readln() throws IOException 
    {
        // Check our logical position against the limit
        if (this.currentPos < to) 
        {
            return readLine();
        }
        else
        {
            return null;
        }
    }

    /**
     * Skip over the first things till \n
     */
    private void skipLine() throws IOException 
    {
        byte c;

        // read the rest
        while ((c = readByte()) != -1) 
        {
            if (c == '\n') 
            {
                break;
            }
        }
    }
    
    /**
     * Helper method to read raw bytes into our lightweight array until a '\n' is found.
     */
    private Line readLine() throws IOException 
    {
        byte c;
        line.length = 0; // Reset line length for the new line
        int hash = 0;

        // read till semicolon
        while ((c = readByte()) != -1) 
        {
            if (c == ';') 
            {
                line.semicolon = line.length++;
                line.cityHash = hash;
                break;
            }
            hash = hash * 31 + c;
            
            // Resize our lightweight buffer if it is too small
            if (line.length == line.bytes.length) 
            {
                line.bytes = Arrays.copyOf(line.bytes, line.bytes.length * 2);
            }
            
            line.bytes[line.length++] = c;
        }
        
        // read the rest
        while ((c = readByte()) != -1) 
        {
            if (c == '\n') 
            {
                break;
            }
            
            // Resize our lightweight buffer if it gets too small
            if (line.length == line.bytes.length) 
            {
                line.bytes = Arrays.copyOf(line.bytes, line.bytes.length * 2);
            }
            
            line.bytes[line.length++] = c;
        }

        // If we hit EOF immediately and collected no bytes, return null
        if (c == -1 && line.length == 0) 
        {
            return null;
        }

        return line;
    }
    
    @Override
    public void close() throws IOException 
    {
        raf.close();
    }
    
    public static class Line
    {
        public byte[] bytes = new byte[100]; // Starts small, grows if needed
        public int start = 0;
        public int length = 0;
        public int semicolon = 0;
        public int cityHash = 0;
        public int temperature = 0;
        
        public String toString()
        {
            return new String(bytes, 0, semicolon);
        }
    }
}

