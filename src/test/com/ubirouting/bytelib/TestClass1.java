package test.com.ubirouting.bytelib;

import com.ubirouting.bytelib.ToByte;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author YangTao
 *         Created on 16/6/29.
 */
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
}
