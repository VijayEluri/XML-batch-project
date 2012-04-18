package edu.misc.report;

import com.browsexml.core.XMLBuildException;
import com.itextpdf.text.DocumentException;

public class Paragraph extends ReportObject {

	com.itextpdf.text.Paragraph paragraph = new com.itextpdf.text.Paragraph();
	
	public void setText(String text) {
		paragraph.add(text);
	}
	
	public Paragraph() {
		setName(null);
	}
	
	public Paragraph(String name) {
		setName(name);
	}
	
	/**
	 * Called after complete parsing of XML document to evaluate the document.
	 */
	public void execute() throws XMLBuildException {
		try {
			document.add(paragraph);
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new XMLBuildException(e.getMessage());
		}
	}
}
