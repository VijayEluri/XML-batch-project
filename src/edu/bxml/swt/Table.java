package edu.bxml.swt;

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlParser;
import com.javalobby.tnt.annotation.attribute;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;

/**
 * Instances of this class implement a selectable user interface object that displays a list of images and strings and issue notification when selected. 
 */
@attribute(value = "", required = false)
public class Table extends ControlObject {
	private static Log log = LogFactory.getLog(Table.class);
	org.eclipse.swt.widgets.Composite GUIParent = null;
	private Boolean linesVisible = null;
	private Boolean headerVisible = null;
	org.eclipse.swt.widgets.Table table = null;
	private Vector<TableColumn>columns = new Vector<TableColumn>();
	private Vector<TableSelect>selectedRows = new Vector<TableSelect>();
	private boolean pack = true;
	
	public org.eclipse.swt.widgets.Table getGUIObject() {
		return table;
	}
	
	/**
	 * Create the table with the style attribute before processing
	 * other attributes
	 */
	public boolean processRawAttributes(org.xml.sax.Attributes attrs) {
		String style = attrs.getValue("style");
		this.style = XmlParser.getFieldValues(SWT.class, style);
		table = new org.eclipse.swt.widgets.Table(getGUIParent(), this.style);
		return true;
	}

	@Override
	public void execute() throws XMLBuildException {
		for (TableSelect s:selectedRows) {
			table.select(s.getIndex());
			table.showItem(table.getItem(s.getIndex()));
		}
	}
	@Override
	public void check() throws XMLBuildException {
		if (pack) {
			pack();
		}
	}
	
	public void pack() {
		StringBuffer widths = new StringBuffer();
		org.eclipse.swt.widgets.TableColumn[] x = table.getColumns();
		for (int i = 0; i < x.length; i++) {
			x[i].pack();
			widths.append(", " + x[i].getWidth());
		}
		log.debug(" table " + getName() + " widths = " + widths.append("  ").substring(2));
	}
	
	/**
	 * Set the lines visible
	 * 
	 * 
	 */
	@attribute(value = "", required = false, defaultValue = "false")
	public void setLinesVisible(Boolean linesVisible) {
		table.setLinesVisible(linesVisible);
	}
	public void setLinesVisible(String linesVisible) throws XMLParseException {
		setLinesVisible(Boolean.parseBoolean(linesVisible));
	}
	
	/**
	 * Set the header visible
	 * 
	 * 
	 */
	@attribute(value = "", required = false, defaultValue="false")
	public void setHeaderVisible(Boolean headerVisible) {
		table.setHeaderVisible(headerVisible);
	}
	public void setHeaderVisible(String headerVisible) throws XMLParseException {
		setHeaderVisible(Boolean.parseBoolean(headerVisible));
	}
	
	/**
	 * Pack the table; makes the columns just wide enough to fit
	 * the headers and/or data.
	 * 
	 * 
	 */
	@attribute(value = "", required = false, defaultValue="false")
	public void setPack(Boolean pack) {
		this.pack = pack;
	}
	public void setPack(String pack) throws XMLParseException {
		this.pack = Boolean.parseBoolean(pack);
	}
	
	/**
	 * Set the table style.  Should be styles from SWT separated by the 
	 * '|' symbol.  A typical setting would be SINGLE|BORDER|FULL_SELECTION.
	 * 
	 * SINGLE, MULTI, CHECK, FULL_SELECTION, HIDE_SELECTION, VIRTUAL 
	 * Events: Selection, DefaultSelection 
	 */
	@attribute(value = "", required = false, defaultValue="NONE")
	public void setStyle(String style) throws XMLParseException {
		// Do nothing but provide a place for the comment.  Style is
		// pulled off manually before attribute processing.
	}
	
	/**
	 * Clear all lines of data from the table.
	 * 
	 * 
	 */
	@attribute(value = "", required = false, defaultValue="false")
	public void setRemoveAll(String clear) throws XMLParseException {
		log.debug("setRemoveAll " + clear);
		if (Boolean.parseBoolean(clear)){
			table.removeAll();
			log.debug("cleared");
		}
		else
			log.debug("not cleared");
		selectedRows = new Vector<TableSelect>();
	}
	
	/**
	 * Add a column 
	 * 
	 * 
	 */
	@attribute(value = "", required = false)
	public void addTableColumn(TableColumn column) {
		column.setGUIParent(table);
		columns.add(column);
	}
	
	/**
	 * Add a row of data values
	 * 
	 * 
	 */
	@attribute(value = "", required = false)
	public void addTr(Tr tr) {
		tr.setGUIParent(table);
	}

	/**
	 * Add a row to be selected
	 * 
	 * 
	 */
	@attribute(value = "", required = false)
	public void addTableSelect(TableSelect ts) {
		selectedRows.add(ts);
	}
	
	public org.eclipse.swt.widgets.Composite getGUIParent() {
		return GUIParent;
	}

	public void setGUIParent(org.eclipse.swt.widgets.Widget parent) {
		GUIParent = (org.eclipse.swt.widgets.Composite) parent;
	}
	
	
}
