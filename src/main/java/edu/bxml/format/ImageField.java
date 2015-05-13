package edu.bxml.format;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;

/**
 * Hold information about how to format one field if embedded in a Select object or 
 * how to expect data to be formated in a flat file if embedded in a Load object.
 * <p>Also has all the attributes and nested elements that Field has.</p>
 * 
 */
@attribute(value = "", required = true)
public class ImageField extends Field {
	private static Log log = LogFactory.getLog(ImageField.class);
	Select select = null;
	String filename = null;
	String dir = null;
	
	ImageField() {
		type = Types.IMAGE;
	}
	/**
	 * The name fo the file containing binary data to load  
	 */
	@attribute(value = "", required = true)
	public void setFile(String filename) {
		this.filename = filename;
		log.debug("FILENAME = " + filename);
	}
	
	/**
	 * The name fo the directory containing binary data to load  
	 */
	@attribute(value = "", required = true)
	public void setDir(String dir) {
		this.dir = dir;
		log.debug("DIR = " + dir);
	}
	
	
	public File getFile() {
		Load l = (Load) getParentOfType(Load.class);
		File f = l.getCurrentFile();
		log.debug("file name = " + f.getAbsolutePath());
		return f;
	}
	/**
	 * let the parent identify itself to this object
	 */

	@Override
	public String format(Object v) {
		return null;
	}

	public String insertFormat(String value) {
		return null;
	}
	
	public void check() throws XMLBuildException {
		super.check();
		
		XmlObject x = this;
		while (x != null && !((x = x.getParent()) instanceof Select));
		select = (Select) x;
	}
	
	public Object getParentOfType(Class theClass) {
		XmlObject x = this;
		while (x != null && !((x = x.getParent()).getClass().equals(theClass) ));
		return x;
	}
	
	public String getSQLType() {
		return "blob";
	}
	
	public Object getObject(String value) {
		return null;
	}
}
