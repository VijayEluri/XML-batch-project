package edu.bxml.format;

import java.io.File;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;

import edu.misc.Excel.Workbook;

/**
 * 
 * @author ritcheyg
 *
 */
@attribute(value = "", required = true)
public class ExcelXml extends XmlObject {
	private static Log log = LogFactory.getLog(ExcelXml.class);
	boolean truncateFirst = false;
	String dir = null;
	String file = null;
	String table = null;
	String connection = null;
	File inFile = null;
	boolean echo = false;
	Workbook x = null;
	Vector<ExcelData> excelDataItems = new Vector<ExcelData>();
	
	public void setEcho(String echo) {
		this.echo = Boolean.parseBoolean(echo);
	}
	public void addExcelData(ExcelData excelData) {
		excelDataItems.add(excelData);
	}
	
	public Connection getConnection() {
		return (Connection)this.getParent();
	}
	
	/**
	 * check that all the fields are set correctly, especially
	 * required fields.  Called when the end-tag of the 
	 * element has been reached and processed.
	 */
	public void check() throws XMLBuildException {
		if (file == null || dir == null) {
			throw new XMLBuildException("File and dir must be set");
		}
		inFile = new File(dir, file);
		if (!inFile.exists()) {
			throw new XMLBuildException("File " + inFile + " does not exit.");
		}
		log.debug("infile = " + inFile.getAbsolutePath());
	}
	/**
	 * Called after complete parsing of XML document
	 * to evaluate the document.
	 */
	public void execute() {
		/*XmlParser f = null;
		try {
			log.debug("Create parser");
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
			f = new XmlParser();
			f.setPackage("edu.wbu.Excel");
			f.setTraceNoSuchMethod(false);
			f.parse(inFile.getAbsolutePath(), factory);
			f.execute();
		} catch (XMLBuildException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		x = (Workbook) f.root;
		x.print();
		log.debug("got root");*/
		/*
		String line = x.getLine(0, 0, 0);
		log.debug("ExcelXML: " + line);
		String regex = "SOCAD-(\\d+): *\\[( *[^ ] *)\\]";
		log.debug("regex = " + regex);
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(line);
		boolean b = m.find();
		if (b) {
			log.debug("MATCH " + m.group(1));
		}
		else 
			log.debug("NO MATCH");
			*/
		ExcelData iterator = (ExcelData) symbolTableLookUp("iterator");
		if (iterator != null) {
			int row = iterator.getRow();
			int maxRow = iterator.getMaxRow();
			for (int i = row; i <= maxRow; ) {
				System.out.println(getDataLine());
				iterator.setRow(++i);
			}
		}
		else {
			System.out.println(getDataLine());
		}

	}
	
	public String getDataLine() {
		StringBuffer line = new StringBuffer();
		for (ExcelData d: excelDataItems) {
			String workingLine = x.getLine(d);
			log.debug("ExcelXML: (" + d.getName() + ":" + d.getRow() + "): " + workingLine);
			Pattern pat = d.getPattern();
			if (pat == null) {
				line.append(",").append(workingLine);
			}
			else {
				Matcher mat = pat.matcher(workingLine);
				line.append(",");
				if (mat.find()) {
					String matched = d.getResult(mat);
					line.append(matched);
					int index = workingLine.indexOf(matched);
					if (index >=0)
						d.cellFoundAt = workingLine.substring(0, index).
							split("\\\\|").length+d.getCell()-3;//Minus indexes printed starting each line
					else
						d.cellFoundAt = -1;
					log.debug("Cell found at = " + d.cellFoundAt);
					log.debug("wokring = " + workingLine);
					log.debug("matched = " + matched);
					//line.append(mat.group(d.getResult()));
					//log.debug("MATCH " + mat.group(d.getResult()));
				}
				else {
					log.debug("NO MATCH");
				}
			}
		}
		return line.substring(1).toString();
	}
	/**
	 * Retrieve the text that was contained inside the tag
	 */
	public void setText(String text) {

	}
	
	/**
	 * Truncate the table before loading new data only-if a 
	 * new data source file exists
	 */
	@attribute(value = "", required = false, defaultValue = "default value is false")
	public void setTruncateFirst(Boolean t) {
		truncateFirst = t;
	}
	public void setTruncateFirst(String t) {
		setTruncateFirst(Boolean.parseBoolean(t));
	}
	
	/**
	 * The directory where the file(s) to load can be found.
	 */
	@attribute(value = "", required = true)	
	public void setDir(String dir) {
		this.dir = dir;
	}
	
	/**
	 * The name of the file containing data to load.  Can be a regular expression
	 * but it must match at least one file name.
	 */
	@attribute(value = "", required = true)	
	public void setFile(String file) {
		this.file = file;
	}
	
	/**
	 * Database table to recieve the data.
	 */
	@attribute(value = "", required = true)	
	public void setTable(String table) {
		this.table = table;
	}
	
	/**
	 * Information on connection to database
	 */
	@attribute(value = "", required = true)	
	public void setConnection(String connection) {
		this.connection = connection;
	}
}
