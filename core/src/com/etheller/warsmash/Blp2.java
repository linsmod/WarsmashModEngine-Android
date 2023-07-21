package com.etheller.warsmash;
import java.util.Stack;

public class Blp2 {
	class image {

		public int type;
		public int encoding;
		public int alphaDepth;
		public int alphaEncoding;
		public int mipLevels;
		public int[] offsets;
		public int[] engths;
		public int[] lengths;
		public int content;
		public int alphaBits;
		public Stack<mipmap> mipmaps = new Stack<>();
		private byte[] data;
		private int width;
		private int height;
		public int[][] palette;

		public image(byte[] bytes, int width, int height) {
			this.data = bytes;
			this.width = width;
			this.height = height;
		}

		public image() {

		}
	}
	class mipmap{

		public int size;
		public int offset;
	}

	int JPEG_TYPE = 0;
	int NORMAL_TYPE = 1;
	// 1: Uncompressed, 2: DXT compression, 3: Uncompressed BGRA
	final int UNCOMPRESSED = 1;
	final int DXT_COMPRESSION = 2;
	final int UNCOMPRESSED_BGRA = 3;
	final int BLP_ENCODING_DXT = 2;
	final int BLP_ALPHA_DEPTH_0 = 0;
	final int BLP_ALPHA_DEPTH_1 = 1;
	final int BLP_ALPHA_DEPTH_4 = 4;
	final int BLP_ALPHA_DEPTH_8 = 8;
	final int BLP_ALPHA_ENCODING_DXT1 = 0;
	final int BLP_ALPHA_ENCODING_DXT3 = 1;
	final int BLP_ALPHA_ENCODING_DXT5 = 7;
	final int BLP_FORMAT_JPEG = 0;

	static {

	}

	final int BLP_FORMAT_PALETTED_NO_ALPHA = ((UNCOMPRESSED << 16) | (BLP_ALPHA_DEPTH_0 << 8));
	final int BLP_FORMAT_PALETTED_ALPHA_1 = ((UNCOMPRESSED << 16) | (BLP_ALPHA_DEPTH_1 << 8));
	final int BLP_FORMAT_PALETTED_ALPHA_4 = ((UNCOMPRESSED << 16) | (BLP_ALPHA_DEPTH_4 << 8));
	final int BLP_FORMAT_PALETTED_ALPHA_8 = ((UNCOMPRESSED << 16) | (BLP_ALPHA_DEPTH_8 << 8));
	final int BLP_FORMAT_RAW_BGRA = (UNCOMPRESSED_BGRA << 16);
	final int BLP_FORMAT_DXT1_NO_ALPHA = (BLP_ENCODING_DXT << 16) | (BLP_ALPHA_DEPTH_0 << 8) | BLP_ALPHA_ENCODING_DXT1;
	final int BLP_FORMAT_DXT1_ALPHA_1 = (BLP_ENCODING_DXT << 16) | (BLP_ALPHA_DEPTH_1 << 8) | BLP_ALPHA_ENCODING_DXT1;
	final int BLP_FORMAT_DXT3_ALPHA_4 = (BLP_ENCODING_DXT << 16) | (BLP_ALPHA_DEPTH_4 << 8) | BLP_ALPHA_ENCODING_DXT3;
	final int BLP_FORMAT_DXT3_ALPHA_8 = (BLP_ENCODING_DXT << 16) | (BLP_ALPHA_DEPTH_8 << 8) | BLP_ALPHA_ENCODING_DXT3;
	final int BLP_FORMAT_DXT5_ALPHA_8 = (BLP_ENCODING_DXT << 16) | (BLP_ALPHA_DEPTH_8 << 8) | BLP_ALPHA_ENCODING_DXT5;
	final int dxt4to8 = convertBitRange(4, 8);
	final int dxt5to8 = convertBitRange(5, 8);
	final int dxt6to8 = convertBitRange(6, 8);
	byte[] dx1colors = new byte[16];
	byte[] dx3colors = new byte[12];
	byte[] dx5alphas = new byte[8];
	byte[] red = new byte[8];
	byte[] green = new byte[8];

	int convertBitRange(int fromBits, int toBits) {
		return ((1 << toBits) - 1) / ((1 << fromBits) - 1);
	}

	void dx1Colors(byte[] out, int color0, int color1) {
		byte r0 = (byte) (((color0 >> 11) & 31) * dxt5to8);
		byte g0 = (byte) (((color0 >> 5) & 63) * dxt6to8);
		byte b0 = (byte) ((color0 & 31) * dxt5to8);
		byte r1 = (byte) (((color1 >> 11) & 31) * dxt5to8);
		byte g1 = (byte) (((color1 >> 5) & 63) * dxt6to8);
		byte b1 = (byte) ((color1 & 31) * dxt5to8);
		// Minimum and maximum colors.
		out[0] = r0;
		out[1] = g0;
		out[2] = b0;
		out[3] = (byte) 255;
		out[4] = r1;
		out[5] = g1;
		out[6] = b1;
		out[7] = (byte) 255;
		// Interpolated colors.
		if (color0 > color1) {
			out[8] = (byte) ((5 * r0 + 3 * r1) >> 3);
			out[9] = (byte) ((5 * g0 + 3 * g1) >> 3);
			out[10] = (byte) ((5 * b0 + 3 * b1) >> 3);
			out[11] = (byte) 255;
			out[12] = (byte) ((5 * r1 + 3 * r0) >> 3);
			out[13] = (byte) ((5 * g1 + 3 * g0) >> 3);
			out[14] = (byte) ((5 * b1 + 3 * b0) >> 3);
			out[15] = (byte) 255;
		}
		else {
			out[8] = (byte) ((r0 + r1) >> 1);
			out[9] = (byte) ((g0 + g1) >> 1);
			out[10] = (byte) ((b0 + b1) >> 1);
			out[11] = (byte) 255;
			out[12] = 0;
			out[13] = 0;
			out[14] = 0;
			out[15] = 0;
		}
	}

	void dx3Colors(byte[] out, int color0, int color1) {
		byte r0 = (byte) (((color0 >> 11) & 31) * dxt5to8);
		byte g0 = (byte) (((color0 >> 5) & 63) * dxt6to8);
		byte b0 = (byte) ((color0 & 31) * dxt5to8);
		byte r1 = (byte) (((color1 >> 11) & 31) * dxt5to8);
		byte g1 = (byte) (((color1 >> 5) & 63) * dxt6to8);
		byte b1 = (byte) ((color1 & 31) * dxt5to8);
		// Minimum and maximum colors.
		out[0] = r0;
		out[1] = g0;
		out[2] = b0;
		out[3] = r1;
		out[4] = g1;
		out[5] = b1;
		// Interpolated colors.
		out[6] = (byte) ((5 * r0 + 3 * r1) >> 3);
		out[7] = (byte) ((5 * g0 + 3 * g1) >> 3);
		out[8] = (byte) ((5 * b0 + 3 * b1) >> 3);
		out[9] = (byte) ((5 * r1 + 3 * r0) >> 3);
		out[10] = (byte) ((5 * g1 + 3 * g0) >> 3);
		out[11] = (byte) ((5 * b1 + 3 * b0) >> 3);
	}

	void dx5Alphas(byte[] out, int alpha0, int alpha1) {
		// Minimum and maximum alphas.
		out[0] = (byte) alpha0;
		out[1] = (byte) alpha1;
		// Interpolated alphas.
		if (alpha0 > alpha1) {
			out[2] = (byte) ((54 * alpha0 + 9 * alpha1) >> 6);
			out[3] = (byte) ((45 * alpha0 + 18 * alpha1) >> 6);
			out[4] = (byte) ((36 * alpha0 + 27 * alpha1) >> 6);
			out[5] = (byte) ((27 * alpha0 + 36 * alpha1) >> 6);
			out[6] = (byte) ((18 * alpha0 + 45 * alpha1) >> 6);
			out[7] = (byte) ((9 * alpha0 + 54 * alpha1) >> 6);
		}
		else {
			out[2] = (byte) ((12 * alpha0 + 3 * alpha1) >> 4);
			out[3] = (byte) ((9 * alpha0 + 6 * alpha1) >> 4);
			out[4] = (byte) ((6 * alpha0 + 9 * alpha1) >> 4);
			out[5] = (byte) ((3 * alpha0 + 12 * alpha1) >> 4);
			out[6] = 0;
			out[7] = (byte) 255;
		}
	}

	void rgColors(byte[] out, byte color0, byte color1) {
		// Minimum and maximum red colors.
		out[0] = color0;
		out[1] = color1;
		// Interpolated red colors.
		if (color0 > color1) {
			out[2] = (byte) ((6 * color0 + 1 * color1) / 7);
			out[3] = (byte) ((5 * color0 + 2 * color1) / 7);
			out[4] = (byte) ((4 * color0 + 3 * color1) / 7);
			out[5] = (byte) ((3 * color0 + 4 * color1) / 7);
			out[6] = (byte) ((2 * color0 + 5 * color1) / 7);
			out[7] = (byte) ((1 * color0 + 6 * color1) / 7);
		}
		else {
			out[2] = (byte) ((4 * color0 + 1 * color1) / 5);
			out[3] = (byte) ((3 * color0 + 2 * color1) / 5);
			out[4] = (byte) ((2 * color0 + 3 * color1) / 5);
			out[5] = (byte) ((1 * color0 + 4 * color1) / 5);
			out[6] = 0;
			out[7] = 1;
		}
	}

	/**
	 * Decodes DXT1 data to a Uint8Array typed array with 8-8-8-8 RGBA bits.
	 * <p>
	 * DXT1 is also known as BC1.
	 */
	image decodeDxt1(DataView src, int width, int height, image img) {
		for (int blockY = 0, blockHeight = height / 4; blockY < blockHeight; blockY++) {
			for (int blockX = 0, blockWidth = width / 4; blockX < blockWidth; blockX++) {
				var i = 8 * (blockY * blockWidth + blockX);
				// Get the color values.
				dx1Colors(dx1colors, src.getUint8(i) + 256 * src.getUint8(i + 1), src.getUint8(i + 2) + 256 * src.getUint8(i + 3));
				// The offset to the first pixel in the destination.
				var dstI = (blockY * 16) * width + blockX * 16;
				// All 32 color bits.
				var bits = src.getUint8(i + 4) | (src.getUint8(i + 5) << 8) | (src.getUint8(i + 6) << 16) | (src.getUint8(i + 7) << 24);
				for (var row = 0; row < 4; row++) {
					var rowOffset = row * 8;
					var dstOffset = dstI + row * width * 4;
					for (var column = 0; column < 4; column++) {
						var dstIndex = dstOffset + column * 4;
						var colorOffset = ((bits >> (rowOffset + column * 2)) & 3) * 4;
						img.data[dstIndex + 0] = dx1colors[colorOffset + 0];
						img.data[dstIndex + 1] = dx1colors[colorOffset + 1];
						img.data[dstIndex + 2] = dx1colors[colorOffset + 2];
						img.data[dstIndex + 3] = dx1colors[colorOffset + 3];
					}
				}
			}
		}
		return img;
	}

	/**
	 * Decodes DXT3 data to a Uint8Array typed array with 8-8-8-8 RGBA bits.
	 * <p>
	 * DXT3 is also known as BC2.
	 */
	image decodeDxt3(DataView src, int width, int height, image img) {
		var rowBytes = width * 4;
		for (int blockY = 0, blockHeight = height / 4; blockY < blockHeight; blockY++) {
			for (int blockX = 0, blockWidth = width / 4; blockX < blockWidth; blockX++) {
				var i = 16 * (blockY * blockWidth + blockX);
				// Get the color values.
				dx3Colors(dx3colors, src.getUint8(i + 8) + 256 * src.getUint8(i + 9), src.getUint8(i + 10) + 256 * src.getUint8(i + 11));
				var dstI = (blockY * 16) * width + blockX * 16;
				for (var row = 0; row < 4; row++) {
					// Get 16 bits of alpha indices.
					var alphaBits = src.getUint8(i + row * 2) + 256 * src.getUint8(i + 1 + row * 2);
					// Get 8 bits of color indices.
					var colorBits = src.getUint8(i + 12 + row);
					for (var column = 0; column < 4; column++) {
						var dstIndex = dstI + column * 4;
						var colorIndex = ((colorBits >> (column * 2)) & 3) * 3;
						img.data[dstIndex + 0] = dx3colors[colorIndex + 0];
						img.data[dstIndex + 1] = dx3colors[colorIndex + 1];
						img.data[dstIndex + 2] = dx3colors[colorIndex + 2];
						img.data[dstIndex + 3] = (byte) (((alphaBits >> (column * 4)) & 0xf) * dxt4to8);
					}
					dstI += rowBytes;
				}
			}
		}
		return img;
	}

	/**
	 * Decodes DXT5 data to a Uint8Array typed array with 8-8-8-8 RGBA bits.
	 * <p>
	 * DXT5 is also known as BC3.
	 */
	image decodeDxt5(DataView src, int width, int height, image img) {
		var rowBytes = width * 4;
		for (int blockY = 0, blockHeight = height / 4; blockY < blockHeight; blockY++) {
			for (int blockX = 0, blockWidth = width / 4; blockX < blockWidth; blockX++) {
				var i = 16 * (blockY * blockWidth + blockX);
				// Get the alpha values.
				dx5Alphas(dx5alphas, src.getUint8(i), src.getUint8(i + 1));
				// Get the color values.
				dx3Colors(dx3colors, src.getUint8(i + 8) + 256 * src.getUint8(i + 9), src.getUint8(i + 10) + 256 * src.getUint8(i + 11));
				// The offset to the first pixel in the destination.
				var dstI = (blockY * 16) * width + blockX * 16;
				// The outer loop is only needed because JS bitwise operators only work on 32bit integers, while the alpha flags contain 48 bits.
				// Processing is instead done in two blocks, where each one handles 24 bits, or two rows of 4 pixels.
				for (var block = 0; block < 2; block++) {
					var alphaOffset = i + 2 + block * 3;
					var colorOffset = i + 12 + block * 2;
					// 24 alpha bits.
					var alphaBits = src.getUint8(alphaOffset) + 256 * (src.getUint8(alphaOffset + 1) + 256 * src.getUint8(alphaOffset + 2));
					// Go over two rows.
					for (var row = 0; row < 2; row++) {
						var colorBits = src.getUint8(colorOffset + row);
						// Go over four columns.
						for (var column = 0; column < 4; column++) {
							var dstIndex = dstI + column * 4;
							var colorIndex = ((colorBits >> (column * 2)) & 3) * 3;
							var alphaIndex = (alphaBits >> (row * 12 + column * 3)) & 7;
							// Set the pixel.
							img.data[dstIndex + 0] = dx3colors[colorIndex + 0];
							img.data[dstIndex + 1] = dx3colors[colorIndex + 1];
							img.data[dstIndex + 2] = dx3colors[colorIndex + 2];
							img.data[dstIndex + 3] = dx5alphas[alphaIndex];
						}
						// Next row.
						dstI += rowBytes;
					}
				}
			}
		}
		return img;
	}

	/**
	 * Decodes RGTC data to a Uint8Array typed array with 8-8 RG bits.
	 * <p>
	 * RGTC is also known as BC5, ATI2, and 3Dc.
	 */
	byte[] decodeRgtc(DataView src, int width, int height) {
		var dst = new byte[width * height * 2];
		var rowBytes = width * 2;
		for (int blockY = 0, blockHeight = height / 4; blockY < blockHeight; blockY++) {
			for (int blockX = 0, blockWidth = width / 4; blockX < blockWidth; blockX++) {
				var i = 16 * (blockY * blockWidth + blockX);
				// Get the red colors.
				rgColors(red, src.buffer[i], src.buffer[i + 1]);
				// Get the green colors.
				rgColors(green, src.buffer[i + 8], src.buffer[i + 9]);
				// The offset to the first pixel in the destination.
				var dstI = (blockY * 8) * width + blockX * 8;
				// Split to two blocks of two rows, because there are 48 color bits.
				for (var block = 0; block < 2; block++) {
					var blockOffset = i + block * 3;
					// Get 24 bits of the color indices.
					var redbits = src.buffer[blockOffset + 2] + 256 * (src.buffer[blockOffset + 3] + 256 * src.buffer[blockOffset + 4]);
					var greenbits = src.buffer[blockOffset + 10] + 256 * (src.buffer[blockOffset + 11] + 256 * src.buffer[blockOffset + 12]);
					for (var row = 0; row < 2; row++) {
						var rowOffset = row * 4;
						for (var column = 0; column < 4; column++) {
							var dstOffset = dstI + column * 2;
							var shifts = 3 * (rowOffset + column);
							dst[dstOffset + 1] = red[(redbits >> shifts) & 7];
							dst[dstOffset + 2] = green[(greenbits >> shifts) & 7];
						}
						// Next row.
						dstI += rowBytes;
					}
				}
			}
		}
		return dst;
	}

	String keyword(DataView view, int offset) {
		return view.readString(offset, 4);
	}

	int uint32(DataView view, int offset) {
		return view.getUint32(offset, true);
	}

	int[] uint32Array(DataView view, int offset, int size) {
		int[] d = new int[size];
		for (int i = 0; i < size; i++) {
			d[i] = uint32(view, offset + i * 4);
		}
		return d;
	}

	int uint8(DataView view, int offset) {
		return view.getUint8(offset);
	}

	int[] readBGRA(DataView view, int offset) {

		return new int[]{
				view.getUint8(offset),
				view.getUint8(offset + 1),
				view.getUint8(offset + 2),
				view.getUint8(offset + 3)};
	}

	int[][] readBGRAArray(DataView view, int offset, int size) {
		int[][] d = new int[4][];
		for (int i = 0; i < size; i++) {
			d[i] = readBGRA(view, offset + i * 4);
		}
		return d;
	}

	int blpFormat(image image) {
		if (image.type == JPEG_TYPE) {
			return BLP_FORMAT_JPEG;
		}
		if (image.encoding == UNCOMPRESSED) {
			// BLP_FORMAT_PALETTED
			return ((image.encoding << 16) | (image.alphaDepth << 8));
		}
		else if (image.encoding == UNCOMPRESSED_BGRA) {
			// BLP_FORMAT_RAW_BGRA
			return (image.encoding << 16);
		}
		// BLP_FORMAT_DXT
		return ((image.encoding << 16) | (image.alphaDepth << 8) | image.alphaEncoding);
	}

	interface BLPContent {
		public static int JPEG=0;
		public static int Direct=1;
	}
	interface BLPType{
		int BLP0=0;
		int BLP1=1;
		int BLP2=2;
	}

	public image decode(byte[] arrayBuffer) {
		var view = new DataView(arrayBuffer);
		var image = new image();
		image.type = BLPType.BLP1;
		image.width = 0;
		image.height = 0;
		image.content = BLPContent.JPEG;
		image.alphaBits = 0;
		image.mipmaps = new Stack<>();
		image.data = arrayBuffer;
		var type = keyword(view, 0);
		if (type.equals("BLP2")) {
			return decodeBLP2(arrayBuffer);
		}
		if (type.equals("BLP0")) {
			throw new RuntimeException("'BLP0/BLP2 not supported'");
		}
		if (!type.equals("BLP1")) {
			throw new RuntimeException("Not a blp image");
		}
		image.content = uint32(view, 1);
		if (image.content != BLPContent.JPEG && image.content != BLPContent.Direct) {
			throw new RuntimeException("Unknown BLP content");
		}
		image.alphaBits = uint32(view, 2);
		image.width = uint32(view, 3);
		image.height = uint32(view, 4);
		for (var i = 0; i < 16; ++i) {
			var mipmap = new mipmap();
			mipmap.offset=uint32(view, 7 + i);
			mipmap.size=uint32(view, 7 + 16 + i);
			if (mipmap.size > 0) {
				image.mipmaps.push(mipmap);
			}
			else {
				break;
			}
		}
		return image;
	}

	public image decodeBLP2(byte[] d) {
		var view = new DataView(d);
		var magic = keyword(view, 0);
		if (!magic.equals("BLP2")) {
			throw new RuntimeException("'Not a blp2 image'");
		}
		var image = new image();
		image.type = uint32(view, 4);
		image.encoding = uint8(view, 8);
		image.alphaDepth = uint8(view, 9);
		image.alphaEncoding = uint8(view, 10);
		image.mipLevels = uint8(view, 11);
		image.width = uint32(view, 12);
		image.height = uint32(view, 16);
		image.offsets = uint32Array(view, 20, 16);
		image.engths = uint32Array(view, 20 + 16 * 4, 16);
		image.palette = readBGRAArray(view, 20 + 32 * 4, 256);
		var mipLevel = image.mipLevels - 1;
		var width = image.width >> mipLevel;
		var height = image.height >> mipLevel;
		var offset = image.offsets[mipLevel];
		var size = image.lengths[mipLevel];
//		console.info(blpFormat(image));
		switch (blpFormat(image)) {
		case BLP_FORMAT_PALETTED_NO_ALPHA:
			return blp2_convert_paletted_no_alpha(view, offset, width, height, image);
		case BLP_FORMAT_PALETTED_ALPHA_1:
			return blp2_convert_paletted_alpha1(view, offset, width, height, image);
		case BLP_FORMAT_PALETTED_ALPHA_4:
			return blp2_convert_paletted_alpha4(view, offset, width, height, image);
		case BLP_FORMAT_PALETTED_ALPHA_8:
			return blp2_convert_paletted_alpha8(view, offset, width, height, image);
		case BLP_FORMAT_RAW_BGRA:
			return blp2_convert_raw_bgra(view, offset, width, height, image);
		case BLP_FORMAT_DXT1_NO_ALPHA:
		case BLP_FORMAT_DXT1_ALPHA_1:
			return blp2_convert_dxt(view, offset, size, width, height, image);
		case BLP_FORMAT_DXT3_ALPHA_4:
			System.err.println("'BLP_FORMAT_DXT3_ALPHA_4'");
		case BLP_FORMAT_DXT3_ALPHA_8:
			return blp2_convert_dxt3_alpha8(view, offset, size, width, height, image);
		case BLP_FORMAT_DXT5_ALPHA_8:
			return blp2_convert_dxt5_alpha8(view, offset, size, width, height, image);
		}
		return null;
	}

	// node.js have no native ImageData
	image createImageData(int width, int height) {
		return new image(new byte[width * height * 4], width, height);
	}

	image blp2_convert_paletted_no_alpha(DataView view, int offset, int width, int height, image image) {
		var img = createImageData(width, height);
		var imgIndex = 0;
		for (var y = 0; y < height; ++y) {
			for (var x = 0; x < width; ++x) {
				var data = view.getUint8(offset++);
				var bgra = image.palette[data];
				img.data[imgIndex++] = (byte) bgra[2];
				img.data[imgIndex++] = (byte) bgra[1];
				img.data[imgIndex++] = (byte) bgra[0];
				img.data[imgIndex++] = (byte) 0xFF;
			}
		}
		return img;
	}

	image blp2_convert_paletted_alpha1(DataView view, int offset, int width, int height, image image) {
		var img = createImageData(width, height);
		var imgIndex = 0;
		var alphaOffset = offset + width * height;
		var counter = 0;
		for (var y = 0; y < height; ++y) {
			for (var x = 0; x < width; ++x) {
				var data = view.getUint8(offset++);
				var bgra = image.palette[data];
				img.data[imgIndex++] = (byte) bgra[2];
				img.data[imgIndex++] = (byte) bgra[1];
				img.data[imgIndex++] = (byte) bgra[0];
				img.data[imgIndex++] = (byte) (((uint8(view, alphaOffset) & (1 << counter)) != 0) ? 0xFF : 0x00);
				counter++;
				if (counter == 8) {
					alphaOffset++;
					counter = 0;
				}
			}
		}
		return img;
	}

	image blp2_convert_paletted_alpha4(DataView view, int offset, int width, int height, image image) {
		var img = createImageData(width, height);
		var imgIndex = 0;
		var alphaOffset = offset + width * height;
		var counter = 0;
		for (var y = 0; y < height; ++y) {
			for (var x = 0; x < width; ++x) {
				var data = view.getUint8(offset++);
				var bgra = image.palette[data];
				img.data[imgIndex++] = (byte) bgra[2];
				img.data[imgIndex++] = (byte) bgra[1];
				img.data[imgIndex++] = (byte) bgra[0];
				var alpha = (uint8(view, alphaOffset) >> counter) & 0xF;
				img.data[imgIndex++] = (byte) ((alpha << 4) | alpha);
				counter += 4;
				if (counter == 8) {
					alphaOffset++;
					counter = 0;
				}
			}
		}
		return img;
	}

	image blp2_convert_paletted_alpha8(DataView view, int offset, int width, int height, image image) {
		var img = createImageData(width, height);
		var imgIndex = 0;
		var alphaOffset = offset + width * height;
		for (var y = 0; y < height; ++y) {
			for (var x = 0; x < width; ++x) {
				var data = view.getUint8(offset++);
				var bgra = image.palette[data];
				img.data[imgIndex++] = (byte) bgra[2];
				img.data[imgIndex++] = (byte) bgra[1];
				img.data[imgIndex++] = (byte) bgra[0];
				img.data[imgIndex++] = (byte) uint8(view, alphaOffset++);
			}
		}
		return img;
	}

	image blp2_convert_raw_bgra(DataView view, int offset, int width, int height, image image) {
		var img = createImageData(width, height);
		var imgIndex = 0;
		for (var y = 0; y < height; ++y) {
			for (var x = 0; x < width; ++x) {
				var b = view.getUint8(offset++);
				var g = view.getUint8(offset++);
				var r = view.getUint8(offset++);
				var a = view.getUint8(offset++);
				img.data[imgIndex++] = (byte) r;
				img.data[imgIndex++] = (byte) g;
				img.data[imgIndex++] = (byte) b;
				img.data[imgIndex++] = (byte) a;
			}
		}
		return img;
	}

	image blp2_convert_dxt(DataView view, int offset, int size, int width, int height, image image) {
		var dxtData = new DataView(view.buffer, offset, size);
		return decodeDxt1(dxtData, width, height, createImageData(width, height));
	}

	image blp2_convert_dxt3_alpha8(DataView view, int offset, int size, int width, int height, image image) {
		var dxtData = new DataView(view.buffer, offset, size);
		return decodeDxt3(dxtData, width, height, createImageData(width, height));
	}

	image blp2_convert_dxt5_alpha8(DataView view, int offset, int size, int width, int height, image image) {
		var dxtData = new DataView(view.buffer, offset, size);
		return decodeDxt5(dxtData, width, height, createImageData(width, height));
	}
}
