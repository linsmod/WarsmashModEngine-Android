package com.etheller.warsmash.audio;

import java.nio.ShortBuffer;

public interface IOpenALAudio {
    boolean noDevice = false;

    void freeSource(int sourceID);

    void addMusic(OpenALMusic openALMusic);

    int getAudioSource(boolean b);

    void removeMusic(OpenALMusic openALMusic);

    long getSoundId(int sourceID);

    void stopSound(long soundId);

    void pauseSource(int bufferID);

    void pauseSound(long soundId);

    void resumeSource(int bufferID);

    void resumeSound(long soundId);

    void setSoundPitch(long soundId, float pitch);

    void setSoundGain(long soundId, float volume);

    void setSoundLooping(long soundId, boolean looping);

    void setSoundPan(long soundId, float pan, float volume);

    void setSoundPosition(long soundId, float x, float y, float z, boolean is3DSound, float maxDistance, float refDistance);

    void retainSound(OpenALSound openALSound, boolean b);

    void stopSource(int bufferID);

    void removeFromRecent(OpenALSound openALMusic);
    void playSound(int sourceID, int bufferID, boolean looping, float volume);


    void CALL_freeBuffer(int bufferID);

    int CALL_alGenBuffers();

    void CALL_alBufferData(int bufferID, int i, ShortBuffer shortBuffer, int sampleRate);


    void CALL_alDeleteBuffers(int bufferID);
}
