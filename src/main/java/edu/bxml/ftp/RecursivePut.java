package edu.bxml.ftp;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlParser;
import com.javalobby.tnt.annotation.attribute;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;

import edu.bxml.io.Filter;
/**
 * Recursively put files in a directory structure on a remote 
 * ftp site.
 * Extends io:Filter; Implements Command
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class RecursivePut extends Filter {
	private static Log log = LogFactory.getLog(Put.class);
	private boolean archive = true;
	
	
	private SimpleDateFormat fileArchiveFormat = 
		new SimpleDateFormat("dd-MMM-yyyy");
	
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
			File out = new File(this.dir);
			  c.lcd(out.getAbsolutePath());
			  c.cd(toDir);
			  try {
				recursivePut(c, out);
			} catch (IOException e1) {
				e1.printStackTrace();
				throw new XMLBuildException(e1.getMessage(), this);
			}
		} catch (SftpException e1) {
			e1.printStackTrace();
			throw new XMLBuildException(e1.getMessage(), this);
		}

	}
	
	 public void recursivePut(ChannelSftp sftp, File out) throws IOException, SftpException {
		 log.debug("Recursive out on local Dir: " + out.getAbsolutePath());
		 log.debug("ftp local Dir: " + sftp.lpwd());
		 log.debug("ftp remote Dir: " + sftp.pwd());
		  File[] list = out.listFiles();
		  
		  for (int i = 0; i < list.length; i++) {
			  String name = list[i].getName();
			  if (!name.equals("archive")) {
				  if (list[i].isDirectory()) {
					  log.debug("lcd " + list[i].getName());
					  sftp.lcd(list[i].getName());
					  log.debug("cd " + list[i].getName());
					  sftp.cd(list[i].getName());
					  recursivePut(sftp, list[i]);
					  log.debug("ls .");
					  if (log.isDebugEnabled()) {
						  for (Object k: sftp.ls(".")) {
							  com.jcraft.jsch.ChannelSftp.LsEntry entry = (com.jcraft.jsch.ChannelSftp.LsEntry) k;
							  log.debug("...  " + entry.getFilename());
						  }
					  }
					  log.debug("cd ..");
					  sftp.cd("..");
					  sftp.lcd("..");
					  sftp.ls(".");
					  log.debug("done cd");
				  }
				  else {
					  log.debug("put " + new File(sftp.lpwd(), name).getAbsolutePath() + " to " + sftp.pwd());
					  log.debug("lpwd: " + sftp.lpwd());
				      // Upload a file
					  sftp.put(new File(sftp.lpwd(), name).getAbsolutePath(), name);
					  log.debug("putting " + name + 
							  " to " + sftp.pwd() );
					  File archiveFile = new File(list[i].getParent() + 
							  "/archive");
					  if (!archiveFile.exists()) {
						  archiveFile.mkdirs();
					  }
					  File f = null;
					  if (this.archive &&
							  (!list[i].renameTo(f = new File(archiveFile, list[i].getName() + 
					  		  "." + fileArchiveFormat.format(new Date()))))) {
						  log.info("archiving of " + f.getAbsolutePath() + " failed.  A file with the same name may already be in the archive directory.");
					  }
					  
				  }
			  }
		  }
		  
	  }

	public void setFileArchiveFormat(String fileArchiveFormat) {
		log.debug("file archive format = " + fileArchiveFormat);
		this.fileArchiveFormat = new SimpleDateFormat(fileArchiveFormat);;
	}


	public String getArchive() {
		return Boolean.toString(archive);
	}

	/**
	 * Move the file just 'put' to this directory after the put completes.
	 * @return
	 */
	@attribute(value = "", required = false)
	public void setArchive(Boolean archive) {
		this.archive = archive; 
	}
	public void setArchive(String archive) {
		this.archive = Boolean.parseBoolean(archive);
	}
	
	
	
}
