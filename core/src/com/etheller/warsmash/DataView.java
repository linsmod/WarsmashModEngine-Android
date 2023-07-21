package com.etheller.warsmash;

import org.apache.commons.compress.utils.ByteUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class DataView extends ByteArrayInputStream {
	public final byte[] buffer;
	private final DataInputStream is;

	public DataView(byte[] buf) {
		super(buf);
		this.buffer=buf;
		this.is = new DataInputStream(this);
	}
	public DataView(byte[] buf,int offset,int size){
		this(Arrays.copyOfRange(buf,offset,offset+size));
	}

	public byte get(int offset){
		return buffer[offset];
	}

	public int getUint8(int i) {
		this.pos = i;
		return read();
	}

	public int getUint32(int offset, boolean b) {
		this.pos = offset;
		try {
			return b ? BitConvert.bytes2intLE(this) : BitConvert.bytes2intBE(this);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public String readString(int offset,int len){
		try {
			this.pos = pos;
			return readString(this,len);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	private static String readString(InputStream in, int len) throws IOException {
		byte[] temp = new byte[len];
		for (int i = 0; i < temp.length; i++) {
			int b = in.read();
			if (b == -1)
				throw new EOFException();
			temp[i] = (byte)b;
		}
		return new String(temp, StandardCharsets.UTF_8);
	}
}
