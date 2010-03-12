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
public class Composite extends ActsAsComposite {
	org.eclipse.swt.widgets.Composite composite = null;
	org.eclipse.swt.widgets.Composite GUIParent = null;

	public org.eclipse.swt.widgets.Composite getGUIObject() {
		return composite;
	}
	
	/**
	 * Determines what style (NO_BACKGROUND, NO_FOCUS, NO_MERGE_PAINTS, 
	 * NO_REDRAW_RESIZE, NO_RADIO_GROUP, EMBEDDED, DOUBLE_BUFFERED) 
	 */
	@attribute(value = "", required = false, defaultValue="NONE")
	public void setStyle(String style) {
		// dummy function for comment
	}
	
	public boolean processRawAttributes(org.xml.sax.Attributes attrs) {
		String style = attrs.getValue("style");
		this.style = XmlParser.getFieldValues(SWT.class, style);
		composite =	new org.eclipse.swt.widgets.Composite(getGUIParent(), this.style);
		return true;
	}
	
	@Override
	public void execute() throws XMLBuildException {

		super.execute();
	}
	@Override
	public void check() throws XMLBuildException {
		
	}
	
	/**
	 * Add a group
	 */
	@attribute(value = "", required = false)
	public void addGroup(Group group) {
		group.setGUIParent(composite);
		children.add(group);
	}

	/**
	 * Add a swing object 
	 */
	public void addSwing(Swing swing) {
		swing.setGUIParent(composite);
		composite.setBounds(0, 0, 2, 2);
		children.add(swing);


	}
	
	public org.eclipse.swt.widgets.Composite getGUIParent() {
		return GUIParent;
	}

	public void setGUIParent(org.eclipse.swt.widgets.Widget parent) {
		GUIParent = (org.eclipse.swt.widgets.Composite) parent;
	}

}
