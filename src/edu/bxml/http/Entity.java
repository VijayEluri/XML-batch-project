package edu.bxml.http;



import org.apache.commons.httpclient.methods.FileRequestEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;

/**
 * Get an http web page
 * 
 */
@attribute(value = "", required = false)
public class Entity extends XmlObject {
	private static Log log = LogFactory.getLog(Entity.class);
	java.io.File file = null;
	String type = null;
	
	@Override
	public void execute() throws XMLBuildException {

	}
	@Override
	public void check() throws XMLBuildException {
	}
	
	public void setFile(String file) {
		this.file = new java.io.File(file);
	}
	
	public void setType (String type ) {
		this.type= type;
	}

	public FileRequestEntity getEntity() {
		log.debug("REQUEST ENTIY file = " + file + "  type = " + type);
		return new FileRequestEntity(file, type);
	}

}
