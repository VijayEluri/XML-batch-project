package edu.bxml.format;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;
/**
 * Declare a property (variable).  The property is created as soon as the end-tag
 * is read.
 * @author ritcheyg
 *
 */
@attribute(value = "", required = true)
public class Property extends XmlObject {
	private static Log log = LogFactory.getLog(Property.class);

	public String value = "";
	
	public Connection getConnection() {
		return (Connection)this.getParent();
	}
	
	/**
	 * check that all the fields are set correctly, especially
	 * required fields.  Called when the end-tag of the 
	 * element has been reached and processed.
	 */
	public void check() throws XMLBuildException {
		HashMap m = getSymbolTable(); 
		m.put("_#" + getName(), value);
		log.debug("put " + getName() + " " + value);
		log.debug("st = " + m);
	}
	/**
	 * Called after complete parsing of XML document
	 * to evaluate the document.
	 */
	public void execute() {
		log.debug("EXECUTE PROPERTY " + this.getName() + " value = " + value);
	}

	/**
	 * set the value of the variable.  The 'name' property of the
	 * variable can be used to reference its value elsewhere using 
	 * the format ${name}
	 * @param text
	 */
	@attribute(value = "", required = false, defaultValue="an empty string will be used by default")
	public void setText(String text) {
		if (text != null) {
			log.debug("value = " + text);
			value = text;
		}
	}
	
	public String getText() {
		log.debug("returning value = " + value);
		return value;
	}
	/**
	 * Retrieve the text that was contained inside the tag
	 */
	public void setFromTextContent(String text) {
		if (text != null)
			value = text;
	}
	
	public String toString() {
		log.debug("returning value = " + value);
		return value;
	}
}
