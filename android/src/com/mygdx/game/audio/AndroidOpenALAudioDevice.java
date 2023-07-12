/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.mygdx.game.audio;

import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.shc.androidopenal.AL;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL11;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static com.shc.androidopenal.AL.*;

/**
 * @author Nathan Sweet
 */
public class AndroidOpenALAudioDevice implements AudioDevice {
    static private final int bytesPerSample = 2;

    private final AndroidOpenALAudio audio;
    private final int channels;
    private IntBuffer buffer;
    private ALAudioSource source = null;
    private final int format, sampleRate;
    private boolean isPlaying;
    private float volume = 1;
    private float renderedSeconds;

    private final float secondsPerBuffer;
    private byte[] bytes;
    private final int bufferSize;
    private final int bufferCount;
    private final ByteBuffer tempBuffer;

    public AndroidOpenALAudioDevice(final AndroidOpenALAudio audio, final int sampleRate, final boolean isMono, final int bufferSize,
                                    final int bufferCount) {
        this.audio = audio;
        this.channels = isMono ? 1 : 2;
        this.bufferSize = bufferSize;
        this.bufferCount = bufferCount;
        this.format = this.channels > 1 ? AL_FORMAT_STEREO16 : AL_FORMAT_MONO16;
        this.sampleRate = sampleRate;
        this.secondsPerBuffer = (float) bufferSize / bytesPerSample / this.channels / sampleRate;
        this.tempBuffer = BufferUtils.createByteBuffer(bufferSize);
    }

    @Override
    public void writeSamples(final short[] samples, final int offset, final int numSamples) {
        if ((this.bytes == null) || (this.bytes.length < (numSamples * 2))) {
            this.bytes = new byte[numSamples * 2];
        }
        final int end = Math.min(offset + numSamples, samples.length);
        for (int i = offset, ii = 0; i < end; i++) {
            final short sample = samples[i];
            this.bytes[ii++] = (byte) (sample & 0xFF);
            this.bytes[ii++] = (byte) ((sample >> 8) & 0xFF);
        }
        writeSamples(this.bytes, 0, numSamples * 2);
    }

    @Override
    public void writeSamples(final float[] samples, final int offset, final int numSamples) {
        if ((this.bytes == null) || (this.bytes.length < (numSamples * 2))) {
            this.bytes = new byte[numSamples * 2];
        }
        final int end = Math.min(offset + numSamples, samples.length);
        for (int i = offset, ii = 0; i < end; i++) {
            float floatSample = samples[i];
            floatSample = MathUtils.clamp(floatSample, -1f, 1f);
            final int intSample = (int) (floatSample * 32767);
            this.bytes[ii++] = (byte) (intSample & 0xFF);
            this.bytes[ii++] = (byte) ((intSample >> 8) & 0xFF);
        }
        writeSamples(this.bytes, 0, numSamples * 2);
    }

    public void writeSamples(final byte[] data, int offset, int length) {
        if (length < 0) {
            throw new IllegalArgumentException("length cannot be < 0.");
        }

        if (this.source == null) {
            int sourceId = this.audio.getAudioSource(true);
            this.source = this.audio.getAudioSource(sourceId);
            if (this.source == null) {
                //no device ?
                return;
            }
            if (this.buffer == null) {
                this.buffer = BufferUtils.createIntBuffer(this.bufferCount);
                AL.alGenBuffers(this.bufferCount, this.buffer);
                if (alGetError() != AL_NO_ERROR) {
                    throw new GdxRuntimeException("Unabe to allocate audio buffers.");
                }
            }
            this.source.alSourcei(AL_LOOPING, AL_FALSE);
            this.source.alSourcef(AL_GAIN, this.volume);
            // Fill initial buffers.
            int queuedBuffers = 0;
            for (int i = 0; i < this.bufferCount; i++) {
                final int bufferID = this.buffer.get(i);
                final int written = Math.min(this.bufferSize, length);
                this.tempBuffer.clear();
                this.tempBuffer.put(data, offset, written).flip();
                final int size = length;
                AL.alBufferData(bufferID, this.format, this.tempBuffer, size, this.sampleRate);
                source.alSourceQueueBuffers(this.buffer, 1);
                length -= written;
                offset += written;
                queuedBuffers++;
            }

            // Queue rest of buffers, empty.
            this.tempBuffer.clear().flip();
            for (int i = queuedBuffers; i < this.bufferCount; i++) {
                final int bufferID = this.buffer.get(i);
                final int size = length;
                alBufferData(bufferID, this.format, this.tempBuffer, size, this.sampleRate);
                source.alSourceQueueBuffers(this.buffer, 1);
            }
            source.alSourcePlay();
            this.isPlaying = true;
        }

        while (length > 0) {
            final int written = fillBuffer(data, offset, length);
            length -= written;
            offset += written;
        }
    }

    /**
     * Blocks until some of the data could be buffered.
     */
    private int fillBuffer(final byte[] data, final int offset, final int length) {
        final int written = Math.min(this.bufferSize, length);

        outer:
        while (true) {
            int buffers = this.source.alGetSourcei(AL_BUFFERS_PROCESSED);
            while (buffers-- > 0) {
                source.alSourceUnqueueBuffer(this.buffer);
                if (AL.alGetError() == AL_INVALID_VALUE) {
                    break;
                }
                int bufferID = this.buffer.get(0);
                this.renderedSeconds += this.secondsPerBuffer;

                this.tempBuffer.clear();
                this.tempBuffer.put(data, offset, written).flip();
                alBufferData(bufferID, this.format, this.tempBuffer, written, this.sampleRate);

                source.alSourceQueueBuffers(this.buffer, 1);
                break outer;
            }
            // Wait for buffer to be free.
            try {
                Thread.sleep((long) (1000 * this.secondsPerBuffer));
            } catch (final InterruptedException ignored) {
            }
        }

        // A buffer underflow will cause the source to stop.
        if (!this.isPlaying || (source.alGetSourcei(AL_SOURCE_STATE) != AL_PLAYING)) {
            source.alSourcePlay();
            this.isPlaying = true;
        }

        return written;
    }

    public void stop() {
        if (this.source == null) {
            return;
        }
        this.audio.freeSource(this.source);
        this.source = null;
        this.renderedSeconds = 0;
        this.isPlaying = false;
    }

    public boolean isPlaying() {
        if (this.source == null) {
            return false;
        }
        return this.isPlaying;
    }

    @Override
    public void setVolume(final float volume) {
        this.volume = volume;
        if (this.source != null) {
            this.source.alSourcef(AL_GAIN, volume);
        }
    }

    public void pause() {

    }

    public void resume() {

    }

    public float getPosition() {
        if (this.source == null) {
            return 0;
        }
        return this.renderedSeconds + this.source.alGetSourcef(AL11.AL_SEC_OFFSET);
    }

    public void setPosition(final float position) {
        this.renderedSeconds = position;
    }

    public int getChannels() {
        return this.format == AL_FORMAT_STEREO16 ? 2 : 1;
    }

    public int getRate() {
        return this.sampleRate;
    }

    @Override
    public void dispose() {
        if (this.buffer == null) {
            return;
        }
        if (this.source != null) {
            this.audio.freeSource(this.source);
            this.source = null;
        }
        alDeleteBuffers(this.bufferCount, this.buffer);
        this.buffer = null;
    }

    @Override
    public boolean isMono() {
        return this.channels == 1;
    }

    @Override
    public int getLatency() {
        return (int) (this.secondsPerBuffer * this.bufferCount * 1000);
    }
}
