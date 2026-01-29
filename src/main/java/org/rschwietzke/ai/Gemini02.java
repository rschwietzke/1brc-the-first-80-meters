package org.rschwietzke.ai;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.TreeMap;

public class Gemini02 {

    private static final String FILE = "./measurements.txt";
    // 2MB buffer keeps data in the CPU L3 cache for fast scanning
    private static final int CHUNK_SIZE = 1024 * 1024 * 2; 

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();

        try (RandomAccessFile file = new RandomAccessFile(args[0], "r");
             FileChannel channel = file.getChannel()) {
            
            long fileSize = channel.size();
            long position = 0;
            
            // Reusable buffer to copy data from disk to heap
            byte[] buffer = new byte[CHUNK_SIZE]; 
            Aggregator aggregator = new Aggregator();
            
            while (position < fileSize) {
                long remaining = fileSize - position;
                int size = (int) Math.min(remaining, CHUNK_SIZE);
                
                MappedByteBuffer mmap = channel.map(MapMode.READ_ONLY, position, size);
                
                // BULK COPY: Fast copy from Disk -> Java Array
                // We do this because scanning a Java byte[] is much faster 
                // than scanning a MappedByteBuffer byte-by-byte.
                mmap.get(buffer, 0, size);
                
                // Handle split lines at the end of the chunk
                // We backtrack to the last newline so we don't process a partial line
                int limit = size;
                if (remaining > size) {
                    while (limit > 0 && buffer[limit - 1] != '\n') {
                        limit--;
                    }
                }
                
                // Process this chunk
                processBuffer(buffer, limit, aggregator);
                
                // Advance position by how much we actually processed
                position += limit;
            }
            
            aggregator.printResults();
        }
        
                System.err.println("Time: " + (System.currentTimeMillis() - start) + "ms");
    }

    private static void processBuffer(byte[] buffer, int limit, Aggregator aggregator) {
        int ptr = 0;
        
        while (ptr < limit) {
            int nameStart = ptr;
            
            // --- 1. Find Semicolon (Standard Loop) ---
            // No bit-twiddling, just a simple scan.
            // The JVM compiles this into very efficient assembly (vectorized AVX instructions) automatically.
            while (buffer[ptr] != ';') {
                ptr++;
            }
            
            // Found it
            int len = ptr - nameStart;
            
            // --- 2. Hashing ---
            // Simple FNV-1a hash
            int hash = 0x811c9dc5;
            for (int i = 0; i < len; i++) {
                hash ^= buffer[nameStart + i];
                hash *= 0x01000193;
            }
            
            // --- 3. Parse Number (Manual) ---
            ptr++; // Skip ';'
            
            int temp = 0;
            boolean negative = false;
            
            // Check negative
            if (buffer[ptr] == '-') {
                negative = true;
                ptr++;
            }
            
            // Parse first digit
            temp = buffer[ptr++] - '0';
            
            // If next char is not '.', we have a 2-digit number (e.g. 12.3)
            if (buffer[ptr] != '.') {
                temp = temp * 10 + (buffer[ptr++] - '0');
            }
            
            ptr++; // Skip '.'
            
            // Parse decimal digit
            temp = temp * 10 + (buffer[ptr++] - '0');
            
            if (negative) temp = -temp;
            
            ptr++; // Skip '\n'
            
            // --- 4. Aggregate ---
            aggregator.add(buffer, nameStart, len, hash, temp);
        }
    }

    /**
     * Standard Aggregator using Arrays.
     * No Unsafe, just standard Java arrays.
     */
    static class Aggregator {
        private static final int MAP_SIZE = 16384; // Must be power of 2
        private static final int MASK = MAP_SIZE - 1;

        // Structure of Arrays (SoA)
        byte[][] names = new byte[MAP_SIZE][];
        int[] mins = new int[MAP_SIZE];
        int[] maxs = new int[MAP_SIZE];
        long[] sums = new long[MAP_SIZE];
        int[] counts = new int[MAP_SIZE];

        Aggregator() {
            Arrays.fill(mins, Integer.MAX_VALUE);
            Arrays.fill(maxs, Integer.MIN_VALUE);
        }

        void add(byte[] buffer, int nameStart, int len, int hash, int temp) {
            // Keep index within bounds
            int idx = hash & MASK;
            
            while (true) {
                // Check if slot is empty
                if (names[idx] == null) {
                    // Found new station!
                    byte[] nameBytes = new byte[len];
                    System.arraycopy(buffer, nameStart, nameBytes, 0, len);
                    
                    names[idx] = nameBytes;
                    mins[idx] = temp;
                    maxs[idx] = temp;
                    sums[idx] = temp;
                    counts[idx] = 1;
                    return;
                }
                
                // Check for collision (are these the same bytes?)
                boolean match = true;
                if (names[idx].length != len) {
                    match = false;
                } else {
                    for (int i = 0; i < len; i++) {
                        if (names[idx][i] != buffer[nameStart + i]) {
                            match = false;
                            break;
                        }
                    }
                }
                
                if (match) {
                    // Same station, update stats
                    if (temp < mins[idx]) mins[idx] = temp;
                    if (temp > maxs[idx]) maxs[idx] = temp;
                    sums[idx] += temp;
                    counts[idx]++;
                    return;
                }
                
                // Collision happened, move to next slot (Linear Probing)
                idx = (idx + 1) & MASK;
            }
        }
        
        void printResults() {
            TreeMap<String, String> sorted = new TreeMap<>();
            for (int i = 0; i < MAP_SIZE; i++) {
                if (names[i] != null) {
                    double min = mins[i] / 10.0;
                    double mean = Math.round((double)sums[i] / counts[i]) / 10.0;
                    double max = maxs[i] / 10.0;
                    
                    String name = new String(names[i], StandardCharsets.UTF_8);
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

