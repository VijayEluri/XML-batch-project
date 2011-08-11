package edu.bxml.format;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlParser;
import com.javalobby.tnt.annotation.attribute;
/**
 * Read properties from a file.  The properties are created as soon as the end-tag
 * is read.
 * @author ritcheyg
 *
 */
@attribute(value = "", required = true)
public class Properties extends XmlObject {
	private static Log log = LogFactory.getLog(Properties.class);

	public String value = "";
	public String file = null;
	private String queryName = null;
	private String separator = ",";
	
	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	private HashMap<String, String> properties = new HashMap<String, String>();
	private List<Property> lstProperties = new ArrayList<Property>();
	
	public String getQueryName() {
		return queryName;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}
	
	/**
	 * check that all the fields are set correctly, especially
	 * required fields.  Called when the end-tag of the 
	 * element has been reached and processed.
	 */
	public void check() throws XMLBuildException {
		if (file == null && queryName == null) {
			throw new XMLBuildException("You must set a file name or query name.");
		}
		if (file != null) {
			File props = new File(file);
			java.util.Properties p = new java.util.Properties();
			try {
				p.load(new FileInputStream(props));
			} catch (FileNotFoundException e) {
				throw new XMLBuildException(file + ": does not exist.");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new XMLBuildException(e.getMessage());
			}
			HashMap m = getSymbolTable(); 
			for (Enumeration e = p.keys();e.hasMoreElements();) {
				String key = (String) e.nextElement();
				String value = p.getProperty(key);
				log.debug("put " + key + ": '" + value + "'");
				m.put("_#" + key, value);
			}
		}

	}
	
	public void execute() {
		if (queryName != null) {
			log.debug("execute properties SQL");
			try {
				String strProperties = "";
				StringBuffer strbfrProperties = new StringBuffer("");
				for (Property x: lstProperties) {
					x.execute();
					log.debug("pair = " + x.getName() + ":" + x.getText());
					strbfrProperties.append(separator).append(x.getText());
					properties.put(x.getText(), x.getName());
				}
				if (strbfrProperties.length() > separator.length()) {
					strProperties = strbfrProperties.substring(separator.length());
					log.debug("strProperties = " + strProperties);
					HashMap m = getSymbolTable(); 
					Sql sql = (Sql) m.get(queryName);
					final Connection connection = sql.getConnection();
					final Statement stmt = connection.getStatement();//con.createStatement();
					String strSql = sql.getRawQuery().replace("${text}", strProperties.toString());
					log.debug("sql = " + strSql);
					strSql = XmlParser.processMacros(m, XmlParser.replacePoundMacros(strSql));
					log.debug("sql = " + strSql);
					final ResultSet rs = stmt.executeQuery(strSql);
					while (rs.next()) {
						String key = rs.getString("name");
						if (key == null) 
							continue;
						log.debug("key = " + key);
						key = properties.get(key).toString();
						String value = rs.getObject("value").toString();
						m.put("_#" + key, value);
						log.debug(key + " = '" + value + "'");
					}
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (XMLBuildException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Load the properties contained in the file named.  The file is in the
	 * format of a Java properties file  i.e. lines with name=value 
	 * @param text
	 */
	@attribute(value = "", required = true)
	public void setFile(String name) {
		this.file = name;
	}

	public void setText(String text) {
		if (text != null)
			value = text;
	}

	public void setFromTextContent(String text) {
		if (text != null)
			value = text;
	}
	
	public void addProperty(Property p) throws XMLBuildException {
		log.debug("add property " + p.getText() + ":" + p);
		lstProperties.add(p);
	}
}
