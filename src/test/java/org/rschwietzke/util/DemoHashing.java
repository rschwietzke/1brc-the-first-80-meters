package org.rschwietzke.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DemoHashing
{
    static int hash31(String s)
    {
        var b = s.getBytes();

        var h = 0;
        for (int i = 0; i < b.length; i++)
        {
            h = h * 17 + b[i];
        }

        return h;
    }

    static int hashShift(String s)
    {
        var b = s.getBytes();

        var h = 0;
        for (int i = 0; i < b.length; i++)
        {
            h ^= b[i] + (h << 5);
        }

        return h;
    }

    static void run(List<String> cities, Function<String, Integer> f)
    {
        int SIZE = 2048;
        int[] counters = new int[SIZE];
        for (String city : cities)
        {
            counters[f.apply(city) & (SIZE - 1)]++;
        }

        for (var i : counters)
        {
            if (i > 1)
            {
                System.out.print(i);
                System.out.print(';');
            }
        }
        System.out.print("\n");
    }

    public static void main(String[] args) throws IOException, URISyntaxException
    {
        final List<String> cities = Files.lines(
                Path.of(DemoHashing.class.getResource("/cities.txt").toURI()))
                .filter(c -> !c.trim().startsWith("#"))
                .limit(413)
                .sorted()
                .collect(Collectors.toList());

        run(cities, s -> hash31(s));
        run(cities, s -> hashShift(s));
    }
}
