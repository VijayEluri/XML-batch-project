package edu.bxml.ftp;

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.Executer;
import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;

/**
 * Execute FTP commands
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class Execute extends XmlObject {
	private static Log log = LogFactory.getLog(Execute.class);
	String connectionName = null;
	Vector<XmlObject> commands = new Vector<XmlObject>();
	Ftp f = null;
	Connection connection = null;

	public Connection getConnection() {
		return connection;
	}
	
	/**
	 *  Name the Connection object used to connect to an FTP server.
	 */
	@attribute(value = "", required = true)
	public void setConnection(String connection) {
		this.connectionName = connection;
	}
	
	/**
	 *  put a file to the remote machine
	 */
	@attribute(value = "", required = false)
	public void addPut(Put put) {
		log.debug("Add put");
		commands.add(put);
	}
	
	/**
	 *  get a file from the remote machine
	 */
	@attribute(value = "", required = false)
	public void addRecursiveGet(RecursiveGet get) {
		log.debug("Add recursive put");
		commands.add(get);
	}
	
	/**
	 *  put a file to the remote machine
	 */
	@attribute(value = "", required = false)
	public void addRecursivePut(RecursivePut put) {
		log.debug("Add recursive put");
		commands.add(put);
	}
	
	/**
	 *  get a file from the remote machine
	 */
	@attribute(value = "", required = false)
	public void addGet(Get get) {
		commands.add(get);
	}
	
	/**
	 *  List the contents of a remote directory
	 */
	@attribute(value = "", required = false)
	public void addList(List list) {
		commands.add(list);
	}
	
	/**
	 *  Change the default remote directory
	 */
	@attribute(value = "", required = false)
	public void addCd(Cd cd) {
		commands.add(cd);
	}
	
	/**
	 *  Allow any Executer object to run during an Ftp connection.  This
	 *  allows the manipulation of local files based on knowledge of remote files.
	 */
	@attribute(value = "", required = false)
	public void addExecuter(Executer e) {
		commands.add(e);
	}
	
	/**
	 *  Move a remote file from one location to anoither.
	 */
	@attribute(value = "", required = false)
	public void addMove(Move move) {
		commands.add(move);
	}
	
	/**
	 *  Delete a remote file
	 */
	@attribute(value = "", required = false)
	public void addRm(Rm rm) {
		commands.add(rm);
	}
	
	/**
	 *  Close the connection to the remote FTP server
	 */
	@attribute(value = "", required = false)
	public void addQuit(Quit quit) {
		commands.add(quit);
	}
	
	@Override
	public void check() throws XMLBuildException {
		f = (Ftp) getAncestorOfType(Ftp.class);
		log.debug("FTP = " + f);
		if (f == null) {
			throw new XMLBuildException ("Could not find FTP parent of " + this.getName());
		}
	}

	@Override
	public void execute() throws XMLBuildException {
		log.debug("EXECUTE execute");
		connection = f.findConnection(connectionName);
		if (connection == null) {
			throw new XMLBuildException("Could not find the connection " + connectionName);
		}
		connection.connect();
		for (XmlObject command: commands) {
			log.debug("executing " + command);
			command.execute();
		}
		connection.quit();
		
	}
	
}
