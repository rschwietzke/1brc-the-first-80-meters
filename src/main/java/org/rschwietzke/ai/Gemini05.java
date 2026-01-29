package org.rschwietzke.ai;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.TreeMap;

public class Gemini05 {

    // 32MB Buffer reduces syscall overhead significantly
    private static final int BUFFER_SIZE = 1024 * 1024 * 32; 

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();

        String fileName = args.length > 0 ? args[0] : "./measurements.txt";

        try (RandomAccessFile file = new RandomAccessFile(fileName, "r");
             FileChannel channel = file.getChannel()) {
            
            // Allocation on Heap is faster for array access in standard Java
            ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE);
            Aggregator aggregator = new Aggregator();
            
            byte[] leftover = new byte[256];
            int leftoverLength = 0;
            
            while (channel.read(buf) != -1) {
                buf.flip();
                byte[] data = buf.array();
                int limit = buf.limit();
                int ptr = 0;
                
                // 1. Handle leftover bytes from the previous chunk
                if (leftoverLength > 0) {
                    int newlinePos = -1;
                    // Scan for the newline that completes the split line
                    for (int i = 0; i < limit; i++) {
                        if (data[i] == '\n') {
                            newlinePos = i;
                            break;
                        }
                    }
                    
                    // Copy the rest of the line into the leftover buffer
                    int pieceLen = newlinePos + 1;
                    System.arraycopy(data, 0, leftover, leftoverLength, pieceLen);
                    
                    // Process the now-complete line
                    processChunk(leftover, 0, leftoverLength + pieceLen, aggregator);
                    
                    ptr = pieceLen;
                    leftoverLength = 0;
                }
                
                // 2. Find the Safe Limit (Backtrack to the last newline)
                // We ensures we only process complete lines in the hot loop
                int safeLimit = limit;
                while (safeLimit > ptr && data[safeLimit - 1] != '\n') {
                    safeLimit--;
                }
                
                // 3. Process the Bulk Data
                processChunk(data, ptr, safeLimit, aggregator);
                
                // 4. Save the tail (split line) for the next iteration
                if (safeLimit < limit) {
                    int remaining = limit - safeLimit;
                    System.arraycopy(data, safeLimit, leftover, 0, remaining);
                    leftoverLength = remaining;
                }
                
                buf.clear();
            }
            
            aggregator.printResults();
        }
        
        System.out.println("Time: " + (System.currentTimeMillis() - start) + "ms");
    }

    private static void processChunk(byte[] buffer, int ptr, int limit, Aggregator aggregator) {
        // Because we calculated 'safeLimit', we know the buffer ends cleanly with a \n.
        // We also know every line has a ';'.
        // This allows us to remove bounds checks (ptr < limit) inside the inner parsing loops.
        
        while (ptr < limit) {
            int nameStart = ptr;
            int hash = 0x811c9dc5;
            
            // --- OPTIMIZATION 1: Sentinel Loop ---
            // We scan for ';' without checking 'ptr < limit'.
            // This saves 1 branch instruction per byte.
            while (true) {
                byte b = buffer[ptr];
                if (b == ';') break;
                hash ^= b;
                hash *= 0x01000193;
                ptr++;
            }
            
            int len = ptr - nameStart;
            ptr++; // Skip ';'
            
            // --- OPTIMIZATION 2: "Speculative" Number Parsing ---
            // We parse blindly assuming valid format (X.X or XX.X) to avoid loops.
            
            int temp;
            
            byte b1 = buffer[ptr++];
            boolean negative = (b1 == '-');
            if (negative) {
                b1 = buffer[ptr++];
            }
            
            int d1 = b1 - '0';
            byte b2 = buffer[ptr++];
            
            if (b2 == '.') {
                // Format: X.X
                int d2 = buffer[ptr++] - '0';
                temp = d1 * 10 + d2;
            } else {
                // Format: XX.X
                int d2 = b2 - '0';
                ptr++; // skip dot
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

        // --- OPTIMIZATION 3: Flat Data Layout ---
        // Storing names in a single byte array reduces object overhead and improves cache hits.
        private final byte[] namesBlob = new byte[1024 * 1024]; 
        private int namesBlobPtr = 0;
        
        // Key Info: High 32 bits = Length, Low 32 bits = Offset in namesBlob
        long[] keys = new long[MAP_SIZE]; 
        
        int[] mins = new int[MAP_SIZE];
        int[] maxs = new int[MAP_SIZE];
        long[] sums = new long[MAP_SIZE];
        int[] counts = new int[MAP_SIZE];

        Aggregator() {
            Arrays.fill(mins, Integer.MAX_VALUE);
            Arrays.fill(maxs, Integer.MIN_VALUE);
            Arrays.fill(keys, -1); // -1 indicates empty slot
        }

        void add(byte[] buffer, int nameStart, int len, int hash, int temp) {
            int idx = hash & MASK;
            
            while (true) {
                long keyInfo = keys[idx];
                
                if (keyInfo == -1) {
                    // --- Empty Slot: Insert New ---
                    System.arraycopy(buffer, nameStart, namesBlob, namesBlobPtr, len);
                    keys[idx] = ((long) len << 32) | (namesBlobPtr & 0xFFFFFFFFL);
                    namesBlobPtr += len;
                    
                    mins[idx] = temp;
                    maxs[idx] = temp;
                    sums[idx] = temp;
                    counts[idx] = 1;
                    return;
                }
                
                // --- Occupied Slot: Check Collision ---
                int storedLen = (int) (keyInfo >>> 32);
                
                if (storedLen == len) {
                    int offset = (int) keyInfo;
                    boolean match = true;
                    
                    // Fast-fail check: Look at first and last byte first
                    if (namesBlob[offset] != buffer[nameStart] || 
                        namesBlob[offset + len - 1] != buffer[nameStart + len - 1]) {
                        match = false;
                    } else {
                        // Full body check
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
                
                // Linear Probe
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
