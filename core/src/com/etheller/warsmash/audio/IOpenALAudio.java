package com.etheller.warsmash.audio;

import java.nio.ShortBuffer;

public interface IOpenALAudio {
    boolean noDevice = false;

    void freeSource(int sourceID);

    void addMusic(OpenALMusic openALMusic);

    int obtainSource(boolean b);

    void removeMusic(OpenALMusic openALMusic);

    long getSoundId(int sourceID);

    void stopSound(long soundId);

    void pauseSourcesWithBuffer( int bufferID);

    void pauseSound(long soundId);

    void resumeSourcesWithBuffer(int bufferID);

    void resumeSound(long soundId);

    void setSoundPitch(long soundId, float pitch);

    void setSoundGain(long soundId, float volume);

    void setSoundLooping(long soundId, boolean looping);

    void setSoundPan(long soundId, float pan, float volume);

    void setSoundPosition(long soundId, float x, float y, float z, boolean is3DSound, float maxDistance, float refDistance);

    void retain(OpenALSound openALSound, boolean b);

    void stopSourcesWithBuffer(int bufferID);

    void forget(OpenALSound openALMusic);
    void playSound(int sourceID, int bufferID, boolean looping, float volume);


    void freeBuffer(int bufferID);

    int alGenBuffers();

    void alBufferData(int bufferID, int i, ShortBuffer shortBuffer, int sampleRate);


    void alDeleteBuffers(int bufferID);

    void update();
}
