package com.ubirouting.bytelib;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;

public class ByteLibTest {

	@Test
	public void testExport() {
		test.com.ubirouting.bytelib.Test t = new test.com.ubirouting.bytelib.Test();
		try {
			ByteUtils.getInstance().toByteBuffer(t);
		} catch (ToByteComplieException e) {
			e.printStackTrace();
		}
	}

	public void testComplie() {
		try {
			List<Token> list = ByteUtils.getInstance().complieOrGet(test.com.ubirouting.bytelib.Test.class);
		} catch (ToByteComplieException e) {
			e.printStackTrace();
		}
	}

	public void testMethod() {
		Method[] ms = test.com.ubirouting.bytelib.Test.class.getDeclaredMethods();
		for (Method m : ms) {
			if (m.getReturnType() == long.class) {
			}
		}
	}

}
