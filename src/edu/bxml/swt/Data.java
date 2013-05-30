package edu.bxml.swt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;
import com.javalobby.tnt.annotation.attribute;

/**
 * Is the root object for xml files attempting to supply data to populate
 * interface objects.  Usually children should be 'Apply' objects.
 * If the parent is an 'Apply' object and the children are other types of objects,
 * then those ojects will be sent to the parent 'Apply' which in turn will use
 * the data to populate an object in the interface.
 */
@attribute(value = "", required = false)
public class Data extends XmlObjectImpl implements XmlObject {
	private static Log log = LogFactory.getLog(Data.class);
	Apply apply = null; 
	
	public void init(XmlObject parent) throws XMLBuildException {
		super.init(parent);
		apply = (Apply) getAncestorOfType(Apply.class);
		if (apply == null) {
			log.debug("Data has no ancestor of type Apply");
		}
	}

	@Override
	public void execute() throws XMLBuildException {
		
	}
	@Override
	public void check() throws XMLBuildException {
	}
	
	/**
	 * Add an object to the parent Apply; or if the object is an 'Apply' 
	 * just let it run itself.
	 */
	@attribute(value = "", required = false)
	public void addXmlObject(XmlObject p) {}
	public void addXmlObjectEnd(XmlObject p) throws XMLBuildException {
		if (apply != null) {
			apply.addXmlObjectEnd(p);	
		}
	}
	
}
