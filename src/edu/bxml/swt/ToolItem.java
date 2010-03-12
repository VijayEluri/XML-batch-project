package edu.bxml.swt;

import org.eclipse.swt.SWT;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlParser;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;

/**
 * A push button, radio button or check box gui object
 */
@attribute(value = "", required = false)
public class ToolItem extends GUIObject {
	org.eclipse.swt.widgets.ToolBar GUIParent = null;
	org.eclipse.swt.widgets.ToolItem toolItem = null;
	boolean isDefault = false;
	
	public org.eclipse.swt.widgets.ToolItem getGUIObject() {
		return toolItem;
	}
	
	public boolean processRawAttributes(org.xml.sax.Attributes attrs) {
		String style = attrs.getValue("style");
		this.style = XmlParser.getFieldValues(SWT.class, style);
		toolItem = new org.eclipse.swt.widgets.ToolItem(getGUIParent(), this.style);
		return true;
	}
	
	/**
	 * ARROW, CHECK, PUSH, RADIO, TOGGLE, FLAT, UP, DOWN, LEFT, RIGHT, CENTER 
	 * Events: Selection 
	 */
	@attribute(value = "", required = false, defaultValue="NONE")
	public void setStyle(String style) {
		// dummy function for comment
	}
	public void init(XmlObject parent) throws XMLBuildException {
		super.init(parent);
	}
	
	@Override
	public void execute() throws XMLBuildException {
	}
	@Override
	public void check() throws XMLBuildException {

	}
	
	/**
	 * Set the text associated with the button
	 */
	@attribute(value = "", required = false, defaultValue="blank string")
	public void setText(String text) {
		toolItem.setText(text);
	}
	
	/**
	 * Set the selection of the button.  For check box style buttons, setting this
	 * to true will cause the button to be checked.
	 */
	@attribute(value = "", required = false, defaultValue="false")
	public void setSelection(Boolean text) {
		toolItem.setSelection(text);
	}
	public void setSelection(String text) {
		setSelection(Boolean.parseBoolean(text));
	}

	public org.eclipse.swt.widgets.ToolBar getGUIParent() {
		return GUIParent;
	}

	public void setGUIParent(org.eclipse.swt.widgets.Widget parent) {
		GUIParent = (org.eclipse.swt.widgets.ToolBar)parent;
	}
	
	public String toString() {
		if (toolItem != null)
			return Boolean.toString(toolItem.getSelection());
		return "";
	}
}
