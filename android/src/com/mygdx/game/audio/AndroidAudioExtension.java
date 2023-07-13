package com.mygdx.game.audio;

import com.badlogic.gdx.audio.Sound;
import com.etheller.warsmash.viewer5.AudioContext;
import com.etheller.warsmash.viewer5.AudioDestination;
import com.etheller.warsmash.viewer5.gl.AudioExtension;
import com.shc.androidopenal.ALC;
import com.shc.androidopenal.ALCcontext;
import com.shc.androidopenal.ALCdevice;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static com.shc.androidopenal.AL.*;

public class AndroidAudioExtension implements AudioExtension {
	final FloatBuffer orientation = (FloatBuffer) BufferUtils.createFloatBuffer(6).clear();
	final FloatBuffer position = (FloatBuffer) BufferUtils.createFloatBuffer(3).clear();
	ALCdevice alcDevice;
	static ALCcontext alcContext;

	@Override
	public AudioContext createContext(boolean world) {
		AudioContext.Listener listener;
		if (world && this.alcContext != null) {
			listener = new AudioContext.Listener() {
				private float x;
				private float y;
				private float z;

				@Override
				public void setPosition(final float x, final float y, final float z) {
					this.x = x;
					this.y = y;
					this.z = z;
					position.put(0, x);
					position.put(1, y);
					position.put(2, z);
					alListenerfv(AL_POSITION, position);
				}

				@Override
				public float getX() {
					return this.x;
				}

				@Override
				public float getY() {
					return this.y;
				}

				@Override
				public float getZ() {
					return this.z;
				}

				@Override
				public void setOrientation(final float forwardX, final float forwardY, final float forwardZ,
						final float upX, final float upY, final float upZ) {
					orientation.put(0, forwardX);
					orientation.put(1, forwardY);
					orientation.put(2, forwardZ);
					orientation.put(3, upX);
					orientation.put(4, upY);
					orientation.put(5, upZ);
					alListenerfv(AL_ORIENTATION, orientation);
				}

				@Override
				public boolean is3DSupported() {
					return true;
				}
			};
		}
		else {
			listener = AudioContext.Listener.DO_NOTHING;
		}

		return new AudioContext(listener, new AudioDestination() {
		});

	}

	@Override
	public float getDuration(Sound sound) {
		return 0;
	}

	@Override
	public void play(Sound buffer, float volume, float pitch, float x, float y, float z, boolean is3DSound, float maxDistance, float refDistance, boolean looping) {

	}
}
