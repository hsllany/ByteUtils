package test.com.ubirouting.bytelib;

import com.ubirouting.bytelib.ToByte;

/**
 * @author YangTao
 *         Created on 16/6/29.
 */
public class TestClass0 {

    @ToByte (order = 1)
    private long a = System.nanoTime();

    @Override
    public String toString() {
        return "testClass@a = " + a;
    }
}
