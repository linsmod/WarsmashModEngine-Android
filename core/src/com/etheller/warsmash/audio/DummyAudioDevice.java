package com.etheller.warsmash.audio;

import com.badlogic.gdx.audio.AudioDevice;

public class DummyAudioDevice implements AudioDevice {
    private final boolean isMono;

    public DummyAudioDevice(boolean isMono) {
        this.isMono = isMono;
    }

    /**
     * @return
     */
    @Override
    public boolean isMono() {
        return this.isMono;
    }

    /**
     * @param samples    The samples.
     * @param offset     The offset into the samples array
     * @param numSamples the number of samples to write to the device
     */
    @Override
    public void writeSamples(short[] samples, int offset, int numSamples) {

    }

    /**
     * @param samples    The samples.
     * @param offset     The offset into the samples array
     * @param numSamples the number of samples to write to the device
     */
    @Override
    public void writeSamples(float[] samples, int offset, int numSamples) {

    }

    /**
     * @return
     */
    @Override
    public int getLatency() {
        return 0;
    }

    /**
     *
     */
    @Override
    public void dispose() {

    }

    /**
     * @param volume
     */
    @Override
    public void setVolume(float volume) {

    }

    /**
     *
     */
    public void pause() {

    }

    /**
     *
     */
    public void resume() {

    }
}
