package edu.bxml.format;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.javalobby.tnt.annotation.attribute;

/**
 * Hold information about how to format one field if embedded in a Select object or 
 * how to expect data to be formated in a flat file if embedded in a Load object.
 * See Field.
 * 
 */
@attribute(value = "", required = true)
public class BinaryField extends Field {
	private static Log log = LogFactory.getLog(BinaryField.class);
	private String on = "1";
	private String off = "0";
	private Integer index = null;

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}
	
	public void setIndex(String index) {
		try {
			this.index = Integer.parseInt(index);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	public BinaryField() {
		type = Types.BIT;
	}
	
	@Override
	public String format(Object v) throws XMLBuildException {
		String returnValue = null;
		if (v == null) {
			returnValue = defaultValue;
		}
		
		if (returnValue == null) {
			if (v instanceof Boolean)
				returnValue = (((Boolean)v))?on:off;
			if (v instanceof Integer)
				returnValue = ((Integer)v!=0)?on:off;
		}
		
		if (pretext == null) 
			return returnValue;
		return pretext + returnValue;
	}
	
	public String getOn() {
		return on;
	}

	public void setOn(String on) {
		this.on = on;
	}

	public String getOff() {
		return off;
	}

	public void setOff(String off) {
		this.off = off;
	}

	public Object getObject(String value) {
		return value;
	}

	@Override
	public String insertFormat(String value) throws XMLBuildException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSQLType() {
		return ("bit");
	}
}
