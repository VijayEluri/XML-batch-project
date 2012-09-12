package edu.bxml.http;



import java.io.FileNotFoundException;

import org.apache.commons.httpclient.methods.multipart.FilePart;
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
public class File extends XmlObject {
	private static Log log = LogFactory.getLog(File.class);
	java.io.File file = null;
	
	@Override
	public void execute() throws XMLBuildException {

	}
	@Override
	public void check() throws XMLBuildException {
	}
	
	public void setFile(String file) {
		this.file = new java.io.File(file);
	}
	
	public FilePart getPart() throws XMLBuildException {
		try {
			log.debug("getting part... file = " + file);
			return new FilePart(file.getName(), file);
		} catch (FileNotFoundException e) {
			throw new XMLBuildException(e.getMessage());
		}
	}
}
