package edu.bxml.ftp;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlParser;
import com.javalobby.tnt.annotation.attribute;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.ChannelSftp.LsEntry;

import edu.bxml.io.Filter;
/**
 * Recursively get all files from a remote FTP server.  Sets
 * the timestamp on all received files to match the timestamp of 
 * the remote file.  Will not get a file already on the local system
 * if the timestamp also matches within timeTolerance.
 * Extends io:Filter; Implements Command.
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class RecursiveGet extends Filter {
	private static Log log = LogFactory.getLog(RecursiveGet.class);
	String include = null;
	long timeTolerance = 5; //five second tolerance match on timestamp between local and remote
	private String exclude = null;
	private String fileNameMatch = null;
	
	@Override
	public void check() throws XMLBuildException {

	}

	
	@Override
	public void execute() throws XMLBuildException {
		log.debug("HERE");
		Execute e = getAncestorOfType(Execute.class);
		Ftp f = getAncestorOfType(Ftp.class);
		in = f.getInputStream();
		out = f.getOutputStream();
		String absoluteFile = null;
		
		if (toDir == null) {
			toDir = "/";
		}

		if (toDir != null) {
			toDir = toDir.trim();

			absoluteFile = XmlParser.processAttributes(this, toDir);
			log.debug("toDir = " + absoluteFile);
		}
		ChannelSftp c = e.getConnection().getChannel();
		try {
			File out = new File(this.toDir);
			if (!out.exists()) {
				out.mkdirs();
			}
			  c.lcd(out.getAbsolutePath());
			  c.cd(this.dir);
			  try {
				recursiveGet(c);
			} catch (IOException e1) {
				e1.printStackTrace();
				throw new XMLBuildException(e1.getMessage(), this);
			}
		} catch (SftpException e1) {
			e1.printStackTrace();
			throw new XMLBuildException(e1.getMessage(), this);
		}

	}
	
	 public void recursiveGet(ChannelSftp sftp) throws IOException, SftpException {

		 Vector<LsEntry> list = sftp.ls(".");
		 log.debug("list object is " + list.get(0).getClass().getName());
		  
		  for (LsEntry entry: list) {
			  String name = entry.getFilename();
			  String dir = sftp.pwd();
			  String fullname = dir + "/" + name;
			  boolean explicitlyIncluded = (include != null) && fullname.matches(include);
			  boolean explicitlyExcluded = (exclude != null) && fullname.matches(exclude);
			  log.debug("full path name = " + fullname);
			  //log.debug("explicit include = " + explicitlyIncluded);
			  //log.debug("explicit exclude = " + explicitlyExcluded);
			  if (!explicitlyIncluded && explicitlyExcluded) {
				  		log.debug("RETURN (No GET) DUE TO include/exclude");
					  return;
			  }
			  if (!name.startsWith(".")) {
				  if (entry.getAttrs().isDir()) {
					  log.debug("lcd " + name);
					  File f = new File(sftp.lpwd(), name);
					  f.mkdirs();
					  sftp.lcd(name);
					  log.debug("cd " + name);
					  sftp.cd(name);
					  recursiveGet(sftp);
					  sftp.cd("..");
					  sftp.lcd("..");
				  }
				  else {
					  if ((fileNameMatch == null) || name.matches(fileNameMatch)) {
						  File localFile = new File(sftp.lpwd(), name);
						  Boolean getIt = true;
						  long remoteModified = entry.getAttrs().getMTime();
						  
						  if (localFile.exists()) {
							  long localModified = localFile.lastModified()/1000;
							  if ((Math.abs(remoteModified - localModified)) < timeTolerance) {
								  getIt = false;
							  }
						  }
						  if (getIt) {
							  log.debug("get " + sftp.pwd() + "/" + name + " to " + localFile.getAbsolutePath());
							  sftp.get(name, new File(sftp.lpwd(), name).getAbsolutePath());
							  localFile.setLastModified(remoteModified*1000);
						  }
						  else {
							  log.debug("get " + sftp.pwd() + "/" + name + " to " + localFile.getAbsolutePath());
							  log.debug("DONT GET");
						  }
					  }
				  }
			  }
		  }
		  
	  }

	/**
	 * used to override explicit exclude (include
	 * files that match this pattern even if they also
	 * match the exclude pattern)
	 * @return
	 */
	@attribute(value = "", required = false)
	public String getInclude() {
		return include;
	}


	@attribute(value = "", required = false)
	public void setInclude(String include) {
		this.include = include;
	}


	public long getTimeTolerance() {
		return timeTolerance;
	}

	public void setTimeTolerance(String timeTolerance) {
		try {
			setTimeTolerance(Long.parseLong(timeTolerance));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
	/**
	 *  The program will not retrieve files already on the local 
	 *  machine if the 'modified' timestamp is within this amount
	 *  of seconds of the remote file's timestamp
	 */
	@attribute(value = "", required = false, defaultValue="5 (seconds)")
	public void setTimeTolerance(Long timeTolerance) {
		this.timeTolerance = timeTolerance;
	}

	public String getExclude() {
		return exclude;
	}

	/**
	 * Don't consider directory + file patterns that match the exclude pattern
	 * @return
	 */
	@attribute(value = "", required = false)
	public void setExclude(String exclude) {
		this.exclude = exclude;
	}

	public String getFileNameMatch() {
		return fileNameMatch;
	}

	/**
	 *  Only retrieve files who's name matches the pattern 
	 */
	@attribute(value = "", required = false)
	public void setFileNameMatch(String fileNameMatch) {
		this.fileNameMatch = fileNameMatch;
	}
}
