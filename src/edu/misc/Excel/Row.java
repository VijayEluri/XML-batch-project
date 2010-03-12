package edu.misc.Excel;

import java.util.Vector;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;

/**
 * Specify the query that needs formatting
 * @author ritcheyg
 *
 */
@attribute(value = "", required = true)
public class Row extends XmlObject {

	Vector<Cell> cells = new Vector<Cell>();
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
		System.err.println("Execute Row");
	}
	/**
	 * Retrieve the text that was contained inside the tag
	 */
	public void setText(String text) {

	}
	public void addCell(Cell c) {
		cells.add(c);
	}
	public Vector<Cell> getCells() {
		return cells;
	}
}
