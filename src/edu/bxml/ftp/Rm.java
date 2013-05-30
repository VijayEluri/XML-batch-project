package edu.bxml.ftp;

import java.io.IOException;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;
import com.javalobby.tnt.annotation.attribute;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.SftpException;
/**
 * Remove a file from the remote FTP server.
 * Implements Command
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class Rm extends XmlObjectImpl implements XmlObject {
	private static Log log = LogFactory.getLog(Rm.class);

	String dir = null;
	String file = null;
	
	@Override
	public void check() throws XMLBuildException {
		if (dir == null) {
			throw new XMLBuildException("Source directory (dir) must be specified.");
		}
		if (file == null) {
			throw new XMLBuildException("File matching regular expresion (file) must be specified.");
		}
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
		System.err.println("rm");
		Execute e = getAncestorOfType(Execute.class);
		Connection conn = e.getConnection();
		ChannelSftp c = conn.getChannel();
		if (c != null) {
		try {
			Vector<LsEntry> v = c.ls(dir);
			for (LsEntry s: v) {
				String fileName = s.getFilename();
				System.err.println("fileName = " + fileName);
				if (fileName.matches(file)) {
					System.err.println("adding " + fileName);
					try {
						c.rm(dir + "/" + fileName);
					}
					catch (SftpException rme) {
						System.err.println("Could not remove " + fileName);
						rme.printStackTrace();
					};
				}
			}
		} catch (SftpException e1) {
			e1.printStackTrace();
			throw new XMLBuildException(e1.getMessage());
		}
		}
		else {
			try {
				FTPClient ftp = conn.getFtp();
				FTPFile[] v = ftp.listFiles(dir);
				for (int i = 0; i < v.length; i++) {
					FTPFile s = v[i];
					String fileName = s.getName();
					System.err.println("fileName = " + fileName);
					if (fileName.matches(file)) {
						System.err.println("adding " + fileName);
						try {
							ftp.deleteFile(dir + "/" + fileName);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
			} catch (IOException io) {
				io.printStackTrace();
			}
		}
	}
	
}
