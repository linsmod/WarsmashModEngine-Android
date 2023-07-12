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

package com.etheller.warsmash.audio;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.GdxRuntimeException;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL11;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static com.shc.androidopenal.AL.*;

/**
 * @author Nathan Sweet
 */
public abstract class OpenALMusic implements Music {
    static private final int bufferSize = 4096 * 10;
    static private final int bufferCount = 3;
    static private final int bytesPerSample = 2;
    static private final byte[] tempBytes = new byte[bufferSize];
    static private final ByteBuffer tempBuffer = BufferUtils.createByteBuffer(bufferSize);

    private final FloatArray renderedSecondsQueue = new FloatArray(bufferCount);

    private final IOpenALAudio audio;
    private IntBuffer buffers;
    private int sourceID = -1;
    private int format, sampleRate;
    private boolean isLooping, isPlaying;
    private float volume = 1;
    private float pan = 0;
    private float renderedSeconds, maxSecondsPerBuffer;

    protected final FileHandle file;
    protected int bufferOverhead = 0;

    private OnCompletionListener onCompletionListener;

    public OpenALMusic(final IOpenALAudio audio, final FileHandle file) {
        this.audio = audio;
        this.file = file;
        this.onCompletionListener = null;
    }

    protected void setup(final int channels, final int sampleRate) {
        this.format = channels > 1 ? AL_FORMAT_STEREO16 : AL_FORMAT_MONO16;
        this.sampleRate = sampleRate;
        this.maxSecondsPerBuffer = (float) (bufferSize - this.bufferOverhead)
                / (bytesPerSample * channels * sampleRate);
    }

    @Override
    public void play() {
        if (this.audio.noDevice) {
            return;
        }
        if (this.sourceID == -1) {
//            this.sourceID = this.audio.obtainSource(true);
            this.sourceID = this.audio.getAudioSource(true);
            if (this.sourceID == -1) {
                return;
            }
            this.audio.addMusic(this);
//            this.audio.music.add(this);

            if (this.buffers == null) {
                this.buffers = BufferUtils.createIntBuffer(bufferCount);
                alGenBuffers(bufferCount, this.buffers);
                final int errorCode = alGetError();
                if (errorCode != AL_NO_ERROR) {
                    throw new GdxRuntimeException("Unable to allocate audio buffers. AL Error: " + errorCode);
                }
            }
            alSourcei(this.sourceID, AL_LOOPING, AL_FALSE);
            setPan(this.pan, this.volume);

            boolean filled = false; // Check if there's anything to actually play.
            for (int i = 0; i < bufferCount; i++) {
                final int bufferID = this.buffers.get(i);
                if (!fill(bufferID)) {
                    break;
                }
                filled = true;
                alSourceQueueBuffers(this.sourceID, 1, bufferID);
            }
            if (!filled && (this.onCompletionListener != null)) {
                this.onCompletionListener.onCompletion(this);
            }

            if (alGetError() != AL_NO_ERROR) {
                stop();
                return;
            }
        }
        if (!this.isPlaying) {
            alSourcePlay(this.sourceID);
            this.isPlaying = true;
        }
    }

    @Override
    public void stop() {
        if (this.audio.noDevice) {
            return;
        }
        if (this.sourceID == -1) {
            return;
        }
//        this.audio.music.removeValue(this, true);
        this.audio.removeMusic(this);
        reset();
        this.audio.freeSource(this.sourceID);
        this.sourceID = -1;
        this.renderedSeconds = 0;
        this.renderedSecondsQueue.clear();
        this.isPlaying = false;
    }

    @Override
    public void pause() {
        if (this.audio.noDevice) {
            return;
        }
        if (this.sourceID != -1) {
            alSourcePause(this.sourceID);
        }
        this.isPlaying = false;
    }

    @Override
    public boolean isPlaying() {
        if (this.audio.noDevice) {
            return false;
        }
        if (this.sourceID == -1) {
            return false;
        }
        return this.isPlaying;
    }

    @Override
    public void setLooping(final boolean isLooping) {
        this.isLooping = isLooping;
    }

    @Override
    public boolean isLooping() {
        return this.isLooping;
    }

    @Override
    public void setVolume(final float volume) {
        this.volume = volume;
        if (this.audio.noDevice) {
            return;
        }
        if (this.sourceID != -1) {
            alSourcef(this.sourceID, AL_GAIN, volume);
        }
    }

    @Override
    public float getVolume() {
        return this.volume;
    }

    @Override
    public void setPan(final float pan, final float volume) {
        this.volume = volume;
        this.pan = pan;
        if (this.audio.noDevice) {
            return;
        }
        if (this.sourceID == -1) {
            return;
        }
        alSource3f(this.sourceID, AL_POSITION, MathUtils.cos(((pan - 1) * MathUtils.PI) / 2), 0,
                MathUtils.sin(((pan + 1) * MathUtils.PI) / 2));
        alSourcef(this.sourceID, AL_GAIN, volume);
    }

    @Override
    public void setPosition(final float position) {
        if (this.audio.noDevice) {
            return;
        }
        if (this.sourceID == -1) {
            return;
        }
        final boolean wasPlaying = this.isPlaying;
        this.isPlaying = false;
        alSourceStop(this.sourceID);
        alSourceUnqueueBuffers(this.sourceID, 1, this.buffers);
        while (this.renderedSecondsQueue.size > 0) {
            this.renderedSeconds = this.renderedSecondsQueue.pop();
        }
        if (position <= this.renderedSeconds) {
            reset();
            this.renderedSeconds = 0;
        }
        while (this.renderedSeconds < (position - this.maxSecondsPerBuffer)) {
            if (read(tempBytes) <= 0) {
                break;
            }
            this.renderedSeconds += this.maxSecondsPerBuffer;
        }
        this.renderedSecondsQueue.add(this.renderedSeconds);
        boolean filled = false;
        for (int i = 0; i < bufferCount; i++) {
            final int bufferID = this.buffers.get(i);
            if (!fill(bufferID)) {
                break;
            }
            filled = true;
            alSourceQueueBuffers(this.sourceID, 1, bufferID);
        }
        this.renderedSecondsQueue.pop();
        if (!filled) {
            stop();
            if (this.onCompletionListener != null) {
                this.onCompletionListener.onCompletion(this);
            }
        }
        alSourcef(this.sourceID, AL11.AL_SEC_OFFSET, position - this.renderedSeconds);
        if (wasPlaying) {
            alSourcePlay(this.sourceID);
            this.isPlaying = true;
        }
    }

    @Override
    public float getPosition() {
        if (this.audio.noDevice) {
            return 0;
        }
        if (this.sourceID == -1) {
            return 0;
        }
        return this.renderedSeconds + alGetSourcef(this.sourceID, AL11.AL_SEC_OFFSET);
    }

    /**
     * Fills as much of the buffer as possible and returns the number of bytes
     * filled. Returns <= 0 to indicate the end of the stream.
     */
    abstract public int read(byte[] buffer);

    /**
     * Resets the stream to the beginning.
     */
    abstract public void reset();

    /**
     * By default, does just the same as reset(). Used to add special behaviour in
     * Ogg.Music.
     */
    protected void loop() {
        reset();
    }

    public int getChannels() {
        return this.format == AL_FORMAT_STEREO16 ? 2 : 1;
    }

    public int getRate() {
        return this.sampleRate;
    }

    public void update() {
        if (this.audio.noDevice) {
            return;
        }
        if (this.sourceID == -1) {
            return;
        }

        boolean end = false;
        int buffers = alGetSourcei(this.sourceID, AL_BUFFERS_PROCESSED);
        while (buffers-- > 0) {
            final int bufferID = alSourceUnqueueBuffers(this.sourceID);
            if (bufferID == AL_INVALID_VALUE) {
                break;
            }
            this.renderedSeconds = this.renderedSecondsQueue.pop();
            if (end) {
                continue;
            }
            if (fill(bufferID)) {
                alSourceQueueBuffers(this.sourceID, bufferID);
            } else {
                end = true;
            }
        }
        if (end && (alGetSourcei(this.sourceID, AL_BUFFERS_QUEUED) == 0)) {
            stop();
            if (this.onCompletionListener != null) {
                this.onCompletionListener.onCompletion(this);
            }
        }

        // A buffer underflow will cause the source to stop.
        if (this.isPlaying && (alGetSourcei(this.sourceID, AL_SOURCE_STATE) != AL_PLAYING)) {
            alSourcePlay(this.sourceID);
        }
    }

    private boolean fill(final int bufferID) {
        tempBuffer.clear();
        int length = read(tempBytes);
        if (length <= 0) {
            if (this.isLooping) {
                loop();
                length = read(tempBytes);
                if (length <= 0) {
                    return false;
                }
                if (this.renderedSecondsQueue.size > 0) {
                    this.renderedSecondsQueue.set(0, 0);
                }
            } else {
                return false;
            }
        }
        final float previousLoadedSeconds = this.renderedSecondsQueue.size > 0 ? this.renderedSecondsQueue.first() : 0;
        final float currentBufferSeconds = (this.maxSecondsPerBuffer * length) / bufferSize;
        this.renderedSecondsQueue.insert(0, previousLoadedSeconds + currentBufferSeconds);

        tempBuffer.put(tempBytes, 0, length).flip();
        final int size = length;
        alBufferData(bufferID, this.format, tempBuffer, size, this.sampleRate);
        return true;
    }

    @Override
    public void dispose() {
        stop();
        if (this.audio.noDevice) {
            return;
        }
        if (this.buffers == null) {
            return;
        }
        alDeleteBuffers(bufferCount, this.buffers);
        this.buffers = null;
        this.onCompletionListener = null;
    }

    @Override
    public void setOnCompletionListener(final OnCompletionListener listener) {
        this.onCompletionListener = listener;
    }

    public int getSourceId() {
        return this.sourceID;
    }
}
