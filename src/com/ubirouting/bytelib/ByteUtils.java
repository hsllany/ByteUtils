package com.ubirouting.bytelib;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ubirouting.bytelib.Token.TokenType;

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
public final class ByteUtils {

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

	/**
	 * make token list for object
	 * 
	 * @param j
	 * @return
	 * @throws ToByteComplieException
	 */
	List<Token> complieOrGet(Class<? extends ToBytes> c) throws ToByteComplieException {

		/**
		 * If j has been complied, than return the compile result immediately.
		 */
		if (complieList.containsKey(c))
			return complieList.get(c);

		List<Token> tokenList = new LinkedList<>();
		Method[] methods = c.getDeclaredMethods();

		for (Method method : methods) {
			ToByte toByte = method.getAnnotation(ToByte.class);
			if (toByte != null) {
				Token t = buildToken(c, method);
				t.order = toByte.order();
				tokenList.add(t);
			}
		}

		Collections.sort(tokenList);
		System.out.println(tokenList.toString());
		complieList.put(c, tokenList);
		return tokenList;
	}

	@SuppressWarnings("unchecked")
	private Token buildToken(Class<? extends ToBytes> c, Method m) throws ToByteComplieException {
		Class<?> returnType = m.getReturnType();
		Token t = null;
		if (returnType == int.class) {
			t = new Token(TokenType.Integer);
		} else if (returnType == long.class) {
			t = new Token(TokenType.Long);
		} else if (returnType == boolean.class) {
			t = new Token(TokenType.Boolean);
		} else if (returnType == byte.class) {
			t = new Token(TokenType.Byte);
		} else if (returnType == short.class) {
			t = new Token(TokenType.Short);
		} else if (returnType == float.class) {
			t = new Token(TokenType.Float);
		} else if (returnType == double.class) {
			t = new Token(TokenType.Double);
		} else if (returnType == char.class) {
			t = new Token(TokenType.Char);
		} else if (returnType == void.class) {
			// ignore
			throw new ToByteComplieException(c.getName() + "." + m.getName() + "() must have a return value!");
		} else {
			t = new Token(TokenType.Object);
			if (Utils.isToBytesClass(returnType)) {
				t.setObjectClass((Class<? extends ToBytes>) returnType);
				complieOrGet((Class<? extends ToBytes>) returnType);
			} else {
				throw new ToByteComplieException(m.getName() + "() return value is not a ToByte class!");
			}

		}

		t.methodName = m.getName();

		return t;
	}

	/**
	 * Export the ToBytes object to byte array;
	 * 
	 * @param j
	 * @param tokenList
	 * @return
	 * @throws SecurityException
	 * @throws ToByteComplieException
	 */
	private void export(ByteBuffer byteBuffer, ToBytes j) throws ToByteComplieException {
		List<Token> tokenList = complieOrGet(j.getClass());

		for (Token t : tokenList) {
			// char type = t.type;
			String fieldString = t.methodName;

			Method getMethod;

			getMethod = Utils.getMethod(fieldString, j.getClass());
			getMethod.setAccessible(true);

			try {
				assembleToByteBuffer(byteBuffer, t.type, getMethod, j);
			} catch (IllegalAccessException e) {
				throw new ToByteComplieException(getMethod.getName() + " can't be accessible");
			} catch (IllegalArgumentException e) {
				throw new ToByteComplieException(getMethod.getName() + " argument is not void");
			} catch (InvocationTargetException e) {
				throw new ToByteComplieException(getMethod.getName() + " can't be accessible for this object");
			} catch (SecurityException e) {
				throw new ToByteComplieException(getMethod.getName() + " is not safe.");
			}
		}

	}

	private int assembleToByteBuffer(ByteBuffer buffer, TokenType type, Method getMethod, ToBytes j)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException,
			ToByteComplieException {
		switch (type) {
		case Integer:
			int iValue = ((Integer) getMethod.invoke(j)).intValue();
			buffer.putInt(iValue);
			return Integer.BYTES;
		case Long:
			long lValue = ((Long) getMethod.invoke(j)).longValue();
			buffer.putLong(lValue);
			return Long.BYTES;
		case Boolean:
			boolean zValue = ((Boolean) getMethod.invoke(j)).booleanValue();
			buffer.put((byte) (zValue ? 1 : 0));
			return 1;
		case Byte:
			byte bValue = ((Byte) getMethod.invoke(j)).byteValue();
			buffer.put(bValue);
			return Byte.BYTES;
		case Char:
			char cValue = ((Character) getMethod.invoke(j)).charValue();
			buffer.putChar(cValue);
			return Character.BYTES;
		case Short:
			short sValue = ((Short) getMethod.invoke(j)).shortValue();
			buffer.putShort(sValue);
			return Short.BYTES;
		case Float:
			float fValue = ((Float) getMethod.invoke(j)).floatValue();
			buffer.putFloat(fValue);
			return Float.BYTES;
		case Double:
			double dValue = ((Double) getMethod.invoke(j)).doubleValue();
			buffer.putDouble(dValue);
			return Double.BYTES;
		case Object:
			ToBytes obj = (ToBytes) getMethod.invoke(j);
			if (obj == null) {
				int size = byteSizeOfToBytes(obj.getClass());
				byte[] b = new byte[size];
				buffer.put(b);
			} else {
				export(buffer, obj);
			}
		default:
			return 0;
		}
	}

	int byteSizeOfToBytes(Class<? extends ToBytes> c) throws ToByteComplieException {
		List<Token> listToken = complieOrGet(c);
		int size = 0;

		for (Token t : listToken) {
			size += sizeOfType(t);
		}

		return size;
	}

	private int sizeOfType(Token tk) throws ToByteComplieException {
		switch (tk.type) {
		case Integer:
			return Integer.BYTES;
		case Float:
			return Float.BYTES;
		case Byte:
			return Byte.BYTES;
		case Char:
			return Character.BYTES;
		case Boolean:
			return 1;
		case Short:
			return Short.BYTES;
		case Long:
			return Long.BYTES;
		case Double:
			return Double.BYTES;
		case Object:
			Class<? extends ToBytes> c = tk.getObjectClass();
			return byteSizeOfToBytes(c);
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
	public ByteBuffer toByteBuffer(ToBytes object) throws ToByteComplieException {
		ByteBuffer exportBuffer = ByteBuffer.allocate(byteSizeOfToBytes(object.getClass()));

		export(exportBuffer, object);

		return (ByteBuffer) exportBuffer.flip();
	}

	/**
	 * return the size of bytes
	 * 
	 * @param object
	 * @return
	 * @throws ToByteComplieException
	 */
	public int byteSize(ToBytes object) throws ToByteComplieException {
		return byteSizeOfToBytes(object.getClass());
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
	public byte[] toBytes(ToBytes object) throws ToByteComplieException {
		return toByteBuffer(object).array();
	}

}
