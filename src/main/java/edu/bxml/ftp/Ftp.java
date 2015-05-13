package edu.bxml.ftp;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.javalobby.tnt.annotation.attribute;

import edu.bxml.io.Filter;

/**
 * Connect to an FTP site for file transfers.
 * Extends io:Filter.
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class Ftp extends Filter {
	private static Log log = LogFactory.getLog(Ftp.class);
	java.util.HashMap<String, Connection> connections = new HashMap<String, Connection>();
	java.util.List<Execute> executeList = new ArrayList<Execute>();
	/**
	 *  Holds information about how to connect to an FTP server
	 */
	@attribute(value = "", required = true)
	public void addConnection(Connection c) {
		log.debug("Add connection " + c.getName());
		connections.put(c.getName(),c);
	}
	
	/**
	 *  Hold commands to execute once connected to an FTP server
	 */
	@attribute(value = "", required = false)
	public void addExecute(Execute e) {
		executeList.add(e);
	}
	
	@Override
	public void check() throws XMLBuildException {
		
	}

	@Override
	public void execute() throws XMLBuildException {
		for (Execute e: executeList) {
			e.execute();
		}
	}
	
	public String toString() {
		return getName();
	}
	
	public InputStream getInputStream() {
		return in;
	}
	
	public OutputStream getOutputStream() {
		return out;
	}
	
	/**
	 *  Used by 'Execute' commands to find their FTP server
	 */
	public Connection findConnection(String name) {
		return connections.get(name);
	}
}
