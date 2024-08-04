package org.rschwietzke.util.bsm;

public interface IByteSkipMap<T>
{
    public IByteSkipMap<T> getNext(final byte key);
    public T getValue();
    public T setValue(T value);
}

