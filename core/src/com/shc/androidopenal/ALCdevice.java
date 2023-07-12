package com.shc.androidopenal;

public class ALCdevice
{
    private transient long pointer;

    protected ALCdevice(long cPtr)
    {
        pointer = cPtr;
    }

    protected ALCdevice()
    {
        pointer = 0;
    }

    public static long getPointer(ALCdevice obj)
    {
        return (obj == null) ? 0 : obj.pointer;
    }
}

