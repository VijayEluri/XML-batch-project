package edu.bxml.ftp;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
 * List the contents of a remote directory.
 * Implements Command.
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class List extends XmlObjectImpl implements XmlObject {
	private static Log log = LogFactory.getLog(List.class);
	String dir = ".";
	private boolean recursive = false;
	private int depth = 0;
	private String exclude = null;
	private String filename = null;
	
	public void setFilename(String fileName) {
		this.filename = fileName;
	}

	private String limitFormat = null;
	private String limit = null;
	private Long limitDate = null;
	private boolean found = false;
	/**
	 *  The name of the remote directory to retrieve contents.  If the name 
	 *  doesn't start with a slash (/) it will be relative to the current directory. 
	 */
	@attribute(value = "", required = false, defaultValue = "The current default directory (.)")
	public void setDir(String dir) {
		this.dir = dir;
	}

	@Override
	public void check() throws XMLBuildException {

	}

	@Override
	public void execute() throws XMLBuildException {
		Execute e = getAncestorOfType(Execute.class);
		ChannelSftp c = e.getConnection().getChannel();
		found = false;
		try {
			if (limit != null && limitFormat != null) {
				try {
					Date l = new SimpleDateFormat(limitFormat).parse(limit);
					log.debug("limit date = " + l);
					limitDate = l.getTime();
					Date epoc = new SimpleDateFormat("yyyy-MM-dd").parse("1970-01-02");
					log.debug("epoc date = " + epoc);
					if (limitDate < epoc.getTime()) { // consider relative date
						log.debug("RELATIVE DATE");
						limitDate = new Date().getTime() - limitDate;
					}
				} catch (ParseException e1) {
					log.debug("format: " + limitFormat);
					log.debug("limit: " + limit);
					e1.printStackTrace();
				}
			}
			log.debug("ls " + dir);
			if (c != null)
				list(c, dir);
			else
				list(e.getConnection().getFtp(), dir);
		} catch (SftpException e1) {
			e1.printStackTrace();
			throw new XMLBuildException(e1.getMessage(), this);
		}

	}
	
	private void list(ChannelSftp c, String dir) throws SftpException {
		String originalDir = c.pwd();
		c.cd(dir);
		depth++;
		Vector<LsEntry> v = c.ls(".");
		for (LsEntry s: v) {
			if (!s.getFilename().startsWith(".")) {

				if (s.getAttrs().isDir() ) {
					printDepth();
					System.out.println(s.getFilename() + " D");
					if (recursive) {
						if (exclude == null || !(c.pwd() + "/" + s.getFilename()).matches(exclude)) {
							list(c, s.getFilename());
						}
					}
				}
				else {
					Calendar cal = new GregorianCalendar();
					if (limitDate != null) {
						cal.setTime(new Date((long)(s.getAttrs().getMTime())*1000));
//						log.debug("limit date = " + new Date(limitDate));
//						log.debug("mod date = " + cal.getTime());
//						log.debug("mod date = " + s.getAttrs().getMtimeString());
					}
					if (filename == null || s.getFilename().matches(filename)) {
						if (limitDate == null || cal.getTime().getTime() > limitDate) {
							found = true;
							printDepth();
							System.out.println(s.getFilename() + " F " + s.getAttrs().getSize() + "  " + s.getAttrs().getMtimeString() );
						}
					}
				}
			}
			else {
				
			}
		}
		depth--;
		c.cd(originalDir);
	}
	
	private void list(FTPClient c, String dir) throws SftpException {
		try {
			String originalDir = c.printWorkingDirectory();
			c.changeWorkingDirectory(dir);
			depth++;
			
			FTPFile[] v = c.listFiles(".");
			for (FTPFile s: v) {
				if (!s.getName().startsWith(".")) {
					if (s.isDirectory() ) {
						printDepth();
						System.out.println(s.getName() + " D");
						if (recursive) {
							if (exclude == null || !(c.pwd() + "/" + s.getName()).matches(exclude)) {
								list(c, s.getName());
							}
						}
					}
					else {
						Calendar cal = s.getTimestamp();
						if (filename == null || s.getName().matches(filename)) {
							if (limitDate == null || cal.getTime().getTime() > limitDate) {
								found = true;
								printDepth();
								System.out.println(s.getName() + " F " + s.getSize() + "  " + new SimpleDateFormat().format(cal.getTime()));
							}
						}
					}
				}
				else {
					
				}
			}
			depth--;
			c.changeWorkingDirectory(originalDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void printDepth() {
		for (int i = 0; i < depth; i++) {
			System.out.print("  ");
		}
	}

	public String isRecursive() {
		return Boolean.toString(recursive);
	}

	public boolean getFound() {
		return found;
	}

	/**
	 * List all subdirectories if set
	 * @param recursive
	 */
	@attribute(value = "", required = false, defaultValue="false")
	public void setRecursive(Boolean recursive) {
		this.recursive = recursive;
	}
	public void setRecursive(String recursive) {
		setRecursive(Boolean.parseBoolean(recursive));
	}

	public String getExclude() {
		return exclude;
	}
	
	/**
	 * Exclude from listing files matching this regular expression.
	 * @param recursive
	 */
	@attribute(value = "", required = false, defaultValue="false")
	public void setExclude(String exclude) {
		this.exclude = exclude;
	}

	public String getLimitFormat() {
		return limitFormat;
	}

	/**
	 * A java SimpleDateFormat for the limit property.  This must
	 * be specified if limit is specified.
	 * @return
	 */
	@attribute(value = "", required = false)
	public void setLimitFormat(String limitFormat) {
		this.limitFormat = limitFormat;
	}

	public String getLimit() {
		return limit;
	}

	/**
	 * Files listed will be more recent than this time.  If a time is specified, 
	 * list all files modified within that time.  If a date is specified, list all
	 * files modified after that date  (the date must be later than the UNIX epoch of 
	 * 1970-01-01).  For example, if limit = 1 and limitFormat="M", then
	 * list all files modified in the last minute.
	 * @return
	 */
	@attribute(value = "", required = false)
	public void setLimit(String limit) {
		this.limit = limit;
	}

	
}
