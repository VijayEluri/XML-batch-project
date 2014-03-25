package edu.bxml.format;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Formatter;

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
public class NumberField extends Field {
	private static Log log = LogFactory.getLog(NumberField.class);
	Formatter f = null;
	private Integer min = null;
	private Integer max = null;
	
	public Integer getMin() {
		return min;
	}

	public void setMin(String min) {
		setMin(Integer.parseInt(min));
	}
	public void setMin(Integer min) {
		this.min = min;
	}

	public Integer getMax() {
		return max;
	}

	public void setMax(String max) {
		setMax(Integer.parseInt(max));
	}
	
	public void setMax(Integer max) {
		this.max = max;
	}

	public NumberField() {
		type = Types.NUMBER;
	}
	
	@Override
	public String format(Object v) throws XMLBuildException {
		String returnValue = null;
		if (v == null) {
			returnValue = defaultValue;
		}
		
		if (min != null &&  ((Integer) v) < min) {
			returnValue = defaultValue;
		}
		if (max != null &&  ((Integer) v) > max) {
			returnValue = defaultValue;
		}
		if (returnValue == null) {
			String value = v.toString();
			
			returnValue = noPadFormat(value);
			boolean negative = false;
			if (returnValue.length() < size) {
				if (leftPadding && padding.startsWith("0")) {
					try {
						int i = Integer.parseInt(value);
						if (i < 0) {
							returnValue = Math.abs(i)+"";
							negative = true;
						}
					}
					catch (NumberFormatException nfe) {
						
					}
					returnValue = (padding + returnValue).substring(returnValue.length());
					if (negative) {
						returnValue = "-" + returnValue.substring(1);
					}
				} 
				else if (rightPadding) {
					returnValue = (returnValue + padding).substring(0, size);
				}
			}
			else {
				if (returnValue.length() > size && size > 0) {
					log.debug(getFieldName() + ": " + " truncated, value = '" + value + "'.");
				}
			}
		}
		if (!isValid(returnValue)) {
			log.warn("field " + this.getName() + " does not validate");
		}
		if (pretext == null) 
			return returnValue;
		return pretext + returnValue;
	}
	
	public String insertFormat(String value) throws XMLBuildException {
		if (value == null || value.trim().equals("")) {
			if (defaultValue == null)
				return "null, ";
			value = defaultValue;
		}
		return noPadFormat(value) + ", ";
	}
	
	/**
	 * Formats the number before padding may or may not be applied
	 * @param value
	 * @return
	 * @throws XMLBuildException
	 */
	public String noPadFormat(String value) throws XMLBuildException {
		StringBuffer retValue = new StringBuffer();
		if (getType().equals(Types.MONEY) || decimals != 0 || 
				(javaFormat != null && javaFormat.contains(".") 
				|| value.contains("."))) {
			Double num = 0.0;
			for (Replace r:replacements) {
				value = 
					value.replaceAll(r.getExpression(), r.getReplacement());
			}
			try {
				//log.debug("decimals = " + decimals);
				//log.debug("number as string = " + value);
				boolean parsed = false;
				if (getType().equals(Types.MONEY))
					try {
						NumberFormat.getCurrencyInstance().parse(value);
						parsed = true;
					} catch (Exception e) {};
				if (!parsed)
					num = Double.parseDouble(value);
					
				//log.debug("number = " + num);
			}
			catch (NumberFormatException  pe) {
				pe.printStackTrace();
				throw new XMLBuildException(getFieldName() + ": " + pe.getMessage(), this);
			}
			if (javaFormat != null) {
				NumberFormat decimalFormat = new DecimalFormat(javaFormat);
				value = decimalFormat.format(num);
				retValue.append(value);
			}
			else {
				String format = "%" + size + "." + decimals + "f";
				retValue.append(String.format(format, num));
			}
		}
		else {
			Integer num = 0;
			try {
				num = Integer.parseInt(value);
			}
			catch (NumberFormatException  pe) {
				log.debug("number: " + value);
				pe.printStackTrace();
				throw new XMLBuildException(getFieldName() + ": " + pe.getMessage(), this);
			}
			retValue.append(num + "");
		}
		return retValue.toString();
	}

	public String getSQLType() {
		if (Types.MONEY.equals(type))
			return "money";
		if (decimals != 0) 
			return("float");
		return ("int");
	}
	
	public Object getObject(String value) {
		return value;
	}
}
