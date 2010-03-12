package edu.bxml.swt;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlParser;
import com.javalobby.tnt.annotation.attribute;

/**
 * Create a menu
 */
@attribute(value = "", required = false)
public class Menu extends XmlObject {
	Control cntParent = null;
	org.eclipse.swt.widgets.MenuItem miParent = null;
	org.eclipse.swt.widgets.Shell shellParent = null;
	
	Vector<MenuItem> menuItems = new Vector<MenuItem>();
	org.eclipse.swt.widgets.Menu menu = null;
	int style = 0;
	
	public boolean processRawAttributes(org.xml.sax.Attributes attrs) throws XMLBuildException {
		String style = attrs.getValue("style");
		this.style = XmlParser.getFieldValues(SWT.class, style);
		
		if (shellParent != null) {
			menu = new org.eclipse.swt.widgets.Menu(shellParent, this.style);
			if (this.style == SWT.BAR) {
				((org.eclipse.swt.widgets.Shell)shellParent).setMenuBar(menu);
			}
		}
		else if (cntParent != null) {
			//log.debug("control Parent = " + cntParent);
			menu = new org.eclipse.swt.widgets.Menu(cntParent);
		}
		else {
			//log.debug("menu item Parent = " + miParent);
			menu = new org.eclipse.swt.widgets.Menu(miParent);
		}
		return true;
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
	 * Add a visible menu item to the menu
	 */
	@attribute(value = "", required = false)
	public void addMenuItem(MenuItem m) {
		m.setGUIParent(menu);
		menuItems.add(m);
	}

	public void setGUIParent(org.eclipse.swt.widgets.Shell c) {
		shellParent = c;
	}
	
	public void setGUIParent(Control c) {
		cntParent = c;
	}
	
	/**
	 * BAR, DROP_DOWN, POP_UP, NO_RADIO_GROUP, LEFT_TO_RIGHT, RIGHT_TO_LEFT 
	 * events: Help, Hide, Show 
	 */
	@attribute(value = "", required = false)
	public void setStyle(String style) {
		//dummy
	}
	
	public void setGUIParent(org.eclipse.swt.widgets.MenuItem mi) {
		miParent = mi;
	}
	
	public org.eclipse.swt.widgets.Menu getGUIObject() {
		//log.debug("getGUIObject returns " + menu);
		return menu;
	}
}
