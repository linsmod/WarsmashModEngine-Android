package com.mygdx.game.audio;

import com.shc.androidopenal.AL;
import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;

import static com.shc.androidopenal.AL.alGenSources;

public class ALAudioSource {
    public int sourceId;

    public ALAudioSource() {
        IntBuffer id = BufferUtils.createIntBuffer(1);
        alGenSources(1, id);
        this.sourceId = id.get(0);
    }

    public int alGetSourcei(int flag) {
        IntBuffer buffer = BufferUtils.createIntBuffer(1);
        AL.alGetSourcei(sourceId, flag, buffer);
        return buffer.get(0);
    }

    public void alSourceStop() {
        AL.alSourceStop(this.sourceId);
    }

    public void alSourcei(int flag, int v) {
        AL.alSourcei(this.sourceId, flag, v);
    }

    public void alSourcef(int flag, float v) {
        AL.alSourcef(this.sourceId, flag, v);
    }

    public void alSource3f(int flag, float v1, float v2, float v3) {
        AL.alSource3f(this.sourceId, flag, v1, v2, v3);
    }

    public void alSourceQueueBuffers(IntBuffer bids, int numEntries) {
        AL.alSourceQueueBuffers(this.sourceId, numEntries, bids);
    }

    public void alSourcePlay() {
        AL.alSourcePlay(this.sourceId);
    }

    public void alSourceUnqueueBuffer(IntBuffer bids) {
        AL.alSourceUnqueueBuffers(this.sourceId, 1, bids);
    }

    public void alSourcePause() {
        AL.alSourcePause(this.sourceId);
    }

    public void alDeleteSources() {

        AL.alDeleteSources(this.sourceId);
    }

    public float alGetSourcef(int alSecOffset) {
        return AL.alGetSourcef(this.sourceId, alSecOffset);
    }
}

