package edu.bxml.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;

import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;
import com.browsexml.core.XmlParser;
import com.javalobby.tnt.annotation.attribute;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;


/**
 * All gui objects are subclassed from this.
 */
@attribute(value = "", required = false)
public abstract class GUIObject extends XmlObjectImpl implements XmlObject {
	int style = 0;
	String text = null;
	GridLayout gridLayout = null;
	
	public abstract org.eclipse.swt.widgets.Widget getGUIObject();
	abstract public void setGUIParent(org.eclipse.swt.widgets.Widget parent);
	abstract public org.eclipse.swt.widgets.Widget getGUIParent();
	
	public void setStyle(String style) throws XMLParseException {
		this.style = XmlParser.getFieldValues(SWT.class, style);
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	/**
	 * Set the layout
	 */
	@attribute(value = "", required = false)
	public void addGridLayout(GridLayout gridLayout) {
		this.gridLayout = gridLayout;
	}
	
	/**
	 * Listen for events and invoke execute on child object
	 */
	@attribute(value = "", required = false)
	public void addListener(Listener listener) {
		
	/*}
	public void addListenerEnd(Listener listener) {*/
		listener.setGUIParent(this.getGUIObject());
	}
	/**
	 * return true if you want the event to be processed.  Allows for filtering
	 * based on the widget the event is coming from specifically for tabFolder.
	 * @param e
	 * @return
	 */
	public boolean handleEvent(Event e) {
		return true;
	}
}
