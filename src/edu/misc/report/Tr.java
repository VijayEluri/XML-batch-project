package edu.misc.report;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.itextpdf.text.Element;


public class Tr extends ReportObject {
	private static Log log = LogFactory.getLog(Tr.class);
	int count = 0;
	int numColumns = 0;
	com.itextpdf.text.pdf.PdfPTable table = null;
	
	@Override
	public void init(XmlObject parent) throws XMLBuildException {
		Table table = null;
		if (parent instanceof Table)
			table = (Table) parent;
		else
			table = parent.getAncestorOfType(Table.class);
		this.table = table.getTable();
		numColumns = table.getWidthCount();
		super.init(parent);
	}
	
	public Tr() {
		setName(null);
	}
	
	public Tr(String name) {
		setName(name);
	}
	
	public void addTd(Td td) {
		
	}
	
	@Override
	public void check() throws XMLBuildException {
		while (count++ < numColumns) {
			table.addCell("");
		}
	}
	
	public void addTdEnd(Td td) {

		if (count < numColumns) {
			table.addCell(td.getCell());
		}
		log.debug("td.HORIZSON = " + td.getCell().getHorizontalAlignment());
		log.debug("El = " + Element.ALIGN_RIGHT);
		count+=td.getSpan();
	}
}
