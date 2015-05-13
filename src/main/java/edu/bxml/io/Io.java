package edu.bxml.io;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;
import com.javalobby.tnt.annotation.attribute;

import edu.bxml.format.Properties;

/**
 * A container to hold file processing objects.  Its functionality is to call
 * execute on all of its children.  Only specifically allows children 
 * which are designed to process files.
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class Io extends XmlObjectImpl implements XmlObject {
	private static Log log = LogFactory.getLog(Io.class);
	List<XmlObject> commands = new ArrayList<XmlObject>();
	
	
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
		if (commands.size() < 1) {
			log.debug("io: no commands to execute");
		}
		for (XmlObject command: commands) {
			try {
				command.execute();
			} catch (XMLBuildException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * make directories
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false)
	public void addMkdir(edu.bxml.io.Mkdir md) {
		commands.add(md);
	}
	
	/**
	 * Copy files
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false)
	public void addCopy(Copy copy) {
		commands.add(copy);
	}
	
	/**
	 * compress files
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false)
	public void addGZip(GZip zip) {
		commands.add(zip);
	}
	
	/**
	 * encrypt files
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false)
	public void addPgp(Pgp pgp) {
		commands.add(pgp);
	}
	
	/**
	 * list contents of a directory
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false)
	public void addDir(Dir dir) {
		commands.add(dir);
	}
	
	/**
	 * load a csv file into memory 
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false)
	public void addLoad(Load load) {
		commands.add(load);
	}
	
	/**
	 * set up a pipeline between classes
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false)
	public void addPipe(Pipe pipe) {
		commands.add(pipe);
	}
	
	/**
	 * copy input to output.  Allows children's output to 
	 * be included in the copy.
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false)
	public void addFilter(Filter filter) {
		commands.add(filter);
	}
	
	/**
	 * Retrieve the text that was contained inside the tag
	 */
	public void setText(String text) {

	}
	
	public void addProperties(Properties p) {}
}
