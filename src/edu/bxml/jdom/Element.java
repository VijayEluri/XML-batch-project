package edu.bxml.jdom;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;

public class Element extends XmlObject {
	private static Log log = LogFactory.getLog(Element.class);
	private String text = null;
	private String nodeName = null;
	org.jdom.Element element = null;
	
	public boolean processRawAttributes(org.xml.sax.Attributes attrs) {
		String nodeName = attrs.getValue("nodeName");
		element = new org.jdom.Element(nodeName);
		return true;
	}

	@Override
	public void check() throws XMLBuildException {
		
	}

	@Override
	public void execute() throws XMLBuildException {
		
	}

	@Override
	public void setFromTextContent(String text) throws XMLBuildException {
		element.addContent(text);
	}
	
	public void addAttribute(Attribute a) throws XMLBuildException {}
	public void addAttributeEnd(Attribute a) throws XMLBuildException {
		log.debug("att name = " + a.getName());
		element.setAttribute(a.getAttributeName(), a.getValue());
	}

	public org.jdom.Element getElement() {
		return element;
	}

	public void setElement(org.jdom.Element element) {
		this.element = element;
	}
	
	public void addElement(Element element) {
		
	}
	
	public void addElementEnd(Element element) {
		this.element.addContent(element.getElement());
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public void setText(String text) {
		this.element.addContent(text);
	}

}
