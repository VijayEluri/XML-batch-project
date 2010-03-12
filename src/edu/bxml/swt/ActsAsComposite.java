package edu.bxml.swt;

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.Executer;
import com.browsexml.core.XMLBuildException;
import com.javalobby.tnt.annotation.attribute;

/**
 * A composite holds other gui objects.
 * See also ControlObject
 */
@attribute(value = "", required = false)
public abstract class ActsAsComposite extends ControlObject {
	private static Log log = LogFactory.getLog(ActsAsComposite.class);
	public abstract org.eclipse.swt.widgets.Composite getGUIObject();
	protected String focus = null;
	Vector<GUIObject>children = new Vector<GUIObject>();
	Layout layout = null;
	
	public void execute() throws XMLBuildException {
		if (layout == null) {
			log.debug("at " + this.getLocator().getLineNumber());
			new XMLBuildException (this.getName() + " layout is not set " ).printStackTrace();
		}
		for (GUIObject child: children) {
			child.setGUIParent(getGUIObject());
			child.execute();
		}
	}
	
	/**
	 * NO_BACKGROUND, NO_FOCUS, NO_MERGE_PAINTS, NO_REDRAW_RESIZE, NO_RADIO_GROUP, EMBEDDED, DOUBLE_BUFFERED
	 * Events: None 
	 */
	@attribute(value = "", required = false)
	public void setStyle(String style) {
		//dummy
	}
	
	@attribute(value = "", required = false)
	public void setLayout() {
		if (layout != null) {
			this.getGUIObject().setLayout(layout.getGUIObject());
		}
	}
	
	@attribute(value = "", required = false)
	public void addGridLayout(edu.bxml.swt.GridLayout gridLayout) {
		this.layout = gridLayout;
		setLayout();
	}
	
	@attribute(value = "", required = false)
	public void addRowLayout(RowLayout rowLayout) {
		this.layout = rowLayout;
		setLayout();
	}
	
	@attribute(value = "", required = false)
	public void addControlObject(ControlObject o) {
		o.setGUIParent(getGUIObject());
		children.add(o);
	}
	
	@attribute(value = "", required = false)
	public void setFocus(String object) {
		this.focus = object;
	}
}
