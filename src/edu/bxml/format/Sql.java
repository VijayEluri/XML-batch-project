package edu.bxml.format;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;
/**
 * Specify the query that needs formatting
 * @author ritcheyg
 *
 */
@attribute(value = "", required = true)
public class Sql extends XmlObject {
	private static Log log = LogFactory.getLog(Sql.class);

	public String query = "";
	
	public Connection getConnection() {
		return (Connection)this.getParent();
	}
	
	/**
	 * check that all the fields are set correctly, especially
	 * required fields.  Called when the end-tag of the 
	 * element has been reached and processed.
	 */
	public void check() throws XMLBuildException {
		if (! (this.getParent() instanceof Connection)) {
			throw new XMLBuildException("Parent of SQL is not a connection.");
		}
		log.debug("query = " + query);
	}
	/**
	 * Called after complete parsing of XML document
	 * to evaluate the document.
	 */
	public void execute() {
		
	}
	/**
	 * Retrieve the text that was contained inside the tag
	 */
	public void setText(String text) {
		if (text != null) 
			query = text;
	}
	public void setFromTextContent(String text) {
		if (text != null)
			query = text;
	}
}
