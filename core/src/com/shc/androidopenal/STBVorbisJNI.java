package com.shc.androidopenal;

public class STBVorbisJNI
{
    public final static native void stb_vorbis_alloc_alloc_buffer_set(long jarg1, STB_VorbisAlloc jarg1_, String jarg2);

    public final static native String stb_vorbis_alloc_alloc_buffer_get(long jarg1, STB_VorbisAlloc jarg1_);

    public final static native void stb_vorbis_alloc_alloc_buffer_length_in_bytes_set(long jarg1, STB_VorbisAlloc jarg1_, int jarg2);

    public final static native int stb_vorbis_alloc_alloc_buffer_length_in_bytes_get(long jarg1, STB_VorbisAlloc jarg1_);

    public final static native long new_stb_vorbis_alloc();

    public final static native void delete_stb_vorbis_alloc(long jarg1);

    public final static native void stb_vorbis_info_sample_rate_set(long jarg1, STB_VorbisInfo jarg1_, long jarg2);

    public final static native long stb_vorbis_info_sample_rate_get(long jarg1, STB_VorbisInfo jarg1_);

    public final static native void stb_vorbis_info_channels_set(long jarg1, STB_VorbisInfo jarg1_, int jarg2);

    public final static native int stb_vorbis_info_channels_get(long jarg1, STB_VorbisInfo jarg1_);

    public final static native void stb_vorbis_info_setup_memory_required_set(long jarg1, STB_VorbisInfo jarg1_, long jarg2);

    public final static native long stb_vorbis_info_setup_memory_required_get(long jarg1, STB_VorbisInfo jarg1_);

    public final static native void stb_vorbis_info_setup_temp_memory_required_set(long jarg1, STB_VorbisInfo jarg1_, long jarg2);

    public final static native long stb_vorbis_info_setup_temp_memory_required_get(long jarg1, STB_VorbisInfo jarg1_);

    public final static native void stb_vorbis_info_temp_memory_required_set(long jarg1, STB_VorbisInfo jarg1_, long jarg2);

    public final static native long stb_vorbis_info_temp_memory_required_get(long jarg1, STB_VorbisInfo jarg1_);

    public final static native void stb_vorbis_info_max_frame_size_set(long jarg1, STB_VorbisInfo jarg1_, int jarg2);

    public final static native int stb_vorbis_info_max_frame_size_get(long jarg1, STB_VorbisInfo jarg1_);

    public final static native long new_stb_vorbis_info();

    public final static native void delete_stb_vorbis_info(long jarg1);

    public final static native long stb_vorbis_get_info(long jarg1);

    public final static native int stb_vorbis_get_error(long jarg1);

    public final static native void stb_vorbis_close(long jarg1);

    public final static native int stb_vorbis_get_sample_offset(long jarg1);

    public final static native long stb_vorbis_get_file_offset(long jarg1);

    public final static native long stb_vorbis_open_pushdata(long jarg1, int jarg2, long jarg3, long jarg4, long jarg5, STB_VorbisAlloc jarg5_);

    public final static native int stb_vorbis_decode_frame_pushdata(long jarg1, long jarg2, int jarg3, long jarg4, long jarg5, long jarg6);

    public final static native void stb_vorbis_flush_pushdata(long jarg1);

    public final static native int stb_vorbis_decode_filename(String jarg1, long jarg2, long jarg3, long jarg4);

    public final static native int stb_vorbis_decode_memory(long jarg1, int jarg2, long jarg3, long jarg4, long jarg5);

    public final static native long stb_vorbis_open_memory(long jarg1, int jarg2, long jarg3, long jarg4, STB_VorbisAlloc jarg4_);

    public final static native long stb_vorbis_open_filename(String jarg1, long jarg2, long jarg3, STB_VorbisAlloc jarg3_);

    public final static native long stb_vorbis_open_file(long jarg1, int jarg2, long jarg3, long jarg4, STB_VorbisAlloc jarg4_);

    public final static native long stb_vorbis_open_file_section(long jarg1, int jarg2, long jarg3, long jarg4, STB_VorbisAlloc jarg4_, long jarg5);

    public final static native int stb_vorbis_seek_frame(long jarg1, long jarg2);

    public final static native int stb_vorbis_seek(long jarg1, long jarg2);

    public final static native void stb_vorbis_seek_start(long jarg1);

    public final static native long stb_vorbis_stream_length_in_samples(long jarg1);

    public final static native float stb_vorbis_stream_length_in_seconds(long jarg1);

    public final static native int stb_vorbis_get_frame_float(long jarg1, long jarg2, long jarg3);

    public final static native int stb_vorbis_get_frame_short_interleaved(long jarg1, int jarg2, long jarg3, int jarg4);

    public final static native int stb_vorbis_get_frame_short(long jarg1, int jarg2, long jarg3, int jarg4);

    public final static native int stb_vorbis_get_samples_float_interleaved(long jarg1, int jarg2, long jarg3, int jarg4);

    public final static native int stb_vorbis_get_samples_float(long jarg1, int jarg2, long jarg3, int jarg4);

    public final static native int stb_vorbis_get_samples_short_interleaved(long jarg1, int jarg2, long jarg3, int jarg4);

    public final static native int stb_vorbis_get_samples_short(long jarg1, int jarg2, long jarg3, int jarg4);

    public final static native int VORBIS_need_more_data_get();

    public final static native int VORBIS_unexpected_eof_get();

    public final static native int VORBIS_invalid_setup_get();

    public final static native int VORBIS_missing_capture_pattern_get();

    static
    {
        Util.loadLibrary();
    }
}
