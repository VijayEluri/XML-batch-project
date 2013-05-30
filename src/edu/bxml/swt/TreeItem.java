package edu.bxml.swt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Widget;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;
import com.browsexml.core.XmlParser;
import com.javalobby.tnt.annotation.attribute;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;

/**
 * Add a row of data to a Tree.
 */
@attribute(value = "", required = false)
public class TreeItem extends XmlObjectImpl implements XmlObject {
	private static Log log = LogFactory.getLog(TreeItem.class);
	org.eclipse.swt.widgets.Widget GUIParent = null;
	int style = 0;
	org.eclipse.swt.widgets.TreeItem item = null;
	int count = 0;
	Apply apply = null;
	Boolean expanded = false;
	
	
	/**
	 * 
	 * Styles: none 
	 * Events: none
	 */
	@attribute(value = "", required = false, defaultValue="NONE")
	public void setStyle(String style) throws XMLParseException {
		// Do nothing but provide a place for the comment.  Style is
		// pulled off manually before attribute processing.
	}
	
	public org.eclipse.swt.widgets.TreeItem getGUIObject() {
		return item;
	}

	public boolean processRawAttributes(org.xml.sax.Attributes attrs) {
		String style = attrs.getValue("style");
		if (style != null)
			this.style = XmlParser.getFieldValues(SWT.class, style);
		
		log.debug("GUI parent = " + getGUIParent());
		log.debug("style = " + this.style);
		Widget parent = getGUIParent();
		if (parent instanceof org.eclipse.swt.widgets.TreeItem )
			item = new org.eclipse.swt.widgets.TreeItem(
				(org.eclipse.swt.widgets.TreeItem )getGUIParent(), this.style);
		else 
			item = new org.eclipse.swt.widgets.TreeItem(
					(org.eclipse.swt.widgets.Tree )getGUIParent(), this.style);
		return true;
	}
	
	@attribute(value = "", required = false)
	public void setExpanded(Boolean expanded) {
		this.expanded = expanded;
	}
	public void setExpanded(String expanded) {
		setExpanded(Boolean.parseBoolean(expanded));

	}
	
	public void setText(String text) {
		String[] textColumns = text.split(",");
		item.setText(textColumns);
	}
	
	@Override
	public void execute() throws XMLBuildException {
		
	}
	@Override
	public void check() throws XMLBuildException {
		log.debug("Set expanded = " + expanded);
		if (expanded)
			item.setExpanded(expanded);
	}
	
	
	public org.eclipse.swt.widgets.Widget getGUIParent() {
		return GUIParent;
	}

	public void setGUIParent(org.eclipse.swt.widgets.Tree parent) {
		GUIParent = parent;
	}
	
	public void setGUIParent(org.eclipse.swt.widgets.TreeItem parent) {
		GUIParent = parent;
	}
	
	
	/**
	 * 
	 * 
	 */
	@attribute(value = "", required = false)
	public void addTreeItem(TreeItem item) {
		log.debug("item set parent to item = " + item);
		item.setGUIParent(this.item);
	}
	
}
