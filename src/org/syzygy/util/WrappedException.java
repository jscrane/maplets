package org.syzygy.util;

public final class WrappedException extends Exception
{
    public WrappedException(Exception wrapped, String message)
    {
        super(message);
        this.wrapped = wrapped;
    }

    public Exception getWrapped()
    {
        return wrapped;
    }

    private final Exception wrapped;
}