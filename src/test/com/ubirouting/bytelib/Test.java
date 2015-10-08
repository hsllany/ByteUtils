package test.com.ubirouting.bytelib;

import com.ubirouting.bytelib.ByteUtils;
import com.ubirouting.bytelib.ToByteComplieException;

public class Test {
	public static void main(String[] args) {
		TestClass testObj = new TestClass();
		TestClass testObj2 = new TestClass();

		try {
			byte[] bytes = ByteUtils.getInstance().toBytes(testObj);
			byte[] bytes2 = ByteUtils.getInstance().toBytes(testObj2);
			for (int i = 0; i < bytes.length; i++) {
				System.out.println(bytes[i] + "");
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (ToByteComplieException e) {
			e.printStackTrace();
		}
	}
}
