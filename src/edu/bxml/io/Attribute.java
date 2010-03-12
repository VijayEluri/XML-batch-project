package edu.bxml.io;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlParser;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;

import edu.bxml.format.Replace;

/**
 * Get an arbitrary attribute from an object and perhaps run a regular
 * expression to transform it.
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class Attribute extends XmlObject {
	private static Log log = LogFactory.getLog(Attribute.class);
	String attribute = null;
	List<Replace> replacements = new ArrayList<Replace>();
	String text = "";

	/**
	 * check that all the fields are set correctly, especially
	 * required fields.  Called when the end-tag of the 
	 * element has been reached and processed.
	 */
	public void check() throws XMLBuildException {
		if (this.attribute == null) 
			throw new XMLBuildException("you must set the attribute");
	}
	
	public void execute() throws XMLBuildException {
		log.debug("attribute = " + attribute);
		text = XmlParser.processAttributes(this, this.attribute);
		log.debug("Text = " + text);

		for (Replace replace:replacements) {
			replace.setText(text);
			replace.execute();
			text = replace.getText();
		}
		HashMap m = getSymbolTable(); 
		m.put("_#" + getName(), text);
		log.debug("ATTRIBUTE: text = " + text);
	}
	
	/**
	 * set the value of the variable.  The 'name' property of the
	 * variable can be used to reference its value elsewhere using 
	 * the format ${name}
	 * @param text
	 */
	@attribute(value = "", required = false, defaultValue="an empty string will be used by default")
	public void setText(String text) {
	}
	
	public String toString() {
		return text;
	}
	
	/**
	 * Should we uncompress the file. If false or missing a compression will be
	 * done.
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false, defaultValue = "false")
	public void setAttribute(String attribute) {
		log.debug("att = " + attribute);
		this.attribute = attribute;
	}
	
	
	public void addReplace(Replace r) {
		replacements.add(r);
	}
	
}
