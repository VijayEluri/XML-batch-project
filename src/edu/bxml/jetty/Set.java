package edu.bxml.jetty;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;

import edu.bxml.io.FilterAJ;
import edu.bxml.io.FilterAJImpl;

public class Set extends FilterAJImpl implements FilterAJ {
	private static Log log = LogFactory.getLog(Set.class);
	
	//Override base name to implement Set.  Set's name property will not be a unique identifier
	// as it is with the base class
	String myName = null;
	String temp = null;
	boolean dontSet = true;
	
	@Override
	public void init(XmlObject parent) throws XMLBuildException {
		myName = temp;
		dontSet = false;
	}

	@Override
	public String getName() {
		return myName;
	}

	@Override
	public void setName(String name) {
		if (dontSet)
			temp = name;
		else
			myName = name;
	}

	@Override
	public void check() throws XMLBuildException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute() throws XMLBuildException {
		// TODO Auto-generated method stub
	}
	
	public void setFromTextContent(String text) {
		if (text != null)
			setValue(text);
	}

}
