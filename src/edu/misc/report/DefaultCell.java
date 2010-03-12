package edu.misc.report;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;

import com.browsexml.core.XMLBuildException;
import com.javalobby.tnt.annotation.attribute;



public class DefaultCell extends Td {
	private static Log log = LogFactory.getLog(Td.class);
	public DefaultCell() {
		setName(null);
	}
	
	@Override
	public boolean processRawAttributes(Attributes attrs)
			throws XMLBuildException {
		Table t = getAncestorOfType(Table.class);
		cell = t.getTable().getDefaultCell();
		return true;
	}
	
	public DefaultCell(String name) {
		setName(name);
	}
	
	/**
	 * Called after complete parsing of XML document to evaluate the document.
	 */
	public void execute() {
		
	}
	
	@attribute(value = "", required = true)
	public void setBorder(Integer border) {
		cell.setBorder(border);
		log.debug("default cell border = " + cell + "   " + cell.getBorder());
	}
	public void setBorder(String strBorder) {
		setBorder(strBorder);
	}
}
