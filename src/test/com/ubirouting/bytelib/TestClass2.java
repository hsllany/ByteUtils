package test.com.ubirouting.bytelib;

import com.ubirouting.bytelib.ToByte;
import com.ubirouting.bytelib.ToBytes;

public class TestClass2 implements ToBytes {

	@ToByte
	public int low() {
		return 123;
	}

	public String format() {
		// TODO Auto-generated method stub
		return null;
	}

}
