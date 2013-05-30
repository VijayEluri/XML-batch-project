package edu.bxml.svg;

import org.w3c.dom.Element;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;

public class Link extends XmlObjectImpl implements XmlObject {
	private Element stroke;
	private LineEndPoint endPoint;
	
	public boolean processRawAttributes(org.xml.sax.Attributes attrs) {
		return true;
	}
	
	@Override
	public void execute() throws XMLBuildException {
		
	}
	public void check() throws XMLBuildException {
		
	}
	
	public Link() {
	}
	
	public Link(Element stroke, LineEndPoint endPoint) {
		this.stroke = stroke;
		this.endPoint = endPoint;
	}
	public Element getStroke() {
		return stroke;
	}
	public void setStroke(Element stroke) {
		this.stroke = stroke;
	}
	public String getEndPointLocation() {
		return endPoint.location;
	}
	public void setEndPoint(LineEndPoint endPoint) {
		this.endPoint = endPoint;
	}
}