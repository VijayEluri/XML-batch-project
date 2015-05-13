package edu.bxml.io;

import java.io.File;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;

/**
 * Specify formatting and the query to be formatted as children of this item.
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class Dir extends FileObject {
	
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
		File dir = new File(this.dir);
		String files[] = dir.list();
		for (int i = 0; i < files.length; i++) {
			if (files[i].matches(file)) {
				System.out.print(files[i]);
				if (new File(files[i]).isDirectory())
					System.out.println("/");
				else 
					System.out.println("");
			}
		}
	}

	
	/**
	 * Retrieve the text that was contained inside the tag
	 */
	public void setText(String text) {

	}

}
