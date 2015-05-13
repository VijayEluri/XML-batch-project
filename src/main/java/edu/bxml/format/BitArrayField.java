package edu.bxml.format;

import java.util.HashMap;

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
public class BitArrayField extends Field {
	private static Log log = LogFactory.getLog(BitArrayField.class);
	private String on = "1";
	private String off = "0";
	private HashMap<String, BinaryField> bitFields = new HashMap<String, BinaryField>();

	public HashMap<String, BinaryField> getBitFields() {
		return bitFields;
	}

	public void setBitFields(HashMap<String, BinaryField> bitFields) {
		this.bitFields = bitFields;
	}

	public BitArrayField() {
		type = Types.BIT;
	}
	
	public Object format(byte[] x, String partialName) throws XMLBuildException {
		BinaryField i = bitFields.get(partialName);
		if (i == null) {
			log.debug(partialName + " could not be found in BitArrayField");
			return null;
		}
		log.debug("object x is a " + x.getClass().getName() + " has a value of " + getByteString(x));
		log.debug("rawIndex = " + i.getIndex());
		int arrayIndex = i.getIndex()/8;
		log.debug("arrayIndex = " + arrayIndex);
		int bitIndex = i.getIndex()%8;  //remainder after divide by 8
		log.debug("bit index = " + bitIndex);  
		log.debug("byte at index = " + x[arrayIndex]);
		Integer value =  x[arrayIndex]&(1<<bitIndex);
		log.debug("value at index = " + value);
		log.debug(partialName + " formats to " + i.format(value));
		return i.format(value);
	}
	
    public static String getByteString(byte[] health) {

        StringBuilder sb = new StringBuilder();
        for (byte b : health) {
        	String x = "00000000" + Integer.toBinaryString(b);
            sb.append(x.substring(x.length() - 8)).append(" ");
        }

    	return sb.toString();
    }
	
	@Override
	public String format(Object v) throws XMLBuildException {
		String returnValue = null;
		if (v == null) {
			returnValue = defaultValue;
		}
		
		if (returnValue == null) {
			if (v instanceof Boolean)
				returnValue = (((Boolean)v))?off:on;
			if (v instanceof Integer)
				returnValue = ((Integer)v==0)?off:on;
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
	
	public void addBinaryFieldEnd(BinaryField field) throws XMLBuildException {
		String name = field.fieldName != null ? field.fieldName : field
				.getName();
		if (field.getIndex() == null) {
			log.debug("BinaryField " + name + " must set an index as a child of a BinaryArrayField");
			return ;
		}

		bitFields.put(name, field);
	}

	@Override
	public String insertFormat(String value) throws XMLBuildException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSQLType() {
		return ("varbinary");
	}
	
}
