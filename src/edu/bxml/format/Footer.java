package edu.bxml.format;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.Vector;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;

import edu.bxml.io.FilterAJ;

/**
 * Hold information about how to format the footer
 * @author ritcheyg
 *
 */
@attribute(value = "", required = true)
public class Footer extends XmlObject {
	String separator = null;
	private Vector<FootField> footerFields = new Vector<FootField>();
	Select parent = null;
	Query s = null;
	
	/**
	 * setter function used by parent object to set delimiter to same 
	 * thing it's using.
	 * @param s
	 */
	public void setDelimiter(String s) {
		separator = s;
	}
	
	public void setParent(Select parent) {
		this.parent = parent;
		
	}
	
	/**
	 * check that all the fields are set correctly, especially
	 * required fields.  Called when the end-tag of the 
	 * element has been reached and processed.
	 */
	public void check() throws XMLBuildException {
		
		if (parent == null) {
			throw new XMLBuildException("Parent did not identity itself or is not a Select)");
		}
		separator = parent.delimit;
	}
	
	/**
	 * Called after complete parsing of XML document
	 * to evaluate the document.
	 */
	public void execute() throws XMLBuildException {

	}
	/**
	 * Retrieve the text that was contained inside the tag
	 */
	public void setText(String text) {
		
	}
	
	/**
	 * Add a field to the footer
	 */
	@attribute(value = "", required = true)
	public void addFootField(FootField field) {
		footerFields.add(field);
	}
	
	/**
	 * generate the footer to standard out
	 *
	 */
	public String output() throws SQLException, XMLBuildException {
		//out = new PrintStream(s.getOut());
		StringBuffer outBuffer = new StringBuffer();
		for (FootField field:footerFields){
			field.execute(); //all fields containing variables up-to-date
			outBuffer.append(separator);
			outBuffer.append(field.getValue());
		}
		if (outBuffer.length() > 1) {
			outBuffer.delete(0, separator.length());
		}
		return outBuffer.toString();
	}
}
