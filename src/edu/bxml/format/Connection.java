package edu.bxml.format;


import java.io.IOException;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;
import com.browsexml.core.XmlParser;
import com.javalobby.tnt.annotation.attribute;

import edu.bxml.jetty.Configure;

/**
 * Define a connection to a database
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class Connection extends XmlObjectImpl implements XmlObject {
	private static Log log = LogFactory.getLog(Connection.class);
	public String login = null; 
	public String password = null;
	public String url = null;
	public String theClass = null;
	public String jndi = null;
	public String getJndi() {
		return jndi;
	}



	public void setJndi(String jndi) {
		log.debug("set jndi to " + jndi);
		this.jndi = jndi;
	}

	Vector<Sql> sqlQueries = new Vector<Sql>();
	Statement stmt = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	

	
	/**
	 * Add an SQL query that must use this connection
	 */
	@attribute(value = "", required = true)
	public void addSql(Sql s) {
		sqlQueries.add(s);
	}

	/**
	 * Sets the connection URL to a database.  An odbc connection 
	 * would be something like jdbc:odbc:Campus6 where
	 * Campus6 is the name of an ODBC data source on 
	 * your computer.  jdbc:microsoft:sqlserver://pinky:1433;databaseName=Campus6
	 * would be the url using MS-SQL JDBC with the same database.
	 * 
	 */
	@attribute(value = "", required = true)
	public void setUrl(String url) {
		this.url = url;
	}

	
	/**
	 * Sets the java jdbc class.  The odbc bridge that comes with java 
	 * is sun.jdbc.odbc.JdbcOdbcDriver; com.microsoft.jdbc.sqlserver.SQLServerDriver
	 * is for the MS SQL JDBC driver
	 * 
	 */
	@attribute(value = "", required = true, defaultValue="")
	public void setClass(String theClass) {
		this.theClass = theClass;
	}

	
	/**
	 * The database password
	 * 
	 */
	@attribute(value = "", required = true)
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * return a connection to the database
	 * 
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public java.sql.Connection getConnection() throws SQLException,
			ClassNotFoundException {
		java.sql.Connection con = null;
		
		con = getJndiConnection();
		log.debug("the class = " + theClass);
		if (con == null) {
			Class.forName(theClass);
			this.execute();
			log.debug("url = " + url);
			// Replace any single backslash with a double backslash
			url = url.replaceAll("\\\\([^\\\\])", "\\\\$1");
			log.debug("url = " + url);
			try {
				con =  DriverManager.getConnection(url, login, password);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		log.debug(" con = " + con);
		return con;
	}
	
	public java.sql.Connection getJndiConnection()  {
		log.debug("getJndiConnection...");
		if (jndi == null) {
			log.debug("getJndiConnection null");
			return null;
		}
		java.sql.Connection  result = null;
			try {
				log.debug("getJndiConnection2...");
		      Context initialContext = new InitialContext();
		      log.debug("getJndiConnection3...");
		      if ( initialContext == null){
		        log.debug("JNDI problem. Cannot get InitialContext.");
		        return null;
		      }
//		      Context env = (Context) initialContext.lookup("java:comp/env");
//		      if (env == null) {
//		    	  log.debug("JNDI problem. Cannot get env.  Reset env..");
//		    	  env = initialContext;
//		      }
		      try {
				XmlParser.updateVariables(this);
			} catch (XMLBuildException e) {
				log.debug("failed to update variables");
				e.printStackTrace();
			}

		      DataSource datasource = null;
			try {
				datasource = (DataSource)initialContext.lookup(jndi);
				log.debug("datasource = " + datasource);
			} catch (javax.naming.NoInitialContextException e) {
                	URL x = this.getClass().getClassLoader().getResource("jetty-env.xml");
                	log.debug("jetty-env.xml = " + x);
                	if (x != null) {
                		//initialContext.createSubcontext("java:comp/env").createSubcontext("jdbc").bind("nslcCampus", ds);
                		SAXParserFactory factory = SAXParserFactory.newInstance();
                		factory.setNamespaceAware(true);
                		
                		XmlParser f = null;
                		Map myMap = new HashMap();
                		myMap.put("#ct_package", "edu.bxml.jetty");
                		try {
							f = new XmlParser(x, factory, myMap);
							f.execute();
						} catch (SAXParseException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (XMLBuildException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (SAXException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (ParserConfigurationException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						Configure root = (Configure) f.getRoot();
				        System.err.println("root = " + root);
				        root.getContext(initialContext);
				        try {
							datasource = (DataSource)initialContext.lookup(jndi);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
                	}           
			}
			log.debug("datasource = " + datasource);
		      if (datasource != null) {
		        result = datasource.getConnection();
		      }
		      else {
		    	  log.error("Failed to lookup datasource.");
		      }
		    }
		    catch ( NamingException ex ) {
		    	log.error("Cannot get connection: " + ex);
		    	ex.printStackTrace();
		    }
		    catch(SQLException ex){
		    	log.error("Cannot get connection: " + ex);
		    	ex.printStackTrace();
		    }
			log.debug("result = " + result);
		    return result;
		
	}
	
	public void check() throws XMLBuildException {
		if (this.getName() == null) {
			throw new XMLBuildException("Name can't be null" ,this);
		}
		if (jndi == null) {
			if (login == null )
				throw new XMLBuildException("both jndi and login can't be null", this);
			if (password == null )
				throw new XMLBuildException("password can't be null", this);
			if (theClass == null)
				throw new XMLBuildException("class can't be null", this);
			if (url == null)
				throw new XMLBuildException("connection can't be null", this);
		}
	}
	
	/**
	 * The database user name
	 * 
	 */
	@attribute(value = "", required = true)
	public void setLogin(String login) {
		this.login = login;
	}
	/**
	 * No-op function
	 */
	public void setText(String text) {
		
	}

	/**
	 * Called after complete parsing of XML document to evaluate the document.
	 */
	public void execute() {
		
	}
	
	public Sql find(String toFind) {
		if (toFind == null) 
			return null;
		for (Sql s: sqlQueries) {
			if (toFind.equals(s.getName()))
				return s;
		}
		return null;
	}
}
