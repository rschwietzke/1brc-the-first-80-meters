package org.onebrc.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class PositionableReader implements Closeable
{
    private final long to;
    private RandomAccessFile raf;
    private byte[] lineBuf = new byte[100]; // Starts small, grows if needed
    
    // Buffering fields
    private static final int BUFFER_SIZE = 81920;
    private final byte[] buffer = new byte[BUFFER_SIZE];
    private int bufferPos = 0;
    private int bufferLimit = 0;
    private long currentPos; // Tracks our logical position in the file

    public PositionableReader(String filePath, long from, long to) throws IOException
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
            readUtf8Line();
        }
    }

    /**
     * Reads a single byte from the internal buffer, refilling it if necessary.
     */
    private int readByte() throws IOException
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
        
        // Return byte as unsigned int (0-255) to match standard InputStream behavior
        return buffer[bufferPos++] & 0xFF; 
    }

    /**
     * Read the next line and returns it. In case of end of file, we simply
     * return null.
     * @return Read string or null when EOF
     * @throws IOException
     */
    public String readln() throws IOException 
    {
        // Check our logical position against the limit
        if (this.currentPos < to) 
        {
            return readUtf8Line();
        }
        else
        {
            return null;
        }
    }

    /**
     * Helper method to read raw bytes into our lightweight array until a '\n' is found.
     */
    private String readUtf8Line() throws IOException 
    {
        int c;
        int lineLen = 0; // Reset line length for the new line

        while ((c = readByte()) != -1) 
        {
            if (c == '\n') 
            {
                break;
            }
            
            // Resize our lightweight buffer if it gets too small
            if (lineLen == lineBuf.length) 
            {
                lineBuf = Arrays.copyOf(lineBuf, lineBuf.length * 2);
            }
            
            // Cast the integer back to a byte to store it
            lineBuf[lineLen++] = (byte) c;
        }

        // If we hit EOF immediately and collected no bytes, return null
        if (c == -1 && lineLen == 0) 
        {
            return null;
        }

        // Construct the UTF-8 string directly from the exact bounds of our byte array
        return new String(lineBuf, 0, lineLen, StandardCharsets.UTF_8);
    }
    
    @Override
    public void close() throws IOException 
    {
        raf.close();
    }
}