package edu.bxml.http;

import java.util.Enumeration;
import java.util.Vector;

import org.apache.commons.httpclient.methods.multipart.Part;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;
/**
 * Get an http web page
 * 
 */
@attribute(value = "", required = false)
public class Parts extends XmlObject {

	Vector<XmlObject> parts = new Vector<XmlObject>();
	Part part[] = null;
	
	@Override
	public void execute() throws XMLBuildException {

	}
	@Override
	public void check() throws XMLBuildException {
	}
	
	public void addFile(File p) {
		parts.add(p);
	}
	
	public Part[] getParts() throws XMLBuildException {
		if (part == null) {
			part = new Part[parts.size()];
			int i = 0;
			for (Enumeration e = parts.elements();e.hasMoreElements();) {
				File f = (File) e.nextElement();
				part[i++] = f.getPart();
			}
		}
		return part;
	}
}
