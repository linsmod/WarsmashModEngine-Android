package com.shc.androidopenal;

public class STB_VorbisAlloc
{
    protected transient boolean ownsMemory;
    private transient   long    pointer;

    protected STB_VorbisAlloc(long cPtr, boolean cMemoryOwn)
    {
        ownsMemory = cMemoryOwn;
        pointer = cPtr;
    }

    public STB_VorbisAlloc()
    {
        this(STBVorbisJNI.new_stb_vorbis_alloc(), true);
    }

    public static long getPointer(STB_VorbisAlloc obj)
    {
        return (obj == null) ? 0 : obj.pointer;
    }

    protected void finalize()
    {
        delete();
    }

    public synchronized void delete()
    {
        if (pointer != 0)
        {
            if (ownsMemory)
            {
                ownsMemory = false;
                STBVorbisJNI.delete_stb_vorbis_alloc(pointer);
            }
            pointer = 0;
        }
    }

    public String getAlloc_buffer()
    {
        return STBVorbisJNI.stb_vorbis_alloc_alloc_buffer_get(pointer, this);
    }

    public void setAlloc_buffer(String value)
    {
        STBVorbisJNI.stb_vorbis_alloc_alloc_buffer_set(pointer, this, value);
    }

    public int getAlloc_buffer_length_in_bytes()
    {
        return STBVorbisJNI.stb_vorbis_alloc_alloc_buffer_length_in_bytes_get(pointer, this);
    }

    public void setAlloc_buffer_length_in_bytes(int value)
    {
        STBVorbisJNI.stb_vorbis_alloc_alloc_buffer_length_in_bytes_set(pointer, this, value);
    }

}
