package com.shc.androidopenal;

public class STB_Vorbis
{
    private transient long pointer;

    protected STB_Vorbis(long cPtr)
    {
        pointer = cPtr;
    }

    public static long getPointer(STB_Vorbis obj)
    {
        return (obj == null) ? 0 : obj.pointer;
    }
}

