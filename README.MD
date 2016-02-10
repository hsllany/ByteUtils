ByteUtils Simple Library
=======

当处理Socket，或二进制文件时，常常需要把Object转换成byte[]或ByteBuffer。当然，除了调用ObjectOutStream外，有时我们也会需要独特的编码格式来保护数据。

ByteUtils编写的目的，就是为了快捷方便的完成这种对象到byte数组的过程。

应用步骤：

##1. 对象implements Bytable接口。对相应的```get*()```方法赋予```@ToByte```注解


```java
//TestClass.java
public TestClass implements Bytable{
    @ToByte(order = 1)
	public int getInt() {
		return 3;
	}
	
	@ToByte(order = 0)
	public int[] getIntArray(){
		return new int[]{1, 2, 3};
	}
	
	@ToByte(order = 0)
	public TestClass2 getTestClass2(){	//TestClass2 is also a Bytable
		return new TestClass2();
	}
}

//TestClass.java
public class TestClass2 implements Bytable {

	@ToByte
	public int getInt2() {
		return 123;
	}
}
```

##2. 调用ByteUtils.toBytes()方法，ByteUtils将对该对象进行解析，并将对象转换成响应字节数组。

```java
TestClass t = new TestClass();
try {
	byte[] output = ByteUtils.getInstance().toBytes(t);
	System.out.println(Arrays.toString(output));
} catch (ToByteComplieException e) {
	e.printStackTrace();
}
```

Output:

```shell
[0, 0, 0, 123, 0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0, 3, 0, 0, 0, 3]
```

##3. 目前支持所有基本类型，以及其构成数组，以及Bytable的对象。暂不支持Bytable数组。
