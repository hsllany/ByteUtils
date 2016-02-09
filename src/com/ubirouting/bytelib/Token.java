package com.ubirouting.bytelib;

class Token implements Comparable<Token> {
	public TokenType type;
	public String methodName;
	public int order;

	private Class<? extends ToBytes> objectClass;

	public Token(TokenType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "[type=" + this.type.toString() + ", methodName=" + methodName + ",order=" + order + "]";

	}

	Class<? extends ToBytes> getObjectClass() {
		return objectClass;
	}

	void setObjectClass(Class<? extends ToBytes> objectClass) {
		this.objectClass = objectClass;
	}

	public static enum TokenType {
		Boolean, Byte, Char, Short, Integer, Long, Float, Double, Object;

		private String tokenContent;

		public void setTokenContent(String tokenContent) {
			if (this == TokenType.Object)
				this.tokenContent = tokenContent;
			else
				throw new UnsupportedOperationException();
		}
	}

	@Override
	public int compareTo(Token o) {
		return order - o.order;
	}
}
