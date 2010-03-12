package edu.bxml.swt;

import org.eclipse.swt.SWT;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlParser;
import com.javalobby.tnt.annotation.attribute;

/**
 * Instances of this class are selectable user interface objects that allow the user to enter and modify text.
 * See also Control Object 
 */
@attribute(value = "", required = false)
public class Browser extends ControlObject {
	private org.eclipse.swt.widgets.Composite GUIParent = null;
	private org.eclipse.swt.browser.Browser browser = null;
	
	public boolean processRawAttributes(org.xml.sax.Attributes attrs) {
		String style = attrs.getValue("style");
		this.style = XmlParser.getFieldValues(SWT.class, style);
		browser = new org.eclipse.swt.browser.Browser(getGUIParent(), this.style);
		return true;
	}
	
	/**
	 *  Styles: 
	 *  Events: CloseWindow Location OpenWindow Progress StatusText 
	 *  		Title VisibilityWindow
	 */
	@attribute(value = "", required = false)
	public void setStyle(String style) {
		//dummy 
	}
	
	public void setUrl(String url) {
		browser.setUrl(url);
	}
	
	public org.eclipse.swt.browser.Browser getGUIObject() {
		return browser;
	}
	
	/**
	 *  forward, back, refresh, stop
	 */
	@attribute(value = "", required = false)
	public void setNavigate(String value) {
		char action = value.charAt(0);
		switch (action) {
		case 'f': browser.forward(); break;
		case 'b': browser.back();break;
		case 's': browser.stop();break;
		case 'r': browser.refresh();break;
		}
	}
	
	@Override
	public void execute() throws XMLBuildException {
	}
	@Override
	public void check() throws XMLBuildException {

	}
	
	public void addLocationListener(LocationListener l) {
		l.setGUIParent(browser);
	}

	public org.eclipse.swt.widgets.Composite getGUIParent() {
		return GUIParent;
	}

	public void setGUIParent(org.eclipse.swt.widgets.Widget parent) {
		GUIParent = (org.eclipse.swt.widgets.Composite) parent;
	}
	
	public String toString() {
		if (browser != null)
			return browser.getUrl();
		return "";
	}
	
}

