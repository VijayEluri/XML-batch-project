package edu.bxml.aj.format;

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;

import edu.bxml.aj.db.BlobFetch;
import edu.bxml.aj.io.Pipe;
import edu.bxml.format.Connection;
import edu.bxml.format.Exists;
import edu.bxml.format.Properties;
import edu.bxml.format.Property;
import edu.bxml.format.ResultMetadata;
import edu.bxml.format.Sql;
import edu.bxml.format.SyncOnArchive;
import edu.bxml.generator.Generator;
import edu.bxml.io.FilterAJ;
import edu.bxml.io.FilterAJImpl;
import edu.misc.report.Pdf;

/**
 * Specify formatting and the query to be formatted as children of this item.
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class Query extends FilterAJImpl implements FilterAJ {
	private static Log log = LogFactory.getLog(Query.class);
	Vector<Connection> connections = new Vector<Connection>();
	Vector<XmlObject> sqlCommands = new Vector<XmlObject>();
	//Vector<Pdf> pdfDocs = new Vector<Pdf>();
	
	public Query() {
		log.debug("Query LOADED");
	}
	
	static public String processDelimit(String delimit) {
		delimit = delimit.replaceAll("\\\\t", "\t");
		delimit = delimit.replaceAll("\\\\n", System.getProperty("line.separator"));
		return delimit;
	}
	
	public void setURL(String connectionName, String url) {
		for (Connection c: connections) {
			if (connectionName.equals(c.getName())) {
				c.setUrl(url);
			}
		}
	}

	
	/**
	 * let the parent identify itself to this object
	 */
	public void setParent(XmlObject parent) {
		
	}
	
	
	/**
	 * Specify how to connect to a database.  Queries to run on the database
	 * such as select, insert and update are children of the connect object.
	 * A Select object used for formatting would get its data from one of these
	 * child objects.
	 */
	@attribute(value = "", required = true)
	public void addConnection(Connection con) {
		connections.add(con);
	}
	
	/**
	 * Allow the program to exit if a query returns or does not return rows.
	 * @param exists
	 */
	@attribute(value = "xx", required = false)
	public void addExists(Exists exists) {
		sqlCommands.add(exists);
	}
	
	
	/**
	 * Fill in a pdf form
	 */
	@attribute(value = "", required = false)
	public void addPdfform(Pdfform s) {
		sqlCommands.add(s);
	}

	/**
	 *  Load data from a flat file into a database.
	 */
	//@attribute(value = "", required = false)
	public void addPdf(Pdf document) {
		sqlCommands.add(document);
	}
	
	public void addSyncOnArchive(SyncOnArchive sync) {
		log.debug("SYNC ADDED");
		sqlCommands.add(sync);
	}
	
	public void addMetadata(ResultMetadata metadata) {
		log.debug("meta ADDED");
		sqlCommands.add(metadata);
	}
	
	/**
	 * check that all the fields are set correctly, especially required fields.
	 * Called when the end-tag of the element has been reached and processed.
	 */
	public void check() throws XMLBuildException {


	}

	/**
	 * Called after complete parsing of XML document to evaluate the document.
	 */
	public void execute() throws XMLBuildException {
		// log.debug("EXECUTE QUERY");
		log.debug("P SQL COMMANDS = " + sqlCommands);
		for (XmlObject sqlCommand : sqlCommands) {
			log.debug("QUERY EXEC: " + sqlCommand);
			sqlCommand.execute();
			log.debug("QUERY EXEC DONE: " + sqlCommand);
		}
		log.debug("QUERY EXEC ALL DONE");
	}
	
	/**  */
	@attribute(value = "", required = false)
	public void addProperty(Property p) {
		// The property will automatically add its value to the symbol 
		// table when it checks itself.
	}
	
	/**  */
	@attribute(value = "", required = false)
	public void addBlobFetch(BlobFetch p) {
		sqlCommands.add(p);
	}	
	
	/**  */
	@attribute(value = "", required = false)
	public void addGenerator(Generator generator) {
		log.debug("ADD generator");
		sqlCommands.add(generator);
	}
	
	/**  */
	@attribute(value = "", required = false)
	public void addProperties(Properties p) {
		sqlCommands.add(p);
	}
	
	/** Unix-like pipe streaming between input and output of children */
	@attribute(value = "", required = false)
	public void addPipe(Pipe p) {
		sqlCommands.add(p);
	}


	/**
	 * Retrieve the text that was contained inside the tag
	 */
	public void setText(String text) {

	}
	
	public Connection findConnection(String conn) {
		for (Connection connection: connections) {
			String name = connection.getName();
			if (name != null) {
				if (name.equals(conn))
					return connection;
			}
		}
		return null;
	}
	
	public XmlObject findCommand(String name) {
		if (name == null) 
			return null;
		for (XmlObject sqlCommand : sqlCommands) {
			if (name.equals(sqlCommand.getName())) 
				return sqlCommand;
		}
		return null;
	}
	
	public Sql getSql(String queryName) {
		Sql found = null;
		for (Connection connection : connections) {
			found = connection.find(queryName);
			if (found != null)
				return found;
		}
		return null;
	}
}
