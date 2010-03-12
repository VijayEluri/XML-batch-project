package edu.bxml.io;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;

import edu.bxml.format.Util;

/**
 *	Move (rename) a file
 * 
 */
@attribute(value = "", required = true)
public class Move extends XmlObject {
	private static Log log = LogFactory.getLog(Move.class);
	String file;
	String dir;
	String toFile;
	String toDir;
	boolean force = false;
	
	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public String getToFile() {
		return toFile;
	}

	public void setToFile(String toFile) {
		this.toFile = toFile;
	}

	public String getToDir() {
		return toDir;
	}

	public void setToDir(String toDir) {
		this.toDir = toDir;
	}

	public boolean isForce() {
		return force;
	}

	/**
	 * If the move operation would overwrite an exiting file, remove the 
	 * existing file before attempting the move.
	 * @param force
	 */
	@attribute(value = "", required = false, defaultValue="false")
	public void setForce(boolean force) {
		this.force = force;
	}
	
	public void setForce(String force) {
		setForce(Boolean.parseBoolean(force));
	}

	@Override
	public void execute() throws XMLBuildException  {
		File existingFile = new File(dir, file);
		if (!existingFile.exists()) {
			throw new XMLBuildException (existingFile + " does not exist");
		}
		Util.move(new File(dir, file), toDir, toFile, force);
	}

	@Override
	public void check() throws XMLBuildException {
		if (toDir == null) {
			throw new XMLBuildException ("you must set the destination directory (toDir)");
		}
		if (dir == null) {
			throw new XMLBuildException ("you must set the source directory (dir)");
		}
		if (file == null) {
			throw new XMLBuildException ("you must set the source filename (file)");
		}

	}
}
