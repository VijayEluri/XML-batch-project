package edu.bxml.swt;

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Display;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;

/**
 * Root of swt portion of an xml file for gui objects
 */
@attribute(value = "", required = false)
public class Interface extends XmlObject {
	private static Log log = LogFactory.getLog(Interface.class);
	Vector<Linkedhashmap> lhm = new Vector<Linkedhashmap>();
	Vector<Shell> shells = new Vector<Shell>();
	private Display display = Display.getDefault();
	private Vector<XmlObject> generic = new Vector<XmlObject>();
	
	
	public void init(XmlObject parent) throws XMLBuildException {
		super.init(parent);
	}
	
	@Override
	public void execute() throws XMLBuildException {
		for (Linkedhashmap lm: lhm) {
			lm.execute();
		}
        for (XmlObject g: generic) {
        	g.execute();
        }
		for (Shell s: shells) {
			s.execute();
		}
	}
	@Override
	public void check() throws XMLBuildException {
		
	}
	
	public void addLinkedhashmap(Linkedhashmap lm) {
		lhm.add(lm);
	}
	
	/**
	 * Add a Shell
	 */
	@attribute(value = "", required = false)
	public void addShell(Shell s) {
		shells.add(s);
	}
	
	/**
	 * Add a Color
	 */
	@attribute(value = "", required = false)
	public void addColor(Color c) {}
	
	public Display getDisplay() {
		return display;
	}
	
	/**
	 * Apply setters and adders for an object
	 */
	@attribute(value = "", required = false)
	public void addApply(Apply a) {}
	
	public void addApplyEnd(Apply a) throws XMLBuildException {
		a.execute();
	}
	
	public void addTimer(Timer t) {
		
	}
	
	public void addXmlObject(XmlObject p) {
		log.debug("add GENERIC object " + p);
		generic.add(p);
	}
	
}
