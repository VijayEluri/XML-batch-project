package edu.misc.report;

import java.util.ArrayList;
import java.util.List;

import com.javalobby.tnt.annotation.attribute;


public class Header extends ReportObject {
	int rowCount = 0;

	public Header() {
		setName(null);
	}
	
	public Header(String name) {
		setName(name);
	}
	
	List<Tr> rows = new ArrayList<Tr>();
	/**
	 * A row of the table
	 * @param newPage
	 */
	@attribute(value = "", required = false)
	public void addTr(Tr tr) {
		rows.add(tr);
		rowCount++;
	}
	
	public void execute() {
		for (Tr tr: rows) {
			tr.execute();
		}
	}
}
