package com.shc.androidopenal;

public class Pointer
{
    private transient long pointer;

    protected Pointer(long cPtr)
    {
        pointer = cPtr;
    }

    public static long getPointer(Pointer obj)
    {
        return (obj == null) ? 0 : obj.pointer;
    }
}

