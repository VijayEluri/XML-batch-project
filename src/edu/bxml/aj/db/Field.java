package edu.bxml.aj.db;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;

public class Field  extends XmlObjectImpl implements XmlObject {
	private String fieldName;
	private String value;
	private String type;
	
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String name) {
		this.fieldName = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public void check() throws XMLBuildException {
	}
	@Override
	public void execute() throws XMLBuildException {
	}
}
