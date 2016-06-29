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
public class TestClass1 {

    @ToByte (order = 0, description = "a")
    public int a = 1;

    @ToByte (order = 2, description = "c")
    protected byte c = 3;

    @ToByte (order = 1, description = "b")
    private short b = 2;

    @ToByte (order = 3, description = "d")
    private long d = 123l;

    @ToByte (order = 4, description = "e")
    private float e = 345.f;

    @ToByte (order = 5, description = "f")
    private double f = 3e10;

    @ToByte (order = 6)
    private String[] as = new String[3];

    @ToByte (order = 7)
    private double[] fs = new double[3];

    @ToByte (order = 8)
    private TestClass0[] hs = new TestClass0[3];

    @ToByte (order = 9)
    private List<TestClass0> is = new ArrayList<>();

    public TestClass1() {
        for (int i = 0; i < as.length; i++) {
            as[i] = "hello world";
            fs[i] = System.nanoTime();
            hs[i] = new TestClass0();
            is.add(new TestClass0());
        }
    }

    @Override
    public String toString() {
        return a + "," + b + "," + c + "," + d + "," + e + "," + f + "," + Arrays.toString(as) + "," + Arrays.toString(fs) + "," + Arrays.toString
                (hs) + "," + is.toString();
    }

//TestClass.java
public class TestClass0 {

    @ToByte (order = 1)
    private long a = System.nanoTime();

    @Override
    public String toString() {
        return "testClass@a = " + a;
    }
}
```

##2. Invoke toBytes function.

```java
TestClass1 t = new TestClass1();
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

	TestClass1 test = ByteUtils.toObject(output, TestClass1.class);
```

##4. Print protocol

See what is the protocol behind the object, just run:
```java
	ByteUtils.printProtocol(TestClass1ixing.class);
```

And this will print something like this, all its fields, which has ToByte annotation, will be printed. Also if field
 is also some object that has ToByte fields, it will also be printed after:
```
Protocol of test.com.ubirouting.bytelib.TestClass1
===============
a -- [int]a
b -- [short]b
c -- [byte]c
d -- [long]d
e -- [float]e
f -- [double]f
as -- [class [Ljava.lang.String;]
fs -- [class [D]
hs -- [class [Ltest.com.ubirouting.bytelib.TestClass0;]
is -- [Array of test.com.ubirouting.bytelib.TestClass0]

Protocol of test.com.ubirouting.bytelib.TestClass0
===============
a -- [long]
```