package edu.bxml.swt;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;
import com.javalobby.tnt.annotation.attribute;
/**
 * Access an Swt object's attributes
 * 
 */
@attribute(value = "", required = false)
public class Parameter extends XmlObjectImpl implements XmlObject {

	String key = null;
	Object fieldValue;
	Class type = null;
	String value = null;
	XmlObject object = null;
	
	public Object getFieldValue() {
		return fieldValue;
	}
	
	public void setFromTextContent(String text) {
		if (text != null && !text.trim().equals(""))
			value = text;
	}
	@Override
	public void execute() throws XMLBuildException {

	}
	@Override
	public void check() throws XMLBuildException {
		if (key == null) {
			throw new XMLBuildException("key must be set");
		}

	}
	//p.getKey(), p.getType(), p.getValue()
	/**
	 *  Get the name of the variable
	 */
	@attribute(value = "", required = true)
	public String getKey() {
		return key;
	}
	
	/**
	 *  Get the value
	 */
	@attribute(value = "", required = true)
	public String getValue() {
		if (type == null || type != String.class) {
			if (object == null)
				object = (XmlObject)getSymbolTable().get(value);
			if (object != null)
				value = object.toString();
			else
				value = "";
		}
		return value;
	}
	
	/**
	 *  Get the type
	 */
	@attribute(value = "", required = true)
	public Class getType() {
		return type;
	}
	
	/**
	 *  Set the name of the variable
	 */
	@attribute(value = "", required = true)
	public void setKey(String key) throws XMLBuildException {
		this.key = key;
	}
	
	/**
	 *  Set the type of the variable
	 */
	@attribute(value = "", required = false, defaultValue = "java.lang.String")
	public void setType(String type) throws XMLBuildException {
		try {
			this.type = Class.forName(type);
		} catch (ClassNotFoundException e) {
			throw new XMLBuildException(e.getMessage());
		}
	}
	
	/**
	 *  Set the value of the variable
	 */
	@attribute(value = "", required = false, defaultValue = "content of the tag")
	public void setValue(String value) {
		this.value = value;
	}
}
