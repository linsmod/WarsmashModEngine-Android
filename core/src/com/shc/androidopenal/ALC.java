package com.shc.androidopenal;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class ALC implements ALCConstants
{
    public static ALCcontext alcCreateContext(ALCdevice device, IntBuffer attrlist)
    {
        long cPtr = ALCJNI.alcCreateContext(ALCdevice.getPointer(device), Util.getAddress(attrlist));
        return (cPtr == 0) ? null : new ALCcontext(cPtr);
    }

    public static char alcMakeContextCurrent(ALCcontext context)
    {
        return ALCJNI.alcMakeContextCurrent(ALCcontext.getPointer(context));
    }

    public static void alcProcessContext(ALCcontext context)
    {
        ALCJNI.alcProcessContext(ALCcontext.getPointer(context));
    }

    public static void alcSuspendContext(ALCcontext context)
    {
        ALCJNI.alcSuspendContext(ALCcontext.getPointer(context));
    }

    public static void alcDestroyContext(ALCcontext context)
    {
        ALCJNI.alcDestroyContext(ALCcontext.getPointer(context));
    }

    public static ALCcontext alcGetCurrentContext()
    {
        long cPtr = ALCJNI.alcGetCurrentContext();
        return (cPtr == 0) ? null : new ALCcontext(cPtr);
    }

    public static ALCdevice alcGetContextsDevice(ALCcontext context)
    {
        long cPtr = ALCJNI.alcGetContextsDevice(ALCcontext.getPointer(context));
        return (cPtr == 0) ? null : new ALCdevice(cPtr);
    }

    public static ALCdevice alcOpenDevice()
    {
        return alcOpenDevice(null);
    }

    public static ALCdevice alcOpenDevice(String devicename)
    {
        long cPtr = ALCJNI.alcOpenDevice(devicename);
        return (cPtr == 0) ? null : new ALCdevice(cPtr);
    }

    public static char alcCloseDevice(ALCdevice device)
    {
        return ALCJNI.alcCloseDevice(ALCdevice.getPointer(device));
    }

    public static int alcGetError(ALCdevice device)
    {
        return ALCJNI.alcGetError(ALCdevice.getPointer(device));
    }

    public static char alcIsExtensionPresent(ALCdevice device, String extname)
    {
        return ALCJNI.alcIsExtensionPresent(ALCdevice.getPointer(device), extname);
    }

    public static Pointer alcGetProcAddress(ALCdevice device, String funcname)
    {
        long cPtr = ALCJNI.alcGetProcAddress(ALCdevice.getPointer(device), funcname);
        return (cPtr == 0) ? null : new Pointer(cPtr);
    }

    public static int alcGetEnumValue(ALCdevice device, String enumname)
    {
        return ALCJNI.alcGetEnumValue(ALCdevice.getPointer(device), enumname);
    }

    public static String alcGetString(ALCdevice device, int param)
    {
        return ALCJNI.alcGetString(ALCdevice.getPointer(device), param);
    }

    public static void alcGetIntegerv(ALCdevice device, int param, int size, IntBuffer data)
    {
        ALCJNI.alcGetIntegerv(ALCdevice.getPointer(device), param, size, Util.getAddress(data));
    }

    public static ALCdevice alcCaptureOpenDevice(String devicename, long frequency, int format, int buffersize)
    {
        long cPtr = ALCJNI.alcCaptureOpenDevice(devicename, frequency, format, buffersize);
        return (cPtr == 0) ? null : new ALCdevice(cPtr);
    }

    public static char alcCaptureCloseDevice(ALCdevice device)
    {
        return ALCJNI.alcCaptureCloseDevice(ALCdevice.getPointer(device));
    }

    public static void alcCaptureStart(ALCdevice device)
    {
        ALCJNI.alcCaptureStart(ALCdevice.getPointer(device));
    }

    public static void alcCaptureStop(ALCdevice device)
    {
        ALCJNI.alcCaptureStop(ALCdevice.getPointer(device));
    }

    public static void alcCaptureSamples(ALCdevice device, ByteBuffer buffer, int samples)
    {
        ALCJNI.alcCaptureSamples(ALCdevice.getPointer(device), Util.getAddress(buffer), samples);
    }
}
