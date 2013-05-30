package edu.bxml.svg;

import java.util.ArrayList;
import java.util.List;

import org.apache.batik.swing.JSVGCanvas;
import org.w3c.dom.Element;

import com.browsexml.core.XMLBuildException;

public class Table extends MovableObject {
	String titleText = null;
	List<Column> columns = new ArrayList<Column>();
	int x = 0;
	int y = 30;
	
	public Table() {
		
	}
	public Table (Element handle) {
		super(handle);
	}
	
	@Override
	public void execute() throws XMLBuildException {
		System.err.println("CREATE execute method called");
		
		Er er = getAncestorOfType(Er.class);
		JSVGCanvas canvas = er.getCanvas();
		create(canvas);
	}
	public void check() throws XMLBuildException {
		System.err.println("CHECK Create");
		
	}
	
	public void create(JSVGCanvas canvas) {	
		/*
		System.err.println("id = " + getId());
		SVGDocument document = canvas.getSVGDocument();
		SVGSVGElement svg = document.getRootElement();
		
		String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
		Element g = document.createElementNS(svgNS, "g");
		g.setAttributeNS(null, "id", this.getId());
		
		Element titleBox = document.createElementNS(svgNS, "rect");
		titleBox.setAttributeNS(null, "width", "250");
		titleBox.setAttributeNS(null, "height", "50");
		titleBox.setAttributeNS(null, "fill", "url(#blue_title)");
		titleBox.setAttributeNS(null, "stroke", "black");
		g.appendChild(titleBox);
		
		if (titleText != null) {
			Element title = document.createElementNS(svgNS, "text");
			title.setAttributeNS(null, "x", "10");
			title.setAttributeNS(null, "y", "25");
			title.setAttributeNS(null, "font-size", "25");
			title.setAttributeNS(null, "fill", "#333333");
			title.setTextContent(titleText);
			g.appendChild(title);
		}
		
		Element body = document.createElementNS(svgNS, "rect");
		body.setAttributeNS(null, "x", "0");
		body.setAttributeNS(null, "y", "30");
		body.setAttributeNS(null, "width", "250");
		body.setAttributeNS(null, "height", "250");
		body.setAttributeNS(null, "fill", "white");
		body.setAttributeNS(null, "stroke", "black");
		g.appendChild(body);
		
		int i = 0;
		for (Column column: columns) {
			Element col = document.createElementNS(svgNS, "text");
			col.setAttributeNS(null, "x", "20");
			col.setAttributeNS(null, "y", "" + (45+i++*16));
			col.setAttributeNS(null, "font-size", "14");
			col.setTextContent(column.getId());
			if (Column.Type.STRING.equals(column.getType()))
				col.setAttributeNS(null, "fill", "blue");
			else if (Column.Type.NUMBER.equals(column.getType()))
				col.setAttributeNS(null, "fill", "purple");
			else if (Column.Type.DATE.equals(column.getType()))
				col.setAttributeNS(null, "fill", "brown");

			g.appendChild(col);
		}
		
		svg.appendChild(g);
		
		this.gHandle = g;
		
		move(x, y);
		*/

	}
	
	public void setX(Integer x) {
		this.x = x;
	}
	public void setX(String x) {
		setX(Integer.parseInt(x));
	}
	
	public void setY(Integer y) {
		this.y = y;
	}
	public void setY(String y) {
		setY(Integer.parseInt(y));
	}
	
	public void setTitle(String title) {
		titleText = title;
	}
	
	public void addColumn(Column column) {
		columns.add(column);
	}
}
