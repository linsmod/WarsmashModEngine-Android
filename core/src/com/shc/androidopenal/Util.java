package com.shc.androidopenal;

import java.nio.Buffer;

class Util
{
    private static boolean loaded = false;

    static void loadLibrary()
    {
        if (loaded)
            return;

        try
        {
            System.loadLibrary("openal");
            loaded = true;
        }
        catch (UnsatisfiedLinkError e)
        {
            System.err.println("Native code library failed to load.");
            e.printStackTrace();
            System.exit(1);
        }
    }

    static native long getAddress(Buffer buffer, int offset);

    static long getAddress(Buffer buffer)
    {
        return buffer == null ? 0 : getAddress(buffer, 0);
    }
}
