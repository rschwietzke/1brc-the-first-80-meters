package org.rschwietzke.ai;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.TreeMap;

public class Gemini04 {

    private static final String FILE = "./measurements.txt";
    // Increase buffer to 32MB to reduce syscalls
    private static final int BUFFER_SIZE = 1024 * 1024 * 32; 

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
    
        try (RandomAccessFile file = new RandomAccessFile(args[0], "r");
             FileChannel channel = file.getChannel()) {
            
            // Direct buffer can sometimes be faster for I/O, but Heap is safer for array access.
            // We stick to Heap for pure Java simplicity.
            ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE);
            Aggregator aggregator = new Aggregator();
            
            byte[] leftover = new byte[256];
            int leftoverLength = 0;
            
            while (channel.read(buf) != -1) {
                buf.flip();
                byte[] data = buf.array();
                int limit = buf.limit();
                int ptr = 0;
                
                // 1. Handle leftover from previous chunk
                if (leftoverLength > 0) {
                    int newlinePos = -1;
                    // Scan safely
                    for (int i = 0; i < limit; i++) {
                        if (data[i] == '\n') {
                            newlinePos = i;
                            break;
                        }
                    }
                    
                    int pieceLen = newlinePos + 1;
                    System.arraycopy(data, 0, leftover, leftoverLength, pieceLen);
                    // Process the stitched line
                    processLine(leftover, leftoverLength + pieceLen, aggregator);
                    
                    ptr = pieceLen;
                    leftoverLength = 0;
                }
                
                // 2. Find the last complete line (Safe Limit)
                int safeLimit = limit;
                while (safeLimit > ptr && data[safeLimit - 1] != '\n') {
                    safeLimit--;
                }
                
                // 3. Process the bulk
                processChunk(data, ptr, safeLimit, aggregator);
                
                // 4. Save tail
                if (safeLimit < limit) {
                    int remaining = limit - safeLimit;
                    System.arraycopy(data, safeLimit, leftover, 0, remaining);
                    leftoverLength = remaining;
                }
                
                buf.clear();
            }
            
            aggregator.printResults();
        }
                        System.err.println("Time: " + (System.currentTimeMillis() - start) + "ms");
    }
    
    private static void processLine(byte[] buffer, int limit, Aggregator agg) {
        // Safe version for single lines
        processChunk(buffer, 0, limit, agg);
    }

    private static void processChunk(byte[] buffer, int ptr, int limit, Aggregator aggregator) {
        // We assume 'limit' falls exactly after a '\n', so every line is complete.
        // This allows us to remove 'ptr < limit' checks in the inner loops.
        
        while (ptr < limit) {
            int nameStart = ptr;
            int hash = 0x811c9dc5;
            
            // --- OPTIMIZATION: Sentinel Loop ---
            // We removed (ptr < limit). We rely on the fact that a ';' MUST exist.
            // This removes 1 branch instruction per byte.
            while (true) {
                byte b = buffer[ptr];
                if (b == ';') break;
                hash ^= b;
                hash *= 0x01000193;
                ptr++;
            }
            
            int len = ptr - nameStart;
            ptr++; // Skip ';'
            
            // --- OPTIMIZATION: Branchless-ish Number Parsing ---
            // We blindly read 3 digits. 
            // Most temps are "12.3" (4 chars) or "8.9" (3 chars).
            
            int temp;
            
            byte b1 = buffer[ptr++];
            boolean negative = (b1 == '-');
            if (negative) {
                b1 = buffer[ptr++];
            }
            
            int d1 = b1 - '0';
            byte b2 = buffer[ptr++];
            
            if (b2 == '.') {
                // Form: X.Y
                int d2 = buffer[ptr++] - '0';
                temp = d1 * 10 + d2;
            } else {
                // Form: XX.Y
                int d2 = b2 - '0';
                // skip dot
                ptr++; 
                int d3 = buffer[ptr++] - '0';
                temp = d1 * 100 + d2 * 10 + d3;
            }
            
            if (negative) temp = -temp;
            
            ptr++; // Skip '\n'
            
            aggregator.add(buffer, nameStart, len, hash, temp);
        }
    }

    static class Aggregator {
        private static final int MAP_SIZE = 16384; 
        private static final int MASK = MAP_SIZE - 1;

        // --- OPTIMIZATION: Flat Byte Array ---
        // Instead of byte[][], we store all names in one giant array.
        // This improves cache locality (no pointer chasing to other objects).
        // [Offset, Length] are stored in a parallel 'info' array.
        private final byte[] namesBlob = new byte[1024 * 1024]; // 1MB buffer for names
        private int namesBlobPtr = 0;
        
        // Key Info: [High 32: Length] [Low 32: Offset]
        long[] keys = new long[MAP_SIZE]; 
        
        int[] mins = new int[MAP_SIZE];
        int[] maxs = new int[MAP_SIZE];
        long[] sums = new long[MAP_SIZE];
        int[] counts = new int[MAP_SIZE];

        Aggregator() {
            Arrays.fill(mins, Integer.MAX_VALUE);
            Arrays.fill(maxs, Integer.MIN_VALUE);
            // Initialize keys with -1 to indicate empty slots
            Arrays.fill(keys, -1);
        }

        // Inline the logic as much as possible
        void add(byte[] buffer, int nameStart, int len, int hash, int temp) {
            int idx = hash & MASK;
            
            while (true) {
                long keyInfo = keys[idx];
                
                if (keyInfo == -1) {
                    // --- New Entry ---
                    // Copy to blob
                    System.arraycopy(buffer, nameStart, namesBlob, namesBlobPtr, len);
                    
                    // Store Metadata: (Length << 32) | Offset
                    keys[idx] = ((long) len << 32) | (namesBlobPtr & 0xFFFFFFFFL);
                    namesBlobPtr += len;
                    
                    mins[idx] = temp;
                    maxs[idx] = temp;
                    sums[idx] = temp;
                    counts[idx] = 1;
                    return;
                }
                
                // --- Collision Check ---
                // Decode length
                int storedLen = (int) (keyInfo >>> 32);
                
                if (storedLen == len) {
                    int offset = (int) keyInfo;
                    boolean match = true;
                    
                    // Optimization: Check first and last byte first
                    // This fails fast for different strings with same length/hash
                    if (namesBlob[offset] != buffer[nameStart] || 
                        namesBlob[offset + len - 1] != buffer[nameStart + len - 1]) {
                        match = false;
                    } else {
                        // Full check
                        for (int i = 1; i < len - 1; i++) {
                            if (namesBlob[offset + i] != buffer[nameStart + i]) {
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
                }
                
                idx = (idx + 1) & MASK;
            }
        }
        
        void printResults() {
            TreeMap<String, String> sorted = new TreeMap<>();
            for (int i = 0; i < MAP_SIZE; i++) {
                if (keys[i] != -1) {
                    long keyInfo = keys[i];
                    int len = (int) (keyInfo >>> 32);
                    int offset = (int) keyInfo;
                    
                    double min = mins[i] / 10.0;
                    double mean = Math.round((double)sums[i] / counts[i]) / 10.0;
                    double max = maxs[i] / 10.0;
                    
                    String name = new String(namesBlob, offset, len, StandardCharsets.UTF_8);
                    sorted.put(name, String.format("%.1f/%.1f/%.1f", min, mean, max));
                }
            }
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            boolean first = true;
            for (var entry : sorted.entrySet()) {
                if (!first) sb.append(", ");
                sb.append(entry.getKey()).append("=").append(entry.getValue());
                first = false;
            }
            sb.append("}");
            System.out.println(sb);
        }
    }
}
