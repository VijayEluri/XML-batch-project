package edu.bxml.ftp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;
import com.jcraft.jsch.ChannelSftp;
/**
 * Disconnect from the FTP server.
 * Implements Command.
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class Quit extends XmlObject {
	private static Log log = LogFactory.getLog(Quit.class);
	@Override
	public void check() throws XMLBuildException {
		
	}

	@Override
	public void execute() throws XMLBuildException {
		Execute e = getAncestorOfType(Execute.class);
		ChannelSftp c = e.getConnection().getChannel();
		log.debug("quit");
		c.exit();
	}

	
}
