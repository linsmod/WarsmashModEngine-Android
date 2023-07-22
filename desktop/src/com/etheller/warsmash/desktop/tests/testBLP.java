package com.etheller.warsmash.desktop.tests;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.etheller.warsmash.pjblp.Blp2;
import com.etheller.warsmash.pjblp.JpegImage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class testBLP {
	public static void main(final String[] arg) {
		try {
			var folder = new File("C:\\Users\\linswin\\Desktop\\jblp\\target\\test-classes\\files");
			for (File f :
					folder.listFiles()) {
				var image = Blp2.decode(new FileInputStream(f).readAllBytes());

				var imageData = Blp2.getImageData(image, 0);
//				var pix = new Pixmap(imageData.width,imageData.height, Pixmap.Format.RGBA8888);
//				pix.setPixels(ByteBuffer.allocate(imageData.width * imageData.height * 4)
//									  .put(imageData.data).flip());
//				PixmapIO.writePNG(new FileHandle(f.getAbsolutePath()+".png"),pix);
//				for (int i = 0; i < imageData.height; i++) {
//					for (int j = 0; j < imageData.width; j++) {
//
//					}
//				}
				System.out.println("decodeBLP OK " + f.getName());
			}
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		JpegImage image = new JpegImage();
		var f = new File("C:\\Program Files (x86)\\Developer Express .NET v8.2\\Demos\\ASPxGridView\\ASPxGridViewDemos\\App_Themes\\Default\\Demo\\Main.jpg");
		byte[] bytes = new byte[0];
		try {
			bytes = new FileInputStream(f).readAllBytes();
			image.parse(bytes);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
