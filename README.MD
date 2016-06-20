ByteUtils Simple Library
=======

When transfer object through stream, object should be transform
into byte array. Besides OjbectOutStream, we may have other
option.

ByteUtils is to convert an object to byte[], or from byte[] to object。

Steps：

##1. implements Bytable interface, and add annotation @ToByte to the field you want to convert.


```java
//TestClass.java
public TestClass{
    @ToByte(order = 1)
	private int a = 3;
	
	@ToByte(order = 0)
	protected int[] b = new int[]{1,3,4};
	
	@ToByte(order = 0)
	public TestClass2 testObj = new TestClass2();
}

//TestClass.java
public class TestClass2{

	@ToByte(order = 1)
	private int c = 3;
}
```

##2. Invoke toBytes function.

```java
TestClass t = new TestClass();
try {
	byte[] output = ByteUtils.toBytes(t);
	System.out.println(Arrays.toString(output));
} catch (ToByteComplieException e) {
	e.printStackTrace();
}
```

##3. From byte array into object:
```java
	byte[] output = ...;

	TestClass test = ByteUtils.toObject(output, TestClass.class);
```