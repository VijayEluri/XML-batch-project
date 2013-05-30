package edu.bxml.ftp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;
import com.javalobby.tnt.annotation.attribute;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
/**
 * Get a single file from the remote ftp server.
 * Implements Command.
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class Get extends XmlObjectImpl implements XmlObject {
	private static Log log = LogFactory.getLog(Get.class);
	String src = null;
	String dst = null;
	
	/**
	 *  Set the name of the destination local file
	 */
	@attribute(value = "", required = true)
	public void setDst(String dst) {
		this.dst = dst;
	}
	
	/**
	 *  Set the name of the source remote file
	 */
	@attribute(value = "", required = true)
	public void setSrc(String src) {
		this.src = src;
	}

	@Override
	public void check() throws XMLBuildException {
		if (src == null || dst == null) 
			throw new XMLBuildException("you must set the source and destination.");
		
	}

	@Override
	public void execute() throws XMLBuildException {
		Execute e = getAncestorOfType(Execute.class);
		ChannelSftp c = e.getConnection().getChannel();
		try {
			log.debug("put " + src + ", " + dst);
			InputStream is = c.get(src);
			OutputStream os = null;
			try {
				os = new FileOutputStream(new File(dst));
				byte[]b = new byte[2048];
				while (-1 != is.read(b)) {
					os.write(b);
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			finally {
				try {is.close();} catch (IOException e1) {}
				try {os.close();} catch (IOException e1) {}
			}
		} catch (SftpException e1) {
			e1.printStackTrace();
			throw new XMLBuildException(e1.getMessage());
		}

	}

	
}
