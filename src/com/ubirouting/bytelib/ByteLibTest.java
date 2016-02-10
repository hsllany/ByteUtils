package com.ubirouting.bytelib;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.Test;

import test.com.ubirouting.bytelib.TestClass;

public class ByteLibTest {

	@Test
	public void testExport() {
		TestClass t = new TestClass();
		try {
			byte[] output = ByteUtils.getInstance().toBytes(t);
			System.out.println(Arrays.toString(output));
		} catch (ToByteComplieException e2) {
			e2.printStackTrace();
		}
	}

	public void testComplie() {
		// try {
		// List<Token> list =
		// ByteUtils.getInstance().complieOrGet(test.com.ubirouting.bytelib.Test.class);
		// } catch (ToByteComplieException e) {
		// e.printStackTrace();
		// }
	}

	public void testMethod() {
		Method[] ms = test.com.ubirouting.bytelib.TestClass.class.getDeclaredMethods();
		for (Method m : ms) {
			if (m.getReturnType() == long.class) {
			}
		}
	}

}
