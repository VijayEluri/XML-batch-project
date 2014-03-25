package edu.bxml.io;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlParser;
import com.javalobby.tnt.annotation.attribute;

import edu.bxml.format.Replace;

/**
 * Copy a file
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class Copy extends Filter  {
	private static Log log = LogFactory.getLog(Copy.class);
	File dir = null;
	File toDir = null;
	String currentItem = null;
	
	public List<Replace> replacements = new ArrayList<Replace>();
	private String currentType = null;
	
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
		log.debug("STARTING COPY");
		if (this.dir == null && this.text != null) {
			log.debug("COPY from " + this.text);
			in = new ByteArrayInputStream(this.text.getBytes());
		}
		if (in != null) {
			if (out == null) {
				log.debug("out is null in Copy");
				File to = new File(toDir, toFile);
				try {
					log.debug("COPY OUT FILE = " + to);
					out = new FileOutputStream(to);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					throw new XMLBuildException(e.getMessage(), this);
				}
			}
			log.debug("copy is a filter");
			if (replacements.size() > 0) {
				log.debug("text copy");
				try {
					copyTextFile(in, out, replacements);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else {
				log.debug("binary copy");
				try {
					copyBinaryFile(in , out);
				} catch (IOException e) {
					e.printStackTrace();
				}
				log.debug("binary copy done");
			}
		}
		else {
			File to = null;
			this.dir = new File(super.dir);
			if (this.toDir == null && super.toDir != null) {
				this.toDir = new File(super.toDir);
				if (!this.dir.exists())
					throw new XMLBuildException(dir + " must exist", this);
				if (!toDir.isDirectory())
					throw new XMLBuildException(toDir + " must be a directory", this);
			}
			
			
			if ( this.dir.listFiles() == null) {
				throw new XMLBuildException("can't get a list of files from " + this.dir.getAbsolutePath(), this);
			}
			for (File file:this.dir.listFiles()) {
				try {
					if (!file.getName().matches(this.file))
						continue;
					log.debug("Copying " + file.getName() + ".... to dir " + this.toDir);
					if (this.toDir != null) {
						if (super.toFile == null) {
							log.debug("toFILE null = " + super.toFile);
							to = new File(toDir, file.getName());
						}
						else {
							log.debug("toFILE not null = " + super.toFile);
							to = new File(toDir, XmlParser.processAttributes(
									this, super.toFile));
						}
					}
					if (replacements.size() > 0) {
						log.debug("copy replcement = " + replacements);
						copyTextFile(file, to, this.out, replacements);
					}
					else {
						copyBinaryFile(file , to, this.out);
					}
				} catch (IOException e) {
					e.printStackTrace();
					throw new XMLBuildException("copy(" + file + ", " + to + ")   " + e.getMessage(), this);
				}
			}
		}
	}
	
   public void copyTextFile(File src, File dst, OutputStream alt, 
		   List<Replace> replacements) throws IOException, XMLBuildException {
       	InputStream in = new FileInputStream(src);
       	OutputStream out = null;
       	if (dst == null) 
       		out = alt;
       	else 
       		out = new PrintStream(dst);
       	copyTextFile(in, out, replacements);
        in.close();
        if (dst != null  && out != alt)
        	out.close();
   }
   
   public String getCurrentItem() {
	   return currentItem;
   }
   public String getCurrentType() {
	   return currentType;
   }
   public void copyTextFile(InputStream input, 
		   OutputStream output, List<Replace> replacements) throws IOException, XMLBuildException {
       String line = null;
       PrintStream out = new PrintStream(output);
       InputStreamReader in= new InputStreamReader(input);
       BufferedReader bin= new BufferedReader(in);
       
        while ((line = bin.readLine()) != null) {
        	String[] lines = null;
			for (Replace r:replacements) {
				List<Select> selectionList = r.getList();
				
				if (Pattern.compile(r.getExpression()).matcher(line).find() 
						&& selectionList.size() > 0) {

						if (lines == null) {
							lines = new String[selectionList.size()];
						}
						for (int i = 0; i < selectionList.size(); i++) {
							if (lines[i] == null)
								lines[i] = line;
							currentItem = (String) selectionList.get(i).getValue();
							currentType  = (String) selectionList.get(i).getType();
							String pre = "";
							String post = "";
							if (r.getTyped()) {
								pre = "TYPEPRE_" + currentType;
								post = "TYPEPOST_" + currentType;
							}
							//lines[i] = lines[i].replaceAll(r.getExpression(), r.getReplacement(pre, post));
							TypeTemplate template = r.getTypeTemplate(currentType);
							if (template != null) 
								lines[i] = lines[i].replaceAll(r.getExpression(), 
									r.getReplacement(template.getText()));
						}
					
				}
				else {
					log.debug("line = " + line);
					log.debug("r = " + r);
					log.debug("r expression = " + r.getExpression());
					log.debug("r replacement = " + r.getReplacement());
					line = line.replaceAll(r.getExpression(), r.getReplacement());
				}
			}
			if (lines != null) {
				for (int i = 0; i < lines.length; i++) {
					out.println(lines[i]);
				}
			}
			else
				out.println(line);
        }
    }
   
   public static void copyBinaryFile(File src, File dst, OutputStream alt) throws IOException {
       InputStream in = new FileInputStream(src);
       OutputStream out = null;
       if (dst == null) 
    	   out = alt;
       else {
    	   log.debug("dst = " + dst.getAbsolutePath());
    	   out = new FileOutputStream(dst);
       }
       copyBinaryFile(in, out);
       in.close();
       if (dst != null && out != alt)
    	   out.close();
   }
   
   public static void copyBinaryFile(InputStream in, OutputStream out) throws IOException {
       // Transfer bytes from in to out
	   log.debug("copy binary file   out  = " + out);
		for (int b = in.read();b != -1;b = in.read()) {
		    out.write(b);
		}
   }

   public void addReplace(Replace replace) {
	   replacements.add(replace);
   }

}
