package com.ubirouting.bytelib;

import java.lang.ref.WeakReference;

class Token implements Comparable<Token> {
	public TokenType type;
	public String methodName;
	public int order;
	private WeakReference<Object> objectRef;

	private Class<? extends Bytable> objectClass;

	public Token(TokenType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "[type=" + this.type.toString() + ", methodName=" + methodName + ",order=" + order + "]";

	}

	Class<? extends Bytable> getObjectClass() {
		return objectClass;
	}

	void setObjectClass(Class<? extends Bytable> objectClass) {
		this.objectClass = objectClass;
	}

	Object getObject() {
		if (objectRef != null)
			return objectRef.get();
		return null;
	}

	void setObject(Object j) {
		if (type == TokenType.BooleanArray || type == TokenType.ByteArray || type == TokenType.CharArray
				|| type == TokenType.ShortArray || type == TokenType.IntegerArray || type == TokenType.LongArray
				|| type == TokenType.DoubleArray || type == TokenType.FloatArray || type == TokenType.Object)
			objectRef = new WeakReference<Object>(j);
	}

	static enum TokenType {
		Boolean, Byte, Char, Short, Integer, Long, Float, Double, Object, BooleanArray, ByteArray, CharArray, ShortArray, IntegerArray, LongArray, FloatArray, DoubleArray;
	}

	@Override
	public int compareTo(Token o) {
		return order - o.order;
	}
}
