package org.rschwietzke.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases by Gemini pro, unless otherwise marked
 */
public class PositionableReaderTest 
{
    @TempDir
    Path tempDir;

    /**
     * Helper method to create test files safely written in UTF-8.
     */
    private Path createTestFile(String fileName, String content) throws IOException 
    {
        Path filePath = tempDir.resolve(fileName);
        Files.write(filePath, content.getBytes(StandardCharsets.UTF_8));
        return filePath;
    }

    @Test
    void testReadFromStartToEnd() throws IOException 
    {
        String content = "Line 1\nLine 2\nLine 3\n";
        Path file = createTestFile("basic.txt", content);
        long fileLength = Files.size(file);

        try (PositionableReader reader = new PositionableReader(file.toString(), 0, fileLength)) 
        {
            assertEquals("Line 1", reader.readln());
            assertEquals("Line 2", reader.readln());
            assertEquals("Line 3", reader.readln());
            assertNull(reader.readln(), "Should return null when EOF is reached");
        }
    }

    /**
     * Non-AI
     * 
     * @throws IOException
     */
    @Test
    void testLargerThanBufferFile() throws IOException 
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 1000; i++)
        {
            sb.append("Line ");
            sb.append(i);
            sb.append('\n');
        }
        
        Path file = createTestFile("basic.txt", sb.toString());
        long fileLength = Files.size(file);

        // full
        try (PositionableReader reader = new PositionableReader(file.toString(), 0, fileLength)) 
        {
            for (int i = 1; i <= 1000; i++)
            {
                assertEquals("Line " + i, reader.readln());
            }
            assertNull(reader.readln(), "Should return null when EOF is reached");
        }
        
        // offset +1
        try (PositionableReader reader = new PositionableReader(file.toString(), 1, fileLength)) 
        {
            for (int i = 2; i <= 1000; i++)
            {
                assertEquals("Line " + i, reader.readln());
            }
            assertNull(reader.readln(), "Should return null when EOF is reached");
        }
        
        // offset +10, end  - 11
        // Line 999 <-- -11 is here 
        // Line 1000
        try (PositionableReader reader = new PositionableReader(file.toString(), 10, fileLength - 12)) 
        {
            for (int i = 3; i <= 999; i++)
            {
                assertEquals("Line " + i, reader.readln());
            }
            assertNull(reader.readln(), "Should return null when EOF is reached");
        }
    }
    
    @Test
    void testChunkingSkipsPartialFirstLine() throws IOException 
    {
        // 0123456 7890123 4567890
        // Line 1\nLine 2\nLine 3\n
        String content = "Line 1\nLine 2\nLine 3\n";
        Path file = createTestFile("chunk.txt", content);

        // Start at byte 2 (middle of "Line 1"). 
        // End at byte 14 (end of "Line 2").
        try (PositionableReader reader = new PositionableReader(file.toString(), 2, 14)) 
        {
            // The constructor should have called raf.readLine() to skip the rest of "Line 1"
            assertEquals("Line 2", reader.readln(), "Should read the first full line after the skipped partial line");
            assertNull(reader.readln(), "Should return null because pointer crossed the 'to' limit");
        }
    }

    @Test
    void testUtf8Handling() throws IOException 
    {
        // "ÄÖÜ" takes 6 bytes in UTF-8. "😎" takes 4 bytes.
        String content = "ÄÖÜ\nHello 😎\nWorld\n";
        Path file = createTestFile("utf8.txt", content);
        long fileLength = Files.size(file);

        try (PositionableReader reader = new PositionableReader(file.toString(), 0, fileLength)) 
        {
            assertEquals("ÄÖÜ", reader.readln(), "Should correctly decode multi-byte German umlauts");
            assertEquals("Hello 😎", reader.readln(), "Should correctly decode multi-byte emojis");
            assertEquals("World", reader.readln());
            assertNull(reader.readln());
        }
    }

    @Test
    void testFileWithoutTrailingNewline() throws IOException 
    {
        String content = "First\nSecond";
        Path file = createTestFile("no_trailing.txt", content);
        long fileLength = Files.size(file);

        try (PositionableReader reader = new PositionableReader(file.toString(), 0, fileLength)) 
        {
            assertEquals("First", reader.readln());
            assertEquals("Second", reader.readln(), "Should return the last line even if it lacks a \\n");
            assertNull(reader.readln());
        }
    }

    @Test
    void testEmptyLinesHandling() throws IOException 
    {
        String content = "A\n\nB\n";
        Path file = createTestFile("empty_lines.txt", content);
        long fileLength = Files.size(file);

        try (PositionableReader reader = new PositionableReader(file.toString(), 0, fileLength)) 
        {
            assertEquals("A", reader.readln());
            assertEquals("", reader.readln(), "Should return an empty string for empty lines");
            assertEquals("B", reader.readln());
            assertNull(reader.readln());
        }
    }

    @Test
    void testBoundaryExactlyAtNewline() throws IOException 
    {
        // A\n (2 bytes)
        // B\n (2 bytes)
        String content = "A\nB\n";
        Path file = createTestFile("boundary.txt", content);
        
        // Start exactly at byte 1 (the '\n' character).
        // constructor: seek(1). raf.readLine() reads from \n to end of line (which is empty string) 
        // Pointer is now at 2 (start of B).
        try (PositionableReader reader = new PositionableReader(file.toString(), 1, 4)) 
        {
            assertEquals("B", reader.readln());
            assertNull(reader.readln());
        }
    }
    
    /**
     * Non-AI
     * @throws IOException
     */
    @Test
    void testBoundaryTests1() throws IOException 
    {
        String content = """
                Line 1
                Line 2
                Line 3
                """;
        Path file = createTestFile("boundary.txt", content);
        
        try (PositionableReader reader = new PositionableReader(file.toString(), 0, content.length())) 
        {
            assertEquals("Line 1", reader.readln());
            assertEquals("Line 2", reader.readln());
            assertEquals("Line 3", reader.readln());
            assertNull(reader.readln());
        }
        // +1 
        try (PositionableReader reader = new PositionableReader(file.toString(), 1, content.length())) 
        {
            assertEquals("Line 2", reader.readln());
            assertEquals("Line 3", reader.readln());
            assertNull(reader.readln());
        }
        // +2 
        try (PositionableReader reader = new PositionableReader(file.toString(), 2, content.length())) 
        {
            assertEquals("Line 2", reader.readln());
            assertEquals("Line 3", reader.readln());
            assertNull(reader.readln());
        }
        // +3 
        try (PositionableReader reader = new PositionableReader(file.toString(), 3, content.length())) 
        {
            assertEquals("Line 2", reader.readln());
            assertEquals("Line 3", reader.readln());
            assertNull(reader.readln());
        }
        // +4 
        try (PositionableReader reader = new PositionableReader(file.toString(), 4, content.length())) 
        {
            assertEquals("Line 2", reader.readln());
            assertEquals("Line 3", reader.readln());
            assertNull(reader.readln());
        }
        // +5 
        try (PositionableReader reader = new PositionableReader(file.toString(), 5, content.length())) 
        {
            assertEquals("Line 2", reader.readln());
            assertEquals("Line 3", reader.readln());
            assertNull(reader.readln());
        }
        // +6 
        try (PositionableReader reader = new PositionableReader(file.toString(), 6, content.length())) 
        {
            assertEquals("Line 2", reader.readln());
            assertEquals("Line 3", reader.readln());
            assertNull(reader.readln());
        }
        // +7, the \n, won't see \n
        try (PositionableReader reader = new PositionableReader(file.toString(), 7, content.length())) 
        {
            assertEquals("Line 3", reader.readln());
            assertNull(reader.readln());
        }
        // +8, the L, hence we don't see 2 
        try (PositionableReader reader = new PositionableReader(file.toString(), 8, content.length())) 
        {
            assertEquals("Line 3", reader.readln());
            assertNull(reader.readln());
        }    
    }

    /**
     * Non-AI
     * @throws IOException
     */
    @Test
    void testBoundaryTests2_BoundariesAtEOF() throws IOException 
    {
        // Line 1 = 0 - 6
        // Line 2 = 7 - 13
        // Line 3 = 14 - 20
        String content = """
                Line 1
                Line 2
                Line 3
                """;
        Path file = createTestFile("boundary.txt", content);
        assertEquals(21, content.length());
        
        try (PositionableReader reader = new PositionableReader(file.toString(), 0, 21)) 
        {
            assertEquals("Line 1", reader.readln());
            assertEquals("Line 2", reader.readln());
            assertEquals("Line 3", reader.readln());
            assertNull(reader.readln());
        }
        // -1 
        try (PositionableReader reader = new PositionableReader(file.toString(), 0, 20)) 
        {
            assertEquals("Line 1", reader.readln());
            assertEquals("Line 2", reader.readln());
            assertEquals("Line 3", reader.readln());
            assertNull(reader.readln());
        }
        // -2 
        try (PositionableReader reader = new PositionableReader(file.toString(), 0, 19)) 
        {
            assertEquals("Line 1", reader.readln());
            assertEquals("Line 2", reader.readln());
            assertEquals("Line 3", reader.readln());
            assertNull(reader.readln());
        }
        // -3
        try (PositionableReader reader = new PositionableReader(file.toString(), 0, 18)) 
        {
            assertEquals("Line 1", reader.readln());
            assertEquals("Line 2", reader.readln());
            assertEquals("Line 3", reader.readln());
            assertNull(reader.readln());
        }
        // -4 
        try (PositionableReader reader = new PositionableReader(file.toString(), 0, 17))
        {
            assertEquals("Line 1", reader.readln());
            assertEquals("Line 2", reader.readln());
            assertEquals("Line 3", reader.readln());
            assertNull(reader.readln());
        }
        // -5 
        try (PositionableReader reader = new PositionableReader(file.toString(), 0, 16)) 
        {
            assertEquals("Line 1", reader.readln());
            assertEquals("Line 2", reader.readln());
            assertEquals("Line 3", reader.readln());
            assertNull(reader.readln());
        }
        // -6 
        try (PositionableReader reader = new PositionableReader(file.toString(), 0, 15)) 
        {
            assertEquals("Line 1", reader.readln());
            assertEquals("Line 2", reader.readln());
            assertEquals("Line 3", reader.readln());
            assertNull(reader.readln());
        }
        // -7 
        try (PositionableReader reader = new PositionableReader(file.toString(), 0, 14)) 
        {
            assertEquals("Line 1", reader.readln());
            assertEquals("Line 2", reader.readln());
            assertNull(reader.readln());
        }
        // -8 
        try (PositionableReader reader = new PositionableReader(file.toString(), 0, 13)) 
        {
            assertEquals("Line 1", reader.readln());
            assertEquals("Line 2", reader.readln());
            assertNull(reader.readln());
        }
        
        // try to find the start point again
        // -7 
        try (PositionableReader reader = new PositionableReader(file.toString(), 13, 21)) 
        {
            assertEquals("Line 3", reader.readln());
            assertNull(reader.readln());
        }
        // -7 
        try (PositionableReader reader = new PositionableReader(file.toString(), 14, 21)) 
        {
            assertNull(reader.readln());
        }

    }
    
    /**
     * Non-AI
     * @throws IOException
     */
    @Test
    void testChunking() throws IOException 
    {
        // Line 1 = 0 - 6
        // Line 2 = 7 - 13
        // Line 3 = 14 - 20
        String content = """
                Line 1
                Line 2
                Line 3
                """;
        Path file = createTestFile("boundary.txt", content);
        assertEquals(21, content.length());
        
        int chunkSize = 21 / 3;
        
        try (PositionableReader reader = new PositionableReader(file.toString(), 0, 1 * chunkSize)) 
        {
            assertEquals("Line 1", reader.readln());
            assertNull(reader.readln());
        }
        try (PositionableReader reader = new PositionableReader(file.toString(), 1 * chunkSize - 1, 2 * chunkSize)) 
        {
            assertEquals("Line 2", reader.readln());
            assertNull(reader.readln());
        }
        try (PositionableReader reader = new PositionableReader(file.toString(), 2 * chunkSize - 1, 3 * chunkSize)) 
        {
            assertEquals("Line 3", reader.readln());
            assertNull(reader.readln());
        }
    }

    @Test
    void testCloseReleasesResource() throws IOException 
    {
        Path file = createTestFile("close.txt", "Test\n");
        PositionableReader reader = new PositionableReader(file.toString(), 0, 10);
        
        reader.close();

        assertThrows(IOException.class, reader::readln, "Reading after close should throw an IOException");
    }
}