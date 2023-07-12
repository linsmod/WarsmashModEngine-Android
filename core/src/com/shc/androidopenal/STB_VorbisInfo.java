package com.shc.androidopenal;

public class STB_VorbisInfo
{
    protected transient boolean ownsMemory;
    private transient   long    pointer;

    protected STB_VorbisInfo(long cPtr, boolean cMemoryOwn)
    {
        ownsMemory = cMemoryOwn;
        pointer = cPtr;
    }

    public STB_VorbisInfo()
    {
        this(STBVorbisJNI.new_stb_vorbis_info(), true);
    }

    public static long getPointer(STB_VorbisInfo obj)
    {
        return (obj == null) ? 0 : obj.pointer;
    }

    protected void finalize()
    {
        free();
    }

    public synchronized void free()
    {
        if (pointer != 0)
        {
            if (ownsMemory)
            {
                ownsMemory = false;
                STBVorbisJNI.delete_stb_vorbis_info(pointer);
            }
            pointer = 0;
        }
    }

    public long getSample_rate()
    {
        return STBVorbisJNI.stb_vorbis_info_sample_rate_get(pointer, this);
    }

    public void setSample_rate(long value)
    {
        STBVorbisJNI.stb_vorbis_info_sample_rate_set(pointer, this, value);
    }

    public int getChannels()
    {
        return STBVorbisJNI.stb_vorbis_info_channels_get(pointer, this);
    }

    public void setChannels(int value)
    {
        STBVorbisJNI.stb_vorbis_info_channels_set(pointer, this, value);
    }

    public long getSetup_memory_required()
    {
        return STBVorbisJNI.stb_vorbis_info_setup_memory_required_get(pointer, this);
    }

    public void setSetup_memory_required(long value)
    {
        STBVorbisJNI.stb_vorbis_info_setup_memory_required_set(pointer, this, value);
    }

    public long getSetup_temp_memory_required()
    {
        return STBVorbisJNI.stb_vorbis_info_setup_temp_memory_required_get(pointer, this);
    }

    public void setSetup_temp_memory_required(long value)
    {
        STBVorbisJNI.stb_vorbis_info_setup_temp_memory_required_set(pointer, this, value);
    }

    public long getTemp_memory_required()
    {
        return STBVorbisJNI.stb_vorbis_info_temp_memory_required_get(pointer, this);
    }

    public void setTemp_memory_required(long value)
    {
        STBVorbisJNI.stb_vorbis_info_temp_memory_required_set(pointer, this, value);
    }

    public int getMax_frame_size()
    {
        return STBVorbisJNI.stb_vorbis_info_max_frame_size_get(pointer, this);
    }

    public void setMax_frame_size(int value)
    {
        STBVorbisJNI.stb_vorbis_info_max_frame_size_set(pointer, this, value);
    }

}
