package edu.misc.report;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.lowagie.text.Document;

public abstract class ReportObject extends XmlObject {	
	protected Document document = null;
	
	public Document getDocument() {
		return document;
	}


	public void setDocument(Document document) {
		this.document = document;
	}
	

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
		
	}
}
