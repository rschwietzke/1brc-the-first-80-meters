package org.rschwietzke.ai; 

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.TreeMap;

/**
 * 1BRC Single-Threaded Java 21 Solution.
 * * Key Optimizations:
 * 1. mmap: Maps file directly into virtual memory.
 * 2. Custom Hash Table: Linear probing, fixed size, no object allocation per row.
 * 3. SWAR: Scans for ';' 8 bytes at a time using bitwise logic.
 * 4. Integer Math: Parses temperatures as integers (12.3 -> 123).
 */
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.TreeMap;

public class Gemini01 {

    private static final String FILE = "./measurements.txt";
    private static final int MAP_SIZE = 1024 * 64; // Power of 2
    private static final long SEMICOLON_PATTERN = 0x3B3B3B3B3B3B3B3BL;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();

        try (RandomAccessFile file = new RandomAccessFile(args[0], "r");
             FileChannel channel = file.getChannel()) {
            
            long fileSize = channel.size();
            long position = 0;
            
            Aggregator aggregator = new Aggregator();

            while (position < fileSize) {
                long remaining = fileSize - position;
                // Map roughly 2GB or whatever is left
                long size = Math.min(remaining, Integer.MAX_VALUE);
                
                MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, position, size);
                buffer.order(ByteOrder.nativeOrder()); // CRITICAL: Fix Endianness
                
                int limit = (int) size;
                
                // CRITICAL: Handle Chunk Boundaries
                // If we didn't reach the exact EOF, we likely cut a line in half.
                // We must backtrack to the last newline '\n' and stop there.
                if (remaining > size) {
                    for (int i = limit - 1; i >= 0; i--) {
                        if (buffer.get(i) == '\n') {
                            limit = i + 1; // Include the \n in this chunk
                            break;
                        }
                    }
                }
                
                processChunk(buffer, aggregator, limit);
                
                // Move position forward by the actual amount processed (limit)
                position += limit;
            }
            
            aggregator.printResults();
        }
        
        System.err.println("Time: " + (System.currentTimeMillis() - start) + "ms");
    }

    private static void processChunk(MappedByteBuffer buffer, Aggregator aggregator, int limit) {
        int ptr = 0;
        
        while (ptr < limit) {
            int nameStart = ptr;
            
            int semicolonPos = -1;
            
            // --- 1. Find the Semicolon using SWAR ---
            // CRITICAL: Safe check to avoid Integer Overflow (ptr + 8 wrapping around)
            // We check if ptr is within the safe zone (limit - 8)
            while (ptr <= limit - 8) {
                long word = buffer.getLong(ptr);
                long match = word ^ SEMICOLON_PATTERN; 
                long mask = (match - 0x0101010101010101L) & (~match) & 0x8080808080808080L;
                
                if (mask != 0) {
                    int index = Long.numberOfTrailingZeros(mask) >>> 3; 
                    if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
                        index = 7 - (Long.numberOfLeadingZeros(mask) >>> 3);
                    }
                    semicolonPos = ptr + index;
                    ptr += index;
                    break;
                }
                ptr += 8;
            }
            
            // Slow path (tail or if fast path didn't find it)
            if (semicolonPos == -1) {
                while (ptr < limit) {
                    if (buffer.get(ptr) == ';') {
                        semicolonPos = ptr;
                        break;
                    }
                    ptr++;
                }
            }

            // If we still didn't find a semicolon, we might be at a trailing fragment (shouldn't happen with correct chunking)
            if (semicolonPos == -1) break;

            // --- 2. Calculate Hash ---
            int len = semicolonPos - nameStart;
            int hash = 0x811c9dc5;
            for (int i = 0; i < len; i++) {
                hash ^= buffer.get(nameStart + i);
                hash *= 0x01000193;
            }
            hash = hash & (MAP_SIZE - 1);

            // --- 3. Parse Temperature ---
            ptr++; // Skip semicolon
            
            int temp = 0;
            boolean negative = false;
            
            // We assume valid format XX.X or X.X so we don't strictly check limit here for speed,
            // relying on correct chunking ending with \n.
            byte b = buffer.get(ptr++);
            if (b == '-') {
                negative = true;
                b = buffer.get(ptr++);
            }
            
            temp = b - '0';
            b = buffer.get(ptr++);
            if (b != '.') {
                temp = temp * 10 + (b - '0');
                ptr++; // Skip dot
            }
            
            b = buffer.get(ptr++);
            temp = temp * 10 + (b - '0');
            
            if (negative) temp = -temp;
            
            ptr++; // Skip newline

            // --- 4. Aggregate ---
            aggregator.add(buffer, nameStart, len, hash, temp);
        }
    }

    static class Aggregator {
        byte[][] names = new byte[MAP_SIZE][];
        int[] mins = new int[MAP_SIZE];
        int[] maxs = new int[MAP_SIZE];
        long[] sums = new long[MAP_SIZE];
        int[] counts = new int[MAP_SIZE];

        Aggregator() {
            Arrays.fill(mins, Integer.MAX_VALUE);
            Arrays.fill(maxs, Integer.MIN_VALUE);
        }

        void add(MappedByteBuffer buffer, int nameStart, int len, int hash, int temp) {
            int idx = hash;
            while (true) {
                if (names[idx] == null) {
                    byte[] nameBytes = new byte[len];
                    for(int i=0; i<len; i++) {
                        nameBytes[i] = buffer.get(nameStart + i);
                    }
                    names[idx] = nameBytes;
                    mins[idx] = temp;
                    maxs[idx] = temp;
                    sums[idx] = temp;
                    counts[idx] = 1;
                    return;
                }
                
                // CRITICAL: Manual Byte Comparison (Arrays.equals crashes with null/ByteBuffer)
                boolean match = true;
                if (names[idx].length != len) {
                    match = false;
                } else {
                    for (int i = 0; i < len; i++) {
                        if (names[idx][i] != buffer.get(nameStart + i)) {
                            match = false;
                            break;
                        }
                    }
                }
                
                if (match) {
                     if (temp < mins[idx]) mins[idx] = temp;
                     if (temp > maxs[idx]) maxs[idx] = temp;
                     sums[idx] += temp;
                     counts[idx]++;
                     return;
                }
                
                idx = (idx + 1) & (MAP_SIZE - 1);
            }
        }
        
        void printResults() {
            TreeMap<String, String> sortedResults = new TreeMap<>();
            for (int i = 0; i < MAP_SIZE; i++) {
                if (names[i] != null) {
                    double min = mins[i] / 10.0;
                    double mean = Math.round((double)sums[i] / counts[i]) / 10.0;
                    double max = maxs[i] / 10.0;
                    String name = new String(names[i], StandardCharsets.UTF_8);
                    sortedResults.put(name, String.format("%.1f/%.1f/%.1f", min, mean, max));
                }
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            boolean first = true;
            for (var entry : sortedResults.entrySet()) {
                if (!first) sb.append(", ");
                sb.append(entry.getKey()).append("=").append(entry.getValue());
                first = false;
            }
            sb.append("}");
            System.out.println(sb);
        }
    }
}
