package com.ubirouting.bytelib;

import java.lang.reflect.Method;

class Utils {
	private Utils() {
	};

	static Method getAccessibleMethod(String methodName, Class<?> clazz) {
		try {
			Method ms = clazz.getDeclaredMethod(methodName);
			ms.setAccessible(true);
			return ms;
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
}
