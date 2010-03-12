package edu.bxml.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.javalobby.tnt.annotation.attribute;

/**
 * Load a cvs file into memory.  The file's fields can be delimited
 * by any character sequence, not just a comma.  Fields can be merged 
 * into a text template file.
 * 
 * @param file
 */
@attribute(value = "", required = false)
public class Load  extends Matches  {
	private static Log log = LogFactory.getLog(Load.class);
	List<Matches> matches = new ArrayList<Matches>();
	String outDateFormat = null;
	InputStreamReader localInput = null;
	
	public Load() {
		
	}
	
	public Load(InputStream in, OutputStream out) {
		this.in = in;
		this.out = out;
	}

	public void execute() throws XMLBuildException {
		PrintStream out = new PrintStream(this.out);
		BufferedReader br = null;
		log.debug("Load execute");
			try {
				if (this.dir == null || this.file == null) {
					br = new BufferedReader(new InputStreamReader(in));
				}
				else {
					localInput = new InputStreamReader(new FileInputStream(
							(new File(this.dir, this.file)).getAbsoluteFile()));
					br = new BufferedReader(localInput);
				}
				while ((line = br.readLine()) != null) {
					log.trace("line = " + line);
					fieldsArray = line.split(delimit);
					processLine(fieldsArray);
					if (matches.size() == 0) {
						super.execute();
					}
					for (Matches match: matches) {
						if (match.matches(line)) {
							match.setOutputStream(out);
							match.execute();
						}
					}

				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				throw new XMLBuildException(e.getMessage());
			} catch (IOException e) {
				e.printStackTrace();
				throw new XMLBuildException(e.getMessage());
			}
			if (localInput != null) {
				try {
					localInput.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	}
	
	/**
	 * Only process lines that match a regular expression
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false)
	public void addMatches(Matches m) {
		matches.add(m);
	}
	
	public void setOutDateFormat(String format) {
		this.outDateFormat = format;
	}
	
}
