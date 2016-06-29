package test.com.ubirouting.bytelib;

import com.ubirouting.bytelib.PrimaryDatas;
import junit.framework.TestCase;

import java.util.Random;

/**
 * @author YangTao
 *         Created on 16/6/29.
 */
public class PrimaryDataTest extends TestCase {

    private static void judge(boolean contidion) {
        if (!contidion) throw new IllegalStateException();
    }

    public void testInt() {
        for (int i = Integer.MIN_VALUE; i < Integer.MAX_VALUE; i++) {
            byte[] b = PrimaryDatas.i2b(i);

            int returnVal = PrimaryDatas.b2i(b, 0);
            judge(returnVal == i);
        }
    }

    public void testLong() {
        for (long i = Integer.MIN_VALUE; i < Integer.MAX_VALUE; i++) {
            byte[] b = PrimaryDatas.l2b(i);

            long returnVal = PrimaryDatas.b2l(b, 0);
            judge(returnVal == i);
        }
    }

    public void testShort() {
        for (short i = Short.MIN_VALUE; i < Short.MAX_VALUE; i++) {
            byte[] b = PrimaryDatas.s2b(i);

            short s = PrimaryDatas.b2s(b, 0);
            judge(s == i);
        }
    }

    public void testFloat() {
        Random r = new Random(System.currentTimeMillis());

        for (int i = 0; i < 1000; i++) {
            float f = r.nextFloat();

            byte[] b = PrimaryDatas.f2b(f);
            float returnVal = PrimaryDatas.b2f(b, 0);

            judge(Float.compare(f, returnVal) == 0);
        }
    }

    public void testDouble() {
        Random r = new Random(System.currentTimeMillis());


        for (int i = 0; i < 1000; i++) {
            double f = 9.649382814404E12;

            byte[] b = PrimaryDatas.d2b(f);
            double returnVal = PrimaryDatas.b2d(b, 0);

            judge(Double.compare(returnVal, f) == 0);
        }
    }
}
