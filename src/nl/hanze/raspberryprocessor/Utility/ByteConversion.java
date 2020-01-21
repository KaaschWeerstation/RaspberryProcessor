package nl.hanze.raspberryprocessor.Utility;

import java.nio.ByteBuffer;

public class ByteConversion {
    private ByteBuffer buffer;
    private ByteBuffer intBuffer;

    public ByteConversion() {
        this.buffer = ByteBuffer.allocate(Long.BYTES);
        this.intBuffer = ByteBuffer.allocate(Integer.BYTES);
    }

    public byte[] longToBytes(long x) {
        buffer.putLong(0, x);
        return buffer.array();
    }

    public long bytesToLong(byte[] bytes) {
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip
        return buffer.getLong();
    }

    public byte[] intToBytes(int x) {
        intBuffer.putInt(0,x );
        return intBuffer.array();
    }
}