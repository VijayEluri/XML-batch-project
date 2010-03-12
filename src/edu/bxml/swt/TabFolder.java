package edu.bxml.swt;

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlParser;
import com.javalobby.tnt.annotation.attribute;

/**
 * Instances of this class implement the notebook user interface metaphor
 */
@attribute(value = "", required = false)
public class TabFolder extends ActsAsComposite {
	private static Log log = LogFactory.getLog(TabFolder.class);
	org.eclipse.swt.widgets.Composite GUIParent = null;
	org.eclipse.swt.widgets.TabFolder tabFolder = null;
	int style = 0;
	GridData gridData = null;
	Vector<TabItem> items = new Vector<TabItem>();
	
	public boolean processRawAttributes(org.xml.sax.Attributes attrs) {
		String style = attrs.getValue("style");
		this.style = XmlParser.getFieldValues(SWT.class, style);
		tabFolder = new org.eclipse.swt.widgets.TabFolder(getGUIParent(), this.style);
		return true;
	}
	
	@Override
	public void execute() throws XMLBuildException {
		if (layout != null) {
			log.debug("at " + this.getLocator().getLineNumber());
			new XMLBuildException (this.getName() + " layout should never be set for tabFolder" ).printStackTrace();
		}
		if (gridData != null) {
			gridData.execute();
			org.eclipse.swt.layout.GridData g = gridData.getGUIObject();
			tabFolder.setLayoutData(g);
		}
		for (TabItem item:items) {
			item.setGUIParent(tabFolder);
			item.execute();
		}
	}
	
	@Override
	public void check() throws XMLBuildException {

	}

	/**
	 * TOP, BOTTOM 
	 * Events:  Selection
	 */
	@attribute(value = "", required = false)
	public void setStyle(String style) {
		//dummy 
	}
	
	/**
	 * Selects the item at the given zero-relative index in the receiver.
	 */
	@attribute(value = "", required = false)
	public void setSelection(Integer select) {
		tabFolder.setSelection(select);
	}
	public void setSelection(String select) throws XMLBuildException {
		setSelection(Integer.parseInt(select));
	}
	
	public org.eclipse.swt.widgets.Composite getGUIObject() {
		return tabFolder;
	}
	
	/**
	 * Add grid data
	 */
	@attribute(value = "", required = false)
	public void addGridData(GridData gridData) {
		this.gridData = gridData;
	}
	
	/**
	 * Add tab item
	 */
	@attribute(value = "", required = false)
	public void addTabItem(TabItem tabItem) {
		tabItem.setGUIParent(tabFolder);
		items.add(tabItem);
	}
	public org.eclipse.swt.widgets.Composite getGUIParent() {
		return GUIParent;
	}
	public void setGUIParent(org.eclipse.swt.widgets.Widget parent) {
		GUIParent = (org.eclipse.swt.widgets.Composite) parent;
	}
}
