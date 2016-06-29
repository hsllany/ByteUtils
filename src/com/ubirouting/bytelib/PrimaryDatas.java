package com.ubirouting.bytelib;

/**
 * primary type convention utilization
 *
 * @author YangTao
 * @version 1.0
 */
public class PrimaryDatas {

    private PrimaryDatas() {
        throw new UnsupportedOperationException("can't create this object");
    }

    public static byte[] s2b(short x) {
        byte[] bytes = new byte[2];
        s2b(x, bytes, 0);
        return bytes;
    }

    public static void s2b(short x, byte[] bytes, int start) {
        bytes[start] = (byte) (x >> 8);
        bytes[start + 1] = (byte) (x);
    }

    public static byte[] i2b(int x) {
        byte[] y = new byte[4];
        i2b(x, y, 0);
        return y;
    }

    public static void i2b(int x, byte[] bytes, int start) {
        bytes[start + 3] = (byte) x;
        bytes[start + 2] = (byte) (x >> 8);
        bytes[start + 1] = (byte) (x >> 16);
        bytes[start] = (byte) (x >> 24);
    }

    public static byte[] l2b(long x) {
        byte[] byteNum = new byte[8];
        l2b(x, byteNum, 0);
        return byteNum;
    }

    public static void l2b(long x, byte[] bytes, int start) {
        for (int ix = 0; ix < 8; ++ix) {
            int offset = 64 - (ix + 1) * 8;
            bytes[ix + start] = (byte) ((x >> offset) & 0xff);
        }
    }


    public static byte[] f2b(float f) {
        byte[] bytes = new byte[4];
        f2b(f, bytes, 0);
        return bytes;
    }

    public static void f2b(float f, byte[] bytes, int start) {
        i2b(Float.floatToIntBits(f), bytes, start);
    }

    public static void d2b(double x, byte[] bytes, int start) {
        long l = Double.doubleToLongBits(x);
        l2b(l, bytes, start);
    }

    public static byte[] d2b(double x) {
        byte[] bytes = new byte[8];
        d2b(x, bytes, 0);
        return bytes;
    }

    /**
     * default byte to integer
     *
     * @param bytes , {@code byte[]}
     * @param start
     * @return
     */
    public final static int b2i(byte[] bytes, int start) {
        int r = 0;

        r = (bytes[start + 3] & 0xff) | ((bytes[start + 2] & 0xff) << 8)
                | ((bytes[start + 1] & 0xff) << 16) | ((bytes[start]) << 24);

        return r;
    }

    public static long b2l(byte[] byteNum, int start) {
        long num = 0;
        for (int ix = 0; ix < 8; ++ix) {
            num <<= 8;
            num |= (byteNum[start + ix] & 0xff);
        }
        return num;
    }

    /**
     * convert bytes which was converted by ShiTu ByteArrayConvertor.s2b, to
     * short
     *
     * @param bytes , {@code byte[]}
     * @param start
     * @return
     */
    public final static short b2s(byte[] bytes, int start) {
        return (short) ((bytes[start] << 8) | (bytes[start + 1] & 0xff));
    }

    /**
     * change byte array to float
     *
     * @param bytes , {@code byte[]}
     * @param start
     * @return
     */
    public final static float b2f(byte[] bytes, int start) {
        int i = b2i(bytes, start);
        return Float.intBitsToFloat(i);
    }

    public final static double b2d(byte[] bytes, int start) {
        long l = b2l(bytes, start);
        return Double.longBitsToDouble(l);
    }

    /**
     * convert short to float
     *
     * @param shortValue
     * @return floatValue
     */
    public final static float s2f(short shortValue) {
        float divide = 100;
        float returnValue = 0;

        // get sign
        short sign = (short) (shortValue & 0x8000);

        if (sign != 0) {
            int integer = (shortValue >> 8) & 0x7f;
            int decimal = shortValue & 0xff;
            returnValue = -integer - decimal / divide;

        } else {
            int integer = shortValue >> 8;
            int decimal = shortValue & 0xff;
            returnValue = integer + decimal / divide;

        }

        return returnValue;
    }

    /**
     * convert byte to String
     *
     * @param bytes
     * @param start
     * @param end
     * @return
     */
    public final static String b2String(byte[] bytes, int start, int end) {
        byte[] transferBytes = new byte[end - start];
        for (int i = start; i < end; i++) {
            transferBytes[i - start] = bytes[i];
        }

        return new String(transferBytes);
    }
}
