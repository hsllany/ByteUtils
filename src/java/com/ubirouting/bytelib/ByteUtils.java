package java.com.ubirouting.bytelib;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import test.com.ubirouting.bytelib.TestClass;

public class ByteUtils {

	private static ByteUtils instance = null;

	public static ByteUtils getInstance() {
		synchronized (ByteUtils.class) {
			if (instance == null)
				instance = new ByteUtils();
		}

		return instance;
	}

	private List<Token> complie(ToBytes j) throws ToByteComplieException {
		List<Token> tokenList = new LinkedList<>();

		char[] formatChars = j.format().toCharArray();
		// 0- stands token, 1-stands '[', 2-stands field;
		int lookingFor = 0;

		Token t = null;
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < formatChars.length; i++) {
			char token = formatChars[i];
			switch (lookingFor) {
			case 0:
				t = new Token();
				if (token == 'z' || token == 'b' || token == 'c' || token == 's' || token == 'i' || token == 'j'
						|| token == 'f' || token == 'd') {
					t.type = token;
					lookingFor = 1;
				} else {
					throw new ToByteComplieException();
				}
				break;
			case 1:
				if (token == '[') {
					lookingFor = 2;
				} else {
					throw new ToByteComplieException();
				}
				break;
			case 2:
				if (token != ']') {
					sb.append(token);
				} else {
					t.field = sb.toString();
					sb.setLength(0);
					tokenList.add(t);
					lookingFor = 0;
				}
				break;
			}

		}

		return tokenList;
	}

	private byte[] export(ToBytes j, List<Token> tokenList) throws IllegalArgumentException, IllegalAccessException {
		byte[] export = new byte[sizeOfTokens(tokenList)];

		Field[] allFields = j.getClass().getFields();

		int exportIndex = 0;

		for (Token t : tokenList) {
			char type = t.type;
			String fieldString = t.field;
			for (Field field : allFields) {
				if (field.getName().equals(fieldString)) {
					int increment = assembleToByte(export, exportIndex, type, field, j);
					exportIndex += increment;
					break;
				}
			}

		}

		return export;
	}

	private int assembleToByte(byte[] des, int start, char type, Field field, ToBytes j)
			throws IllegalArgumentException, IllegalAccessException {
		switch (type) {
		case 'i':
			int iValue = field.getInt(j);
			Assembler.putIntToBytes(des, start, iValue);
			return 4;
		case 'j':
			long lValue = field.getLong(j);
			Assembler.putLongToBytes(des, start, lValue);
			return 8;
		case 'z':
			boolean zValue = field.getBoolean(j);
			Assembler.putBooleanToBytes(des, start, zValue);
			return 1;
		case 'b':
			byte bValue = field.getByte(j);
			des[start] = bValue;
			return 1;
		case 'c':
			char cValue = field.getChar(j);
			Assembler.putCharToBytes(des, start, cValue);
			return 1;
		case 's':
			short sValue = field.getShort(j);
			Assembler.putShortToBytes(des, start, sValue);
			return 2;
		case 'f':
			float fValue = field.getFloat(j);
			Assembler.putFloatToBytes(des, start, fValue);
			return 4;
		case 'd':
			double dValue = field.getDouble(j);
			Assembler.putDoubleToBytes(des, start, dValue);
			return 8;
		default:
			return 0;
		}
	}

	private int sizeOfTokens(List<Token> tokenList) {
		int size = 0;

		for (Token t : tokenList) {
			size += sizeOfType(t.type);
		}

		return size;
	}

	private static int sizeOfType(char type) {
		switch (type) {
		case 'i':
		case 'f':
			return 4;
		case 'b':
		case 'c':
		case 'z':
			return 1;
		case 's':
			return 2;
		case 'j':
		case 'd':
			return 8;
		default:
			return 0;
		}
	}

	public byte[] toBytes(ToBytes j) throws ToByteComplieException, IllegalArgumentException, IllegalAccessException {
		List<Token> tokenList = complie(j);
		return export(j, tokenList);
	}

	public static class Token {
		public char type;
		public String field;
	}

	public static void main(String args[]) {
		TestClass test = new TestClass();
		try {
			ByteUtils.getInstance().complie(test);
		} catch (ToByteComplieException e) {
			e.printStackTrace();
		}
	}

}
