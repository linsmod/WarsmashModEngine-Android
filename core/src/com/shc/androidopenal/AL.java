package com.shc.androidopenal;
import org.lwjgl.BufferUtils;

import java.nio.Buffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class AL implements ALConstants {
    static {
        try {
            System.loadLibrary("openal");
//            System.loadLibrary("openalsupport");
        } catch (Error e) {
            e.printStackTrace();
        }
    }

    /**
     * Bad value
     */
    public static final int AL_INVALID = 0xFFFFFFFF;

    /**
     * Disable value
     */
    public static final int AL_NONE = 0x0;

    /**
     * Boolean False
     */
    public static final int AL_FALSE = 0x0;

    /**
     * Boolean True
     */
    public static final int AL_TRUE = 0x1;

    /**
     * Indicate the type of SOURCE.
     * Sources can be spatialized
     */
    public static final int AL_SOURCE_TYPE = 0x1027;

    /**
     * Indicate source has absolute coordinates
     */
    public static final int AL_SOURCE_ABSOLUTE = 0x201;

    /**
     * Indicate Source has listener relative coordinates
     */
    public static final int AL_SOURCE_RELATIVE = 0x202;

    /**
     * Directional source, inner cone angle, in degrees
     * Range:		[0-360]
     * Default:	360
     */
    public static final int AL_CONE_INNER_ANGLE = 0x1001;

    /**
     * Directional source, outer cone angle, in degrees.
     * Range:		[0-360]
     * Default:	360
     */
    public static final int AL_CONE_OUTER_ANGLE = 0x1002;

    /**
     * Specify the pitch to be applied, either at source,
     * or on mixer results, at listener.
     * Range:	[0.5-2.0]
     * Default:	1.0
     */
    public static final int AL_PITCH = 0x1003;

    /**
     * Specify the current location in three dimensional space.
     * OpenAL, like OpenGL, uses a right handed coordinate system,
     * where in a frontal default view X (thumb) points right,
     * Y points up (index finger), and Z points towards the
     * viewer/camera (middle finger).
     * To switch from a left handed coordinate system, flip the
     * sign on the Z coordinate.
     * Listener position is always in the world coordinate system.
     */
    public static final int AL_POSITION = 0x1004;

    /**
     * Specify the current direction as forward vector.
     */
    public static final int AL_DIRECTION = 0x1005;

    /**
     * Specify the current velocity in three dimensional space.
     */
    public static final int AL_VELOCITY = 0x1006;

    /**
     * Indicate whether source has to loop infinite.
     * Type: ALboolean
     * Range:		[TRUE, FALSE]
     * Default:	FALSE
     */
    public static final int AL_LOOPING = 0x1007;

    /**
     * Indicate the buffer to provide sound samples.
     * Type: ALuint.
     * Range: any valid Buffer id.
     */
    public static final int AL_BUFFER = 0x1009;

    /**
     * Indicate the gain (volume amplification) applied.
     * Type:		 ALfloat.
     * Range:		]0.0-	]
     * A value of 1.0 means un-attenuated/unchanged.
     * Each division by 2 equals an attenuation of -6dB.
     * Each multiplicaton with 2 equals an amplification of +6dB.
     * A value of 0.0 is meaningless with respect to a logarithmic
     * scale; it is interpreted as zero volume - the channel
     * is effectively disabled.
     */
    public static final int AL_GAIN = 0x100A;

    /**
     * Indicate minimum source attenuation.
     * Type:		 ALfloat
     * Range:	[0.0 - 1.0]
     */
    public static final int AL_MIN_GAIN = 0x100D;

    /**
     * Indicate maximum source attenuation.
     * Type:	 ALfloat
     * Range:	[0.0 - 1.0]
     */
    public static final int AL_MAX_GAIN = 0x100E;

    /**
     * Specify the current orientation.
     * Type:	 ALfv6 (at/up)
     * Range:	N/A
     */
    public static final int AL_ORIENTATION = 0x100F,
            AL_REFERENCE_DISTANCE = 0x1020;

    /**
     * Indicate the rolloff factor for the source.
     * Type: ALfloat
     * Range:		[0.0 - ]
     * Default:	1.0
     */
    public static final int AL_ROLLOFF_FACTOR = 0x1021;

    /**
     * Indicate the gain (volume amplification) applied.
     * Type:		 ALfloat.
     * Range:		]0.0-	]
     * A value of 1.0 means un-attenuated/unchanged.
     * Each division by 2 equals an attenuation of -6dB.
     * Each multiplicaton with 2 equals an amplification of +6dB.
     * A value of 0.0 is meaningless with respect to a logarithmic
     * scale; it is interpreted as zero volume - the channel
     * is effectively disabled.
     */
    public static final int AL_CONE_OUTER_GAIN = 0x1022;

    /**
     * Specify the maximum distance.
     * Type:	 ALfloat
     * Range:	[0.0 - ]
     */
    public static final int AL_MAX_DISTANCE = 0x1023;

    /**
     * Specify the channel mask. (Creative)
     * Type:	 ALuint
     * Range:	[0 - 255]
     */
    public static final int AL_CHANNEL_MASK = 0x3000;

    /**
     * Source state information
     */
    public static final int AL_SOURCE_STATE = 0x1010,
            AL_INITIAL = 0x1011,
            AL_PLAYING = 0x1012,
            AL_PAUSED = 0x1013,
            AL_STOPPED = 0x1014;

    /**
     * Buffer Queue params
     */
    public static final int AL_BUFFERS_QUEUED = 0x1015,
            AL_BUFFERS_PROCESSED = 0x1016;

    /**
     * Sound buffers: format specifier.
     */
    public static final int AL_FORMAT_MONO8 = 0x1100,
            AL_FORMAT_MONO16 = 0x1101,
            AL_FORMAT_STEREO8 = 0x1102,
            AL_FORMAT_STEREO16 = 0x1103;

    /**
     * Ogg Vorbis format specifier.
     */
    public static final int AL_FORMAT_VORBIS_EXT = 0x10003;

    /**
     * Sound buffers: frequency, in units of Hertz [Hz].
     * This is the number of samples per second. Half of the
     * sample frequency marks the maximum significant
     * frequency component.
     */
    public static final int AL_FREQUENCY = 0x2001;

    /**
     * Sound buffers: The number of bits per sample for the
     * data contained in the buffer.
     */
    public static final int AL_BITS = 0x2002;

    /**
     * Sound buffers: The number of channels for the data
     * contained in the buffer.
     */
    public static final int AL_CHANNELS = 0x2003;

    /**
     * Sound buffers: Size in bytes of the buffer data.
     */
    public static final int AL_SIZE = 0x2004;

    /**
     * @deprecated This token is a relict of the early OpenAL days and is
     * no longer supported. Neither the OpenAL spec nor OpenAL Soft define
     * it.
     */
    @Deprecated
    public static final int AL_DATA = 0x2005;

    /**
     * Buffer state.
     * <p>
     * Not supported for public use (yet).
     */
    public static final int AL_UNUSED = 0x2010,
            AL_PENDING = 0x2011,
            AL_PROCESSED = 0x2012;

    /**
     * Errors: No Error.
     */
    public static final int AL_NO_ERROR = 0x0;

    /**
     * Illegal name passed as an argument to an AL call.
     */
    public static final int AL_INVALID_NAME = 0xA001;

    /**
     * Illegal enum passed as an argument to an AL call.
     */
    public static final int AL_INVALID_ENUM = 0xA002;

    /**
     * Illegal value passed as an argument to an AL call.
     * Applies to parameter values, but not to enumerations.
     */
    public static final int AL_INVALID_VALUE = 0xA003;

    /**
     * A function was called at inappropriate time,
     * or in an inappropriate way, causing an illegal state.
     * This can be an incompatible ALenum, object ID,
     * and/or function.
     */
    public static final int AL_INVALID_OPERATION = 0xA004;

    /**
     * A function could not be completed,
     * because there is not enough memory available.
     */
    public static final int AL_OUT_OF_MEMORY = 0xA005;

    /**
     * Context strings: Vendor
     */
    public static final int AL_VENDOR = 0xB001;

    /**
     * Context strings: Version
     */
    public static final int AL_VERSION = 0xB002;

    /**
     * Context strings: Renderer
     */
    public static final int AL_RENDERER = 0xB003;

    /**
     * Context strings: Extensions
     */
    public static final int AL_EXTENSIONS = 0xB004;

    /**
     * Doppler scale.	Default 1.0
     */
    public static final int AL_DOPPLER_FACTOR = 0xC000;

    /**
     * Doppler velocity.	Default 1.0
     */
    public static final int AL_DOPPLER_VELOCITY = 0xC001;

    /**
     * Distance model.	Default INVERSE_DISTANCE_CLAMPED
     */
    public static final int AL_DISTANCE_MODEL = 0xD000;

    /**
     * Distance model
     */
    public static final int AL_INVERSE_DISTANCE = 0xD001,
            AL_INVERSE_DISTANCE_CLAMPED = 0xD002;

    public static void alEnable(int capability) {
        ALJNI.alEnable(capability);
    }

    public static void alDisable(int capability) {
        ALJNI.alDisable(capability);
    }

    public static char alIsEnabled(int capability) {
        return ALJNI.alIsEnabled(capability);
    }

    public static String alGetString(int param) {
        return ALJNI.alGetString(param);
    }

    public static void alGetBooleanv(int param, String data) {
        ALJNI.alGetBooleanv(param, data);
    }

    public static void alGetIntegerv(int param, IntBuffer data) {
        ALJNI.alGetIntegerv(param, Util.getAddress(data, 0));
    }

    public static void alGetFloatv(int param, FloatBuffer data) {
        ALJNI.alGetFloatv(param, Util.getAddress(data, 0));
    }

    public static void alGetDoublev(int param, DoubleBuffer data) {
        ALJNI.alGetDoublev(param, Util.getAddress(data, 0));
    }

    public static char alGetBoolean(int param) {
        return ALJNI.alGetBoolean(param);
    }

    public static int alGetInteger(int param) {
        return ALJNI.alGetInteger(param);
    }

    public static float alGetFloat(int param) {
        return ALJNI.alGetFloat(param);
    }

    public static double alGetDouble(int param) {
        return ALJNI.alGetDouble(param);
    }

    public static int alGetError() {
        return ALJNI.alGetError();
    }

    public static char alIsExtensionPresent(String extname) {
        return ALJNI.alIsExtensionPresent(extname);
    }

    public static long alGetProcAddress(String fname) {
        return ALJNI.alGetProcAddress(fname);
    }

    public static int alGetEnumValue(String ename) {
        return ALJNI.alGetEnumValue(ename);
    }

    public static void alListenerf(int param, float value) {
        ALJNI.alListenerf(param, value);
    }

    public static void alListener3f(int param, float value1, float value2, float value3) {
        ALJNI.alListener3f(param, value1, value2, value3);
    }

    public static void alListenerfv(int param, FloatBuffer values) {
        ALJNI.alListenerfv(param, Util.getAddress(values, 0));
    }

    public static void alListeneri(int param, int value) {
        ALJNI.alListeneri(param, value);
    }

    public static void alListener3i(int param, int value1, int value2, int value3) {
        ALJNI.alListener3i(param, value1, value2, value3);
    }

    public static void alListeneriv(int param, IntBuffer values) {
        ALJNI.alListeneriv(param, Util.getAddress(values, 0));
    }

    public static void alGetListenerf(int param, FloatBuffer value) {
        ALJNI.alGetListenerf(param, Util.getAddress(value, 0));
    }

    public static void alGetListener3f(int param, FloatBuffer value1, FloatBuffer value2, FloatBuffer value3) {
        ALJNI.alGetListener3f(param, Util.getAddress(value1, 0), Util.getAddress(value2, 0), Util.getAddress(value3, 0));
    }

    public static void alGetListenerfv(int param, FloatBuffer values) {
        ALJNI.alGetListenerfv(param, Util.getAddress(values, 0));
    }

    public static void alGetListeneri(int param, IntBuffer value) {
        ALJNI.alGetListeneri(param, Util.getAddress(value, 0));
    }

    public static void alGetListener3i(int param, IntBuffer value1, IntBuffer value2, IntBuffer value3) {
        ALJNI.alGetListener3i(param, Util.getAddress(value1, 0), Util.getAddress(value2, 0), Util.getAddress(value3, 0));
    }

    public static void alGetListeneriv(int param, IntBuffer values) {
        ALJNI.alGetListeneriv(param, Util.getAddress(values, 0));
    }

    public static void alGenSources(int n, IntBuffer sources) {
        ALJNI.alGenSources(n, Util.getAddress(sources, 0));
    }

    public static int alGenSources() {
        IntBuffer id = org.lwjgl.BufferUtils.createIntBuffer(1);
        alGenSources(1, id);
        return id.get(0);
    }

    public static int alDeleteSources(int n) {
        IntBuffer buffer = BufferUtils.createIntBuffer(1);
        ALJNI.alDeleteSources(n, Util.getAddress(buffer, 0));
        return buffer.get(0);
    }

    public static void alDeleteSources(int n, IntBuffer sources) {
        ALJNI.alDeleteSources(n, Util.getAddress(sources, 0));
    }

    public static char alIsSource(long sid) {
        return ALJNI.alIsSource(sid);
    }

    public static void alSourcef(long sid, int param, float value) {
        ALJNI.alSourcef(sid, param, value);
    }

    public static void alSource3f(long sid, int param, float value1, float value2, float value3) {
        ALJNI.alSource3f(sid, param, value1, value2, value3);
    }

    public static void alSourcefv(long sid, int param, FloatBuffer values) {
        ALJNI.alSourcefv(sid, param, Util.getAddress(values, 0));
    }

    public static void alSourcei(long sid, int param, int value) {
        ALJNI.alSourcei(sid, param, value);
    }

    public static void alSource3i(long sid, int param, int value1, int value2, int value3) {
        ALJNI.alSource3i(sid, param, value1, value2, value3);
    }

    public static void alSourceiv(long sid, int param, IntBuffer values) {
        ALJNI.alSourceiv(sid, param, Util.getAddress(values, 0));
    }

    public static void alGetSourcef(long sid, int param, FloatBuffer value) {
        ALJNI.alGetSourcef(sid, param, Util.getAddress(value, 0));
    }

    public static int alGetSourcei(long sid, int param) {
        IntBuffer value = BufferUtils.createIntBuffer(1);
        ALJNI.alGetSourcei(sid, param, Util.getAddress(value, 0));
        return value.get(0);
    }

    public static float alGetSourcef(long sid, int param) {
        FloatBuffer value = BufferUtils.createFloatBuffer(1);
        ALJNI.alGetSourcef(sid, param, Util.getAddress(value, 0));
        return value.get(0);
    }

    public static void alGetSource3f(long sid, int param, FloatBuffer value1, FloatBuffer value2, FloatBuffer value3) {
        ALJNI.alGetSource3f(sid, param, Util.getAddress(value1, 0), Util.getAddress(value2, 0), Util.getAddress(value3, 0));
    }

    public static void alGetSourcefv(long sid, int param, FloatBuffer values) {
        ALJNI.alGetSourcefv(sid, param, Util.getAddress(values, 0));
    }

    public static void alGetSourcei(long sid, int param, IntBuffer value) {
        ALJNI.alGetSourcei(sid, param, Util.getAddress(value, 0));
    }

    public static void alGetSource3i(long sid, int param, IntBuffer value1, IntBuffer value2, IntBuffer value3) {
        ALJNI.alGetSource3i(sid, param, Util.getAddress(value1, 0), Util.getAddress(value1, 0), Util.getAddress(value1, 0));
    }

    public static void alGetSourceiv(long sid, int param, IntBuffer values) {
        ALJNI.alGetSourceiv(sid, param, Util.getAddress(values, 0));
    }

    public static void alSourcePlayv(int ns, IntBuffer sids) {
        ALJNI.alSourcePlayv(ns, Util.getAddress(sids, 0));
    }

    public static void alSourceStopv(int ns, IntBuffer sids) {
        ALJNI.alSourceStopv(ns, Util.getAddress(sids, 0));
    }

    public static void alSourceRewindv(int ns, IntBuffer sids) {
        ALJNI.alSourceRewindv(ns, Util.getAddress(sids, 0));
    }

    public static void alSourcePausev(int ns, IntBuffer sids) {
        ALJNI.alSourcePausev(ns, Util.getAddress(sids, 0));
    }

    public static void alSourcePlay(long sid) {
        ALJNI.alSourcePlay(sid);
    }

    public static void alSourceStop(long sid) {
        ALJNI.alSourceStop(sid);
    }

    public static void alSourceRewind(long sid) {
        ALJNI.alSourceRewind(sid);
    }

    public static void alSourcePause(long sid) {
        ALJNI.alSourcePause(sid);
    }

    public static void alSourceQueueBuffers(int sid, int numEntries, IntBuffer bids) {
        ALJNI.alSourceQueueBuffers(sid, numEntries, Util.getAddress(bids, 0));
    }

    public static void alSourceQueueBuffers(int sid, int numEntries, int bufferId) {
        ALJNI.alSourceQueueBuffers(sid, numEntries, bufferId);
    }

    public static void alSourceQueueBuffers(int sid, int bufferId) {
        ALJNI.alSourceQueueBuffers(sid, 1, bufferId);
    }

    public static void alSourceUnqueueBuffers(long sid, int numEntries, IntBuffer bids) {
        ALJNI.alSourceUnqueueBuffers(sid, numEntries, Util.getAddress(bids, 0));
    }

    public static int alSourceUnqueueBuffers(long sid) {
        IntBuffer buffer = BufferUtils.createIntBuffer(1);
        ALJNI.alSourceUnqueueBuffers(sid, 1, Util.getAddress(buffer, 0));
        return buffer.get(0);
    }

    public static void alGenBuffers(int n, IntBuffer buffers) {
        ALJNI.alGenBuffers(n, Util.getAddress(buffers, 0));
    }

    public static void alGenBuffers(IntBuffer buffers) {
        ALJNI.alGenBuffers(buffers.remaining(), Util.getAddress(buffers, 0));
    }

    public static int alGenBuffers() {
        IntBuffer buffers = BufferUtils.createIntBuffer(1);
        ALJNI.alGenBuffers(buffers.remaining(), Util.getAddress(buffers, 0));
        if (buffers.get(0) == 0) {
            throw new RuntimeException("may not initialize ALC context.");
        }
        return buffers.get(0);
    }

    public static void alDeleteBuffers(int n, IntBuffer buffers) {
        ALJNI.alDeleteBuffers(n, Util.getAddress(buffers, 0));
    }

    /**
     * alDeleteBuffers by bufferId
     *
     * @param n bufferId
     */
    public static void alDeleteBuffers(int n) {
        IntBuffer buffers = IntBuffer.allocate(1);
        buffers.put(0, n);
        //n is index to the buffer
        ALJNI.alDeleteBuffers(0, Util.getAddress(buffers, 0));
    }

    public static char alIsBuffer(long bid) {
        return ALJNI.alIsBuffer(bid);
    }

    public static void alBufferData(long bid, int format, Buffer data, int size, int freq) {
        ALJNI.alBufferData(bid, format, Util.getAddress(data, 0), size, freq);
    }

    public static void alBufferData(long bid, int format, Buffer data, int freq) {
        ALJNI.alBufferData(bid, format, Util.getAddress(data, 0), 1, freq);
    }

    public static void alBufferf(long bid, int param, float value) {
        ALJNI.alBufferf(bid, param, value);
    }

    public static void alBuffer3f(long bid, int param, float value1, float value2, float value3) {
        ALJNI.alBuffer3f(bid, param, value1, value2, value3);
    }

    public static void alBufferfv(long bid, int param, FloatBuffer values) {
        ALJNI.alBufferfv(bid, param, Util.getAddress(values, 0));
    }

    public static void alBufferi(long bid, int param, int value) {
        ALJNI.alBufferi(bid, param, value);
    }

    public static void alBuffer3i(long bid, int param, int value1, int value2, int value3) {
        ALJNI.alBuffer3i(bid, param, value1, value2, value3);
    }

    public static void alBufferiv(long bid, int param, IntBuffer values) {
        ALJNI.alBufferiv(bid, param, Util.getAddress(values, 0));
    }

    public static void alGetBufferf(long bid, int param, FloatBuffer value) {
        ALJNI.alGetBufferf(bid, param, Util.getAddress(value, 0));
    }

    public static void alGetBuffer3f(long bid, int param, FloatBuffer value1, FloatBuffer value2, FloatBuffer value3) {
        ALJNI.alGetBuffer3f(bid, param, Util.getAddress(value1, 0), Util.getAddress(value2, 0), Util.getAddress(value3, 0));
    }

    public static void alGetBufferfv(long bid, int param, FloatBuffer values) {
        ALJNI.alGetBufferfv(bid, param, Util.getAddress(values, 0));
    }

    public static void alGetBufferi(long bid, int param, IntBuffer value) {
        ALJNI.alGetBufferi(bid, param, Util.getAddress(value, 0));
    }

    public static void alGetBuffer3i(long bid, int param, IntBuffer value1, IntBuffer value2, IntBuffer value3) {
        ALJNI.alGetBuffer3i(bid, param, Util.getAddress(value1, 0), Util.getAddress(value2, 0), Util.getAddress(value3, 0));
    }

    public static void alGetBufferiv(long bid, int param, IntBuffer values) {
        ALJNI.alGetBufferiv(bid, param, Util.getAddress(values, 0));
    }

    public static void alDopplerFactor(float value) {
        ALJNI.alDopplerFactor(value);
    }

    public static void alDopplerVelocity(float value) {
        ALJNI.alDopplerVelocity(value);
    }

    public static void alSpeedOfSound(float value) {
        ALJNI.alSpeedOfSound(value);
    }

    public static void alDistanceModel(int distanceModel) {
        ALJNI.alDistanceModel(distanceModel);
    }
}
