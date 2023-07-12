package com.etheller.warsmash.loader;

import com.badlogic.gdx.audio.Sound;
import com.etheller.warsmash.audio.OpenALSound;
import com.etheller.warsmash.viewer5.AudioContext;
import com.etheller.warsmash.viewer5.AudioContext.Listener;
import com.etheller.warsmash.viewer5.AudioDestination;
import com.etheller.warsmash.viewer5.gl.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32;

import java.nio.FloatBuffer;

import static com.shc.androidopenal.AL.*;

public class ExtensionLoader {

    public static void setupExtensions(ANGLEInstancedArrays angleInstancedArrays) {
//		LwjglNativesLoader.load();
        Extensions.angleInstancedArrays = angleInstancedArrays;
        Extensions.dynamicShadowExtension = new DynamicShadowExtension() {
            @Override
            public void glFramebufferTexture(final int target, final int attachment, final int texture,
                                             final int level) {
                GL32.glFramebufferTexture(target, attachment, texture, level);
            }

            @Override
            public void glDrawBuffer(final int mode) {
                GL11.glDrawBuffer(mode);
            }
        };
        Extensions.wireframeExtension = new WireframeExtension() {
            @Override
            public void glPolygonMode(final int face, final int mode) {
                GL11.glPolygonMode(face, mode);
            }
        };
        Extensions.audio = new AudioExtension() {
            final FloatBuffer orientation = (FloatBuffer) BufferUtils.createFloatBuffer(6).clear();
            final FloatBuffer position = (FloatBuffer) BufferUtils.createFloatBuffer(3).clear();

            @Override
            public float getDuration(final Sound sound) {
                if (sound == null) {
                    return 1;
                }
                return ((OpenALSound) sound).duration();
            }

            @Override
            public void play(final Sound buffer, final float volume, final float pitch, final float x, final float y,
                             final float z, final boolean is3dSound, final float maxDistance, final float refDistance,
                             final boolean looping) {
                ((OpenALSound) buffer).play(volume, pitch, x, y, z, is3dSound, maxDistance, refDistance, looping);
            }

            @Override
            public AudioContext createContext(final boolean world) {
                Listener listener;
                if (world && AL.isCreated()) {
                    listener = new Listener() {
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
                } else {
                    listener = Listener.DO_NOTHING;
                }

                return new AudioContext(listener, new AudioDestination() {
                });
            }
        };
        Extensions.GL_LINE = GL11.GL_LINE;
        Extensions.GL_FILL = GL11.GL_FILL;
    }
}
