package edu.bxml.swt;

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;
import com.browsexml.core.XmlParser;
import com.javalobby.tnt.annotation.attribute;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;

import edu.bxml.http.Get;
import edu.bxml.http.Parameter;

/**
 * Execute children when an event of the given type occurs
 */
@attribute(value = "", required = false)
public class Listener extends XmlObjectImpl implements XmlObject {
	private static Log log = LogFactory.getLog(Listener.class);
	int mask = 0;
	org.eclipse.swt.widgets.Widget parent = null;
	Vector<XmlObject>actions = new Vector<XmlObject>();
	Vector<Parameter>parameters = new Vector<Parameter>();
	final Display display = Display.getDefault();
	
	public void setGUIParent(org.eclipse.swt.widgets.Widget parent) {
		this.parent = parent;
	}
	
	@Override
	public void execute() throws XMLBuildException {
	}
	
	public void runActions() {
		for (XmlObject action: actions) {
			try {
				action.execute();
			} catch (XMLBuildException e) {
				e.printStackTrace();
				break;
			} 
		}
	}
	
	@Override
	public void check() throws XMLBuildException {
		//log.debug("Listener: " + parent + ".addListener  mask = " + mask);
		parent.addListener(mask, new org.eclipse.swt.widgets.Listener() {
			public void handleEvent (Event e) {
				//log.debug("call handle event for " + getParent());
				if (!((GUIObject)getParent()).handleEvent(e)) 
					return;
				//log.debug("create RUNNABle");
				Runnable job = new Runnable() {
					public void run() {
						log.debug("Listener FIRED!");
						runActions();
					}
				};
				BusyIndicator.showWhile(display, job);
			}
		});
	}

	/**
	 * The type of event to listen for.  The value depends on the parent.
	 */
	@attribute(value = "", required = false)
	public void setMask(String mask) throws XMLParseException {
		this.mask = XmlParser.getFieldValues(SWT.class, mask);
	}
	
	/**
	 * Run an html:get operation on listener.
	 */
	@attribute(value = "", required = false)
	public void addGet(Get get) {
		actions.add(get);
	}
	
	public void addParameter(Parameter parameter) {
		parameters.add(parameter);
	}
	
	/**
	 * Exit the System
	 */
	@attribute(value = "", required = false)
	public void addXmlObject(XmlObject t) {
		actions.add(t);
	}
	
}
