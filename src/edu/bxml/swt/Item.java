package edu.bxml.swt;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;

/**
 * Hold a key/value pair; the value is in the body of the tag
 */
@attribute(value = "", required = false)
public class Item extends XmlObject {
	private String key = null;
	private String value = null;
	
	public void setFromTextContent(String text) {
		value = text;
	}
	
	@Override
	public void execute() throws XMLBuildException {
		
	}
	@Override
	public void check() throws XMLBuildException {
	}
	
	/**
	 * set the key
	 */
	@attribute(value = "", required = false)
	public void setKey(java.lang.String key) {
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}
	public String getValue() {
		return value;
	}
}
