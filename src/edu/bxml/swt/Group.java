package edu.bxml.swt;

import org.eclipse.swt.SWT;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlParser;
import com.javalobby.tnt.annotation.attribute;

/**
 * Instances of this class provide an etched border with an optional title.
 * See also ActsAsComposite
 */
@attribute(value = "", required = false)
public class Group extends ActsAsComposite {
	org.eclipse.swt.widgets.Group group = null;
	org.eclipse.swt.widgets.Composite GUIParent = null;

	public org.eclipse.swt.widgets.Composite getGUIObject() {
		return group;
	}
	
	public boolean processRawAttributes(org.xml.sax.Attributes attrs) {
		String style = attrs.getValue("style");
		this.style = XmlParser.getFieldValues(SWT.class, style);
		group = new org.eclipse.swt.widgets.Group (getGUIParent(), this.style);
		return true;
	}
	
	@Override
	public void execute() throws XMLBuildException {
		super.execute();
	}
	@Override
	public void check() throws XMLBuildException {

	}
	
	public void setText(String text) {
		group.setText(text);
	}

	public org.eclipse.swt.widgets.Composite getGUIParent() {
		return GUIParent;
	}

	public void setGUIParent(org.eclipse.swt.widgets.Widget parent) {
		GUIParent = (org.eclipse.swt.widgets.Composite) parent;
	}
	
	enum Styles {SHADOW_ETCHED_IN, SHADOW_ETCHED_OUT, SHADOW_IN, SHADOW_OUT, SHADOW_NONE};
	/**
	 * SHADOW_ETCHED_IN, SHADOW_ETCHED_OUT, SHADOW_IN, SHADOW_OUT, SHADOW_NONE 
	 * <br/>Events: None
	 */
	@attribute(value = "", required = false)
	public void setStyle(Styles style) {
		//dummy
	}
}
