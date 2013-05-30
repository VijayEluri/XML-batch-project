package edu.bxml.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;
import com.javalobby.tnt.annotation.attribute;

/**
 * Make a directory
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class Compare extends XmlObjectImpl implements XmlObject {

	String file1 = null;
	String file2 = null;
	
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
	
	public static boolean compare(String file1, String file2) {
		boolean eof = false;
		File fileF1 = new File(file1);
		File fileF2 = new File(file2);
		FileInputStream f1 = null;
		FileInputStream f2 = null;
		try {
			f1 = new FileInputStream(fileF1);
			f2 = new FileInputStream(fileF2);
			int c1 = 0;
			while ((c1 = f1.read()) == f2.read()) {
				if (c1 == -1) {
					return true;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("file1 = " + fileF1.getAbsolutePath());
			System.err.println("file2 = " + fileF2.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				f1.close();
				f2.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Retrieve the text that was contained inside the tag
	 */
	public void setText(String text) {

	}
}
