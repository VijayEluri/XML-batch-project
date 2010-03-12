package edu.bxml.jdom;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlParser;
import com.browsexml.core.XmlObject;

public class Match extends XmlObject {
	private static Log log = LogFactory.getLog(Match.class);
	private String command;
	private String path;
	private String value;
	List<Element> elements = new ArrayList<Element>();

	@Override
	public void check() throws XMLBuildException {
		
	}

	@Override
	public void execute() throws XMLBuildException {
		
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) throws XMLBuildException {
		this.path = path;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) throws XMLBuildException {
		this.value = value;
	}
	
	public void addElement(Element element) {
		elements.add(element);
	}

	public List<Element> getElements() {
		return elements;
	}

	public void setElements(List<Element> elements) {
		this.elements = elements;
	}

}
