package edu.bxml.io;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;
import com.browsexml.core.XmlParser;

/**
 * Delete the file named by file and dir.  If dependsDir and dependsFile is set,
 * don't delete unless dependsDir/dependsFile exists (for example a copy has been successful).
 * Dir and file support getting properties from other objects using the '%' symbol.
 * @author geoff.ritchey
 *
 */
public class Delete  extends XmlObjectImpl implements XmlObject {
	private static Log log = LogFactory.getLog(Delete.class);
	String file = null;
	String dir = null;
	String dependsDir = null;
	String dependsFile = null;
	File currentFile = null;
	
	public void check() throws XMLBuildException {
		if (file == null) 
			throw new XMLBuildException("file must be set.");
		if (dir == null)
			throw new XMLBuildException("dir must be set.");
	}
	public void execute() throws XMLBuildException {
		File[] files = (new File(dir)).listFiles();
		if (files == null) {
			throw new XMLBuildException("can't open " +dir);
		}
		for (File file : files) {
			currentFile = file;
			log.debug("file test match = " + file.getPath());
			if (!file.getName().matches(this.file))
				continue;
		
			log.debug("delete file = " + file.getAbsolutePath());
			boolean precondition = true;
			if (dependsDir != null) {
				String strDepends = getFileName(dependsDir, 
						XmlParser.processAttributes(this, dependsFile));
				log.debug("depends = " + strDepends);
				File depends = new File(strDepends);
				precondition = depends.exists();
				log.debug("exists? " + precondition);
			}
			if (precondition) {
				boolean result = file.delete();
				log.debug("delete " + file.getAbsolutePath() + "  result: " + result);
			}
		}
	}
	
	public void setFile(String file) {
		this.file = file;
	}
	public void setDir(String dir) {
		this.dir = dir;
	}
	public void setDependsFile(String dependsFile) {
		this.dependsFile = dependsFile;
	}
	public void setDependsDir(String dependsDir) {
		this.dependsDir = dependsDir;
	}
	
	public String getFileName(String directory, String filename) throws XMLBuildException {
		String absoluteFile = null;
		if (directory != null) {
			directory = directory.trim();
			
			if (directory.endsWith("/")) {
				absoluteFile = directory + filename;
			}
			else {
				absoluteFile = directory + "/" + filename;
			}
			absoluteFile = XmlParser.processAttributes(this, absoluteFile);
		}
		return absoluteFile;
	}
	public File getCurrentFile() {
		return currentFile;
	}

	
}
