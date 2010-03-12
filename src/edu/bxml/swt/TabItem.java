package edu.bxml.swt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlParser;
import com.javalobby.tnt.annotation.attribute;

/**
 * Instances of this class represent a selectable user interface object corresponding to a tab for a page in a tab folder. 
 */
@attribute(value = "", required = false)
public class TabItem extends GUIObject {
	private static Log log = LogFactory.getLog(TabItem.class);
	org.eclipse.swt.widgets.TabItem item = null;
	org.eclipse.swt.widgets.TabFolder GUIParent = null;
	ControlObject composite = null;

	public boolean processRawAttributes(org.xml.sax.Attributes attrs) {
		String style = attrs.getValue("style");
		this.style = XmlParser.getFieldValues(SWT.class, style);
		item = new org.eclipse.swt.widgets.TabItem (getGUIParent(), this.style);
		return true;
	}
	
	public org.eclipse.swt.widgets.Widget getGUIObject() {
		return item;
	}
	
	@Override
	public void execute() throws XMLBuildException {
	}
	
	/**
	 * Set the text on the tab 
	 */
	@attribute(value = "", required = false)
	public void setText(String text) {
		item.setText(text);
	}
	@Override
	public void check() throws XMLBuildException {
		if (composite != null)
			item.setControl (composite.getGUIObject());
	}

	/**
	 * Add a composite 
	 */
	@attribute(value = "", required = false)
	public void addComposite(Composite composite) {
		composite.setGUIParent(getGUIParent());
		this.composite = composite;
	}
	
	/**
	 * Add a group 
	 */
	@attribute(value = "", required = false)
	public void addGroup(Group group) {
		group.setGUIParent(getGUIParent());
		this.composite = group;
	}

	public org.eclipse.swt.widgets.TabFolder getGUIParent() {
		return GUIParent;
	}

	public void setGUIParent(org.eclipse.swt.widgets.TabFolder parent) {
		GUIParent = parent;
	}
	
	public void setGUIParent(org.eclipse.swt.widgets.Widget parent) {
		setGUIParent((org.eclipse.swt.widgets.TabFolder) parent);
	}
	
	/**
	 * Add a listener 
	 */
	@attribute(value = "", required = false)
	public void addListener(Listener listener) {
		listener.setGUIParent(this.getGUIParent());
		log.debug("GUI OBject = " + this.getGUIObject());
	}
	
	public boolean handleEvent(Event e) {
		org.eclipse.swt.widgets.TabFolder folder = 
			(org.eclipse.swt.widgets.TabFolder)e.widget;
		org.eclipse.swt.widgets.TabItem item = folder.getSelection()[0];
		return this.item.equals(item);
	}
}
