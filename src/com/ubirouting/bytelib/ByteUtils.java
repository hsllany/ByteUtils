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

	/*
	 * Invocation order: 1. byteSizeOfToBytes() invoke ComplieOrGet() 2.export()
	 */

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
	List<Token> complieOrGet(Bytable b) throws ToByteComplieException {

		Class<? extends Bytable> c = b.getClass();

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
				Token t = buildToken(method, b);
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
	private Token buildToken(Method m, Bytable b) throws ToByteComplieException {
		try {
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
			} else if (returnType == int[].class) {
				int[] returnValue = (int[]) m.invoke(b);
				t = new Token(TokenType.IntegerArray);
				t.setObject(returnValue);

			} else if (returnType == long[].class) {
				long[] returnValue = (long[]) m.invoke(b);
				t = new Token(TokenType.LongArray);
				t.setObject(returnValue);

			} else if (returnType == boolean[].class) {
				boolean[] returnValue = (boolean[]) m.invoke(b);
				t = new Token(TokenType.BooleanArray);
				t.setObject(returnValue);

			} else if (returnType == byte[].class) {
				byte[] returnValue = (byte[]) m.invoke(b);
				t = new Token(TokenType.ByteArray);
				t.setObject(returnValue);

			} else if (returnType == short[].class) {
				short[] returnValue = (short[]) m.invoke(b);
				t = new Token(TokenType.ShortArray);
				t.setObject(returnValue);

			} else if (returnType == float[].class) {
				float[] returnValue = (float[]) m.invoke(b);
				t = new Token(TokenType.FloatArray);
				t.setObject(returnValue);

			} else if (returnType == double[].class) {
				double[] returnValue = (double[]) m.invoke(b);
				t = new Token(TokenType.DoubleArray);
				t.setObject(returnValue);

			} else if (returnType == void.class) {
				// ignore
				throw new ToByteComplieException(
						b.getClass().getName() + "." + m.getName() + "() must have a return value!");
			} else {
				if (Utils.isToBytesClass(returnType)) {
					t = new Token(TokenType.Object);
					t.setObjectClass((Class<? extends Bytable>) returnType);

					Bytable j = (Bytable) m.invoke(b);
					complieOrGet(j);
					t.setObject(j);

				} else {
					throw new ToByteComplieException(m.getName() + "() return value is not a ToByte class!");
				}
			}

			t.methodName = m.getName();
			return t;
		} catch (IllegalAccessException e) {
			throw new ToByteComplieException(m.getName() + "() can't be accessible");
		} catch (IllegalArgumentException e) {
			throw new ToByteComplieException(m.getName() + "() argument is not void!");
		} catch (InvocationTargetException e) {
			throw new ToByteComplieException(m.getName() + "()can't be accessible for this object!");
		}

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
	private void export(ByteBuffer byteBuffer, Bytable j) throws ToByteComplieException {
		List<Token> tokenList = complieOrGet(j);

		for (Token t : tokenList) {
			// char type = t.type;
			String fieldString = t.methodName;

			Method getMethod;

			getMethod = Utils.getAccessibleMethod(fieldString, j.getClass());

			try {
				assembleToByteBuffer(byteBuffer, t, getMethod, j);
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

	private void assembleToByteBuffer(ByteBuffer buffer, Token t, Method getMethod, Bytable j)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException,
			ToByteComplieException {
		switch (t.type) {
		case Integer:
			int iValue = ((Integer) getMethod.invoke(j)).intValue();
			buffer.putInt(iValue);
			break;
		case Long:
			System.out.println("-----------------" + j.getClass() + t.toString());
			long lValue = ((Long) getMethod.invoke(j)).longValue();

			buffer.putLong(lValue);
			break;
		case Boolean:
			boolean zValue = ((Boolean) getMethod.invoke(j)).booleanValue();
			buffer.put((byte) (zValue ? 1 : 0));
			break;
		case Byte:
			byte bValue = ((Byte) getMethod.invoke(j)).byteValue();
			buffer.put(bValue);
			break;
		case Char:
			char cValue = ((Character) getMethod.invoke(j)).charValue();
			buffer.putChar(cValue);
			break;
		case Short:
			short sValue = ((Short) getMethod.invoke(j)).shortValue();
			buffer.putShort(sValue);
			break;
		case Float:
			float fValue = ((Float) getMethod.invoke(j)).floatValue();
			buffer.putFloat(fValue);
			break;
		case Double:
			double dValue = ((Double) getMethod.invoke(j)).doubleValue();
			buffer.putDouble(dValue);
			break;
		case Object:
			Bytable obj = (Bytable) getTokenObject(t, j);
			if (obj == null) {
				int size = byteSizeOfToBytes(obj);
				byte[] b = new byte[size];
				buffer.put(b);
			} else {
				export(buffer, obj);
			}
			break;
		case IntegerArray:
			int[] intR = (int[]) getTokenObject(t, j);
			for (int i = 0; i < intR.length; i++) {
				buffer.putInt(intR[i]);
			}
			break;

		case LongArray:
			long[] longR = (long[]) getTokenObject(t, j);
			for (int i = 0; i < longR.length; i++) {
				buffer.putLong(longR[i]);
			}
			break;

		case BooleanArray:
			boolean[] booleanR = (boolean[]) getTokenObject(t, j);
			for (int i = 0; i < booleanR.length; i++) {
				buffer.put((byte) (booleanR[i] ? 1 : 0));
			}
			break;
		case ByteArray:
			byte[] byteR = (byte[]) getTokenObject(t, j);
			buffer.put(byteR);

			break;
		case CharArray:
			char[] charR = (char[]) getTokenObject(t, j);
			for (int i = 0; i < charR.length; i++) {
				buffer.putChar(charR[i]);
			}
			break;
		case ShortArray:
			short[] shortR = (short[]) getTokenObject(t, j);
			for (int i = 0; i < shortR.length; i++) {
				buffer.putShort(shortR[i]);
			}
			break;
		case FloatArray:
			float[] floatR = (float[]) getTokenObject(t, j);
			for (int i = 0; i < floatR.length; i++) {
				buffer.putFloat(floatR[i]);
			}
			break;
		case DoubleArray:
			double[] doubleR = (double[]) getTokenObject(t, j);
			for (int i = 0; i < doubleR.length; i++) {
				buffer.putDouble(doubleR[i]);
			}
			break;

		default:
		}
	}

	private Object getTokenObject(Token t, Bytable j) {
		Object obj = t.getObject();
		if (obj == null) {
			Method m = Utils.getAccessibleMethod(t.methodName, j.getClass());
			try {
				obj = m.invoke(j);
				t.setObject(obj);
				return t.getObject();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		} else {
			return obj;
		}
		return obj;
	}

	int byteSizeOfToBytes(Bytable j) throws ToByteComplieException {
		List<Token> listToken = complieOrGet(j);
		int size = 0;

		for (Token t : listToken) {
			size += sizeOfType(t, j);
		}

		return size;
	}

	private int sizeOfType(Token t, Bytable j) throws ToByteComplieException {
		switch (t.type) {
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
		case IntegerArray:
			int[] a = (int[]) getTokenObject(t, j);
			return a.length * Integer.BYTES;
		case LongArray:
			long[] longR = (long[]) getTokenObject(t, j);
			return longR.length * Long.BYTES;
		case BooleanArray:
			boolean[] booleanR = (boolean[]) getTokenObject(t, j);
			return booleanR.length;
		case ByteArray:
			byte[] byteR = (byte[]) getTokenObject(t, j);
			return byteR.length;
		case CharArray:
			char[] charR = (char[]) getTokenObject(t, j);
			return charR.length * Character.BYTES;
		case ShortArray:
			short[] shortR = (short[]) getTokenObject(t, j);
			return shortR.length * Short.BYTES;
		case FloatArray:
			float[] floatR = (float[]) getTokenObject(t, j);
			return floatR.length * Float.BYTES;
		case DoubleArray:
			double[] doubleR = (double[]) getTokenObject(t, j);
			return doubleR.length * Double.BYTES;
		case Object:
			return byteSizeOfToBytes((Bytable) getTokenObject(t, j));
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
	public ByteBuffer toByteBuffer(Bytable object) throws ToByteComplieException {
		ByteBuffer exportBuffer = ByteBuffer.allocate(byteSizeOfToBytes(object));

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
	public int byteSize(Bytable object) throws ToByteComplieException {
		return byteSizeOfToBytes(object);
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
	public byte[] toBytes(Bytable object) throws ToByteComplieException {
		return toByteBuffer(object).array();
	}

}
