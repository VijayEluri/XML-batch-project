package edu.bxml.swt;

import org.eclipse.swt.SWT;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlParser;
import com.javalobby.tnt.annotation.attribute;

/**
 * A selectable user interface object that issues notification when pressed and released. 
 */
@attribute(value = "", required = false)
public class MenuItem extends GUIObject {
	int style = 0; //CHECK, CASCADE, PUSH, RADIO, SEPARATOR 
	org.eclipse.swt.widgets.Menu parentMenu = null;
	org.eclipse.swt.widgets.MenuItem menuItem = null; 
	
	public boolean processRawAttributes(org.xml.sax.Attributes attrs) throws XMLBuildException {
		if (parentMenu == null) {
			throw new XMLBuildException("Parent menu has not been set");
		}
		String style = attrs.getValue("style");
		this.style = XmlParser.getFieldValues(SWT.class, style);
		menuItem = new org.eclipse.swt.widgets.MenuItem(parentMenu, this.style);
		return true;
	}
	
	@Override
	public void execute() throws XMLBuildException {

	}
	@Override
	public void check() throws XMLBuildException {

	}

	/**
	 * Set the text of the menu item 
	 */
	@attribute(value = "", required = false)
	public void setText(String text) {
		menuItem.setText(text);
	}
	
	/**
	 * CHECK, CASCADE, PUSH, RADIO, SEPARATOR
	 * events: Arm, Help, Selection  
	 */
	@attribute(value = "", required = false)
	public void setStyle(String text) {
		//dummy
	}
	
	/**
	 * Add a sub-menu to this item
	 */
	@attribute(value = "", required = false)
	public void addMenu(Menu childMenu) {
		childMenu.setGUIParent(menuItem);
	}
	public void addMenuEnd(Menu childMenu) {
		menuItem.setMenu(childMenu.getGUIObject());
	}

	public void setGUIParent(org.eclipse.swt.widgets.Widget m) {
		parentMenu = (org.eclipse.swt.widgets.Menu) m;
	}
	
	public org.eclipse.swt.widgets.Menu getGUIParent() {
		return parentMenu;
	}
	
	public org.eclipse.swt.widgets.MenuItem getGUIObject() {
		//log.debug("menuitem.getGUI = " + menuItem);
		return menuItem; 
	}

}
