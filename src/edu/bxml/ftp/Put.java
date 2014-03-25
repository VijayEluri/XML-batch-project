package edu.bxml.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlParser;
import com.javalobby.tnt.annotation.attribute;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;

import edu.bxml.io.Filter;
/**
 * Put a single local file up to a remote ftp server.
 * Extends io:Filter; Implements Command.
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class Put extends Filter {
	private static Log log = LogFactory.getLog(Put.class);
	@Override
	public void check() throws XMLBuildException {

	}

	
	@Override
	public void execute() throws XMLBuildException {
		Execute e = getAncestorOfType(Execute.class);
		Ftp f = getAncestorOfType(Ftp.class);
		log.debug("PUT");
		in = f.getInputStream();
		out = f.getOutputStream();
		String absoluteFile = null;
		
		if (toDir == null) {
			toDir = "/";
		}
		String toFile = this.toFile;
		if (toFile == null) {
			if (this.file != null) 
				this.toFile = this.file;
			else
				throw new XMLBuildException("to file must not be null", this);
		}
		
		log.debug("toFile = " + toFile);
		if (toDir == null) {
			toDir = ".";
		}
			
		toDir = toDir.trim();
		
		if (toDir.endsWith("/")) {
			absoluteFile = toDir + toFile;
		}
		else {
			absoluteFile = this.toDir + "/" + this.toFile;
		}
		absoluteFile = XmlParser.processAttributes(this, absoluteFile);
		log.debug("toFile = " + absoluteFile);
		
		Connection conn = e.getConnection();
		log.debug("got conn");

		ChannelSftp c = conn.getChannel();
		log.debug("got channel");
		if (c != null) {
			log.debug("c != null");
			try {
				if (in != null) {
					log.debug("using standard in");
					log.debug("in = " + in);
					log.debug("to file = " + absoluteFile);
					c.put(in, absoluteFile);
				}
				else {
					log.debug("doing put from " + this.dir + "/" + file + " to " + absoluteFile);
					c.put(new File(this.dir, file).getAbsolutePath(), absoluteFile);
				}
			} catch (SftpException e1) {
				e1.printStackTrace();
				throw new XMLBuildException(e1.getMessage(), this);
			}
		}
		else {
			log.debug("c == null");
			try {
				conn.getFtp().setSoTimeout(conn.getTimeout());
			} catch (SocketException e2) {
				e2.printStackTrace();
				throw new XMLBuildException(e2.getMessage(), this);
			}
			try {
				String path = absoluteFile;
				if (in != null) {
					log.debug("ftp store from in file " + path);
					conn.getFtp().storeFile(path, in);
					log.debug("ftp store file " + path + " done " );
				}
				else {
					File file = new File(this.dir, this.file);
					log.debug("ftp store file from dir + file = " + file.getAbsolutePath());
					conn.getFtp().storeFile(path, new FileInputStream(file));
					log.debug("ftp store file " + path + " done " );
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				throw new XMLBuildException(e1.getMessage(), this);
			} catch (IOException e1) {
				e1.printStackTrace();
				throw new XMLBuildException(e1.getMessage(), this);
			}
		}

	}
	
}
