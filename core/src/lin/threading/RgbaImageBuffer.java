package lin.threading;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class RgbaImageBuffer {
    private final int[] rgbArray;

    public RgbaImageBuffer(int[] rgbArray, int width, int height, int bytesPerPixel) {
        this.totalBytes = width * height * bytesPerPixel;
        this.rgbArray = rgbArray == null ? new int[width * height] : rgbArray;
        this.buffer = ByteBuffer.allocateDirect(totalBytes)
                .order(ByteOrder.nativeOrder());
        buffer.mark();
        this.bytes = new byte[totalBytes];
    }

    private final byte[] bytes;
    private final int totalBytes;
    private final ByteBuffer buffer;

    public byte[] getBytes() {
        return bytes;
    }

    public int[] getRgb() {
        return this.rgbArray;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    public int pixelRead(int offset, int x, int y, int pixel) {
        this.bytes[offset * 4 + 0] = (byte) ((pixel >> 16) & 0xFF);
        this.bytes[offset * 4 + 1] = (byte) ((pixel >> 8) & 0xFF);
        this.bytes[offset * 4 + 2] = (byte) ((pixel >> 0) & 0xFF);
        this.bytes[offset * 4 + 3] = (byte) ((pixel >> 24) & 0xFF);
        return pixel;
    }

    public void complete() {
        buffer.put(this.bytes);
        buffer.flip();
        int r = buffer.remaining();
    }
}
