package edu.bxml.swt;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.program.Program;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;
import com.javalobby.tnt.annotation.attribute;

/**
 * Spawn a separate program. 
 */
@attribute(value = "", required = false)
public class Execute extends XmlObjectImpl implements XmlObject {
	private static Log log = LogFactory.getLog(Execute.class);
	private Program p = null;
	private List<edu.bxml.swt.Argument> arguments = new ArrayList<edu.bxml.swt.Argument>();
	
	@Override
	public void execute() throws XMLBuildException {
		StringBuffer commandLine = new StringBuffer("");
		for (edu.bxml.swt.Argument a: arguments)
			commandLine.append(" ").append(a.getText());
		log.debug("command line: '" + commandLine + "'");
		p.execute(commandLine.toString().trim());
	}
	@Override
	public void check() throws XMLBuildException {
		if (p == null) {
			throw new XMLBuildException("The program to run could not be located.  Check that the extension you specifed is correct.", this);
		}
	}
	
	/**
	 * Extension is used to find which program to run.  For example 'html' is 
	 * usually Internet Explorer.
	 * 
	 * @param extension
	 */
	@attribute(value = "", required = true)
	public void setExtension(java.lang.String extension) {
		p = Program.findProgram(extension);
	}
	
	public void addArgument(edu.bxml.swt.Argument a) {
		arguments.add(a);
	}
}
