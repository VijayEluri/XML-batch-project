package edu.bxml.io;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;

/**
 * Hold file system information
 * 
 */
@attribute(value = "", required = true)
public class FileObject extends XmlObject implements Runnable {
	private static Log log = LogFactory.getLog(FileObject.class);
	String dir = ".";
	String file = ".*";
	String toDir = null;
	String toFile = null;
	String archive = null;
	String text = null;
	InputStream in = null;
	OutputStream out = null;
	
    public void run() {
        try {
        	log.debug("FileObject: RUN called");
			execute();
		} catch (XMLBuildException e) {
			e.printStackTrace();
		}
    }
	
    /**
     * Directory path
     * 
     */
    @attribute(value = "", required = false)
	public void setDir(java.lang.String dir) {
		this.dir = dir;
	}
	
	public void setInputStream(InputStream in) {
		log.debug("FileObject: set input stream");
		this.in = in;
	}
	
	public void setOutputStream(OutputStream out) {
		this.out = out;
	}

	/**
	 * File name
	 * 
	 */
	@attribute(value = "", required = false)
	public void setFile(String file) {
		this.file = file;
	}
	
	/**
	 * Destination Directory path
	 * 
	 */
	@attribute(value = "", required = false)
	public void setToDir(String toDir) {
		this.toDir = toDir;
	}
	
	/**
	 * Destination file name
	 * 
	 */
	@attribute(value = "", required = false)
	public void setToFile(String toFile) {
		this.toFile = toFile;
	}
	
	public void setArchive(String archive) {
		this.archive = archive;
	}
	
	public void execute() throws XMLBuildException {}
	public void check() throws XMLBuildException {}
	public void setText(String text) {}
	public void setFromTextContent(String text) {
		this.text = text;
	}
}
