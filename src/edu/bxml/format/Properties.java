package edu.bxml.format;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;
/**
 * Read properties from a file.  The properties are created as soon as the end-tag
 * is read.
 * @author ritcheyg
 *
 */
@attribute(value = "", required = true)
public class Properties extends XmlObject {

	public String value = "";
	public String file = null;
	
	public Connection getConnection() {
		return (Connection)this.getParent();
	}
	
	/**
	 * check that all the fields are set correctly, especially
	 * required fields.  Called when the end-tag of the 
	 * element has been reached and processed.
	 */
	public void check() throws XMLBuildException {
		if (file == null) {
			throw new XMLBuildException("You must set a file name.");
		}
		File props = new File(file);
		java.util.Properties p = new java.util.Properties();
		try {
			p.load(new FileInputStream(props));
		} catch (FileNotFoundException e) {
			throw new XMLBuildException(file + ": does not exist.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new XMLBuildException(e.getMessage());
		}
		HashMap m = getSymbolTable(); 
		for (Enumeration e = p.keys();e.hasMoreElements();) {
			String key = (String) e.nextElement();
			String value = p.getProperty(key);
			m.put("_#" + key, value);
		}
	}
	
	/**
	 * Load the properties contained in the file named.  The file is in the
	 * format of a Java properties file  i.e. lines with name=value 
	 * @param text
	 */
	@attribute(value = "", required = true)
	public void setFile(String name) {
		this.file = name;
	}
	/**
	 * Called after complete parsing of XML document
	 * to evaluate the document.
	 */
	public void execute() {
		
	}

	public void setText(String text) {
		if (text != null)
			value = text;
	}

	public void setFromTextContent(String text) {
		if (text != null)
			value = text;
	}
}
