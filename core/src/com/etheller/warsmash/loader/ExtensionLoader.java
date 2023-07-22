package com.etheller.warsmash.loader;

import com.etheller.warsmash.viewer5.gl.*;
import org.lwjgl.opengl.GL11;

public class ExtensionLoader {

    public static void setupExtensions(
            ANGLEInstancedArrays angleInstancedArrays,
            DynamicShadowExtension dynamicShadowExtension,
            AudioExtension audioExtension) {
//		LwjglNativesLoader.load();
        Extensions.angleInstancedArrays = angleInstancedArrays;
        Extensions.dynamicShadowExtension = dynamicShadowExtension;
        Extensions.wireframeExtension = new WireframeExtension() {
            @Override
            public void glPolygonMode(final int face, final int mode) {
                GL11.glPolygonMode(face, mode);
            }
        };
        Extensions.audio = audioExtension;
        Extensions.GL_LINE = GL11.GL_LINE;
        Extensions.GL_FILL = GL11.GL_FILL;
    }
}
