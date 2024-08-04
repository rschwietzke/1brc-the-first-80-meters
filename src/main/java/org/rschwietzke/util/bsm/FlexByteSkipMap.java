package org.rschwietzke.util.bsm;

import java.util.Arrays;

public class FlexByteSkipMap<T> implements IByteSkipMap<T>
{
    @SuppressWarnings("unchecked")
    FlexByteSkipMap<T>[] next = new FlexByteSkipMap[0];
    byte[] bytes = new byte[0];

    // for root, this is guaranteed empty
    public T value;

    public FlexByteSkipMap<T> getNext(final byte key)
    {
        int i = 0;
        for (; i < bytes.length; i++)
        {
            var b = bytes[i];
            if (b == key)
            {
                return next[i];
            }
        }

        return grow(key, i);
    }

    public T getValue()
    {
        return value;
    }

    public T setValue(T value)
    {
        return this.value = value;
    }

    private FlexByteSkipMap<T> grow(final byte key, final int pos)
    {
        // not found, enlarge, append;
        var l = bytes.length + 1;

        next = Arrays.copyOf(next, l);
        bytes = Arrays.copyOf(bytes, l);

        bytes[pos] = key;
        next[pos] = new FlexByteSkipMap<T>();

        // sort
        for (int i = 0; i < next.length; i++)
        {
            for (int j = i + 1; j < next.length; j++)
            {
                if (bytes[i] > bytes[j])
                {
                    var ob = bytes[i];
                    bytes[i] = bytes[j];
                    bytes[j] = ob;

                    var on = next[i];
                    next[i] = next[j];
                    next[j] = on;
                }
            }
        }

        return next[pos];
    }
}
