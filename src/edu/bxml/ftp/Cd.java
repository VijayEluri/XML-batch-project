package edu.bxml.ftp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;
import com.javalobby.tnt.annotation.attribute;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
/**
 * Change default directory on the FTP server.
 * Implements Command.
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class Cd extends XmlObjectImpl implements XmlObject {
	private static Log log = LogFactory.getLog(Cd.class);

	String dir = null;
	
	/**
	 *  The directory to make the default
	 */
	@attribute(value = "", required = true)
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
		try {
			log.debug("cd " + dir);
			c.cd(dir);
		} catch (SftpException e1) {
			e1.printStackTrace();
			throw new XMLBuildException(e1.getMessage());
		}

	}

	
}
