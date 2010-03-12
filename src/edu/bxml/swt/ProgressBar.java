package edu.bxml.swt;

import org.eclipse.swt.SWT;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlParser;
import com.javalobby.tnt.annotation.attribute;

/**
 * A combo box gui item
 */
@attribute(value = "", required = false)
public class ProgressBar extends ControlObject {
	org.eclipse.swt.widgets.ProgressBar progressBar = null;
	org.eclipse.swt.widgets.Composite GUIParent = null;
	
	public org.eclipse.swt.widgets.ProgressBar getGUIObject() {
		return progressBar;
	}
	
	public boolean processRawAttributes(org.xml.sax.Attributes attrs) {
		String style = attrs.getValue("style");
		this.style = XmlParser.getFieldValues(SWT.class, style);
		progressBar = new org.eclipse.swt.widgets.ProgressBar(getGUIParent(), this.style);
		return true;
	}
	
	/**
	 * SMOOTH, HORIZONTAL, VERTICAL, INDETERMINATE 
	 * Events: none 
	 */
	@attribute(value = "", required = false)
	public void setStyle(String style) {
		// dummy function for comment
	}
	
	@Override
	public void execute() throws XMLBuildException {
	}
	@Override
	public void check() throws XMLBuildException {
		
	}

	/**
	 * Set the maximum
	 */
	@attribute(value = "", required = false)
	public void setMaximum(Integer max) {
		progressBar.setMaximum(max);
	}
	public void setMaximum(String max) {
		setMaximum(Integer.parseInt(max)); 
	}
	
	/**
	 * Set the minimum
	 */
	@attribute(value = "", required = false)
	public void setMinimum(Integer min) {
		progressBar.setMinimum(min);
	}
	public void setMinimum(String min) {
		setMinimum(Integer.parseInt(min)); 
	}
	
	/**
	 * Set the selection
	 */
	@attribute(value = "", required = false)
	public void setSelection(Integer selection) {
		progressBar.setSelection(selection);
	}
	public void setSelection(String selection) {
		setSelection(Integer.parseInt(selection)); 
	}
	
	/**
	 * increment the selection
	 */
	@attribute(value = "", required = false)
	public void setIncrementSelection(Integer selection) {
		progressBar.setSelection(selection + progressBar.getSelection());
	}
	public void setIncrementSelection(String selection) {
		setIncrementSelection(Integer.parseInt(selection)); 
	}

	public org.eclipse.swt.widgets.Composite getGUIParent() {
		return GUIParent;
	}

	public void setGUIParent(org.eclipse.swt.widgets.Widget parent) {
		GUIParent = (org.eclipse.swt.widgets.Composite)parent;
	}
	
	public String toString() {
		if (progressBar != null)
			return "" + progressBar.getSelection();
		return "";
	}
	
}
