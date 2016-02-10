package test.com.ubirouting.bytelib;

import com.ubirouting.bytelib.ToByte;
import com.ubirouting.bytelib.Bytable;

public class TestClass2 implements Bytable {

	@ToByte
	public int low() {
		return 123;
	}
}
