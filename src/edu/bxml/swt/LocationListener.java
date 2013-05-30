package edu.bxml.swt;

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.widgets.Display;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;
import com.javalobby.tnt.annotation.attribute;

import edu.bxml.http.Get;
import edu.bxml.http.Parameter;

/**
 * Execute children when an event of the given type occurs
 */
@attribute(value = "", required = false)
public class LocationListener extends XmlObjectImpl implements XmlObject {
	private static Log log = LogFactory.getLog(LocationListener.class);
	org.eclipse.swt.browser.Browser parent = null;
	Vector<XmlObject>actions = new Vector<XmlObject>();
	Vector<Parameter>parameters = new Vector<Parameter>();
	private XMLBuildException runException = null;
	final Display display = Display.getDefault();
	private String url = null;
	
	public String getUrl() {
		return url;
	}
	
	public void setGUIParent(org.eclipse.swt.browser.Browser parent) {
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
		log.debug("Listener: " + parent + ".addLocationListener");
		org.eclipse.swt.browser.LocationListener x = 
			new org.eclipse.swt.browser.LocationListener() {
			public void changing(LocationEvent e) {
				if (e.location.startsWith("bxml")) {
					e.doit = false;
					url = e.location;
					runActions();
				}
				else
					log.debug("changing = " + e.location);
			}
			public void changed(LocationEvent e) {
				log.debug("changed = " + e.location);
			}
		};
		parent.addLocationListener(x);
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
