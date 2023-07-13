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
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.backends.android.AndroidAudio;
import com.badlogic.gdx.backends.android.AndroidMusic;
import com.badlogic.gdx.backends.lwjgl.audio.JavaSoundAudioRecorder;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.*;
import com.etheller.warsmash.audio.*;
import com.etheller.warsmash.loader.ExtensionLoader;
import com.etheller.warsmash.viewer5.gl.Extensions;
import com.shc.androidopenal.AL;
import com.shc.androidopenal.ALC;
import com.shc.androidopenal.ALCcontext;
import com.shc.androidopenal.ALCdevice;
import org.lwjgl.BufferUtils;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import static com.shc.androidopenal.AL.*;

/**
 * @author Nathan Sweet
 */
public class AndroidOpenALAudio implements AndroidAudio, IOpenALAudio {
	private final int deviceBufferSize;
	private final int deviceBufferCount;
	private ALCcontext alcContext;
	private ALCdevice alcDevice;
	private List<ALAudioSource> idleSources, allSources;
	private LongMap<Integer> soundIdToSource;
	private IntMap<Long> sourceToSoundId;
	private long nextSoundId = 0;
	private ObjectMap<String, Class<? extends OpenALSound>> extensionToSoundClass = new ObjectMap();
	private ObjectMap<String, Class<? extends OpenALMusic>> extensionToMusicClass = new ObjectMap();
	private OpenALSound[] recentSounds;
	private int mostRecetSound = -1;

	Array<OpenALMusic> music = new Array(false, 1, OpenALMusic.class);
	public boolean noDevice = false;

	public AndroidOpenALAudio() {
		this(16, 9, 512);
	}

	public AndroidOpenALAudio(int simultaneousSources, int deviceBufferCount, int deviceBufferSize) {
		this.deviceBufferSize = deviceBufferSize;
		this.deviceBufferCount = deviceBufferCount;


		ALCdevice device = ALC.alcOpenDevice();
		if (device != null) {
			ALCcontext context = ALC.alcCreateContext(device, null);
			ALC.alcMakeContextCurrent(context);
			this.alcDevice = device;
			this.alcContext = context;
			AndroidAudioExtension.alcContext = context;
			registerSound("ogg", Ogg.Sound.class);
			registerMusic("ogg", Ogg.Music.class);
			registerSound("wav", Wav.Sound.class);
			registerMusic("wav", Wav.Music.class);
			registerSound("mp3", Mp3.Sound.class);
			registerMusic("mp3", Mp3.Music.class);
			registerSound("flac", Flac.Sound.class);
			registerMusic("flac", Flac.Music.class);
		}
		else {
			this.noDevice = true;
			System.out.println("alcContext creation failed in AndroidOpenALAudio");
			return;
		}

		//alGetError();
		allSources = new ArrayList<>();
		for (int i = 0; i < simultaneousSources; i++) {
			allSources.add(new ALAudioSource());
			if (alGetError() != AL_NO_ERROR) break;
		}
		idleSources = new ArrayList<>(allSources);
		soundIdToSource = new LongMap<Integer>();
		sourceToSoundId = new IntMap<Long>();

		FloatBuffer orientation = (FloatBuffer) BufferUtils.createFloatBuffer(6)
														.put(new float[]{0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f});
		((Buffer) orientation).flip();
		AL.alListenerfv(AL_ORIENTATION, orientation);
		FloatBuffer velocity = (FloatBuffer) BufferUtils.createFloatBuffer(3).put(new float[]{0.0f, 0.0f, 0.0f});
		((Buffer) velocity).flip();
		AL.alListenerfv(AL_VELOCITY, velocity);
		FloatBuffer position = (FloatBuffer) BufferUtils.createFloatBuffer(3).put(new float[]{0.0f, 0.0f, 0.0f});
		((Buffer) position).flip();
		AL.alListenerfv(AL_POSITION, position);

		recentSounds = new OpenALSound[simultaneousSources];
	}

	public void registerSound(String extension, Class<? extends OpenALSound> soundClass) {
		if (extension == null) throw new IllegalArgumentException("extension cannot be null.");
		if (soundClass == null) throw new IllegalArgumentException("soundClass cannot be null.");
		extensionToSoundClass.put(extension, soundClass);
	}

	public void registerMusic(String extension, Class<? extends OpenALMusic> musicClass) {
		if (extension == null) throw new IllegalArgumentException("extension cannot be null.");
		if (musicClass == null) throw new IllegalArgumentException("musicClass cannot be null.");
		extensionToMusicClass.put(extension, musicClass);
	}

	public OpenALSound newSound(FileHandle file) {
		if (file == null) throw new IllegalArgumentException("file cannot be null.");
		Class<? extends OpenALSound> soundClass = extensionToSoundClass.get(file.extension().toLowerCase());
		if (soundClass == null)
			throw new GdxRuntimeException("Unknown file extension for sound: " + file);
		try {
			return soundClass.getConstructor(new Class[]{IOpenALAudio.class, FileHandle.class}).newInstance(this, file);
		}
		catch (Exception ex) {
			throw new GdxRuntimeException("Error creating sound " + soundClass.getName() + " for file: " + file, ex);
		}
	}

	public OpenALMusic newMusic(FileHandle file) {
		if (file == null) throw new IllegalArgumentException("file cannot be null.");
		Class<? extends OpenALMusic> musicClass = extensionToMusicClass.get(file.extension().toLowerCase());
		if (musicClass == null)
			throw new GdxRuntimeException("Unknown file extension for music: " + file);
		try {
			return musicClass.getConstructor(new Class[]{AndroidOpenALAudio.class, FileHandle.class}).newInstance(this, file);
		}
		catch (Exception ex) {
			throw new GdxRuntimeException("Error creating music " + musicClass.getName() + " for file: " + file, ex);
		}
	}

	void freeSource(ALAudioSource source) {
		if (noDevice) return;
		AL.alGetError();
		source.alSourceStop();
		int e = AL.alGetError();
		if (e != AL_NO_ERROR) throw new GdxRuntimeException("AL Error: " + e);
		source.alSourcei(AL_BUFFER, 0);
		e = AL.alGetError();
		if (e != AL_NO_ERROR) throw new GdxRuntimeException("AL Error: " + e);
		Long soundId = sourceToSoundId.remove(source.sourceId);
		if (soundId != null) soundIdToSource.remove(soundId);
		idleSources.add(source);
	}

	public void CALL_freeBuffer(int bufferID) {
		if (noDevice) return;
		for (int i = 0, n = idleSources.size(); i < n; i++) {
			ALAudioSource source = idleSources.get(i);
			if (source.alGetSourcei(AL_BUFFER) == bufferID) {
				Long soundId = sourceToSoundId.remove(source.sourceId);
				if (soundId != null) soundIdToSource.remove(soundId);
				source.alSourceStop();
				source.alSourcei(AL_BUFFER, 0);
			}
		}
	}

	/**
	 * @return
	 */
	@Override
	public int CALL_alGenBuffers() {
		return AL.alGenBuffers();
	}

	/**
	 * @param bufferID
	 * @param i
	 * @param shortBuffer
	 * @param sampleRate
	 */
	@Override
	public void CALL_alBufferData(int bufferID, int i, ShortBuffer shortBuffer, int sampleRate) {
		AL.alBufferData(bufferID, i, shortBuffer, sampleRate);
	}

	/**
	 * @param sourceID
	 * @param bufferID
	 * @param looping
	 * @param volume
	 */
	@Override
	public void playSound(int sourceID, int bufferID, boolean looping, float volume) {
		AL.alSourcei(sourceID, AL_BUFFER, bufferID);
		AL.alSourcei(sourceID, AL_LOOPING, AL_TRUE);
		AL.alSourcef(sourceID, AL_GAIN, volume);
		AL.alSourcePlay(sourceID);
	}

	/**
	 * @param bufferID
	 */
	@Override
	public void CALL_alDeleteBuffers(int bufferID) {
		AL.alDeleteBuffers(bufferID);
	}

	public void stopSource(int bufferID) {
		if (noDevice) return;
		for (int i = 0, n = idleSources.size(); i < n; i++) {
			ALAudioSource source = idleSources.get(i);
			if (source.alGetSourcei(AL_BUFFER) == bufferID) {
				Long soundId = sourceToSoundId.remove(source.sourceId);
				if (soundId != null) soundIdToSource.remove(soundId);
				source.alSourceStop();
			}
		}
	}

	public void pauseSource(int bufferID) {
		if (noDevice) return;
		for (int i = 0, n = idleSources.size(); i < n; i++) {
			ALAudioSource source = idleSources.get(i);
			if (source.alGetSourcei(AL_BUFFER) == bufferID)
				source.alSourcePause();
		}
	}

	public void resumeSource(int bufferID) {
		if (noDevice) return;
		for (int i = 0, n = idleSources.size(); i < n; i++) {
			ALAudioSource source = idleSources.get(i);
			if (source.alGetSourcei(AL_BUFFER) == bufferID) {
				if (source.alGetSourcei(AL_SOURCE_STATE) == AL_PAUSED)
					source.alSourcePlay();
			}
		}
	}

	public ALAudioSource getAudioSource(int sourceId) {
		if (noDevice) return null;
		for (int i = 0, n = idleSources.size(); i < n; i++) {
			ALAudioSource source = idleSources.get(i);
			if (source.sourceId == sourceId)
				return source;
		}
		return null;
	}

	/**
	 * @param sourceID
	 */
	@Override
	public void freeSource(int sourceID) {
		if (this.noDevice) {
			return;
		}
		AL.alSourceStop(sourceID);
		AL.alSourcei(sourceID, AL_BUFFER, 0);
		if (this.sourceToSoundId.containsKey(sourceID)) {
			final long soundId = this.sourceToSoundId.remove(sourceID);
			this.soundIdToSource.remove(soundId);
		}
		this.idleSources.add(getAudioSource(sourceID));
	}

	/**
	 * @param openALMusic
	 */
	@Override
	public void addMusic(OpenALMusic openALMusic) {
		this.music.add(openALMusic);
	}

	/**
	 * @param isMusic
	 * @return
	 */

	public int getAudioSource(boolean isMusic) {
		if (noDevice) return -1;
		for (int i = 0, n = idleSources.size(); i < n; i++) {
			ALAudioSource source = idleSources.get(i);
			int state = source.alGetSourcei(AL_SOURCE_STATE);
			if (state != AL_PLAYING && state != AL_PAUSED) {
				Long oldSoundId = sourceToSoundId.remove(source.sourceId);
				if (oldSoundId != null) soundIdToSource.remove(oldSoundId);
				if (isMusic) {
					idleSources.remove(source);
				}
				else {
					long soundId = nextSoundId++;
					sourceToSoundId.put(source.sourceId, soundId);
					soundIdToSource.put(soundId, source.sourceId);
				}
				source.alSourceStop();
				source.alSourcei(AL_BUFFER, 0);
				source.alSourcef(AL_GAIN, 1);
				source.alSourcef(AL_PITCH, 1);
				source.alSource3f(AL_POSITION, 0, 0, 1f);
				return source.sourceId;
			}
		}
		return -1;
	}

	/**
	 * @param openALMusic
	 */
	@Override
	public void removeMusic(OpenALMusic openALMusic) {
		this.music.removeValue(openALMusic, true);
	}

	public long getSoundId(int sourceId) {
		Long soundId = sourceToSoundId.get(sourceId);
		return soundId != null ? soundId : -1;
	}

	public int getSourceId(long soundId) {
		Integer sourceId = soundIdToSource.get(soundId);
		return sourceId != null ? sourceId : -1;
	}

	public void stopSound(long soundId) {
		Integer sourceId = soundIdToSource.get(soundId);
		if (sourceId != null) AL.alSourceStop(sourceId);
	}

	public void pauseSound(long soundId) {
		Integer sourceId = soundIdToSource.get(soundId);
		if (sourceId != null) AL.alSourcePause(sourceId);
	}

	public void resumeSound(long soundId) {
		int sourceId = soundIdToSource.get(soundId, -1);
		ALAudioSource source = getAudioSource(sourceId);
		if (source != null) {
			if (source.alGetSourcei(AL_SOURCE_STATE) == AL_PAUSED) {
				source.alSourcePlay();
			}
		}
	}

	public void setSoundGain(long soundId, float volume) {
		Integer sourceId = soundIdToSource.get(soundId);
		if (sourceId != null) AL.alSourcef(sourceId, AL_GAIN, volume);
	}

	public void setSoundLooping(long soundId, boolean looping) {
		Integer sourceId = soundIdToSource.get(soundId);
		if (sourceId != null)
			AL.alSourcei(sourceId, AL_LOOPING, looping ? AL_TRUE : AL_FALSE);
	}

	public void setSoundPitch(long soundId, float pitch) {
		Integer sourceId = soundIdToSource.get(soundId);
		if (sourceId != null) AL.alSourcef(sourceId, AL_PITCH, pitch);
	}

	public void setSoundPan(long soundId, float pan, float volume) {
		int sourceId = soundIdToSource.get(soundId, -1);
		if (sourceId != -1) {
			AL.alSource3f(sourceId, AL_POSITION, MathUtils.cos((pan - 1) * MathUtils.PI), 0,
					MathUtils.sin((pan + 1) * MathUtils.PI));
			AL.alSourcef(sourceId, AL_GAIN, volume);
		}
	}

	/**
	 * @param soundId
	 * @param x
	 * @param y
	 * @param z
	 * @param is3DSound
	 * @param maxDistance
	 * @param refDistance
	 */
	@Override
	public void setSoundPosition(long soundId, float x, float y, float z, boolean is3DSound, float maxDistance, float refDistance) {
		if (!this.soundIdToSource.containsKey(soundId)) {
			return;
		}
		final int sourceId = this.soundIdToSource.get(soundId);

		AL.alSource3f(sourceId, AL.AL_POSITION, x, y, z);
		AL.alSourcef(sourceId, AL.AL_MAX_DISTANCE, maxDistance);
		AL.alSourcef(sourceId, AL.AL_REFERENCE_DISTANCE, refDistance);
		AL.alSourcef(sourceId, AL.AL_ROLLOFF_FACTOR, 1.0f);
		AL.alSourcei(sourceId, AL.AL_SOURCE_RELATIVE, is3DSound ? AL.AL_FALSE : AL.AL_TRUE);
	}

	@Override
	public void dispose() {
		if (noDevice) return;
		for (int i = 0, n = allSources.size(); i < n; i++) {
			ALAudioSource source = allSources.get(i);
			int state = source.alGetSourcei(AL_SOURCE_STATE);
			if (state != AL_STOPPED) source.alSourceStop();
			source.alDeleteSources();
		}

		sourceToSoundId = null;
		soundIdToSource = null;

		ALC.alcDestroyContext(this.alcContext);
		ALC.alcCloseDevice(this.alcDevice);
		while (org.lwjgl.openal.AL.isCreated()) {
			try {
				Thread.sleep(10);
			}
			catch (InterruptedException e) {
			}
		}
	}

	public AudioDevice newAudioDevice(int sampleRate, final boolean isMono) {
		if (noDevice) return new DummyAudioDevice(isMono);
		return new AndroidOpenALAudioDevice(this, sampleRate, isMono, deviceBufferSize, deviceBufferCount);
	}

	public AudioRecorder newAudioRecorder(int samplingRate, boolean isMono) {
		if (noDevice) return new AudioRecorder() {
			@Override
			public void read(short[] samples, int offset, int numSamples) {
			}

			@Override
			public void dispose() {
			}
		};
		return new JavaSoundAudioRecorder(samplingRate, isMono);
	}

	/**
	 * Retains a list of the most recently played sounds and stops the sound played least recently if necessary for a new sound to
	 * play
	 */
	public void retainSound(OpenALSound sound, boolean stop) {
		// Move the pointer ahead and wrap
		mostRecetSound++;
		mostRecetSound %= recentSounds.length;

		if (stop) {
			// Stop the least recent sound (the one we are about to bump off the buffer)
			if (recentSounds[mostRecetSound] != null) recentSounds[mostRecetSound].stop();
		}

		recentSounds[mostRecetSound] = sound;
	}

	/**
	 * Removes the disposed sound from the least recently played list
	 */
	public void removeFromRecent(OpenALSound sound) {
		for (int i = 0; i < recentSounds.length; i++) {
			if (recentSounds[i] == sound) recentSounds[i] = null;
		}
	}

	/**
	 * Pauses all playing sounds and musics
	 **/
	@Override
	public void pause() {
		for (OpenALMusic m :
				music) {
			m.pause();
		}
	}

	/**
	 * Resumes all playing sounds and musics
	 **/
	@Override
	public void resume() {
		for (OpenALMusic m :
				music) {
			m.play();
		}
	}

	/**
	 * Notifies the AndroidAudio if an AndroidMusic is disposed
	 *
	 * @param music
	 **/
	@Override
	public void notifyMusicDisposed(AndroidMusic music) {
		music.dispose();
	}
}
