package com.shc.androidopenal;

public class ALCJNI
{
    public static native int ALC_INVALID_get();

    public static native int ALC_VERSION_0_1_get();

    public static native int ALC_FALSE_get();

    public static native int ALC_TRUE_get();

    public static native int ALC_FREQUENCY_get();

    public static native int ALC_REFRESH_get();

    public static native int ALC_SYNC_get();

    public static native int ALC_MONO_SOURCES_get();

    public static native int ALC_STEREO_SOURCES_get();

    public static native int ALC_NO_ERROR_get();

    public static native int ALC_INVALID_DEVICE_get();

    public static native int ALC_INVALID_CONTEXT_get();

    public static native int ALC_INVALID_ENUM_get();

    public static native int ALC_INVALID_VALUE_get();

    public static native int ALC_OUT_OF_MEMORY_get();

    public static native int ALC_DEFAULT_DEVICE_SPECIFIER_get();

    public static native int ALC_DEVICE_SPECIFIER_get();

    public static native int ALC_EXTENSIONS_get();

    public static native int ALC_MAJOR_VERSION_get();

    public static native int ALC_MINOR_VERSION_get();

    public static native int ALC_ATTRIBUTES_SIZE_get();

    public static native int ALC_ALL_ATTRIBUTES_get();

    public static native int ALC_CAPTURE_DEVICE_SPECIFIER_get();

    public static native int ALC_CAPTURE_DEFAULT_DEVICE_SPECIFIER_get();

    public static native int ALC_CAPTURE_SAMPLES_get();

    public static native long alcCreateContext(long jarg1, long jarg2);

    public static native char alcMakeContextCurrent(long jarg1);

    public static native void alcProcessContext(long jarg1);

    public static native void alcSuspendContext(long jarg1);

    public static native void alcDestroyContext(long jarg1);

    public static native long alcGetCurrentContext();

    public static native long alcGetContextsDevice(long jarg1);

    public static native long alcOpenDevice(String jarg1);

    public static native char alcCloseDevice(long jarg1);

    public static native int alcGetError(long jarg1);

    public static native char alcIsExtensionPresent(long jarg1, String jarg2);

    public static native long alcGetProcAddress(long jarg1, String jarg2);

    public static native int alcGetEnumValue(long jarg1, String jarg2);

    public static native String alcGetString(long jarg1, int jarg2);

    public static native void alcGetIntegerv(long jarg1, int jarg2, int jarg3, long jarg4);

    public static native long alcCaptureOpenDevice(String jarg1, long jarg2, int jarg3, int jarg4);

    public static native char alcCaptureCloseDevice(long jarg1);

    public static native void alcCaptureStart(long jarg1);

    public static native void alcCaptureStop(long jarg1);

    public static native void alcCaptureSamples(long jarg1, long jarg2, int jarg3);

    static
    {
        Util.loadLibrary();
    }
}
