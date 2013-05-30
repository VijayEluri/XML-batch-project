package edu.bxml.svg ;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;
import com.javalobby.tnt.annotation.attribute;

/**
 * Draw a Scaled Vector Graphics image
 */
/*import ;*/
@attribute(value = "", required = false)
public class Create extends XmlObjectImpl implements XmlObject {
	private static Log log = LogFactory.getLog(Create.class);
	String diagram = null;
	Er er = null;
	
	public boolean processRawAttributes(org.xml.sax.Attributes attrs) {
		return true;
	}

	@Override
	public void execute() throws XMLBuildException {
		log.debug("CREATE execute method called");
		if (er == null) {
			er = (Er) symbolTableLookUp(diagram);
			log.debug("er = " + er);
		}
		try {
			//er.addTable();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void check() throws XMLBuildException {
		log.debug("CHECK Create");
		if (diagram == null) {
			throw new XMLBuildException("Diagram must be specified.");
		}
		
	}
	
	public void setDiagram(String diagram) {
		log.debug("diagram = " + diagram);
		this.diagram = diagram;

	}

	public void setObject(String name) {
		
	}
}



