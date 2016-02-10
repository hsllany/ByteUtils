package test.com.ubirouting.bytelib;

import com.ubirouting.bytelib.Bytable;
import com.ubirouting.bytelib.ToByte;

public class TestClass implements Bytable {

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
