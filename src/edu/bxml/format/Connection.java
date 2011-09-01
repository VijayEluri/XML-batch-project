package edu.bxml.format;


import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlParser;
import com.javalobby.tnt.annotation.attribute;

/**
 * Define a connection to a database
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class Connection extends XmlObject {
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
		
		if (con == null) {
			Class.forName(theClass);
			this.execute();
			log.debug("url = " + url);
			con =  DriverManager.getConnection(url, login, password);
		}
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
				
		      Context initialContext = new InitialContext();
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
		      DataSource datasource = (DataSource)initialContext.lookup(jndi);
		      if (datasource != null) {
		        result = datasource.getConnection();
		      }
		      else {
		    	  log.debug("Failed to lookup datasource.");
		      }
		    }
		    catch ( NamingException ex ) {
		    	log.debug("Cannot get connection: " + ex);
		    	ex.printStackTrace();
		    }
		    catch(SQLException ex){
		    	log.debug("Cannot get connection: " + ex);
		    	ex.printStackTrace();
		    }
		    return result;
		
	}

	public Statement getStatement() throws SQLException, ClassNotFoundException {
		return getConnection().createStatement();
	}
	
	public void close() throws SQLException {
//		if (stmt != null)
//			stmt.close();
//		if (con != null)
//			con.close();
//		stmt = null;
//		con = null;
	}
	public void check() throws XMLBuildException {
		if (this.getName() == null) {
			throw new XMLBuildException("Name can't be null");
		}
		if (login == null)
			throw new XMLBuildException("login can't be null");
		if (password == null)
			throw new XMLBuildException("password can't be null");
		if (theClass == null)
			throw new XMLBuildException("class can't be null");
		if (url == null)
			throw new XMLBuildException("connection can't be null");
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
