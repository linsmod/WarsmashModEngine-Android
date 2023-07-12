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

import com.badlogic.gdx.audio.Sound;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.lwjgl.openal.AL10.AL_FORMAT_MONO16;
import static org.lwjgl.openal.AL10.AL_FORMAT_STEREO16;

/** @author Nathan Sweet */
public class OpenALSound implements Sound {
	private int bufferID = -1;
	private final IOpenALAudio audio;
	private float duration;

	public OpenALSound(final IOpenALAudio audio) {
		this.audio = audio;
	}

	void setup(final byte[] pcm, final int channels, final int sampleRate) {
		final int bytes = pcm.length - (pcm.length % (channels > 1 ? 4 : 2));
		final int samples = bytes / (2 * channels);
		this.duration = samples / (float) sampleRate;

		final ByteBuffer buffer = ByteBuffer.allocateDirect(bytes);
		buffer.order(ByteOrder.nativeOrder());
		buffer.put(pcm, 0, bytes);
		buffer.flip();
		
		if (this.bufferID == -1) {
			this.bufferID = audio.CALL_alGenBuffers();
			audio.CALL_alBufferData(this.bufferID, channels > 1 ? AL_FORMAT_STEREO16 : AL_FORMAT_MONO16, buffer.asShortBuffer(),
					sampleRate);
		}
	}

	@Override
	public long play() {
		return play(1);
	}

	@Override
	public long play(final float volume) {
		if (this.audio.noDevice) {
			return 0;
		}
		int sourceID = this.audio.getAudioSource(false);
		if (sourceID == -1) {
			// Attempt to recover by stopping the least recently played sound
			this.audio.retainSound(this, true);
			sourceID = this.audio.getAudioSource(false);
		}
		else {
			this.audio.retainSound(this, false);
		}
		// In case it still didn't work
		if (sourceID == -1) {
			return -1;
		}

		final long soundId = this.audio.getSoundId(sourceID);
//		alSourcei(sourceID, AL_BUFFER, this.bufferID);
//		alSourcei(sourceID, AL_LOOPING, AL_FALSE);
//		alSourcef(sourceID, AL_GAIN, volume);
//		alSourcePlay(sourceID);
		final boolean looping = false;
		audio.playSound(sourceID,this.bufferID,looping,volume);
		return soundId;
	}

	@Override
	public long loop() {
		return loop(1);
	}

	@Override
	public long loop(final float volume) {
		if (this.audio.noDevice) {
			return 0;
		}
		final int sourceID = this.audio.getAudioSource(false);
		if (sourceID == -1) {
			return -1;
		}
		final long soundId = this.audio.getSoundId(sourceID);
//		alSourcei(sourceID, AL_BUFFER, this.bufferID);
//		alSourcei(sourceID, AL_LOOPING, AL_TRUE);
//		alSourcef(sourceID, AL_GAIN, volume);
//		alSourcePlay(sourceID);
		final boolean looping = true;
		audio.playSound(sourceID,this.bufferID,looping,volume);
		return soundId;
	}

	@Override
	public void stop() {
		if (this.audio.noDevice) {
			return;
		}
		this.audio.stopSource(this.bufferID);
	}

	@Override
	public void dispose() {
		if (this.audio.noDevice) {
			return;
		}
		if (this.bufferID == -1) {
			return;
		}
		this.audio.CALL_freeBuffer(this.bufferID);
		audio.CALL_alDeleteBuffers(this.bufferID);
		this.bufferID = -1;
		this.audio.removeFromRecent(this);
	}

	@Override
	public void stop(final long soundId) {
		if (this.audio.noDevice) {
			return;
		}
		this.audio.stopSound(soundId);
	}

	@Override
	public void pause() {
		if (this.audio.noDevice) {
			return;
		}
		this.audio.pauseSource(this.bufferID);
	}

	@Override
	public void pause(final long soundId) {
		if (this.audio.noDevice) {
			return;
		}
		this.audio.pauseSound(soundId);
	}

	@Override
	public void resume() {
		if (this.audio.noDevice) {
			return;
		}
		this.audio.resumeSource(this.bufferID);
	}

	@Override
	public void resume(final long soundId) {
		if (this.audio.noDevice) {
			return;
		}
		this.audio.resumeSound(soundId);
	}

	@Override
	public void setPitch(final long soundId, final float pitch) {
		if (this.audio.noDevice) {
			return;
		}
		this.audio.setSoundPitch(soundId, pitch);
	}

	@Override
	public void setVolume(final long soundId, final float volume) {
		if (this.audio.noDevice) {
			return;
		}
		this.audio.setSoundGain(soundId, volume);
	}

	@Override
	public void setLooping(final long soundId, final boolean looping) {
		if (this.audio.noDevice) {
			return;
		}
		this.audio.setSoundLooping(soundId, looping);
	}

	@Override
	public void setPan(final long soundId, final float pan, final float volume) {
		if (this.audio.noDevice) {
			return;
		}
		this.audio.setSoundPan(soundId, pan, volume);
	}

	public void setPosition(final long soundId, final float x, final float y, final float z, final boolean is3DSound,
			final float maxDistance, final float refDistance) {
		if (this.audio.noDevice) {
			return;
		}
		this.audio.setSoundPosition(soundId, x, y, z, is3DSound, maxDistance, refDistance);
	}

	@Override
	public long play(final float volume, final float pitch, final float pan) {
		final long id = play();
		setPitch(id, pitch);
		setPan(id, pan, volume);
		return id;
	}

	public long play(final float volume, final float pitch, final float x, final float y, final float z,
			final boolean is3DSound, final float maxDistance, final float refDistance, final boolean looping) {
		final long id = looping ? loop() : play();
		setPitch(id, pitch);
		setVolume(id, volume);
		setPosition(id, x, y, z, is3DSound, maxDistance, refDistance);
		return id;
	}

	@Override
	public long loop(final float volume, final float pitch, final float pan) {
		final long id = loop();
		setPitch(id, pitch);
		setPan(id, pan, volume);
		return id;
	}

	/** Returns the length of the sound in seconds. */
	public float duration() {
		return this.duration;
	}
}
