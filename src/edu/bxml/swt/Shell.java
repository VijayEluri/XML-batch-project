package edu.bxml.swt;

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlParser;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;

/**
 * Instances of this class represent the "windows" which the desktop or "window manager" is managing.
 */
@attribute(value = "", required = false)
public class Shell extends ActsAsComposite {
	private static Log log = LogFactory.getLog(Shell.class);
	org.eclipse.swt.widgets.Composite GUIParent = null;
	private Display display = null;
	private Vector<Menu> menus = new Vector<Menu>();
	private String defaultButton = null;
	private boolean opened = false;
	private boolean visible = false;
	private boolean pack = true;

	org.eclipse.swt.widgets.Shell shell = null;
	
	public boolean processRawAttributes(org.xml.sax.Attributes attrs) {
		String style = attrs.getValue("style");
		this.style = XmlParser.getFieldValues(SWT.class, style);
		display = Display.getDefault();
		shell = new org.eclipse.swt.widgets.Shell (display);
		return true;
	}
	
	public org.eclipse.swt.widgets.Composite getGUIObject() {
		return shell;
	}
	
	public void init(XmlObject parent) throws XMLBuildException {
		super.init(parent);

	}
	
	@Override
	public void execute() throws XMLBuildException {

		if (shell.isDisposed())
			return;
		
		super.execute();
		
		if (defaultButton != null) {
			Button b = (Button) getSymbolTable().get(defaultButton);
			if (b != null) {
				org.eclipse.swt.widgets.Button button = b.getGUIObject();
				shell.setDefaultButton(button);
			}
		}
		
		if (focus != null) {
			ControlObject b = (ControlObject) getSymbolTable().get(focus);
			if (b != null) {
				org.eclipse.swt.widgets.Control c = b.getGUIObject();
				c.setFocus();
			}
		}

		if (pack)
			shell.pack ();
		for (Menu m: menus) {
			m.execute();
		}
		
		shell.setVisible(visible);
		if (!opened) {
			opened = true;
			shell.open();
			while (!shell.isDisposed ()) {
				if (!display.readAndDispatch ()) display.sleep ();
			}
			Shell s = (Shell) getAncestorOfType(Shell.class);
			if (s == null) {
				log.debug("display.dispose");
				display.dispose ();
			}
		}
	}
	@Override
	public void check() throws XMLBuildException {

	}

	/**
	 * BORDER, CLOSE, MIN, MAX, NO_TRIM, RESIZE, TITLE, ON_TOP, TOOL, APPLICATION_MODAL, MODELESS, PRIMARY_MODAL, SYSTEM_MODAL
	 * events: Activate, Close, Deactivate, Deiconify, Iconify  
	 */
	@attribute(value = "", required = false)
	public void setStyle(String style) {
		//dummy
	}
	
	/**
	 * Set the default button.
	 * The default button is the button that is selected when the receiver is active and the user presses ENTER. 
	 */
	@attribute(value = "", required = false)
	public void setDefaultButton(String button) {
		this.defaultButton = button;
	}
	
	/**
	 * Sets whether it is visible
	 */
	@attribute(value = "", required = false)
	public void setVisible(Boolean visible) {
		this.visible = visible;
	}
	public void setVisible(String visible) throws XMLParseException {
		setVisible(Boolean.parseBoolean(visible));
	}
	
	/**
	 * Pack the controls in the shell
	 */
	@attribute(value = "", required = false, defaultValue="true")
	public void setPack(Boolean text) {
		pack = text;
	}
	public void setPack(String text) {
		setPack(Boolean.parseBoolean(text));
	}
	
	/**
	 * Set the text (title bar text)
	 */
	@attribute(value = "", required = false)
	public void setText(String text) {
		shell.setText(text);
	}
	
	/**
	 * Add a menu
	 */
	@attribute(value = "", required = false)
	public void addMenu(Menu menu) {
		menu.setGUIParent(shell);
		menus.add(menu);
	}
	
	/**
	 * Add a group
	 */
	@attribute(value = "", required = false)
	public void addGroup(Group group) {
		group.setGUIParent(shell);
		children.add(group);
	}
	
	/**
	 * Add a tab folder
	 */
	@attribute(value = "", required = false)
	public void addTabFolder(edu.bxml.swt.TabFolder tabFolder) {
		tabFolder.setGUIParent(shell);
		children.add(tabFolder);
	}
	
	/**
	 * Add a composite
	 */
	@attribute(value = "", required = false)
	public void addComposite(edu.bxml.swt.Composite c) {
		c.setGUIParent(shell);
		children.add(c);
	}

	public org.eclipse.swt.widgets.Composite getGUIParent() {
		return GUIParent;
	}

	public void setGUIParent(org.eclipse.swt.widgets.Widget parent) {
		GUIParent = (org.eclipse.swt.widgets.Composite) parent;
	}
}
