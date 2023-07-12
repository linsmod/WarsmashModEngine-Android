package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.etheller.warsmash.WarsmashGdxFDFTestRenderScreen;
import com.etheller.warsmash.WarsmashGdxMenuScreen;
import com.etheller.warsmash.WarsmashGdxMultiScreenGame;
import com.etheller.warsmash.audio.OpenALSound;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.util.StringBundle;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.AudioContext;
import com.etheller.warsmash.viewer5.AudioContext.Listener;
import com.etheller.warsmash.viewer5.AudioDestination;
import com.etheller.warsmash.viewer5.gl.AudioExtension;
import com.etheller.warsmash.viewer5.gl.DynamicShadowExtension;
import com.etheller.warsmash.viewer5.gl.Extensions;
import com.etheller.warsmash.viewer5.gl.WireframeExtension;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;

import static org.lwjgl.openal.AL10.AL_ORIENTATION;
import static org.lwjgl.openal.AL10.AL_POSITION;
import static org.lwjgl.openal.AL10.alListener;

public class WarCraft3 {
    public static void RunGame(final WarsmashGdxMultiScreenGame arg) {
        System.out.println("You ran it.");
        String fileToLoad = null;
        String iniPath = "warsmashAndroid.ini";
//		boolean noLogs = true;
//		if (!noLogs) {
//			new File("Logs").mkdir();
//			try {
//				System.setOut(new PrintStream(
//						new FileOutputStream(new File("Logs/app.out.log"))));
//			}
//			catch (final FileNotFoundException e) {
//				e.printStackTrace();
//			}
//			try {
//				System.setErr(new PrintStream(
//						new FileOutputStream(new File("Logs/app.err.log"))));
//			}
//			catch (final FileNotFoundException e) {
//				e.printStackTrace();
//			}
//		}
        loadExtensions();
        final DataTable warsmashIni = loadWarsmashIni(iniPath);
        final Element emulatorConstants = warsmashIni.get("Emulator");
        WarsmashConstants.loadConstants(emulatorConstants, warsmashIni);

        if (fileToLoad != null) {
            System.out.println("About to run loading file: " + fileToLoad);
        }
        final String finalFileToLoad = fileToLoad;
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                if ((finalFileToLoad != null) && finalFileToLoad.toLowerCase().endsWith(".toc")) {
                    arg.setScreen(new WarsmashGdxFDFTestRenderScreen(warsmashIni,
                            arg, finalFileToLoad));
                } else {
                    final WarsmashGdxMenuScreen menuScreen = new WarsmashGdxMenuScreen(warsmashIni,
                            arg);
                    arg.setScreen(menuScreen);
                    if (finalFileToLoad != null) {
                        menuScreen.startMap(finalFileToLoad);
                    }
                }
            }
        });
    }

    public static DataTable loadWarsmashIni(final String iniPath) {
        final DataTable warsmashIni = new DataTable(StringBundle.EMPTY);
        FileHandle h = Gdx.files.internal(iniPath);
        try (InputStream warsmashIniInputStream = h.read()) {
            warsmashIni.readTXT(warsmashIniInputStream, true);
        } catch (final FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return warsmashIni;
    }

    public static void loadExtensions() {
//		LwjglNativesLoader.load();
//		Extensions.angleInstancedArrays = new ANGLEInstancedArrays() {
//			@Override
//			public void glVertexAttribDivisorANGLE(final int index, final int divisor) {
//				GL33.glVertexAttribDivisor(index, divisor);
//			}
//
//			@Override
//			public void glDrawElementsInstancedANGLE(final int mode, final int count, final int type,
//					final int indicesOffset, final int instanceCount) {
//				GL31.glDrawElementsInstanced(mode, count, type, indicesOffset, instanceCount);
//			}
//
//			@Override
//			public void glDrawArraysInstancedANGLE(final int mode, final int first, final int count,
//					final int instanceCount) {
//				GL31.glDrawArraysInstanced(mode, first, count, instanceCount);
//			}
//		};
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
                            alListener(AL_POSITION, position);
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
                            alListener(AL_ORIENTATION, orientation);
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
