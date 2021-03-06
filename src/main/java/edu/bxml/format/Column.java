package edu.bxml.format;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;

public class Column extends XmlObjectImpl implements XmlObject {
	private String fieldName;
	private String header;
	private Integer index;
	private String width = null;
	private String dateTimeFormat = null;
	private int formatNumber = 0;

	public int getFormatNumber() {
		return formatNumber;
	}

	public void setFormatNumber(int formatNumber) {
		this.formatNumber = formatNumber;
	}

	public String getDateTimeFormat() {
		return dateTimeFormat;
	}

	public void setDateTimeFormat(String dateTimeFormat) {
		this.dateTimeFormat = dateTimeFormat;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) throws XMLBuildException {
		if (index == null) {
			throw new XMLBuildException(fieldName + " could not be found in the query", this);
		}
		this.index = index;
	}

	public Column() {
		
	}
	
	@Override
	public String toString() {
		return "Column [fieldName=" + fieldName + ", header=" + header
				+ ", index=" + index + "]";
	}

	public Column(String fieldName, String header) {
		super();
		this.fieldName = fieldName;
		this.header = header;
	}

	public String getFieldName() {
		return fieldName;
	}

	public Column(String fieldName, Integer index) {
		super();
		this.fieldName = fieldName;
		this.index = index;
		this.header = camelCase(fieldName);
	}
	
    public static String camelCase(String name){
        String parts[] = name.split("_");
        String string = "";
        for (String part : parts) {
            string += part.substring(0, 1).toUpperCase();
            string += part.substring(1).toLowerCase();
        }

        return string;
    } 

	public Column(String fieldName, String header, Integer index) {
		super();
		this.fieldName = fieldName;
		this.header = header;
		this.index = index;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	@Override
	public void check() throws XMLBuildException {
		if (header == null) {
			throw new XMLBuildException("header must be set", this);
		}
		if (fieldName == null) {
			throw new XMLBuildException("field name must be set", this);
		}
	}

	@Override
	public void execute() throws XMLBuildException {
	}

}
