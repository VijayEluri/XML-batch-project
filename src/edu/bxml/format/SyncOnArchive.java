package edu.bxml.format;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;
/**
 * Copy files from an archive directory to a processing directory
 * if the file does not exist in the 'processed files' archive directory.
 * 
 */
@attribute(value = "", required = true)
public class SyncOnArchive extends XmlObject {
	private static Log log = LogFactory.getLog(SyncOnArchive.class);
	private File liveDir = null;
	private File archiveBase = null;
	private File processedArchive = null;
	private String fileEnding = ".txt";

	private String liveDirRelativeArchive = "archive";
	
	/**
	 * check that all the fields are set correctly, especially required fields.
	 * Called when the end-tag of the element has been reached and processed.
	 */
	public void check() throws XMLBuildException {
		if (liveDir == null) {
			throw new XMLBuildException("The live dir must be set.");
		}
		if (archiveBase == null) {
			throw new XMLBuildException("The archive base dir must be set.");
		}
		if (!liveDir.exists()) {
			throw new XMLBuildException(liveDir + " does not exist.");
		}
		if (!archiveBase.exists()) {
			throw new XMLBuildException(archiveBase + " does not exist.");
		}
		processedArchive = new File(liveDir, liveDirRelativeArchive);
		log.debug("CHECK: processArchive = " + processedArchive);
		
	}

	/**
	 * Called after complete parsing of XML document to evaluate the document.
	 */
	public void execute() {
		log.debug("EXEC: processArchive = " + processedArchive);
		List<String> processedArchiveFiles = Arrays.asList(processedArchive
				.list(new TxtFileFilter()));
		log.debug("archiveBase = " + archiveBase);
		String[] files = archiveBase.list(new TxtFileFilter());
		log.debug("files = " + files);
		List<String> allArchiveFiles = Arrays.asList(files);
		for (String name : allArchiveFiles) {
			log.debug("check " + name);
			if (!processedArchiveFiles.contains(name)) {
				log.debug(" need " + name);
				File from = new File(archiveBase, name);
				File to = new File(liveDir, name);
				try {
					log.debug("copy from " + archiveBase + " to " + liveDir);
					Util.copy(from, to);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Retrieve the text that was contained inside the tag
	 */
	public void setText(String text) {

	}
	
	/**
	 *  All files considered for copy must have this extension.
	 */
	@attribute(value = "", required = false, defaultValue = ".txt")
	public void setFileEnding(String ending) {
		this.fileEnding = ending;
	}
	
	/**
	 *  Set the location of the processing directory.  Files in this directory
	 *  are expected to be processed in some way by another process, such as loading into 
	 *  a database, and then moved to an archive location so that they will
	 *  not be processed again.
	 */
	@attribute(value = "", required = true)
	public void setLiveDir(String liveDir) {
		this.liveDir = new File(liveDir);
	}
	
	/**
	 *  An archive directory path relative to the live dir.
	 */
	@attribute(value = "", required = false, defaultValue = "archive")
	public void setLiveDirRelativeArchive(String arch) {
		this.liveDirRelativeArchive = arch;
	}
	
	/**
	 *  An archive directory that holds a complete collection 
	 *  of all files that should have been processed.
	 */
	@attribute(value = "", required = true)
	public void setArchiveBase(String archiveBase) {
		this.archiveBase = new File(archiveBase);
	}

	class TxtFileFilter implements FilenameFilter {
		public boolean accept(File file, String base) {
			if (base.endsWith(fileEnding))
				return true;
			else
				return false;
		}
	}
	
	public String toString() {
		return "sync (" + liveDir + ", " + archiveBase + ")";
	}
}
