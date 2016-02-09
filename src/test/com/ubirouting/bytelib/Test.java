package test.com.ubirouting.bytelib;

import java.util.Arrays;

import com.ubirouting.bytelib.ToByte;
import com.ubirouting.bytelib.ByteUtils;
import com.ubirouting.bytelib.ToByteComplieException;
import com.ubirouting.bytelib.ToBytes;

public class Test implements ToBytes {

	private int aa;
	private int mHaha;
	private float f = 123.456f;
	private double d = 34567;
	private short s = 123;
	private long l = 1234567890;

	public Test() {
		aa = 1;
		mHaha = 48;
	}

	public String format() {
		return "i[getAA]i[getHaha]z[getBoolean]f[getF]d[getD]s[getS]j[getL]";
	}

	@ToByte(order = 1)
	public int getAA() {
		return 3;
	}

	@ToByte(order = 2)
	private int getHaha() {
		return mHaha;
	}

	@ToByte(order = 4)
	public boolean getBoolean() {
		return true;
	}

	@ToByte(order = 4)
	public float getF() {
		return f;
	}

	@ToByte(order = 4)
	public double getD() {
		return d;
	}

	@ToByte(order = 4)
	public short getS() {
		return s;
	}

	@ToByte(order = 4)
	public long getL() {
		return l;
	}

	@ToByte(order = 0)
	public TestClass2 haha() {
		return new TestClass2();
	}

	public static void main(String[] args) {
		Test t = new Test();
		try {
			byte[] b = ByteUtils.getInstance().toBytes(t);
			System.out.println(Arrays.toString(b));
		} catch (ToByteComplieException e) {
			e.printStackTrace();
		}
	}
}
