package com.browsexml.core;

import java.util.ArrayList;
import java.util.List;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;

import edu.bxml.format.Properties;
import edu.bxml.ftp.Ftp;

/**
 * Call Execute on each immediate child.
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class Exit extends XmlObjectImpl implements XmlObject {
	
	
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
		System.exit(0);
	}
	
	public void setText(String text) {

	}
	
	public void setFromTextContent(String text) {
		
	}
	
	public void addProperties(Properties p) {}

}
