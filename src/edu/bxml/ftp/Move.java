package edu.bxml.ftp;

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.ChannelSftp.LsEntry;
/**
 * Move a remote file to another remote location on the same FTP server.
 * Implements Command.
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class Move extends XmlObject {
	private static Log log = LogFactory.getLog(Move.class);
	String dir = null;
	String file = null;
	String dest = null;
	
	@Override
	public void check() throws XMLBuildException {
		if (dir == null) {
			throw new XMLBuildException("Source directory (dir) must be specified.");
		}
		if (file == null) {
			throw new XMLBuildException("File matching regular expresion (file) must be specified.");
		}
		if (dest == null) {
			throw new XMLBuildException("Destination directory (dest) must be specified.");
		}
	}

	/**
	 *  The remote directory to move the file to. If the directory name
	 *  doesn't start with the slash (/) then it will be relative to the
	 *  current remote directory.
	 */
	@attribute(value = "", required = true)
	public void setDest(String dest) {
		this.dest = dest;
	}

	/**
	 *  The remote directory to move the file from.  If the directory name
	 *  doesn't start with the slash (/) then it will be relative to the
	 *  current remote directory.
	 */
	@attribute(value = "", required = true)
	public void setDir(String dir) {
		this.dir = dir;
	}

	/**
	 *  A regular expression that must match the name of all remote files to 
	 *  be moved.  The regular expression is that of java.String.match().
	 */
	@attribute(value = "", required = true)
	public void setFile(String file) {
		this.file = file;
	}

	@Override
	public void execute() throws XMLBuildException {
		log.debug("move");
		Execute e = getAncestorOfType(Execute.class);
		ChannelSftp c = e.getConnection().getChannel();
		try {
			log.debug("cd " + dir);
			log.debug("file = " + file);
			Vector<LsEntry> v = c.ls(dir);
			for (LsEntry s: v) {
				String fileName = s.getFilename();
				log.debug("fileName = " + fileName);
				if (fileName.matches(file)) {
					log.debug("adding " + fileName);
					log.debug("move " + dir + "/" + fileName + " " + dest);
					try {c.rm(dest + "/" + fileName);}catch (SftpException rme) {};
					c.rename(dir + "/" + fileName, dest + "/" + fileName);
				}
			}
		} catch (SftpException e1) {
			e1.printStackTrace();
			throw new XMLBuildException(e1.getMessage());
		}
	}

	
}
