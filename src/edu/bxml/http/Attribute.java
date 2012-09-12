package edu.bxml.http;

import java.util.Vector;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;
/**
 * Get an http web page
 * 
 */
@attribute(value = "", required = false)
public class Attribute extends XmlObject {
	String value = "";
	
	Vector<Parameter> params = new Vector<Parameter>();

	@Override
	public void execute() throws XMLBuildException {
		
	}
	
	public void check() throws XMLBuildException {
		
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
