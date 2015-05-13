package edu.misc.Excel;

import java.util.Vector;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;
import com.javalobby.tnt.annotation.attribute;

/**
 * Specify the query that needs formatting
 * @author ritcheyg
 *
 */
@attribute(value = "", required = true)
public class Table extends XmlObjectImpl implements XmlObject {

	Vector<Row> rows = new Vector<Row>();
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
	 * Retrieve the text that was contained inside the tag
	 */
	public void setText(String text) {

	}
	public void addRow(Row r) {
		rows.add(r);
	}
	public Vector<Row> getRows() {
		return rows;
	}
}
