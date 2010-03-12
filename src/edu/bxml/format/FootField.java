package edu.bxml.format;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;

/**
 * Hold information about how to format one field of a footer record and
 * what data that field should hold, current time, string constant or record count.
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class FootField extends XmlObject {
	private static Log log = LogFactory.getLog(FootField.class);
	Vector <Select> selections = new Vector<Select>();
	Select select = null;
	PrintStream out = System.out;

	public FootField() {
		
	}
	
	public FootField(Field f) {
		subField = f;
	}
	
	public enum FootFieldTypes {
		COUNT, CONSTANT, DATE, LENGTH, SQL
	};

	public Field subField = null;
	
	public String name = null;

	public FootFieldTypes type = null;
	String constantValue = null;

	public int size = -1;
	private int add = 0;
	
	SimpleDateFormat outDateFormat = null;

	public int decimals = 0;

	public String padLeft = null;
	public String padRight = null;

	public String defaultValue = null;

	private String leftPadding = null;
	private String rightPadding = null;
	
	
	public String getDelimit() {
		if (subField != null)
			return subField.getDelimit();
		return null;
	}

	/**
	 * setType converter function- method visible to documentation 
	 * 	and xsd generator has target type
	 * @param type
	 * @throws XMLBuildException
	 */
	public void setType(String type) throws XMLBuildException {
		try {
			setType(FootFieldTypes.valueOf(type));
		} catch (IllegalArgumentException iae) {
			throw new XMLBuildException("Type must be one of "
					+ Arrays.toString(FootFieldTypes.values()));
		}
	}
	/**
	 * Must be one of: COUNT - the number of records in the file, CONSTANT a 
	 * constant string usually to identify this as a footer record, DATE - the 
	 * current date and/or time.  SQL - a separate independent SQL query that 
	 * usually would be expected to return a single element of data.
	 */
	@attribute(value = "", required = true)
	public void setType(FootFieldTypes type) throws XMLBuildException {
		this.type = type;
	}
	
	/**
	 * The character to pad left with; usually zero for numeric fields or 
	 * date fields 
	 * and space for character fields.  
	 * 
	 */
	@attribute(value = "", required = false, defaultValue="There must be a size specified.")
	public void setPadleft(String padLeft) {
		this.padLeft = padLeft;
	}
	
	/**
	 * The character to pad right with; usually space for character fields.
	 * 
	 */
	@attribute(value = "", required = false)
	public void setPadright(String padRight) {
		this.padRight = padRight;
	}

	public FootFieldTypes getType() {
		return type;
	}

	/**
	 * Sets the format.  For times, the format is the same as
	 * the Java SimpleDataFormat. "HH:mm:ss MM/dd/yyyy" would be an hour, minute,
	 * second, month, day, year time stamp on a 24 hour clock. 
	 */
	@attribute(value = "", required = false, defaultValue = "As much width is given as is needed")
	public void setFormat(String format) {
		outDateFormat = new SimpleDateFormat(format);
	}

	/**
	 * Set the maximum length of characters to be taken up in the output file
	 * for this field.
	 */
	@attribute(value = "", required = false, defaultValue = "As much width is given as is needed")
	public void setSize(Integer size) {
		this.size = size;
	}
	
	public void setSize(String size) {
		 setSize(Integer.parseInt(size));
	}
	
	/**
	 * Set the maximum length of characters to be taken up in the output file
	 * for this field.
	 */
	@attribute(value = "", required = false, defaultValue = "As much width is given as is needed")
	public void setAdd(Integer add) {
		this.add = add;
	}
	public void setAdd(String add) {
		setAdd(Integer.parseInt(add));
	}

	/**
	 * The number of places after the decimal point to output for numeric types.
	 * 
	 */
	@attribute(value = "", required = false, defaultValue = "As much width is given as is needed "
			+ "to show all digits after decimal point")
	public void setDecimals(Integer decimals) {
		this.decimals = decimals;
	}
	public void setDecimals(String decimals) {
		setDecimals(Integer.parseInt(decimals));
	}

	/**
	 * check that all the fields are set correctly, especially required fields.
	 * Called when the end-tag of the element has been reached and processed.
	 */
	public void check() throws XMLBuildException {
		if (type == null) {
			throw new XMLBuildException("No type specified");
		}
		if (outDateFormat == null && type.equals(FootFieldTypes.DATE)) {
			throw new XMLBuildException("No date format given");
		}
		if (type.equals(FootFieldTypes.CONSTANT) && constantValue == null) {
			throw new XMLBuildException("Constant value specified but never set");
		}
		if (padLeft != null && padRight != null) {
			throw new XMLBuildException("only one of padleft and padright may be specified");
		}
		if (size > 0 && padLeft != null) {
			char[] x = new char[size];
			Arrays.fill(x, padLeft.charAt(0));
			leftPadding = new String(x);
		}
		if (size > 0 && padRight != null) {
			char[] x = new char[size];
			Arrays.fill(x, padRight.charAt(0));
			rightPadding = new String(x);
		}
	}

	/**
	 * Called after complete parsing of XML document to evaluate the document.
	 */
	public void execute() {
		
	}
	
	Select getParentSelect() {
		XmlObject x = this;
		while (x != null && !((x = x.getParent()) instanceof Select));
		return (Select) x;
	}
	/**
	 * Get the value of the footer column
	 * @return
	 * FIXME: getValue() returns 0 for data connected fields (??).
	 */
	public String getValue() throws XMLBuildException {
		if (subField != null) {
			return subField.format("0");
		}
		if (type.equals(FootFieldTypes.CONSTANT)) {
			return constantValue;
		}
		if (type.equals((FootFieldTypes.DATE))) {
			return 	outDateFormat.format(new Date());			
		}
		if (type.equals(FootFieldTypes.COUNT)) {
			int count = getParentSelect().getCount()+add;
			String value = "" + count;
			if (leftPadding != null) {
				return (leftPadding + value).substring(value.length());
			}
			if (rightPadding != null) {
				return (value + rightPadding).substring(0, size);
			}
			return value;
		}
		if (type.equals(FootFieldTypes.LENGTH)) {
			String value = ""+getParentSelect().getLength();
			if (leftPadding != null) {
				return (leftPadding + value).substring(value.length());
			}
			if (rightPadding != null) {
				return (value + rightPadding).substring(0, size);
			}
			return value;
		}
		if (type.equals(FootFieldTypes.SQL)) {
			if (select != null) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				PrintStream ps = new PrintStream(baos);
				select.setOutput(ps);
				select.execute();
				log.debug("BAOS: " + baos.toString());
				ps.close();
				try {baos.close();}
				catch (IOException io) {io.printStackTrace();}
				String returnString =  baos.toString();
				return returnString.toString().substring(0, returnString.length()-
						System.getProperty("line.separator").length());
			}
		}
		return "";
	}

	/**
	 * Retrieve the text that was contained inside the tag
	 */
	public void setText(String text) {
		constantValue = text;
	}
	
	/**
	 * Have the contents of a footer/header field come from a separate query 
	 * @param select
	 */
	@attribute(value = "", required = false)
	public void addSelect(Select select) {
		this.select = select;
	}
	
	public void setFromTextContent(String text) {
		if (text != null)
			constantValue = text;
	}
}
