package com.shc.androidopenal;

public class ALCcontext
{
    private transient long pointer;

    protected ALCcontext(long cPtr)
    {
        pointer = cPtr;
    }

    protected ALCcontext()
    {
        pointer = 0;
    }

    public static long getPointer(ALCcontext obj)
    {
        return (obj == null) ? 0 : obj.pointer;
    }
}

