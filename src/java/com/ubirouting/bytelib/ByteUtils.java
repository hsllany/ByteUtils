package java.com.ubirouting.bytelib;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
	private Map<Class<?>, List<Token>> complieList;

	private ByteUtils() {
		complieList = Collections.synchronizedMap(new HashMap<Class<?>, List<Token>>());
	}

	private List<Token> complie(ToBytes j) throws ToByteComplieException {

		/**
		 * If j has been complied, than return the compile result immediately.
		 */
		if (complieList.containsKey(j.getClass()))
			return complieList.get(j.getClass());

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

		complieList.put(j.getClass(), tokenList);

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
	private ByteBuffer export(ToBytes j, List<Token> tokenList) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		ByteBuffer exportBuffer = ByteBuffer.allocate(sizeOfTokens(tokenList));

		for (Token t : tokenList) {
			char type = t.type;
			String fieldString = t.field;

			Method getMethod = j.getClass().getMethod("get" + fieldString);
			assembleToByteBuffer(exportBuffer, type, getMethod, j);

		}

		return exportBuffer;
	}

	private int assembleToByteBuffer(ByteBuffer buffer, char type, Method getMethod, ToBytes j)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		switch (type) {
		case 'i':
			int iValue = ((Integer) getMethod.invoke(j)).intValue();
			buffer.putInt(iValue);
			return Integer.BYTES;
		case 'j':
			long lValue = ((Long) getMethod.invoke(j)).longValue();
			buffer.putLong(lValue);
			return Long.BYTES;
		case 'z':
			boolean zValue = ((Boolean) getMethod.invoke(j)).booleanValue();
			buffer.put((byte) (zValue ? 1 : 0));
			return 1;
		case 'b':
			byte bValue = ((Byte) getMethod.invoke(j)).byteValue();
			buffer.put(bValue);
			return Byte.BYTES;
		case 'c':
			char cValue = ((Character) getMethod.invoke(j)).charValue();
			buffer.putChar(cValue);
			return Character.BYTES;
		case 's':
			short sValue = ((Short) getMethod.invoke(j)).shortValue();
			buffer.putShort(sValue);
			return Short.BYTES;
		case 'f':
			float fValue = ((Float) getMethod.invoke(j)).floatValue();
			buffer.putFloat(fValue);
			return Float.BYTES;
		case 'd':
			double dValue = ((Double) getMethod.invoke(j)).doubleValue();
			buffer.putDouble(dValue);
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
	 * To convert object field to ByteBuffer where position is set to 0, and
	 * limit is set to capacity.
	 * 
	 * @param object
	 * @return
	 * @throws ToByteComplieException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InvocationTargetException
	 */
	public ByteBuffer toByteBuffer(ToBytes object) throws ToByteComplieException, IllegalArgumentException,
			IllegalAccessException, NoSuchMethodException, SecurityException, InvocationTargetException {
		List<Token> tokenList = complie(object);
		return (ByteBuffer) export(object, tokenList).flip();
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
		return toByteBuffer(object).array();
	}

	static class Token {
		public char type;
		public String field;
	}

}
