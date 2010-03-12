package edu.bxml.swt;

import org.eclipse.swt.SWT;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlParser;
import com.javalobby.tnt.annotation.attribute;

/**
 * A composite holds other gui objects.
 * See also ActsAsComposite
 */
@attribute(value = "", required = false)
public class ToolBar extends ActsAsComposite {
	org.eclipse.swt.widgets.ToolBar toolbar = null;
	org.eclipse.swt.widgets.Composite GUIParent = null;

	public org.eclipse.swt.widgets.Composite getGUIObject() {
		return toolbar;
	}
	
	/**
	 * FLAT, WRAP, RIGHT, HORIZONTAL, VERTICAL, SHADOW_OUT 
	 * Events: none
	 */
	@attribute(value = "", required = false, defaultValue="NONE")
	public void setStyle(String style) {
		// dummy function for comment
	}
	
	public boolean processRawAttributes(org.xml.sax.Attributes attrs) {
		String style = attrs.getValue("style");
		this.style = XmlParser.getFieldValues(SWT.class, style);
		toolbar =	new org.eclipse.swt.widgets.ToolBar(getGUIParent(), this.style);
		return true;
	}
	
	@Override
	public void execute() throws XMLBuildException {

		super.execute();
	}
	@Override
	public void check() throws XMLBuildException {
		toolbar.pack();
	}
	
	/**
	 * Add an item
	 */
	@attribute(value = "", required = false)
	public void addToolItem(ToolItem i) {
		i.setGUIParent(toolbar);
	}

	
	public org.eclipse.swt.widgets.Composite getGUIParent() {
		return GUIParent;
	}

	public void setGUIParent(org.eclipse.swt.widgets.Widget parent) {
		GUIParent = (org.eclipse.swt.widgets.Composite) parent;
	}

}
