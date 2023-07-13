package com.mygdx.game;

import android.opengl.GLES10Ext;
import android.opengl.GLES30;
import android.opengl.GLES31;
import android.opengl.GLES32;
import com.etheller.warsmash.viewer5.gl.DynamicShadowExtension;
import org.apache.commons.compress.utils.ByteUtils;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

import java.nio.ByteBuffer;

public class DynamicShadowExtensionGLES30 implements DynamicShadowExtension {
	@Override
	public void glFramebufferTexture(int target, int attachment, int texture, int level) {
		GLES32.glFramebufferTexture(target, attachment, texture, level);
	}

	@Override
	public void glDrawBuffer(int mode) {
		ByteBuffer buff = ByteBuffer.allocateDirect(4).putInt(mode);
		GLES30.glDrawBuffers(1, buff.asIntBuffer());
	}
}
