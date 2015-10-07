package java.com.ubirouting.bytelib;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import test.com.ubirouting.bytelib.TestClass;

/**
 * Convert a object to bytes. Single Instance invoke by {@code getInstance()}
 * <br/>
 * To use this class, Object should implements {@code toBytes} interface, and
 * override the {@code format()} function. Then you can invoke the
 * {@code toBytes()} to convert object to byte array in the order you set up.
 * 
 * @author YangTao
 *
 */
public class ByteUtils {

	private static ByteUtils instance = null;

	public static ByteUtils getInstance() {
		synchronized (ByteUtils.class) {
			if (instance == null)
				instance = new ByteUtils();
		}

		return instance;
	}

	/**
	 * Map which stores the complies result.
	 */
	private Map<Class<?>, List<Token>> mComplieList;

	private ByteUtils() {
		mComplieList = Collections.synchronizedMap(new HashMap<Class<?>, List<Token>>());
	}

	private List<Token> complie(ToBytes j) throws ToByteComplieException {

		/**
		 * If j has been complied, than return the compile result immediately.
		 */
		if (mComplieList.containsKey(j.getClass()))
			return mComplieList.get(j.getClass());

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

		mComplieList.put(j.getClass(), tokenList);

		return tokenList;
	}

	/**
	 * Export the ToBytes object to byte array;
	 * 
	 * @param j
	 * @param tokenList
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	private byte[] export(ToBytes j, List<Token> tokenList) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		byte[] export = new byte[sizeOfTokens(tokenList)];

		int exportIndex = 0;

		for (Token t : tokenList) {
			char type = t.type;
			String fieldString = t.field;

			Method getMethod = j.getClass().getMethod("get" + fieldString);
			int increment = assembleToByte(export, exportIndex, type, getMethod, j);
			exportIndex += increment;

		}

		return export;
	}

	private int assembleToByte(byte[] des, int start, char type, Method getMethod, ToBytes j)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		switch (type) {
		case 'i':
			int iValue = ((Integer) getMethod.invoke(j)).intValue();
			Assembler.putIntToBytes(des, start, iValue);
			return Integer.BYTES;
		case 'j':
			long lValue = ((Long) getMethod.invoke(j)).longValue();
			Assembler.putLongToBytes(des, start, lValue);
			return Long.BYTES;
		case 'z':
			boolean zValue = ((Boolean) getMethod.invoke(j)).booleanValue();
			Assembler.putBooleanToBytes(des, start, zValue);
			return 1;
		case 'b':
			byte bValue = ((Byte) getMethod.invoke(j)).byteValue();
			des[start] = bValue;
			return Byte.BYTES;
		case 'c':
			char cValue = ((Character) getMethod.invoke(j)).charValue();
			Assembler.putCharToBytes(des, start, cValue);
			return Character.BYTES;
		case 's':
			short sValue = ((Short) getMethod.invoke(j)).shortValue();
			Assembler.putShortToBytes(des, start, sValue);
			return Short.BYTES;
		case 'f':
			float fValue = ((Float) getMethod.invoke(j)).floatValue();
			Assembler.putFloatToBytes(des, start, fValue);
			return Float.BYTES;
		case 'd':
			double dValue = ((Double) getMethod.invoke(j)).doubleValue();
			Assembler.putDoubleToBytes(des, start, dValue);
			return Double.BYTES;
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
			return Integer.BYTES;
		case 'f':
			return Float.BYTES;
		case 'b':
			return Byte.BYTES;
		case 'c':
			return Character.BYTES;
		case 'z':
			return 1;
		case 's':
			return Short.BYTES;
		case 'j':
			return Long.BYTES;
		case 'd':
			return Double.BYTES;
		default:
			return 0;
		}
	}

	/**
	 * To convert object to byte array.
	 * 
	 * @param object
	 *            which implements {@code toBytes} interface
	 * @return byte array following the format you set up in {@code format()}
	 * @throws ToByteComplieException
	 *             wrong format string.
	 * @throws IllegalArgumentException
	 *             no such field in your object.
	 * @throws IllegalAccessException
	 *             can't access the field in your object
	 * @throws InvocationTargetException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 */
	public byte[] toBytes(ToBytes object) throws ToByteComplieException, IllegalArgumentException,
			IllegalAccessException, NoSuchMethodException, SecurityException, InvocationTargetException {
		List<Token> tokenList = complie(object);
		return export(object, tokenList);
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
