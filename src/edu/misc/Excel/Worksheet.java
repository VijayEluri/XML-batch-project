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
public class Worksheet extends XmlObjectImpl implements XmlObject {
	Vector <Table> tables = new Vector<Table>();
	
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
	public void addTable(Table t) {
		tables.add(t);
	}
	public Vector<Table> getTables() {
		return tables;
	}
}
