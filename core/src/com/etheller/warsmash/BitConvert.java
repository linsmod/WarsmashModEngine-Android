package com.etheller.warsmash;

import java.io.IOException;
import java.io.InputStream;

public class BitConvert {
	/**
	 * int转字节数组 大端模式
	 */
	public static byte[] int2bytesBE(int x) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte) (x >> 24);
		bytes[1] = (byte) (x >> 16);
		bytes[2] = (byte) (x >> 8);
		bytes[3] = (byte) x;
		return bytes;
	}

	/**
	 * int转字节数组 小端模式
	 */
	public static byte[] int2bytesLE(int x) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte) x;
		bytes[1] = (byte) (x >> 8);
		bytes[2] = (byte) (x >> 16);
		bytes[3] = (byte) (x >> 24);
		return bytes;
	}
	public static int bytes2intBE(InputStream src) throws IOException {
		byte[] bytes = new byte[4];
		src.read(bytes);
		return bytes2intBE(bytes);
	}
	/**
	 * 字节数组转int 大端模式
	 */
	public static int bytes2intBE(byte[] bytes) {
		int x = 0;
		for (int i = 0; i < 4; i++) {
			x <<= 8;
			int b = bytes[i] & 0xFF;
			x |= b;
		}
		return x;
	}
	public static int bytes2intLE(InputStream src) throws IOException {
		byte[] bytes = new byte[4];
		src.read(bytes);
		return bytes2intLE(bytes);
	}
	/**
	 * 字节数组转int 小端模式
	 */
	public static int bytes2intLE(byte[] bytes) {
		int x = 0;
		for (int i = 0; i < 4; i++) {
			int b = (bytes[i] & 0xFF) << (i * 8);
			x |= b;
		}
		return x;
	}

	/**
	 * 字节数组转int 大端模式
	 */
	public static int bytes2intLE(byte[] bytes, int byteOffset, int byteCount) {
		int intValue = 0;
		for (int i = byteOffset; i < (byteOffset + byteCount); i++) {
			intValue |= (bytes[i] & 0xFF) << (8 * (i - byteOffset));
		}
		return intValue;
	}

	/**
	 * 字节数组转int 小端模式
	 */
	public static int bytes2intBE(byte[] bytes, int byteOffset, int byteCount) {
		int intValue = 0;
		for (int i = byteOffset; i < (byteOffset + byteCount); i++) {
			intValue <<= 8;
			int b = bytes[i] & 0xFF;
			intValue |= b;
		}
		return intValue;
	}
}
