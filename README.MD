ByteUtils Simple Library
=======

当处理Socket，或二进制文件时，常常需要把Object转换成byte[]或ByteBuffer。当然，除了调用ObjectOutStream外，有时我们也会需要独特的编码格式来保护数据。

ByteUtils编写的目的，就是为了快捷方便的完成这种对象到byte数组的过程。

应用步骤：
1. 对象implements ToByte接口
2. 将需要编码的变量，实现public的访问接口(get*()方法)；
3. 重写format()方法，来定义对象内变量的编码格式及其在byte数组中的顺序；
4. 调用ByteUtils.toBytes()方法，ByteUtils将对format()方法进行解析，并根据其[编写规则](#format)，将对象转换成响应字节数组。

举例:

```
//object should implements ToBytes interface
public class Test implements ToBytes {

	private int aa;
	private int mHaha;
	private float f = 123.456f;
	private double d = 34567;
	private short s = 123;
	private long l = 1234567890;
	
	public Test(){
		aa = 1;
		mHaha = 48;
	}

	@Override
	public String format() {
		return "i[getAA]i[getHaha]z[getBoolean]f[getF]d[getD]s[getS]j[getL]";
	}
	
	//write get*() method
	public int getAA(){
		return aa;
	}
	
	public int getHaha(){
		return mHaha;
	}
	
	public boolean getBoolean(){
		return true;
	}
	
	public float getF(){
		return f;
	}
	
	public double getD(){
		return d;
	}
	
	public short getS(){
		return s;
	}
	
	public long getL(){
		return l;
	}
	
	public static void main(String[] args){
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
将打印：

[0, 0, 0, 1, 0, 0, 0, 48, 1, 66, -10, -23, 121, 64, -32, -32, -32, 0, 0, 0, 0, 0, 123, 0, 0, 0, 0, 73, -106, 2, -46]

#[format规则](id:format)
format的编写规则如下：

```i[getAA]i[getHaha]z[getBoolean]f[getF]d[getD]s[getS]j[getL]```

1. format返回的是一个字符串，其中包含了各个成员变量的类型，以及其访问的方法。
2. i、z、f，*类型码*，表示的是该方法返回的类型，类型即提示了该变量该如何编码，例如i表示该变量是一个integer，即按照java默认字节顺序，将integer转换为4个byte。
3. 类型后面[]内，表示的是该类型对应的访问方法，ByteUtils将会通过反射调用该方法。
在上例中，i[getAA]表示的是getAA()方法返回的是一个integer，ByteUtils将按照integer的方式将其转换为4个byte。
4. 目前所有的类型如下所示：
	1. 'z' for boolean
	2. 'b' for byte
	3. 'c' for char
	4. 's' for short
	5. 'i' for integer
	6. 'j' for long
	7. 'f' for float 
	8. 'd' for double.

#TODO

ByteUtils应提供将复杂类型的成员变量（toByte对象）转换成byte的类型码。


