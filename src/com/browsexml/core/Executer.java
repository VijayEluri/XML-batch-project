package com.browsexml.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.javalobby.tnt.annotation.attribute;

import edu.bxml.format.Properties;

/**
 * Call Execute on each immediate child.
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class Executer extends XmlObjectImpl implements XmlObject {
	private static Log log = LogFactory.getLog(Executer.class);
	List<XmlObject> commands = new ArrayList<XmlObject>();
	
	
	/**
	 * check that all the fields are set correctly, especially required fields.
	 * Called when the end-tag of the element has been reached and processed.
	 */
	public void check() throws XMLBuildException {


	}

	/**
	 * Called after complete parsing of XML document to evaluate the document.
	 */
	public void execute() throws XMLBuildException {
		log.debug("executer commands count = " + commands.size());
		for (XmlObject command: commands) {
			try {
				log.debug("Execute: " + command.toString());
				command.execute();
			} catch (XMLBuildException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * add any XmlObject as a child to be executed
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false)
	public void addXmlObjectImpl(XmlObjectImpl object) {
		log.debug("ADD XML OBJECT " + object);
		commands.add(object);
	}
	
	/**
	 * Retrieve the text that was contained inside the tag
	 */
	public void setText(String text) {

	}
	
	public void addProperties(Properties p) {}
}
