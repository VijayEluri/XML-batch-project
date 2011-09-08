package edu.bxml.format;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;
/**
 * Load binary data and the name of the file it came from into the database. The
 * database table is expected to have at least two fields, one a varchar for the
 * name of the file and the second a blob for the data.
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class LoadBlob extends XmlObject {
	private static Log log = LogFactory.getLog(LoadBlob.class);
	File currentFile = null;

	File dir = null;

	String file = null;

	String table = null;

	String connectionString = null;

	Connection connection = null;

	Vector<File> files = new Vector<File>();

	String archive = null;

	boolean deleteFirst = true;

	String filenameField = "filename";

	String dataField = "data";

	/**
	 * Delete any records with the same filename before the import if true.
	 */
	@attribute(value = "", required = false, defaultValue = "true")
	public void setDeleteFirst(Boolean delete) {
		this.deleteFirst = delete;
	}
	public void setDeleteFirst(String delete) {
		setDeleteFirst(Boolean.parseBoolean(delete));
	}

	/**
	 * The field in which to store the name of the source file.
	 */
	@attribute(value = "", required = false, defaultValue = "will be &quot;filename&quot; by default")
	public void setFilenameField(String fileField) {
		this.filenameField = fileField;
	}

	/**
	 * The field in which to store the data.
	 */
	@attribute(value = "", required = false, defaultValue = "will be &quot;data&quot; by default")
	public void setDataField(String dataField) {
		this.dataField = dataField;
	}

	/**
	 * The name of a directory relative to 'dir' where the file is moved if the
	 * loaded file is saved for archive purposes. Since this is a move
	 * operation, the file will not be in the original location after the load.
	 */
	@attribute(value = "", required = false)
	public void setArchive(String archive) {
		this.archive = archive;
	}

	/**
	 * Information on connection to database
	 */
	@attribute(value = "", required = true)
	public void setConnection(String connection) {
		this.connectionString = connection;
	}

	/**
	 * The directory where the file(s) to load can be found.
	 */
	@attribute(value = "", required = true)
	public void setDir(String dir) {
		this.dir = new File(dir);
	}

	/**
	 * The name of the file containing data to load. Can be a regular expression
	 * but it must match at least one file name.
	 */
	@attribute(value = "", required = true)
	public void setFile(String file) {
		this.file = file;
	}

	public File getCurrentFile() {
		return currentFile;
	}

	/**
	 * Database table to recieve the data.
	 */
	@attribute(value = "", required = true)
	public void setTable(String table) {
		this.table = table;
	}

	/**
	 * No-op function
	 */
	public void setText(String text) {

	}

	public void check() throws XMLBuildException {
		files.setSize(0);
		if (dir == null)
			throw new XMLBuildException("dir not set");
		if (!dir.exists()) {
			throw new XMLBuildException(dir.getAbsolutePath()
					+ " does not exist");
		}
		if (file == null)
			throw new XMLBuildException("file not set");
		File[] flist = dir.listFiles();
		for (int i = 0; i < flist.length; i++) {
			String name = flist[i].getName();
			// log.debug("file name = " + name);
			if (name.matches(file)) {
				log.debug("adding " + name);
				files.add(flist[i]);
			}
		}
		if (files.size() == 0) {
			throw new XMLBuildException(file + ": no matching files exist");
		}

		if (connectionString == null) {
			throw new XMLBuildException("no connection");
		}
		if (table == null) {
			throw new XMLBuildException("no table");
		}
	}

	public String toString() {
		return "";
	}

	/**
	 * Called after complete parsing of XML document to evaluate the document.
	 */
	public void execute() throws XMLBuildException {
		Query query = (Query) getParent();
		connection = query.findConnection(connectionString);
		// log.debug("connection = " + this.connection);
		if (connection == null) {
			throw new XMLBuildException("connection not found");
		}
		java.sql.Connection c = null;
		try {
			c = connection.getConnection();
		} catch (Exception e) {
			e.printStackTrace();
			throw new XMLBuildException(e.getMessage());
		}
		loadBinaryData(c);
		try {
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new XMLBuildException(e.getMessage());
		}
	}

	public void loadBinaryData(java.sql.Connection c) throws XMLBuildException {

		PreparedStatement sqlStatement = null;

		String query = "insert into " + table + " (" + filenameField + ","
				+ dataField + ") values (?, ?)";
		int offset = 0;

		if (deleteFirst) {
			query = "delete from " + table + " where " + filenameField
					+ " = ?;" + query;
			offset = 1;
		}

		log.debug(" query = " + query);
		try {
			// create prepared statement
			sqlStatement = c.prepareStatement(query);
		} catch (SQLException s) {
			s.printStackTrace();
			throw new XMLBuildException(s.getMessage());
		}
		InputStream inputFileInputStream = null;
		for (File currentFile : files) {
			log.debug("file = " + currentFile.getName());
			try {
				inputFileInputStream = new FileInputStream(currentFile);

				if (deleteFirst)
					sqlStatement.setString(1, currentFile.getName());
				sqlStatement.setString(1 + offset, currentFile.getName());
				sqlStatement.setBinaryStream(2 + offset, inputFileInputStream,
						inputFileInputStream.available());
				sqlStatement.executeUpdate();

			} catch (SQLException s) {
				s.printStackTrace();
				continue;
			} catch (IOException fio) {
				throw new XMLBuildException(fio.getMessage());
			} finally {
				try {
					inputFileInputStream.close();
				} catch (IOException fio) {
				}
			}

			if (archive != null) {
				Util.move(currentFile, archive, true);
			}
		}
	}
}
