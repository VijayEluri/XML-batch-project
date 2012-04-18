package edu.bxml.aj.io;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.javalobby.tnt.annotation.attribute;

import edu.bxml.io.FilterAJ;
import edu.bxml.io.TeeOutputStream;

/**
 * Copy a file
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class Tee extends FilterAJ  {
	private static Log log = LogFactory.getLog(Tee.class);
	OutputStream teeOut = null;
	
	@Override
	public void closeOut() {
		if (getCloseOut()) {
			try {
				teeOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Tee() {
		log.debug("TEE instantiated");
	}
	
	@Override
	public void execute() throws XMLBuildException  {
		try {
			// Replace the 'out' stream with the TeeOut stream
			// before execute and then restore
			teeOut = new FileOutputStream(outFile);
			TeeOutputStream tee = new TeeOutputStream(teeOut, out);
			OutputStream o = out;
			out = tee;
			super.execute();
			out = o;
			tee.close();
			teeOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}catch (NullPointerException e) {
			e.printStackTrace();
		}
		finally {
			try {
				teeOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
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
	}
}
