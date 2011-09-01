package edu.bxml.format;

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;

import edu.bxml.io.Filter;
import edu.bxml.io.Pipe;
import edu.misc.report.Pdf;

/**
 * Specify formatting and the query to be formatted as children of this item.
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class Query extends Filter {
	private static Log log = LogFactory.getLog(Query.class);
	Vector<Connection> connections = new Vector<Connection>();
	Vector<XmlObject> sqlCommands = new Vector<XmlObject>();
	Vector<Pdf> pdfDocs = new Vector<Pdf>();
	
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
	 * Execute queries (update/insert) or procedures not expected 
	 * to return a resultset 
	 */
	@attribute(value = "", required = true)
	public void addExecute(Execute execute) {
		log.debug("EXECUTE ADDED");
		sqlCommands.add(execute);
		execute.setParent(this);
	}
	
	/**
	 * Execute queries (update/insert) or procedures not expected 
	 * to return a resultset 
	 */
	@attribute(value = "", required = true)
	public void addExcel(Excel execute) {
		log.debug("EXCEL ADDED");
		sqlCommands.add(execute);
		execute.setParent(this);
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
	 * Format the output of a select query.
	 */
	@attribute(value = "", required = false)
	public void addSelect(Select s) {
		sqlCommands.add(s);
	}

	/**
	 *  Load data from a flat text file into a database.
	 */
	@attribute(value = "", required = false)
	public void addLoad(Load l) {
		log.debug("LOAD ADDED");
		sqlCommands.add(l);
	}
	
	public void addXmlLoad(XmlLoad l) {
		log.debug("XMLLOAD ADDED");
		sqlCommands.add(l);
	}

	public void addXmlSelect(XmlSelect s) {
		log.debug("XMLSELECT ADDED");
		sqlCommands.add(s);
	}
	/**
	 *  Load Blob data from a binary file into a database.
	 */
	@attribute(value = "", required = false)
	public void addLoadBlob(LoadBlob l) {
		sqlCommands.add(l);
	}
	
	/**
	 *  Load data from a flat file into a database.
	 */
	@attribute(value = "", required = false)
	public void addExcelXml(ExcelXml xml) {
		sqlCommands.add(xml);
	}
	/**
	 *  Load data from a flat file into a database.
	 */
	//@attribute(value = "", required = false)
	public void addPdf(Pdf document) {
		pdfDocs.add(document);
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
		log.debug("SQL COMMANDS = " + sqlCommands);
		for (XmlObject sqlCommand : sqlCommands) {
			log.debug("QUERY EXEC: " + sqlCommand);
			sqlCommand.execute();
		}
		// log.debug("EXECUTE QUERY DONE");
	}
	
	/**  */
	@attribute(value = "", required = false)
	public void addProperty(Property p) {
		// The property will automatically add its value to the symbol 
		// table when it checks itself.
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
