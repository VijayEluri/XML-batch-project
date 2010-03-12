package edu.bxml.swt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlParser;
import com.javalobby.tnt.annotation.attribute;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;

/**
 * Instances of this class implement a selecTree user interface object that displays a list of images and strings and issue notification when selected. 
 */
@attribute(value = "", required = false)
public class Tree extends ControlObject {
	private static Log log = LogFactory.getLog(Tree.class);
	org.eclipse.swt.widgets.Composite GUIParent = null;
	private Boolean linesVisible = null;
	private Boolean headerVisible = null;
	org.eclipse.swt.widgets.Tree tree = null;
	private boolean pack = true;
	
	public org.eclipse.swt.widgets.Tree getGUIObject() {
		return tree;
	}
	
	/**
	 * Create the Tree with the style attribute before processing
	 * other attributes
	 */
	public boolean processRawAttributes(org.xml.sax.Attributes attrs) {
		String style = attrs.getValue("style");
		this.style = XmlParser.getFieldValues(SWT.class, style);
		tree = new org.eclipse.swt.widgets.Tree(getGUIParent(), this.style);
		return true;
	}
	
	@Override
	public void execute() throws XMLBuildException {
		if (pack) {
			pack();
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
		org.eclipse.swt.widgets.TreeColumn[] x = tree.getColumns();
		for (int i = 0; i < x.length; i++) {
			x[i].pack();
			widths.append(", " + x[i].getWidth());
		}
		log.debug(" Tree " + getName() + " widths = " + widths.append("  ").substring(2));
	}
	
	/**
	 * Set the lines visible
	 * 
	 * 
	 */
	@attribute(value = "", required = false, defaultValue = "false")
	public void setLinesVisible(Boolean linesVisible) {
		tree.setLinesVisible(linesVisible);
	}
	public void setLinesVisible(String linesVisible) throws XMLParseException {
		setLinesVisible((Boolean.parseBoolean(linesVisible)));
	}
	
	/**
	 * Set the header visible
	 * 
	 * 
	 */
	@attribute(value = "", required = false, defaultValue="false")
	public void setHeaderVisible(Boolean headerVisible) {
		tree.setHeaderVisible(headerVisible);
	}
	public void setHeaderVisible(String headerVisible) throws XMLParseException {
		setHeaderVisible(Boolean.parseBoolean(headerVisible));
	}
	
	/**
	 * Pack the Tree; makes the columns just wide enough to fit
	 * the headers and/or data.
	 * 
	 * 
	 */
	@attribute(value = "", required = false, defaultValue="false")
	public void setPack(Boolean pack) {
		this.pack = pack;
	}
	public void setPack(String pack) throws XMLParseException {
		setPack(Boolean.parseBoolean(pack));
	}
	
	/**
	 * Set the Tree style.  Should be styles from SWT separated by the 
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
	 * Clear all lines of data from the Tree.
	 * 
	 * 
	 */
	@attribute(value = "", required = false, defaultValue="false")
	public void setRemoveAll(Boolean clear) {
		log.debug("setRemoveAll " + clear);
		if (clear){
			tree.removeAll();
			log.debug("cleared");
		}
		else
			log.debug("not cleared");
	}
	public void setRemoveAll(String clear) throws XMLParseException {
		setRemoveAll(Boolean.parseBoolean(clear));
	}
	
	/**
	 * Add a column 
	 * 
	 * 
	 */
	@attribute(value = "", required = false)
	public void addTreeColumn(TreeColumn column) {
		column.setGUIParent(tree);
	}
	
	/**
	 * Add a column 
	 * 
	 * 
	 */
	@attribute(value = "", required = false)
	public void addTreeItem(TreeItem item) {
		log.debug("item set parent to tree = " + tree);
		item.setGUIParent(tree);
	}
	
	/**
	 * Add a row of data values
	 * 
	 * 
	 */
	@attribute(value = "", required = false)
	public void addTr(Tr tr) {
		//tr.setGUIParent(tree);
	}
	
	public org.eclipse.swt.widgets.Composite getGUIParent() {
		return GUIParent;
	}

	public void setGUIParent(org.eclipse.swt.widgets.Widget parent) {
		GUIParent = (org.eclipse.swt.widgets.Composite) parent;
	}
	
	
}
