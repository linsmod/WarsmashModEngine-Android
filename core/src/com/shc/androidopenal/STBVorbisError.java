package com.shc.androidopenal;

public final class STBVorbisError
{
    public final static STBVorbisError VORBIS__no_error                        = new STBVorbisError("VORBIS__no_error");
    public final static STBVorbisError VORBIS_need_more_data                   = new STBVorbisError("VORBIS_need_more_data", STBVorbisJNI.VORBIS_need_more_data_get());
    public final static STBVorbisError VORBIS_invalid_api_mixing               = new STBVorbisError("VORBIS_invalid_api_mixing");
    public final static STBVorbisError VORBIS_outofmem                         = new STBVorbisError("VORBIS_outofmem");
    public final static STBVorbisError VORBIS_feature_not_supported            = new STBVorbisError("VORBIS_feature_not_supported");
    public final static STBVorbisError VORBIS_too_many_channels                = new STBVorbisError("VORBIS_too_many_channels");
    public final static STBVorbisError VORBIS_file_open_failure                = new STBVorbisError("VORBIS_file_open_failure");
    public final static STBVorbisError VORBIS_seek_without_length              = new STBVorbisError("VORBIS_seek_without_length");
    public final static STBVorbisError VORBIS_unexpected_eof                   = new STBVorbisError("VORBIS_unexpected_eof", STBVorbisJNI.VORBIS_unexpected_eof_get());
    public final static STBVorbisError VORBIS_seek_invalid                     = new STBVorbisError("VORBIS_seek_invalid");
    public final static STBVorbisError VORBIS_invalid_setup                    = new STBVorbisError("VORBIS_invalid_setup", STBVorbisJNI.VORBIS_invalid_setup_get());
    public final static STBVorbisError VORBIS_invalid_stream                   = new STBVorbisError("VORBIS_invalid_stream");
    public final static STBVorbisError VORBIS_missing_capture_pattern          = new STBVorbisError("VORBIS_missing_capture_pattern", STBVorbisJNI.VORBIS_missing_capture_pattern_get());
    public final static STBVorbisError VORBIS_invalid_stream_structure_version = new STBVorbisError("VORBIS_invalid_stream_structure_version");
    public final static STBVorbisError VORBIS_continued_packet_flag_invalid    = new STBVorbisError("VORBIS_continued_packet_flag_invalid");
    public final static STBVorbisError VORBIS_incorrect_stream_serial_number   = new STBVorbisError("VORBIS_incorrect_stream_serial_number");
    public final static STBVorbisError VORBIS_invalid_first_page               = new STBVorbisError("VORBIS_invalid_first_page");
    public final static STBVorbisError VORBIS_bad_packet_type                  = new STBVorbisError("VORBIS_bad_packet_type");
    public final static STBVorbisError VORBIS_cant_find_last_page              = new STBVorbisError("VORBIS_cant_find_last_page");
    public final static STBVorbisError VORBIS_seek_failed                      = new STBVorbisError("VORBIS_seek_failed");

    private static STBVorbisError[] values = {
            VORBIS__no_error,
            VORBIS_need_more_data,
            VORBIS_invalid_api_mixing,
            VORBIS_outofmem,
            VORBIS_feature_not_supported,
            VORBIS_too_many_channels,
            VORBIS_file_open_failure,
            VORBIS_seek_without_length,
            VORBIS_unexpected_eof,
            VORBIS_seek_invalid,
            VORBIS_invalid_setup,
            VORBIS_invalid_stream,
            VORBIS_missing_capture_pattern,
            VORBIS_invalid_stream_structure_version,
            VORBIS_continued_packet_flag_invalid,
            VORBIS_incorrect_stream_serial_number,
            VORBIS_invalid_first_page,
            VORBIS_bad_packet_type,
            VORBIS_cant_find_last_page,
            VORBIS_seek_failed
    };

    private static int swigNext = 0;

    private final int    value;
    private final String name;

    private STBVorbisError(String name)
    {
        this.name = name;
        this.value = swigNext++;
    }

    private STBVorbisError(String name, int value)
    {
        this.name = name;
        this.value = value;
        swigNext = value + 1;
    }

    private STBVorbisError(String name, STBVorbisError error)
    {
        this.name = name;
        this.value = error.value;
        swigNext = this.value + 1;
    }

    public static STBVorbisError valueOf(int value)
    {
        if (value < values.length && value >= 0 && values[value].value == value)
            return values[value];
        for (int i = 0; i < values.length; i++)
            if (values[i].value == value)
                return values[i];
        throw new IllegalArgumentException("No enum " + STBVorbisError.class + " with value " + value);
    }

    public final int getValue()
    {
        return value;
    }

    public String toString()
    {
        return name;
    }
}

