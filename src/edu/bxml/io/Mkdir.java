package edu.bxml.io;

import java.io.File;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;
import com.javalobby.tnt.annotation.attribute;

/**
 * Make a directory
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class Mkdir extends XmlObjectImpl implements XmlObject {

	String dir = null;
	
	/**
	 * check that all the fields are set correctly, especially required fields.
	 * Called when the end-tag of the element has been reached and processed.
	 */
	public void check() throws XMLBuildException {
		if (dir == null) {
			throw new XMLBuildException("dir must be set", this);
		}

	}

	/**
	 * Called after complete parsing of XML document to evaluate the document.
	 */
	public void execute() throws XMLBuildException {
		if (! new File(dir).mkdirs())
			throw new XMLBuildException("mkdirs: failed for " + dir, this);
	}
	
	/**
	 * Specify the directory to be created.
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false)
	public void setDir(java.lang.String dir) {
		this.dir = dir;
	}

	/**
	 * Retrieve the text that was contained inside the tag
	 */
	public void setText(String text) {

	}
}
