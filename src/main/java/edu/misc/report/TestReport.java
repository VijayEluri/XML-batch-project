package edu.misc.report;

import java.util.concurrent.ConcurrentHashMap;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.browsexml.core.XMLBuildException;


public class TestReport extends TestCase {

    public static Test suite() {

        TestSuite suite = new TestSuite();
        

        suite.addTestSuite(TestReport.class);
        return suite;
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
	
	protected void setUp() {

	}
	
	protected void tearDown() {
	}
	
	public void testBasic() throws XMLBuildException {
		/*
<pdf size="LETTER" xmlns='edu.misc.report'>
	<font name="h9" face="HELVETICA" size="9" style="BOLD|ITALIC"/>

	<table>
	
			<tr>
				<td colSpan="4" border="0" alignment="ALIGN_LEFT">
					<phrase font="h9"></phrase>
				</td>
				<td border="0" alignment="ALIGN_RIGHT">
					<phrase font="h9">.00</phrase>
				</td>
				<td border="0" alignment="ALIGN_RIGHT">
					<phrase font="h9">.00</phrase>
				</td>
			</tr>
			<tr><td><phrase> </phrase></td></tr>
		</table>
		 */
		
	
		Pdf pdf = new Pdf();
		Font h9 = new Font();
		h9.setFace("HELVETICA");
		h9.setSize("9");
		h9.setStyle("BOLD|ITALIC");
		pdf.addFont(h9);
		
		Table table = new Table();
		Tr tr = new Tr();
		Td td = new Td();
		td.setColSpan("4");
		td.setBorder("0");
		td.setAlignment("ALIGN_LEFT");
		Phrase phrase = new Phrase();
		
		phrase.setSymbolTable(new ConcurrentHashMap());
		ConcurrentHashMap st = phrase.getSymbolTable();
		st.put("h9", h9);
		
		//phrase.setFont("h9");
		phrase.setFromTextContent(".00");
		td.addPhrase(phrase);
		tr.addTd(td);
		tr.addTdEnd(td);
		table.addTr(tr);
		pdf.addTable(table);
		
		try {
			h9.init(pdf);
			table.init(pdf);
			tr.init(table);
			td.init(tr);
			phrase.init(td);
			
			phrase.check();
			td.check();
			tr.check();
			table.check();
			h9.check();
			pdf.check();
		} catch (XMLBuildException e2) {
			assertTrue("check failed", false);
			e2.printStackTrace();
		}
		
		try {
			pdf.execute();
		} catch (XMLBuildException e1) {
			e1.printStackTrace();
		}

		
	}

}
