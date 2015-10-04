package test.com.ubirouting.bytelib;

import java.com.ubirouting.bytelib.ToBytes;

public class TestClass implements ToBytes {

	private int aa;
	private int mHaha;

	@Override
	public String format() {
		return "d[aa]d[mHaha]";
	}
}
