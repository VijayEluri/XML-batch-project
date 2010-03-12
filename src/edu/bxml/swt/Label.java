package edu.bxml.swt;

import org.eclipse.swt.SWT;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlParser;
import com.javalobby.tnt.annotation.attribute;

import edu.bxml.swt.Enumerations.align;

/**
 * Instances of this class represent a non-selectable user interface object that displays a string or image. When SEPARATOR is specified, displays a single vertical or horizontal line.
 * See also ControlObject. 
 */
@attribute(value = "", required = false)
public class Label extends ControlObject {
	org.eclipse.swt.widgets.Composite GUIParent = null;
	org.eclipse.swt.widgets.Label label = null;
	
	public org.eclipse.swt.widgets.Label getGUIObject() {
		return label;
	}
	
	
	/**
	 * SEPARATOR, HORIZONTAL, VERTICAL, SHADOW_IN, SHADOW_OUT, SHADOW_NONE, CENTER, LEFT, RIGHT, WRAP 
	 */
	@attribute(value = "", required = false)
	public void setStyle(String style) {
		//dummy
	}
	
	public boolean processRawAttributes(org.xml.sax.Attributes attrs) {
		String style = attrs.getValue("style");
		this.style = XmlParser.getFieldValues(SWT.class, style);
		label = new org.eclipse.swt.widgets.Label(getGUIParent(), this.style);
		return true;
	}
	
	@Override
	public void execute() throws XMLBuildException {
	}
	
	/**
	 * Sets the text 
	 */
	@attribute(value = "", required = false)
	public void setText(String text) {
		label.setText(text);
	}
	
	/**
	 * Controls how text and images will be displayed in the receiver
	 */
	@attribute(value = "", required = false)
	public void setAlignment(align alignment) {
		label.setAlignment(XmlParser.getFieldValues(SWT.class, alignment.toString()));
	}
	public void setAlignment(String alignment) {
		setAlignment(align.valueOf(alignment));
	}
	
	@Override
	public void check() throws XMLBuildException {

	}
	public org.eclipse.swt.widgets.Composite getGUIParent() {
		return GUIParent;
	}
	public void setGUIParent(org.eclipse.swt.widgets.Widget parent) {
		GUIParent = (org.eclipse.swt.widgets.Composite) parent;
	}
}
