package test.com.ubirouting.bytelib;

import com.ubirouting.bytelib.ByteUtils;
import junit.framework.TestCase;

import java.util.Arrays;

/**
 * @author YangTao
 *         Created on 16/6/29.
 */
public class testByteUtils extends TestCase {

    public void testClass1() {

        long startTime = System.currentTimeMillis();
        TestClass1 obj = new TestClass1();

        System.out.println(obj.toString());
        byte[] b = ByteUtils.toByte(obj);
        System.out.println("encode bytes is " + Arrays.toString(b));

        TestClass1 obj2 = ByteUtils.toObject(b, TestClass1.class);
        System.out.println(obj2.toString());

        System.out.println("use " + (System.currentTimeMillis() - startTime));


    }

    public void testPrintProtocol() {
        ByteUtils.printProtocol(TestClass1.class);
    }
}
