package edu.bxml.format;

import com.javalobby.tnt.annotation.attribute;

/**
 * Hold information about how to format one field if embedded in a Select object.
 * This field is used to hold a key and is not put in the output file.
 * See Field.
 * 
 */
@attribute(value = "", required = true)
public class HiddenField extends Field {

	public HiddenField() {
		type = Types.HIDDEN;
	}
	@Override
	public String format(Object v) {
		return "";
	}

	public String insertFormat(String value) {
		return "";
	}
	
	public String getSQLType() {
		return "";
	}
	
	public Object getObject(String value) {
		return null;
	}
	
}
