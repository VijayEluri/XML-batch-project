package edu.bxml.format;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
public class DateField extends Field {
	private static Log log = LogFactory.getLog(DateField.class);
	
	DateFormat df = null;

	public DateField() {
		type = Types.DATE;
		setFormat("yyyy-MM-dd HH:mm:ss");
		df = new SimpleDateFormat(javaFormat);
	}
	@Override//java.sql.Timestamp
	public String format(Object v) throws XMLBuildException {
		String value = null;
		if (v == null) 
			return defaultValue;
		if (v instanceof Timestamp) {
			DateFormat inDateFormat = new SimpleDateFormat(getFormat());
			value = inDateFormat.format(v);
		}
		else
			value = v.toString();

		if ("".equals(value.trim())) {
			return defaultValue;
		}
		if (leftPadding) {
			value = (padding + value).substring(value.length());
		}

		Date d = null;
		DateFormat format = new SimpleDateFormat(getFormat());
		DateFormat outFormat = new SimpleDateFormat(getOutFormat());
		try {d=format.parse(value);
		}
		catch (ParseException  pe) {
			throw new XMLBuildException(getFieldName() + ": " + pe.getMessage());
		}
		
		StringBuffer retValue = new StringBuffer(outFormat.format(d));
		if (pretext != null) {
			retValue.insert(0, pretext);
		}
		if (postText != null)
			retValue.append(postText);
		return retValue.toString();
	}
	
	public String insertFormat(String value) throws XMLBuildException {
		StringBuffer retValue = new StringBuffer();
		DateFormat inDateFormat = new SimpleDateFormat(getFormat());

		Date date = null;
		Calendar c = null;
		if (value != null && format(value) != null) {
			try {
				date = inDateFormat.parse(format(value));
				c = new GregorianCalendar();
				c.setTime(date);
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
		}
		
		if (value == null || value.trim().equals("") || c == null ||
				c.get(Calendar.YEAR) < 1753)
			retValue.append("null, ");
		else {
			//log.debug("value = " + value + "  date = " + df.format(d));
			retValue.append("'").append(df.format(date)).append("', ");
		}

		return retValue.toString();
	}

	public String getSQLType() {
		return "datetime";
	}
	
	public java.sql.Date getObject(String value) throws XMLBuildException {
		if (value == null) {
			return null;
		}
		DateFormat inDateFormat = new SimpleDateFormat(getFormat());
		java.sql.Date sqlDate = null;
		try {
			java.util.Date d = inDateFormat.parse(value);
			sqlDate = new java.sql.Date(d.getTime());
		}
		catch (Exception s) {
			throw new XMLBuildException(s.getMessage());
		}
		return sqlDate;
	}
}
