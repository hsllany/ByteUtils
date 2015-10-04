package java.com.ubirouting.bytelib;

public interface ToBytes {

	/**
	 * 
	 * You should write the format in the following format:<br/>
	 * 'indicator1[fieldName]indicator2[fieldName2]' <br/>
	 * Where 'z' for boolean, 'b' for byte, 'c' for char, 's' short, 'i' int,
	 * 'j' long, 'f' float, 'd' double.<br/>
	 * 
	 * @return format you want to pack
	 */
	String format();

}
