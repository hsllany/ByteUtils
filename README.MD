ByteUtils Simple Library
=======

When transfer object through stream, object should be transform
into byte array. Besides OjbectOutStream, we may have other
option.

ByteUtils is to convert an object to byte[], or from byte[] to object。

Steps：

##1. Add annotation @ToByte to the field you want to convert.


```java
//TestClass.java
public TestClass{
    @ToByte(order = 1, description="a is stands for blablabla")
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

##4. Print protocol

See what is the protocol behind the object, just run:
```java
	ByteUtils.printProtocol(DoLocateMessage.class);
```

And this will print something like this:
```
Protocol of com.ubirouting.imhereserver.message.DoLocateMessage
===============
_id -- [class java.lang.String]
mBeaconList -- [Array of com.ubirouting.imhereserver.model.Beacon]
mReceiveClientTime -- [long]
mLat -- [float]
mLng -- [float]
mAddress -- [class java.lang.String]

Protocol of com.ubirouting.imhereserver.model.Beacon
===============
minor -- [int]
major -- [int]
level -- [byte]
battery -- [byte]
```