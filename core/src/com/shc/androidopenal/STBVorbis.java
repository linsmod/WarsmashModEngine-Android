package com.shc.androidopenal;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class STBVorbis
{
    public static STB_VorbisInfo stb_vorbis_get_info(STB_Vorbis f)
    {
        return new STB_VorbisInfo(STBVorbisJNI.stb_vorbis_get_info(STB_Vorbis.getPointer(f)), true);
    }

    public static int stb_vorbis_get_error(STB_Vorbis f)
    {
        return STBVorbisJNI.stb_vorbis_get_error(STB_Vorbis.getPointer(f));
    }

    public static void stb_vorbis_close(STB_Vorbis f)
    {
        STBVorbisJNI.stb_vorbis_close(STB_Vorbis.getPointer(f));
    }

    public static int stb_vorbis_get_sample_offset(STB_Vorbis f)
    {
        return STBVorbisJNI.stb_vorbis_get_sample_offset(STB_Vorbis.getPointer(f));
    }

    public static long stb_vorbis_get_file_offset(STB_Vorbis f)
    {
        return STBVorbisJNI.stb_vorbis_get_file_offset(STB_Vorbis.getPointer(f));
    }

    public static STB_Vorbis stb_vorbis_open_pushdata(ByteBuffer datablock, int datablock_length_in_bytes, IntBuffer datablock_memory_consumed_in_bytes, IntBuffer error, STB_VorbisAlloc alloc_buffer)
    {
        long cPtr = STBVorbisJNI.stb_vorbis_open_pushdata(Util.getAddress(datablock), datablock_length_in_bytes, Util.getAddress(datablock_memory_consumed_in_bytes), Util.getAddress(error), STB_VorbisAlloc.getPointer(alloc_buffer), alloc_buffer);
        return (cPtr == 0) ? null : new STB_Vorbis(cPtr);
    }

    public static int stb_vorbis_decode_frame_pushdata(STB_Vorbis f, ByteBuffer datablock, int datablock_length_in_bytes, IntBuffer channels, FloatBuffer output, IntBuffer samples)
    {
        return STBVorbisJNI.stb_vorbis_decode_frame_pushdata(STB_Vorbis.getPointer(f), Util.getAddress(datablock), datablock_length_in_bytes, Util.getAddress(channels), Util.getAddress(output), Util.getAddress(samples));
    }

    public static void stb_vorbis_flush_pushdata(STB_Vorbis f)
    {
        STBVorbisJNI.stb_vorbis_flush_pushdata(STB_Vorbis.getPointer(f));
    }

    public static int stb_vorbis_decode_filename(String filename, IntBuffer channels, IntBuffer sample_rate, ShortBuffer output)
    {
        return STBVorbisJNI.stb_vorbis_decode_filename(filename, Util.getAddress(channels), Util.getAddress(sample_rate, 0), Util.getAddress(output));
    }

    public static int stb_vorbis_decode_memory(ByteBuffer mem, int len, IntBuffer channels, IntBuffer sample_rate, ShortBuffer output)
    {
        return STBVorbisJNI.stb_vorbis_decode_memory(Util.getAddress(mem), len, Util.getAddress(channels), Util.getAddress(sample_rate, 0), Util.getAddress(output));
    }

    public static STB_Vorbis stb_vorbis_open_memory(ByteBuffer data, int len, IntBuffer error, STB_VorbisAlloc alloc_buffer)
    {
        long cPtr = STBVorbisJNI.stb_vorbis_open_memory(Util.getAddress(data), len, Util.getAddress(error), STB_VorbisAlloc.getPointer(alloc_buffer), alloc_buffer);
        return (cPtr == 0) ? null : new STB_Vorbis(cPtr);
    }

    public static STB_Vorbis stb_vorbis_open_filename(String filename, IntBuffer error, STB_VorbisAlloc alloc_buffer)
    {
        long cPtr = STBVorbisJNI.stb_vorbis_open_filename(filename, Util.getAddress(error), STB_VorbisAlloc.getPointer(alloc_buffer), alloc_buffer);
        return (cPtr == 0) ? null : new STB_Vorbis(cPtr);
    }

    public static STB_Vorbis stb_vorbis_open_file(long f, int close_handle_on_close, IntBuffer error, STB_VorbisAlloc alloc_buffer)
    {
        long cPtr = STBVorbisJNI.stb_vorbis_open_file(f, close_handle_on_close, Util.getAddress(error), STB_VorbisAlloc.getPointer(alloc_buffer), alloc_buffer);
        return (cPtr == 0) ? null : new STB_Vorbis(cPtr);
    }

    public static STB_Vorbis stb_vorbis_open_file_section(long f, int close_handle_on_close, IntBuffer error, STB_VorbisAlloc alloc_buffer, long len)
    {
        long cPtr = STBVorbisJNI.stb_vorbis_open_file_section(f, close_handle_on_close, Util.getAddress(error), STB_VorbisAlloc.getPointer(alloc_buffer), alloc_buffer, len);
        return (cPtr == 0) ? null : new STB_Vorbis(cPtr);
    }

    public static int stb_vorbis_seek_frame(STB_Vorbis f, long sample_number)
    {
        return STBVorbisJNI.stb_vorbis_seek_frame(STB_Vorbis.getPointer(f), sample_number);
    }

    public static int stb_vorbis_seek(STB_Vorbis f, long sample_number)
    {
        return STBVorbisJNI.stb_vorbis_seek(STB_Vorbis.getPointer(f), sample_number);
    }

    public static void stb_vorbis_seek_start(STB_Vorbis f)
    {
        STBVorbisJNI.stb_vorbis_seek_start(STB_Vorbis.getPointer(f));
    }

    public static long stb_vorbis_stream_length_in_samples(STB_Vorbis f)
    {
        return STBVorbisJNI.stb_vorbis_stream_length_in_samples(STB_Vorbis.getPointer(f));
    }

    public static float stb_vorbis_stream_length_in_seconds(STB_Vorbis f)
    {
        return STBVorbisJNI.stb_vorbis_stream_length_in_seconds(STB_Vorbis.getPointer(f));
    }

    public static int stb_vorbis_get_frame_float(STB_Vorbis f, IntBuffer channels, FloatBuffer output)
    {
        return STBVorbisJNI.stb_vorbis_get_frame_float(STB_Vorbis.getPointer(f), Util.getAddress(channels), Util.getAddress(output));
    }

    public static int stb_vorbis_get_frame_short_interleaved(STB_Vorbis f, int num_c, ShortBuffer buffer, int num_shorts)
    {
        return STBVorbisJNI.stb_vorbis_get_frame_short_interleaved(STB_Vorbis.getPointer(f), num_c, Util.getAddress(buffer), num_shorts);
    }

    public static int stb_vorbis_get_frame_short(STB_Vorbis f, int num_c, ShortBuffer buffer, int num_samples)
    {
        return STBVorbisJNI.stb_vorbis_get_frame_short(STB_Vorbis.getPointer(f), num_c, Util.getAddress(buffer, 0), num_samples);
    }

    public static int stb_vorbis_get_samples_float_interleaved(STB_Vorbis f, int channels, FloatBuffer buffer, int num_floats)
    {
        return STBVorbisJNI.stb_vorbis_get_samples_float_interleaved(STB_Vorbis.getPointer(f), channels, Util.getAddress(buffer), num_floats);
    }

    public static int stb_vorbis_get_samples_float(STB_Vorbis f, int channels, FloatBuffer buffer, int num_samples)
    {
        return STBVorbisJNI.stb_vorbis_get_samples_float(STB_Vorbis.getPointer(f), channels, Util.getAddress(buffer), num_samples);
    }

    public static int stb_vorbis_get_samples_short_interleaved(STB_Vorbis f, int channels, ShortBuffer buffer, int num_shorts)
    {
        return STBVorbisJNI.stb_vorbis_get_samples_short_interleaved(STB_Vorbis.getPointer(f), channels, Util.getAddress(buffer), num_shorts);
    }

    public static int stb_vorbis_get_samples_short(STB_Vorbis f, int channels, ShortBuffer buffer, int num_samples)
    {
        return STBVorbisJNI.stb_vorbis_get_samples_short(STB_Vorbis.getPointer(f), channels, Util.getAddress(buffer, 0), num_samples);
    }
}
