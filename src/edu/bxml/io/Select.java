package edu.bxml.io;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;

public class Select extends XmlObject {
	String field = null;
	String type = "string";

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	@Override
	public void check() throws XMLBuildException {
		
	}

	@Override
	public void execute() throws XMLBuildException {
		
	}
	
	@Override
	public String getValue() {
		return field;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
