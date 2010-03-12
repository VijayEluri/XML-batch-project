package edu.misc.report;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



public class Newpage extends ReportObject {
	String text = null;
	private static Log log = LogFactory.getLog(Pdf.class);
	
	public void setText(String text) {
		this.text = text;
	}
	
	public Newpage() {
		setName(null);
	}
	
	public Newpage(String name) {
		setName(name);
	}
	
	/**
	 * Called after complete parsing of XML document to evaluate the document.
	 */
	public void execute() {
		document.newPage();
	}
	
}
