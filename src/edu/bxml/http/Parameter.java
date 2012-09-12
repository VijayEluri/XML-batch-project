package edu.bxml.http;

//import org.apache.soap.Constants;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;
/**
 * Get an http web page
 * 
 */
@attribute(value = "", required = false)
public class Parameter extends XmlObject {

	String key = null;
	Object fieldValue;
	Class type = null;
	String value = null;
	XmlObject object = null;
	//String encodingStyle = Constants.NS_URI_SOAP_ENC;
	
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
	public Object getValue() {
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
	 *  Get the encoding style
	 *
	@attribute(value = "", required = true)
	public String getEncodingStyle() {
		return encodingStyle;
	}
	/**
	 *  set the encoding style
	 *
	@attribute(value = "", required = false, defaultValue = "NS_URI_SOAP_ENC")
	public void setEncodingStyle(String style) throws XMLBuildException {
		Class c = Constants.class;
		
		try {
			this.encodingStyle = (String) c.getField(style).get(c);
			log.debug("encoding style = " + this.encodingStyle);
			//this.encodingStyle = (String)Constants.class.getField(style).get(new Constants());
		} catch (IllegalArgumentException e) {
			throw new XMLBuildException (e.getMessage());
		} catch (SecurityException e) {
			throw new XMLBuildException (e.getMessage());
		} catch (IllegalAccessException e) {
			throw new XMLBuildException (e.getMessage());
		} catch (NoSuchFieldException e) {
			throw new XMLBuildException (e.getMessage());
		}
	}
	*/
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
