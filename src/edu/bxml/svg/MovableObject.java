package edu.bxml.svg;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;

public  class MovableObject extends XmlObjectImpl implements XmlObject {
	private static Log log = LogFactory.getLog(MovableObject.class);
	static private int idCount = 0;

	List<Link> links = new ArrayList<Link>();
	
	Element gHandle = null;
	int x = 0;
	int y = 0;
	String id = null;
	
	public boolean processRawAttributes(org.xml.sax.Attributes attrs) {
		return true;
	}

	@Override
	public void execute() throws XMLBuildException {
		
	}
	public void check() throws XMLBuildException {
		
	}
	
	public void setHandle(Element handle) {
		this.gHandle = handle;
		try {
			this.x = Integer.parseInt(gHandle.getAttributeNode("cx").getValue());
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		try {
			this.y = Integer.parseInt(gHandle.getAttributeNode("cy").getValue());
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		this.id = gHandle.getAttributeNode("id").getValue();
		if (this.id == null) {
			gHandle.setAttribute("id", getId());
		}
		log.debug("moveableObject id = " + this.id);
	}
	public MovableObject() {
		idCount++;
	}

	public MovableObject(Element handle) {
		setHandle(handle);
	}
	
	public final String getId() {
		if (id == null) {
			idCount++;
			id = "moveableObject" + idCount;
		}
		return id;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void addLink(Element stroke, LineEndPoint endPoint) {
		links.add(new Link(stroke, endPoint));
	}
	
	public void move(int x, int y) {
		
		if (Math.abs(x - this.x) < 5 && Math.abs(y - this.y)< 5)
			return;

		String tx = "matrix(1 0 0 1 " + x + " " + y + ")";
	
		gHandle.setAttribute("transform", tx );
	
		for (Link link: links) {
			link.getStroke().setAttribute("x" + link.getEndPointLocation(), ""+x );
			link.getStroke().setAttribute("y" + link.getEndPointLocation(), ""+y );
		}

		this.x = x;
		this.y = y;
	}
}