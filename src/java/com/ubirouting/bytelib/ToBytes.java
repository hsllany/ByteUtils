package java.com.ubirouting.bytelib;

public interface ToBytes {

	/**
	 * You should write the format in the following format:<br/>
	 * <br/>
	 * "indicator1[fieldName]indicator2[fieldName2]" <br/>
	 * <br/>
	 * Indicators are: 'z' for boolean, 'b' for byte, 'c' for char, 's' for
	 * short, 'i' for integer, 'j' for long, 'f' for float and 'd' for double.
	 * <br/>
	 * Besides, <b>all the fields in the byte format should contain the relating
	 * getFieldName() method.</b> Otherwise exception may be thrown.
	 * 
	 * 
	 * @return format you want to pack
	 */
	String format();

}
