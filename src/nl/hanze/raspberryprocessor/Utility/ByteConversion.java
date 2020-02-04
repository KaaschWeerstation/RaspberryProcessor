package nl.hanze.raspberryprocessor.Utility;

import java.nio.ByteBuffer;

public class ByteConversion {
    private ByteBuffer buffer;
    private ByteBuffer intBuffer;
    private ByteBuffer shortBuffer;
    private ByteBuffer byteBuffer;
    private ByteBuffer threeBytesBuffer;

    public ByteConversion() {
        this.buffer = ByteBuffer.allocate(Long.BYTES);
        this.intBuffer = ByteBuffer.allocate(Integer.BYTES);
        this.shortBuffer = ByteBuffer.allocate(Short.BYTES);
        this.byteBuffer = ByteBuffer.allocate(Byte.BYTES);
        this.threeBytesBuffer = ByteBuffer.allocate(3);
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

    public byte[] shortToBytes(short x) {
        shortBuffer.putShort(0, x);
        return shortBuffer.array();
    }

    public byte[] byteToBytes(byte x) {
        byteBuffer.put(0, x);
        return byteBuffer.array();
    }

//    public byte[] threeByteToBytes(int x) {
//        byteBuffer.put(0, x);
//    }
}