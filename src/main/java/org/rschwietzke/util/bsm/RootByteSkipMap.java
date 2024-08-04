package org.rschwietzke.util.bsm;

public class RootByteSkipMap<T> implements IByteSkipMap<T>
{
    @SuppressWarnings("unchecked")
    IByteSkipMap<T>[] next = new IByteSkipMap[256];

    // for root, this is guaranteed empty
    public T value;

    public RootByteSkipMap()
    {
        for (int i = 0; i < next.length; i++)
        {
            this.next[i] = new FlexByteSkipMap<T>();
        }
    }

    public IByteSkipMap<T> getNext(final byte key)
    {
        return next[key + 128];
    }

    public T getValue()
    {
        return value;
    }

    public T setValue(T value)
    {
        return this.value = value;
    }
}
