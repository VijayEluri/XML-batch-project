package edu.bxml.jdom;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;

public class Attribute extends XmlObjectImpl implements XmlObject {
	private static Log log = LogFactory.getLog(Attribute.class);
	private String value = "";
	private String attributeName = "";

	@Override
	public void check() throws XMLBuildException {

	}

	@Override
	public void execute() throws XMLBuildException {

	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

}
