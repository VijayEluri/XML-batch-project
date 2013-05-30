package edu.bxml.ftp;

import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;
import com.javalobby.tnt.annotation.attribute;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
/**
 * Store information about how to connect to an FTP server
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class Connection extends XmlObjectImpl implements XmlObject, UserInfo {
	private static Log log = LogFactory.getLog(Connection.class);
	String user = "anonymous";
	String password = null;
	String host = null;
	String encrypt = null;
	JSch jsch = new JSch();
	//MyUserInfo m = new MyUserInfo();
	int timeout = 30000; // .5 minutes
	String knownHosts = System.getProperty("user.home") + "/.ssh/known_hosts";;
	Session session = null;
	FTPClient ftp = new FTPClient(); //for plain old unencrypted ftp 
	//Timer timer = null;
	//UserInfo ui = null; 
	ChannelTypes channelType = ChannelTypes.sftp;
	ChannelSftp c = null;
	Channel channel = null;
	int port = 22;
	String passPhrase = null;
	String keyfile = null;
	boolean trust = false;
	
	public Connection() {
		log.debug("Connection CREATED");
	}
		
	/**
	 * Get a communication channel to the server.  If it is closed open it.
	 * @return
	 * @throws XMLBuildException
	 */
	public ChannelSftp getChannel() throws XMLBuildException {
		if (c == null) {
			execute();
			connect();
		}
		return c;
	}
	
	public void setEncrypt(String encrypt) {
		this.encrypt = encrypt;
	}

	/**
	 *  Ip address or DNS name of the FTP server
	 */
	@attribute(value = "", required = true)
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 *  Login name to use when connecting to server
	 */
	@attribute(value = "", required = false, defaultValue="anonymous")
	public void setLogin(String login) {
		this.user = login;
	}

	/**
	 *  Password to use when connecting to server
	 */
	@attribute(value = "", required = false, defaultValue="The empty string")
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 *  Port to connect to.  Should be 22 for sftp.
	 */
	@attribute(value = "", required = false, defaultValue="22")
	public void setPort(Integer port) {
		this.port = port;
	}
	public void setPort(String port) {
		setPort(Integer.parseInt(port));
	}

	/**
	 *  File location listing known hosts and their finger print.
	 */
	@attribute(value = "", required = false, defaultValue=".ssh/known_hosts in the user's home directory")
	public void setKnownHosts(String hosts) {
		knownHosts = hosts;
	}
	
	/**
	 *  Timeout during connect.
	 */
	@attribute(value = "", required = false, defaultValue="30000 miliseconds (1/2 minute)")
	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}
	public void setTimeout(String timeout) {
		setTimeout(Integer.parseInt(timeout));
	}
	public Integer getTimeout() {
		return this.timeout;
	}
	
	enum ChannelTypes {sftp, ftp};
	/**
	 *  Type of data transfer.  Currently only sftp supported.
	 */
	@attribute(value = "", required = false, defaultValue="sftp")
	public void setChannelType(ChannelTypes channel) {
		channelType = channel;
	} 
	
	public void setChannelType(String channel) {
		channelType = ChannelTypes.valueOf(channel);
	}
	
	public String getChannelType() {
		return channelType.toString();
	}
	
	@Override
	public void check() throws XMLBuildException {
		if (getName() == null) {
			log.debug("Name was not set");
		}
		else 
			log.debug("Name = " + getName());
		if (knownHosts == null && trust == false) {
			throw new XMLBuildException("You must set the knownHosts file location.");
		}
		
	}

	public void execute() throws XMLBuildException {
	
	}

	/**
	 * Connect or re-connect to the server
	 * @throws XMLBuildException
	 */
	public void connect() throws XMLBuildException {
		if (channelType.equals(ChannelTypes.sftp))
			connectSftp();
		else
			connectFtp();
	}
	public void connectFtp()  throws XMLBuildException {
		try {
			ftp.connect(host);
		} catch (SocketException e1) {
			e1.printStackTrace();
			throw new XMLBuildException(e1.getMessage());
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new XMLBuildException(e1.getMessage());
		}
        int reply = ftp.getReplyCode();    
        if(!FTPReply.isPositiveCompletion(reply)) {
            try {
                ftp.disconnect();
            } catch (Exception e) {
                System.err.println("Unable to disconnect from FTP server " +
                                   "after server refused connection. "+e.toString());
            }
            throw new XMLBuildException ("FTP server refused connection.");
        }  
        try {
			if (!ftp.login(user, password)) {
			    throw new XMLBuildException ("Unable to login to FTP server " +
			                         "using username "+user+" " +
			                         "and password "+password);
			}
			ftp.setFileType(FTP.BINARY_FILE_TYPE);

		} catch (IOException e) {
			e.printStackTrace();
			throw new XMLBuildException (e.getMessage());
		}
		log.debug("Connected ftp...");

	}
	public void connectSftp() throws XMLBuildException {
		//try {
		//timer = new Timer(timeout);
		try {
			if (knownHosts != null) {
				log.debug("set known hosts to " + knownHosts);
				jsch.setKnownHosts(knownHosts);
			}

			session = jsch.getSession(user, host, port);
			session.setTimeout(timeout);
		} catch (JSchException e) {
			e.printStackTrace();
			throw new XMLBuildException(e.getMessage());
		}
		if (keyfile != null) {
			try {
				jsch.addIdentity(keyfile, passPhrase);
				log.debug("Added key file: " + keyfile);
			} catch (JSchException e) {
				e.printStackTrace();
				throw new XMLBuildException(e.getMessage());
			}
		}
		//session.setUserInfo(new MyUserInfo());
		session.setUserInfo(this);
		log.debug("session.connect()---");
		try {
			session.connect();
			channel = session.openChannel(channelType.toString());
			channel.connect(timeout);
			c = (ChannelSftp) channel;
		} catch (JSchException e) {
			e.printStackTrace();
			String msg = e.getMessage();
			if (msg != null && msg.indexOf("reject") >= 0)
				msg = "Trust is false and " + host + " is not in knownHosts(" + knownHosts + ").  If you connect with trust set to true, this host will be added to knownHosts and you can set trust to false again.";

			throw new XMLBuildException(msg);
		}
		log.debug("Connected sftp...");
		//timer.stop = true;
	}
	
	public void quit() throws XMLBuildException {
		if (channelType.equals(ChannelTypes.sftp)) {
			c.exit();
			session.disconnect();
			log.debug("Disconnect");
		}
		else {
			try {
				ftp.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
				throw new XMLBuildException(e.getMessage());
			}
		}
	}

	  
	public void setPassPhrase(String passPhrase) {
		this.passPhrase = passPhrase;
	}
	
	public void setKeyfile(String keyfile) {
		this.keyfile = keyfile;
	}
	
	/**
	 * Trust the other computer - don't require a finger print.  You can
	 * set this to true on the first connect to get a finger print and then
	 * set to false;
	 * @param trust
	 */
	@attribute(value = "", required = false, defaultValue="no")
	public void setTrust(Boolean trust) {
		this.trust = trust;
	}
	public void setTrust(String trust) {
		this.trust = Boolean.parseBoolean(trust);
	}
	
    public boolean promptPassphrase(String message) {
        return true;
    }

    /**
     * Implement the UserInfo interface.
     * @param passwordPrompt ignored
     * @return true the first time this is called, false otherwise
     */
    public boolean promptPassword(String passwordPrompt) {
        return true;
    }

    /**
     * Implement the UserInfo interface.
     * @param message ignored
     * @return the value of trustAllCertificates
     */
    public boolean promptYesNo(String message) {
        return trust;
    }

    public void showMessage(String message) {

    }
    
    public String getPassphrase() {
        return passPhrase;
    }
    
    public String getPassword() {
        return password;
    }
    
    public FTPClient getFtp() {
    	return ftp;
    }
}
