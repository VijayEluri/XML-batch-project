package edu.bxml.jdom;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlParser;
import com.javalobby.tnt.annotation.attribute;

import edu.bxml.io.Filter;

public class XPath extends Filter {
	private static Log log = LogFactory.getLog(XPath.class);
	
	
	File xmlDocument = null;
	List<Match> matches = new ArrayList<Match>();
	Boolean inplace = false;
	
	File dir = null;
	File toDir = null;
	
	File src = null;
	
	public void execute() throws XMLBuildException {
		log.debug("STARTING COPY");
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
			if (matches.size() > 0) {
				log.debug("text copy");
				try {
					evaluate(in, out, null);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else {
				log.debug("binary copy");
				try {
					evaluate(in , out, null);
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
					log.debug("file = " + file);
					log.debug("to = " + to);
					evaluate(file , to, this.out);
				} catch (IOException e) {
					e.printStackTrace();
					throw new XMLBuildException("copy(" + file + ", " + to + ")   " + e.getMessage(), this);
				}
			}
		}
	}
	
	public void evaluate(File src, File dst, OutputStream alternate) throws IOException {
		this.src = src;
		InputStream source = new FileInputStream(src);
		OutputStream output = alternate;
		if (dst != null) 
			output = new FileOutputStream(dst);
	
		evaluate(source, output, alternate);
	}
	
	public void evaluate(InputStream src, OutputStream dst, OutputStream alternate) throws IOException {
		if (dst == null)
			dst = alternate;
		SAXBuilder saxBuilder =  new SAXBuilder("org.apache.xerces.parsers.SAXParser");
		org.jdom.Document jdomDocument = null;
		try {
			jdomDocument = saxBuilder.build(src);
		} catch (JDOMException e) {
			e.printStackTrace();
		}
		org.jdom.xpath.XPath xpath = null;
		try {
			xpath = org.jdom.xpath.XPath.newInstance("/");
			xpath.addNamespace(null, "http://www.springframework.org/schema/beans");
		} catch (JDOMException e1) {
			e1.printStackTrace();
		}
		log.debug("ROOT NAMEP: " + jdomDocument.getRootElement().getName());
		log.debug("ROOT NAMEP: '" + jdomDocument.getRootElement().getNamespacePrefix() + "'");
		log.debug("ROOT NAMEP: '" + jdomDocument.getRootElement().getNamespaceURI() + "'");
		if (inplace) {
			src.close();
			dst = new FileOutputStream(this.src);
		}
		List<org.jdom.Element> nodes = null;
		log.debug("Matches = " + matches.size());
		for (Match m: matches) {
			log.debug("matched = " + m);
			log.debug("command = '" + m.getCommand() + "' ");
			try {
				nodes = xpath.selectNodes(jdomDocument, m.getPath());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (nodes.size() < 1) 
				log.debug("no nodes matched " + m.getPath());
			for (org.jdom.Element node: nodes) {
				if ("list".equals(m.getCommand())) {
					log.debug("LIST");
					XMLOutputter outputter = new XMLOutputter();
					outputter.output(node, System.out);
				}
				if ("detach".equals(m.getCommand())) {
					node.detach();
				}
				if ("addContent".equals(m.getCommand())) {
					for (Element e: m.getElements()) {
						node.addContent(e.getElement());
					}
				}
			}
		}
	
		XMLOutputter outputter = new XMLOutputter(org.jdom.output.Format.getPrettyFormat());
		outputter.output(jdomDocument, dst);
	}
	
	public void addMatch(Match match) {
		matches.add(match);
	}

	public String getInplace() {
		return "" + inplace;
	}

	@attribute(value = "", required = true)
	public void setInplace(Boolean inplace) {
		this.inplace = inplace;
	}
	public void setInplace(String inplace) {
		setInplace(Boolean.parseBoolean(inplace));
	}
}
