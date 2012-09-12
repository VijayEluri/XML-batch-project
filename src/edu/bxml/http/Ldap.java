package edu.bxml.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlParser;
//import com.bxml.IoFilter;
import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPSearchResults;

public class Ldap extends IoFilter {
	private static Log log = LogFactory.getLog(Ldap.class);
	File dir = null;
	File toDir = null;

	HashMap options = new HashMap();

	String user = null;
	String password = null;
	String query = null;
	String host = null;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	String[] attributes = null;
	String separator = "|";
	String searchBase = null;

	public String getSearchBase() {
		return searchBase;
	}

	public void setSearchBase(String searchBase) {
		this.searchBase = searchBase;
	}

	@Override
	public void init(XmlObject parent) {

	}

	@Override
	public void check() throws XMLBuildException {
		// if (query == null)
		// throw new XMLBuildException("query must be set");
		if (attributes == null)
			throw new XMLBuildException("attributes must be set");
		if (searchBase == null)
			throw new XMLBuildException("searchBase must be set");
	}

	// @Override
	// public void execute() throws XMLBuildException {
	// log.debug("STARTING COPY");
	// if (this.dir == null && this.getText() != null) {
	// log.debug("COPY from " + this.getText());
	// in = new ByteArrayInputStream(this.getText().getBytes());
	// }
	// if (in != null) {
	// if (out == null) {
	// log.debug("out is null in Copy");
	// File to = new File(toDir, getToFile());
	// try {
	// log.debug("COPY OUT FILE = " + to);
	// out = new FileOutputStream(to);
	// } catch (FileNotFoundException e) {
	// log.error(e.getStackTrace());
	// throw new XMLBuildException(e.getMessage());
	// }
	// }
	// log.debug("copy is a filter");
	//			
	// execute2();
	//
	// }
	// else {
	// File to = null;
	// this.dir = new File(getDir());
	// if (this.toDir == null && getToDir() != null) {
	// this.toDir = new File(getToDir());
	// if (!this.dir.exists())
	// throw new XMLBuildException(dir + " must exist");
	// if (!toDir.isDirectory())
	// throw new XMLBuildException(toDir + " must be a directory");
	// }
	//			
	//			
	// if ( this.dir.listFiles() == null) {
	// throw new XMLBuildException("can't get a list of files from " +
	// this.dir.getAbsolutePath());
	// }
	// for (File file:this.dir.listFiles()) {
	//
	// if (!file.getName().matches(this.getFile()))
	// continue;
	// log.debug("Copying " + file.getName() + ".... to dir " + this.toDir);
	// if (this.toDir != null) {
	// if (super.getToFile() == null) {
	// log.debug("toFILE null = " + super.getToFile());
	// to = new File(toDir, file.getName());
	// }
	// else {
	// log.debug("toFILE not null = " + super.getToFile());
	// to = new File(toDir, XmlParser.processAttributes(
	// this, super.getToFile()));
	// }
	// }
	// execute2();
	//
	// }
	// }
	// }
	public void execute() throws XMLBuildException {
		PrintStream out = new PrintStream(this.out);
		BufferedReader bin = new BufferedReader(new InputStreamReader(this.in));

		try {
			while ((query = bin.readLine()) != null && query != null) {
				query = query.trim();
				if (query.equals(""))
					continue;
				query = XmlParser.processMacros(getSymbolTable(), XmlParser
						.replacePoundMacros(query));

				log.debug("query = " + query);

				int ldapPort = LDAPConnection.DEFAULT_PORT;
				int searchScope = LDAPConnection.SCOPE_SUB;
				int ldapVersion = LDAPConnection.LDAP_V3;
				LDAPConnection lc = new LDAPConnection();

				try {
					lc.connect(host, ldapPort);
					log.debug("user = " + this.getUser());
					log.debug("password = " + this.getPassword());
					log.debug("query = " + query);
					lc.bind(ldapVersion, this.getUser(), this.getPassword()
							.getBytes("UTF8"));
					LDAPSearchResults searchResults =

					lc.search(searchBase, searchScope, query, attributes, // return
																			// all
																			// attributes
																			// if
																			// null
							false); // return attrs and values

					/*
					 * To print out the search results,
					 * 
					 * -- The first while loop goes through all the entries
					 * 
					 * -- The second while loop goes through all the attributes
					 * 
					 * -- The third while loop goes through all the attribute
					 * values
					 */

					while (searchResults.hasMore()) {
						LDAPEntry nextEntry = null;
						StringBuffer outBuffer = new StringBuffer();
						try {
							nextEntry = searchResults.next();
							for (String att : attributes) {
								LDAPAttribute value = nextEntry
										.getAttribute(att);
								String strValue = "null";
								if (value != null)
									strValue = value.getStringValue();
								outBuffer.append(separator + strValue);
							}
							out.println(outBuffer.substring(1));
							//System.out.println(outBuffer.substring(1));
						} catch (LDAPException e) {
							System.out.println("Error: " + e.toString());
							// Exception is thrown, go for next entry
							if (e.getResultCode() == LDAPException.LDAP_TIMEOUT
									|| e.getResultCode() == LDAPException.CONNECT_ERROR)
								break;
							else
								continue;
						}

						/*
						 * 
						 * System.out.println("\n" + nextEntry.getDN());
						 * 
						 * System.out.println("  Attributes: ");
						 * 
						 * 
						 * 
						 * LDAPAttributeSet attributeSet =
						 * nextEntry.getAttributeSet();
						 * 
						 * Iterator allAttributes = attributeSet.iterator();
						 * 
						 * 
						 * 
						 * while(allAttributes.hasNext()) {
						 * 
						 * LDAPAttribute attribute =
						 * 
						 * (LDAPAttribute)allAttributes.next();
						 * 
						 * String attributeName = attribute.getName();
						 * 
						 * 
						 * 
						 * System.out.println("    " + attributeName);
						 * 
						 * 
						 * 
						 * Enumeration allValues = attribute.getStringValues();
						 * 
						 * 
						 * 
						 * if( allValues != null) {
						 * 
						 * while(allValues.hasMoreElements()) {
						 * 
						 * String Value = (String) allValues.nextElement();
						 * 
						 * if (Base64.isLDIFSafe(Value)) {
						 * 
						 * // is printable
						 * 
						 * 
						 * System.out.println("      " + Value);
						 * 
						 * }
						 * 
						 * else {
						 * 
						 * // base64 encode and then print out
						 * 
						 * 
						 * Value = Base64.encode(Value.getBytes());
						 * 
						 * System.out.println("      " + Value);
						 * 
						 * }
						 * 
						 * 
						 * }
						 * 
						 * 
						 * }
						 * 
						 * } /*
						 */
					}

					// disconnect with the server

					lc.disconnect();

				}

				catch (LDAPException e) {

					System.out.println("Error: " + e.toString());

				}

				catch (UnsupportedEncodingException e) {

					System.out.println("Error: " + e.toString());
				}

			}
		} catch (IOException e) {
			throw new XMLBuildException(e.getMessage());
		}

	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String[] getAttributes() {
		return attributes;
	}

	public void setAttributes(String attributes) {
		this.attributes = attributes.split("\\s*,\\s*");
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

}
