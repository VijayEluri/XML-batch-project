package edu.bxml.format;

import java.util.Arrays;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;
import com.javalobby.tnt.annotation.attribute;

/**
 * Hold information about how to format one field if embedded in a Select object or 
 * how to expect data to be formated in a flat file if embedded in a Load object.  This 
 * is now an abstract class and must be used via charField, textField, numberField,
 * dateField or hiddenField.
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public abstract class Field extends XmlObjectImpl implements XmlObject {
	private static Log log = LogFactory.getLog(Field.class);
	public Vector<Replace> replacements = new Vector<Replace>();

	public enum Grouping {
		NONE, CONCAT
	};

	public enum Types {
		IMAGE, CHAR, NUMBER, TEXT, DATE, HIDDEN, MONEY, BIT
	};

	public String fieldName = null;
	public String headerName = null;
	public String comment = null;
	public String valid = null;
	public String validateColumn = null;

	public boolean isColumnValid(String line) {
		if (validateColumn == null) {
			return true;
		}
		String[] equations = validateColumn.split(" *= *");
		if (equations.length != 2) {
			log.warn(getName() + ": validateColumn (" + validateColumn + ") must be of the form nnn = x1+x2+x3...");
			return false;
		}
		
		int value = Integer.parseInt(equations[0]);
		int total = 0;
		for (String summand: equations[1].split(" *\\+ *")) {
			total += Integer.parseInt(summand);
		}
		if (total != value) {
			log.warn(getName() + ": validateColumn: total (" + total + ") does not equal value (" + value + ")");
		}
		
		if (value != line.length()) {
			log.warn(getName() + ": validateColumn: line (" + line + ") length != " + value);
			return false;
		}

		return true;
	}
	
	public String getValidateColumn() {
		return validateColumn;
	}

	public void setValidateColumn(String validateColumn) {
		this.validateColumn = validateColumn;
	}

	public String getValid() {
		return valid;
	}

	public void setValid(String valid) {
		this.valid = valid;
	}
	
	public Boolean isValid(Object value) {
		if (valid == null) {
			return true;
		}
		if (value == null) { 
			log.warn(value + " does not match (validate) against " + valid);
			return false;
		}
		if (value.toString().matches(valid)) {
			return true;
		}
		log.warn(value + " does not match (validate) against " + valid);
		return false;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Types type = null;

	public int size = -1;

	public String javaFormat = null;
	public String outFormat = null;

	public String getOutFormat() {
		if (outFormat == null)
			return javaFormat;
		return outFormat;
	}

	@attribute(value = "", required = false, defaultValue="format")
	public void setOutFormat(String outFormat) {
		this.outFormat = outFormat;
	}

	public int decimals = 0;
	
	String delimit = null;

	public Grouping group = Grouping.NONE;

	public String defaultValue = "";

	boolean leftPadding = false;
	boolean rightPadding = false;
	String padding = null;
	Character padChar = null;
	String pretext = null;
	String postText = null;
	public String getPostText() {
		return postText;
	}

	/**
	 * Set text to appear after the database field value
	 * 
	 */
	@attribute(value = "", required = false)
	public void setPostText(String postText) {
		this.postText = postText;
	}

	String text = null;
	Integer index = null;
	
	public Integer getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = Integer.parseInt(index);
	}

	public int getSize() {
		return size;
	}
	
	public String toString() {
		return ("name: " + getName() + "  fieldName: " + fieldName + "  type: " + type + "   size: " + size + "  delimit: " + delimit);
	}

	
	public String format(Object value) throws XMLBuildException {
		isValid(value+"");
		return value + "";
	}
	
	public abstract String insertFormat(String value) throws XMLBuildException;
	public abstract Object getObject(String value) throws XMLBuildException;
	
	/**
	 * Override the parent delimiter at the end of this field.  Especially 
	 * effective for changing the delimiter to 'newline' in headers.
	 * @param delimit
	 */
	@attribute(value = "", required = false, defaultValue="parents delimiter setting.")
	public void setDelimit(String delimit) {
		this.delimit = Query.processDelimit(delimit);
		
	}
	
	/**
	 * Set text to be output imediately before value.  Can be used as a 
	 * label of the values meaning, especially in headers.
	 * 	
	 */
	@attribute(value = "", required = false, defaultValue="")
	public void setPreText(String text) {
		this.pretext = text;
	}

	public String getFormat() {
		return javaFormat;
	}
	
	
	/**
	 * The name of the query's field to be output.  The name will be in the header
	 * if a header is printed.  It must also match a column in the sql query
	 * that is used as the source data.
	 * 
	 * FIXME com.browsexml.XMLObject calling setName with null name
	 * 
	 */
	@attribute(value = "", required = true)
	public void setFieldName(String name) {
		if (name != null) {
			this.fieldName = name;
		}
	}
	
	/**
	 * The name of the data in the input header file.
	 * 
	 */
	@attribute(value = "", required = false, defaultValue = "the header is assumed to be the same name as the field in the database")
	public void setHeaderName(String name) {
		if (name != null) {
			this.headerName = name;
		}
	}
	
	public String getFieldName() {
		return fieldName;
	}
	
	public String getLabelName() {
		return (headerName==null)?fieldName:headerName;
	}
	
	public String getHeaderName() {
		return headerName;
	}

	public String getDelimit() {
		return delimit;
	}
	/**
	 * Set the datatype of this field.  Must be one of IMAGE, CHAR, NUMBER, TEXT, 
	 * DATE or HIDDEN.
	 * 	
	 */
	@attribute(value = "", required = true, defaultValue="")
	public void setType(Types type) throws XMLBuildException {
		this.type = type;
	}
	public void setType(String type) throws XMLBuildException {
		try {
			setType(Types.valueOf(type));
		} catch (IllegalArgumentException iae) {
			throw new XMLBuildException("Type must be one of "
					+ Arrays.toString(Types.values()), this);
		}
	}
	
	/**
	 * Set the maximum length of characters to be taken up in the output file
	 * for this field.  The data will be cut off if it excedes size.  If the data is
	 * shorter than size, only enough space is given to hold the data.  If you 
	 * want the data to always have a width of 'size', you should specify padLeft
	 * or padRight.
	 * 
	 */
	@attribute(value = "", required = false, defaultValue = "As much width is given as is needed")
	public void setSize(Integer size) {
		this.size = size;
	}
	public void setSize(String size) {
		setSize(Integer.parseInt(size));
	}
	
	/**
	 * Sets the default value.  During a select, this will be
	 * output rather than null.  If the value is {skipline}, the 
	 * entire line of data will be skipped if this value is null.
	 * During a load, this value will 
	 * go into the database rather than the empty string.
	 * 
	 */
	@attribute(value = "", required = false)
	public void setDefault(String defaultValue) {
		this.defaultValue = defaultValue;
		if (defaultValue.equals("{null}")) {
			this.defaultValue = null;
		}
	}
	
	/**
	 * The character to pad left with; usually zero for numeric fields or 
	 * date fields
	 * and space for character fields.  If the field is numeric and negative
	 * and the pad left character is zero, then the negative sign will be
	 * placed in the left-most position.
	 * 
	 */
	@attribute(value = "", required = false, defaultValue="There must be a size specified.")
	public void setPadleft(String padLeft) throws XMLBuildException {
		leftPadding = true;
		if (padLeft.length() != 1) {
			throw new XMLBuildException("pad must be one character", this);
		}
		padChar = padLeft.charAt(0);
	}
	
	/**
	 * The character to pad right with; usually space for character fields.
	 * 
	 */
	@attribute(value = "", required = false, defaultValue="There must be a size specified.")
	public void setPadright(String padRight) throws XMLBuildException {
		rightPadding = true;
		if (padRight.length() != 1) {
			throw new XMLBuildException("pad must be one character", this);
		}
		padChar = padRight.charAt(0);
	}

	public Types getType() {
		return type;
	}

	/**
	 * Sets the format.  For times, the format is the same as
	 * the Java SimpleDataFormat. "HH:mm:ss MM/dd/yyyy" would be an hour, minute,
	 * second, month, day, year time stamp on a 24 hour clock. 
	 */
	@attribute(value = "", required = false, defaultValue = "As much width is given as is needed")
	public void setFormat(String format) {
		this.javaFormat = format;
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
	 * Run an aggregate function on this field as long as key (see the 'key' object) is the same as its
	 * previous value. Value must be CONCAT to create a comma separated
	 * string
	 */
	@attribute(value = "", required = false, defaultValue = "do not run an aggregate function on this field")
	public void setGroup(Grouping group) throws XMLBuildException {
		this.group = group;
	}
	public void setGroup(String group) throws XMLBuildException {
		try {
			setGroup(Grouping.valueOf(group));
		} catch (IllegalArgumentException iae) {
			throw new XMLBuildException("Group must be one of "
					+ Arrays.toString(Grouping.values()), this);
		}
	}

	/**
	 * check that all the fields are set correctly, especially required fields.
	 * Called when the end-tag of the element has been reached and processed.
	 */
	public void check() throws XMLBuildException {
		if (getName() != null) {
			if (fieldName == null) 
				fieldName = getName();
		}
		if (size == -1) {
			if (javaFormat != null)
				size = javaFormat.length();
		}
		if (leftPadding || rightPadding) {
			if (size < 1)
				throw new XMLBuildException("padding set but size not set or less than 2.", this);
			else {
				char[] padding = new char[size];
				log.debug("name = " + getName() + "   padChar = " + padChar);
				Arrays.fill(padding, padChar);
				this.padding = new String(padding);
			}
		}
	}

	/**
	 * Called after complete parsing of XML document to evaluate the document.
	 */
	public void execute() {

	}

	/**
	 * Retrieve the text that was contained inside the tag
	 */
	public void setText(String text) {
		this.text = text;
	}
	
	public void setFromTextContent(String text) {
		this.text = text;
	}
	
	public String getText() {
		return this.text;
	}
	
	/**
	 * Use regular expression matching to replace data coming out of the database.
	 * Can be used to eliminate delimiter characters or multiple spaces for example.
	 * 
	 */
	@attribute(value = "", required = false)
	public void addReplace(Replace r) {
		replacements.add(r);
	}
	
	public abstract String getSQLType();
}
