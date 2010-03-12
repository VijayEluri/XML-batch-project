package edu.bxml.io;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;

public class TypeTemplate extends XmlObject {
	String text = "";
	String type = "";

	@Override
	public void check() throws XMLBuildException {
		
	}

	@Override
	public void execute() throws XMLBuildException {
		
	}

	@Override
	public void setFromTextContent(String text) throws XMLBuildException {
		this.text = text;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	

}
