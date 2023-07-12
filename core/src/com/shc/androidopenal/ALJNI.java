package com.shc.androidopenal;

public class ALJNI
{
    public static native int AL_INVALID_get();

    public static native int AL_NONE_get();

    public static native int AL_FALSE_get();

    public static native int AL_TRUE_get();

    public static native int AL_SOURCE_RELATIVE_get();

    public static native int AL_CONE_INNER_ANGLE_get();

    public static native int AL_CONE_OUTER_ANGLE_get();

    public static native int AL_PITCH_get();

    public static native int AL_POSITION_get();

    public static native int AL_DIRECTION_get();

    public static native int AL_VELOCITY_get();

    public static native int AL_LOOPING_get();

    public static native int AL_BUFFER_get();

    public static native int AL_GAIN_get();

    public static native int AL_MIN_GAIN_get();

    public static native int AL_MAX_GAIN_get();

    public static native int AL_ORIENTATION_get();

    public static native int AL_SOURCE_STATE_get();

    public static native int AL_INITIAL_get();

    public static native int AL_PLAYING_get();

    public static native int AL_PAUSED_get();

    public static native int AL_STOPPED_get();

    public static native int AL_BUFFERS_QUEUED_get();

    public static native int AL_BUFFERS_PROCESSED_get();

    public static native int AL_SEC_OFFSET_get();

    public static native int AL_SAMPLE_OFFSET_get();

    public static native int AL_BYTE_OFFSET_get();

    public static native int AL_SOURCE_TYPE_get();

    public static native int AL_STATIC_get();

    public static native int AL_STREAMING_get();

    public static native int AL_UNDETERMINED_get();

    public static native int AL_FORMAT_MONO8_get();

    public static native int AL_FORMAT_MONO16_get();

    public static native int AL_FORMAT_STEREO8_get();

    public static native int AL_FORMAT_STEREO16_get();

    public static native int AL_REFERENCE_DISTANCE_get();

    public static native int AL_ROLLOFF_FACTOR_get();

    public static native int AL_CONE_OUTER_GAIN_get();

    public static native int AL_MAX_DISTANCE_get();

    public static native int AL_FREQUENCY_get();

    public static native int AL_BITS_get();

    public static native int AL_CHANNELS_get();

    public static native int AL_SIZE_get();

    public static native int AL_UNUSED_get();

    public static native int AL_PENDING_get();

    public static native int AL_PROCESSED_get();

    public static native int AL_NO_ERROR_get();

    public static native int AL_INVALID_NAME_get();

    public static native int AL_INVALID_ENUM_get();

    public static native int AL_INVALID_VALUE_get();

    public static native int AL_INVALID_OPERATION_get();

    public static native int AL_OUT_OF_MEMORY_get();

    public static native int AL_VENDOR_get();

    public static native int AL_VERSION_get();

    public static native int AL_RENDERER_get();

    public static native int AL_EXTENSIONS_get();

    public static native int AL_DOPPLER_FACTOR_get();

    public static native int AL_DOPPLER_VELOCITY_get();

    public static native int AL_SPEED_OF_SOUND_get();

    public static native int AL_DISTANCE_MODEL_get();

    public static native int AL_INVERSE_DISTANCE_get();

    public static native int AL_INVERSE_DISTANCE_CLAMPED_get();

    public static native int AL_LINEAR_DISTANCE_get();

    public static native int AL_LINEAR_DISTANCE_CLAMPED_get();

    public static native int AL_EXPONENT_DISTANCE_get();

    public static native int AL_EXPONENT_DISTANCE_CLAMPED_get();

    public static native void alEnable(int jarg1);

    public static native void alDisable(int jarg1);

    public static native char alIsEnabled(int jarg1);

    public static native String alGetString(int jarg1);

    public static native void alGetBooleanv(int jarg1, String jarg2);

    public static native void alGetIntegerv(int jarg1, long jarg2);

    public static native void alGetFloatv(int jarg1, long jarg2);

    public static native void alGetDoublev(int jarg1, long jarg2);

    public static native char alGetBoolean(int jarg1);

    public static native int alGetInteger(int jarg1);

    public static native float alGetFloat(int jarg1);

    public static native double alGetDouble(int jarg1);

    public static native int alGetError();

    public static native char alIsExtensionPresent(String jarg1);

    public static native long alGetProcAddress(String jarg1);

    public static native int alGetEnumValue(String jarg1);

    public static native void alListenerf(int jarg1, float jarg2);

    public static native void alListener3f(int jarg1, float jarg2, float jarg3, float jarg4);

    public static native void alListenerfv(int jarg1, long jarg2);

    public static native void alListeneri(int jarg1, int jarg2);

    public static native void alListener3i(int jarg1, int jarg2, int jarg3, int jarg4);

    public static native void alListeneriv(int jarg1, long jarg2);

    public static native void alGetListenerf(int jarg1, long jarg2);

    public static native void alGetListener3f(int jarg1, long jarg2, long jarg3, long jarg4);

    public static native void alGetListenerfv(int jarg1, long jarg2);

    public static native void alGetListeneri(int jarg1, long jarg2);

    public static native void alGetListener3i(int jarg1, long jarg2, long jarg3, long jarg4);

    public static native void alGetListeneriv(int jarg1, long jarg2);

    public static native void alGenSources(int jarg1, long jarg2);

    public static native void alDeleteSources(int jarg1, long jarg2);

    public static native char alIsSource(long jarg1);

    public static native void alSourcef(long jarg1, int jarg2, float jarg3);

    public static native void alSource3f(long jarg1, int jarg2, float jarg3, float jarg4, float jarg5);

    public static native void alSourcefv(long jarg1, int jarg2, long jarg3);

    public static native void alSourcei(long jarg1, int jarg2, int jarg3);

    public static native void alSource3i(long jarg1, int jarg2, int jarg3, int jarg4, int jarg5);

    public static native void alSourceiv(long jarg1, int jarg2, long jarg3);

    public static native void alGetSourcef(long jarg1, int jarg2, long jarg3);

    public static native void alGetSource3f(long jarg1, int jarg2, long jarg3, long jarg4, long jarg5);

    public static native void alGetSourcefv(long jarg1, int jarg2, long jarg3);

    public static native void alGetSourcei(long jarg1, int jarg2, long jarg3);

    public static native void alGetSource3i(long jarg1, int jarg2, long jarg3, long jarg4, long jarg5);

    public static native void alGetSourceiv(long jarg1, int jarg2, long jarg3);

    public static native void alSourcePlayv(int jarg1, long jarg2);

    public static native void alSourceStopv(int jarg1, long jarg2);

    public static native void alSourceRewindv(int jarg1, long jarg2);

    public static native void alSourcePausev(int jarg1, long jarg2);

    public static native void alSourcePlay(long jarg1);

    public static native void alSourceStop(long jarg1);

    public static native void alSourceRewind(long jarg1);

    public static native void alSourcePause(long jarg1);

    public static native void alSourceQueueBuffers(long jarg1, int jarg2, long jarg3);

    public static native void alSourceUnqueueBuffers(long jarg1, int jarg2, long jarg3);

    public static native void alGenBuffers(int jarg1, long jarg2);

    public static native void alDeleteBuffers(int jarg1, long jarg2);

    public static native char alIsBuffer(long jarg1);

    public static native void alBufferData(long jarg1, int jarg2, long jarg3, int jarg4, int jarg5);

    public static native void alBufferf(long jarg1, int jarg2, float jarg3);

    public static native void alBuffer3f(long jarg1, int jarg2, float jarg3, float jarg4, float jarg5);

    public static native void alBufferfv(long jarg1, int jarg2, long jarg3);

    public static native void alBufferi(long jarg1, int jarg2, int jarg3);

    public static native void alBuffer3i(long jarg1, int jarg2, int jarg3, int jarg4, int jarg5);

    public static native void alBufferiv(long jarg1, int jarg2, long jarg3);

    public static native void alGetBufferf(long jarg1, int jarg2, long jarg3);

    public static native void alGetBuffer3f(long jarg1, int jarg2, long jarg3, long jarg4, long jarg5);

    public static native void alGetBufferfv(long jarg1, int jarg2, long jarg3);

    public static native void alGetBufferi(long jarg1, int jarg2, long jarg3);

    public static native void alGetBuffer3i(long jarg1, int jarg2, long jarg3, long jarg4, long jarg5);

    public static native void alGetBufferiv(long jarg1, int jarg2, long jarg3);

    public static native void alDopplerFactor(float jarg1);

    public static native void alDopplerVelocity(float jarg1);

    public static native void alSpeedOfSound(float jarg1);

    public static native void alDistanceModel(int jarg1);

    static
    {
        Util.loadLibrary();
    }
}
