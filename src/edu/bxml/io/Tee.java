package edu.bxml.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlParser;
import com.javalobby.tnt.annotation.attribute;

/**
 * Copy a file
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class Tee extends Filter  {
	private static Log log = LogFactory.getLog(Tee.class);
	String teeDir = null;
	String teeFile = null;
	
	public OutputStream getOutFilter(OutputStream out) throws IOException {
		FileOutputStream fileOut = null;
		try {
			String fileName = XmlParser.processAttributes(this, teeFile);
			File teeFile = new File(teeDir, fileName);
			log.debug("tee file = " + teeFile);
			fileOut = new FileOutputStream(teeFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		} catch (XMLBuildException be) {
			be.printStackTrace();
			throw new IOException(be.getMessage());
		}
		TeeOutputStream tee = new TeeOutputStream(fileOut, out);
		return tee;
	}
	
	public void finish(OutputStream out) {
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * check that all the fields are set correctly, especially required fields.
	 * Called when the end-tag of the element has been reached and processed.
	 */
	public void check() throws XMLBuildException {
		if (toDir == null) 
			throw new XMLBuildException("toDir must be set");
		if (toFile == null) 
			throw new XMLBuildException("toFile must be set");
		if (dir != null) 
			throw new XMLBuildException("dir must not be set");
		if (file != null) 
			throw new XMLBuildException("file must not be set");
		this.teeFile = toFile;
		this.teeDir = toDir;
		toFile = null;
		toDir = null;
	}
}
