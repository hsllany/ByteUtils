ByteUtils Simple Library
=======

当处理Socket，或二进制文件时，常常需要把Object转换成byte[]或ByteBuffer。当然，除了调用ObjectOutStream外，有时我们也会需要独特的编码格式来保护数据。

ByteUtils编写的目的，就是为了快捷方便的完成这种对象到byte数组的过程。

应用步骤：
1. 对象implements ToBytes接口
2. 将需要编码的变量，实现访问接口(get*()方法)；
3. 对于该方法，添加ToByte注解，若需制定该方法转换为Byte后的顺序，还需赋予order属性
4. 调用ByteUtils.toBytes()方法，ByteUtils将对该对象进行解析，并将对象转换成响应字节数组。

举例:

```
//Test.java
//object should implements ToBytes interface
public class TestClass implements ToBytes {

	private int aa;
	private int mHaha;
	private float f = 123.456f;
	private double d = 34567;
	private short s = 123;
	private long l = 1234567890;

	public Test() {
		aa = 1;
		mHaha = 48;
	}

	public String format() {
		return "i[getAA]i[getHaha]z[getBoolean]f[getF]d[getD]s[getS]j[getL]";
	}

	@ToByte(order = 1)
	public int getAA() {
		return 3;
	}

	@ToByte(order = 2)
	private int getHaha() {
		return mHaha;
	}

	@ToByte(order = 4)
	public boolean getBoolean() {
		return true;
	}

	@ToByte(order = 4)
	public float getF() {
		return f;
	}

	@ToByte(order = 4)
	public double getD() {
		return d;
	}

	@ToByte(order = 4)
	public short getS() {
		return s;
	}

	@ToByte(order = 4)
	public long getL() {
		return l;
	}

	@ToByte(order = 0)
	public TestClass2 haha() {
		return new TestClass2();
	}

	public static void main(String[] args) {
		Test t = new Test();
		try {
			byte[] b = ByteUtils.getInstance().toBytes(t);
			System.out.println(Arrays.toString(b));
		} catch (ToByteComplieException e) {
			e.printStackTrace();
		}
	}
}

```

```
//TestClass.java
public class TestClass2 implements ToBytes {

	@ToByte
	public int low() {
		return 123;
	}

	public String format() {
		// TODO Auto-generated method stub
		return null;
	}

}
```
将打印：

[0, 0, 0, 123, 0, 0, 0, 3, 0, 0, 0, 48, 66, -10, -23, 121, 64, -32, -32, -32, 0, 0, 0, 0, 0, 123, 0, 0, 0, 0, 73, -106, 2, -46, 1]

