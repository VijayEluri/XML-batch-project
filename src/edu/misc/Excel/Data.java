package edu.misc.Excel;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;

/**
 * Specify the query that needs formatting
 * @author ritcheyg
 *
 */
@attribute(value = "", required = true)
public class Data extends XmlObject {
	String text = null;
	
	/**
	 * check that all the fields are set correctly, especially
	 * required fields.  Called when the end-tag of the 
	 * element has been reached and processed.
	 */
	public void check() throws XMLBuildException {

	}
	/**
	 * Called after complete parsing of XML document
	 * to evaluate the document.
	 */
	public void execute() {
		
	}
	/**
	 * Store text set from an attribute
	 */
	public void setText(String text) {
		
	}
	/**
	 * Store text set from within the begin-tag/End-tag area
	 */
	public void setFromTextContent(String text) {
		this.text = text;
	}
	/**
	 * Get the data held in this data item
	 * @return
	 */
	public String getText() {
		return text;
	}
}
