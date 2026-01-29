package org.rschwietzke.ai;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.TreeMap;

public class Gemini03 {

    private static final String FILE = "./measurements.txt";
    private static final int BUFFER_SIZE = 1024 * 1024; // 1MB buffer

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();

        try (RandomAccessFile file = new RandomAccessFile(args[0], "r");
             FileChannel channel = file.getChannel()) {
            
            ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE);
            Aggregator aggregator = new Aggregator();
            
            byte[] leftover = new byte[256];
            int leftoverLength = 0;
            
            while (channel.read(buf) != -1) {
                buf.flip();
                byte[] data = buf.array();
                int limit = buf.limit();
                int ptr = 0;
                
                // 1. Handle leftover
                if (leftoverLength > 0) {
                    int newlinePos = -1;
                    for (int i = 0; i < limit; i++) {
                        if (data[i] == '\n') {
                            newlinePos = i;
                            break;
                        }
                    }
                    int pieceLen = newlinePos + 1;
                    System.arraycopy(data, 0, leftover, leftoverLength, pieceLen);
                    processLine(leftover, 0, leftoverLength + pieceLen, aggregator);
                    ptr = pieceLen;
                    leftoverLength = 0;
                }
                
                // 2. Find safe limit
                int safeLimit = limit;
                while (safeLimit > ptr && data[safeLimit - 1] != '\n') {
                    safeLimit--;
                }
                
                // 3. Process Chunk
                processChunk(data, ptr, safeLimit, aggregator);
                
                // 4. Save leftover
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
    
    private static void processLine(byte[] data, int start, int limit, Aggregator agg) {
        processChunk(data, start, limit, agg);
    }

    private static void processChunk(byte[] buffer, int start, int limit, Aggregator aggregator) {
        int ptr = start;
        
        while (ptr < limit) {
            int nameStart = ptr;
            
            // --- OPTIMIZATION: Hash & Scan together ---
            int hash = 0x811c9dc5;
            while (ptr < limit) {
                byte b = buffer[ptr];
                if (b == ';') {
                    break;
                }
                hash ^= b;
                hash *= 0x01000193;
                ptr++;
            }
            
            int len = ptr - nameStart;
            ptr++; // Skip ';'
            
            // --- Fast Number Parse ---
            int temp = 0;
            boolean negative = false;
            
            if (buffer[ptr] == '-') {
                negative = true;
                ptr++;
            }
            
            int d1 = buffer[ptr++] - '0';
            
            byte b2 = buffer[ptr++];
            if (b2 == '.') {
                int d2 = buffer[ptr++] - '0';
                temp = d1 * 10 + d2;
            } else {
                int d2 = b2 - '0';
                ptr++; // skip dot
                int d3 = buffer[ptr++] - '0';
                temp = d1 * 100 + d2 * 10 + d3;
            }
            
            if (negative) temp = -temp;
            ptr++; // Skip newline
            
            aggregator.add(buffer, nameStart, len, hash, temp);
        }
    }

    static class Aggregator {
        private static final int MAP_SIZE = 16384; 
        private static final int MASK = MAP_SIZE - 1;

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
            int idx = hash & MASK;
            
            while (true) {
                if (names[idx] == null) {
                    byte[] nameBytes = new byte[len];
                    System.arraycopy(buffer, nameStart, nameBytes, 0, len);
                    names[idx] = nameBytes;
                    mins[idx] = temp;
                    maxs[idx] = temp;
                    sums[idx] = temp;
                    counts[idx] = 1;
                    return;
                }
                
                if (names[idx].length == len) {
                    boolean match = true;
                    // Optimization: Check first and last byte first to fail fast
                    // (Assuming len > 0, which it is)
                    if (names[idx][0] != buffer[nameStart] || 
                        names[idx][len-1] != buffer[nameStart+len-1]) {
                        match = false;
                    } else {
                        for (int i = 1; i < len - 1; i++) {
                            if (names[idx][i] != buffer[nameStart + i]) {
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
                if (names[i] != null) {
                    double min = mins[i] / 10.0;
                    double mean = Math.round((double)sums[i] / counts[i]) / 10.0;
                    double max = maxs[i] / 10.0;
                    sorted.put(new String(names[i], StandardCharsets.UTF_8), 
                               String.format("%.1f/%.1f/%.1f", min, mean, max));
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
