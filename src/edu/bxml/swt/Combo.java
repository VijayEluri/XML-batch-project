package edu.bxml.swt;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlParser;
import com.javalobby.tnt.annotation.attribute;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;

/**
 * A combo box gui item
 */
@attribute(value = "", required = false)
public class Combo extends ControlObject {
	private static Log log = LogFactory.getLog(Combo.class);
	org.eclipse.swt.widgets.Combo combo = null;
	org.eclipse.swt.widgets.Composite GUIParent = null;
	HashMap<String, String> values = new HashMap<String, String>();
	
	public org.eclipse.swt.widgets.Combo getGUIObject() {
		return combo;
	}
	
	public boolean processRawAttributes(org.xml.sax.Attributes attrs) {
		String style = attrs.getValue("style");
		this.style = XmlParser.getFieldValues(SWT.class, style);
		combo = new org.eclipse.swt.widgets.Combo(getGUIParent(), this.style);
		return true;
	}
	
	/**
	 * DROP_DOWN, READ_ONLY, SIMPLE 
	 * Events: DefaultSelection, Modify, Selection 
	 */
	@attribute(value = "", required = false, defaultValue="NONE")
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
	 * Clear the dropdown list
	 */
	@attribute(value = "", required = false, defaultValue="false")
	public void removeAll() {
		combo.removeAll();
	}

	/**
	 * Set the text
	 */
	@attribute(value = "", required = false, defaultValue="blank")
	public void setText(String text) {
		combo.setText(text);
	}
	
	/**
	 * Set the number of items appearing without the need to scroll.
	 * If more items this this are in the drop down list, a scrollbar
	 * will be drawn at the right.
	 */
	@attribute(value = "", required = false, defaultValue="SWT default")
	public void setVisibleItemCount(Integer count) {
		combo.setVisibleItemCount(count);
	}
	public void setVisibleItemCount(String count) throws XMLParseException {
		setVisibleItemCount(Integer.parseInt(count));
	}
	
	public void setItems(String items) {
		log.debug("COMBO SET ITEMS");
		edu.bxml.swt.Linkedhashmap lhm = (edu.bxml.swt.Linkedhashmap) getSymbolTable().get(items);
		HashMap values = lhm.values;
		log.debug("values size = " + values.size());
		Iterator myVeryOwnIterator = values.keySet().iterator();
		while(myVeryOwnIterator.hasNext()) {
			String item = (String) myVeryOwnIterator.next();
			log.debug(item);
			combo.add(item);
		}
	}
	
	/**
	 * Add an item to the dropdown list
	 */
	@attribute(value = "", required = false)
	public void addItem(Item i) {
	}
	
	public void addItemEnd(Item i) {
		combo.add(i.getValue());
		values.put( i.getValue(), i.getKey());
	}
	
	public String getText() {
		return values.get(combo.getText());
	}

	public org.eclipse.swt.widgets.Composite getGUIParent() {
		return GUIParent;
	}

	public void setGUIParent(org.eclipse.swt.widgets.Widget parent) {
		GUIParent = (org.eclipse.swt.widgets.Composite)parent;
	}
	
	public String toString() {
		if (combo != null)
			return values.get(combo.getText());
		return "";
	}
	
}
