package edu.bxml.http;

import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlParser;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
/**
 * Get an http web page
 * 
 */
@attribute(value = "", required = false)
public class Post extends XmlObject {
	private static Log log = LogFactory.getLog(Post.class);
	private String url = null;
	private boolean followRedirects = false;
	private int timeout = 30000;
	private PostMethod post = null;
	private Parts parts = null;
	private Entity entity = null;
	
	private Http http = null;
	private String httpName = null;
	
	private boolean parse = true;
	private boolean print = false;
	private XmlParser parser = null;
	private byte[] response = null;
	
	Vector<Parameter> params = new Vector<Parameter>();
	
	public PostMethod getPost() {
		return post;
	}
	
	@attribute(value = "", required = false)
	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}
	public void setTimeout(String timeout) throws XMLBuildException {
		try {
			setTimeout(Integer.parseInt(timeout));
		} catch (NumberFormatException e) {
			throw new XMLBuildException(timeout);
		}
	}

	@Override
	public void execute() throws XMLBuildException {

		String url = this.url;
		if (url == null) {
			return;
		}
		
		if (http == null) {
			if (httpName != null) {
				http = (Http) this.getSymbolTable().get(httpName);
			}
			else 
				http = (Http) getAncestorOfType(Http.class);
		}
		if (http == null) {
			throw new XMLBuildException("http definition " + httpName + " could not be found.");
		}
		
		try {
			if (url.startsWith("/"))
				url = http.getRoot() + url;
			post = new PostMethod(url);
			if (entity == null) 
				post.setFollowRedirects(followRedirects);
		} catch (RuntimeException e) {
			throw new XMLBuildException (e.getMessage());
		}
		
		for (Iterator i = params.iterator();i.hasNext();) {
			Parameter p = (Parameter) i.next();
			post.setParameter(p.getKey(), (String) p.getValue());
		}
		
		int iGetResultCode = 0;
		
		if (parts != null) {
			Part[] p = parts.getParts();
			post.setRequestEntity(
                new MultipartRequestEntity(p, post.getParams())
                );
		}
		if (entity != null) {
			post.setRequestEntity(entity.getEntity());
		}
		post.setFollowRedirects(followRedirects);

		try {
			log.debug("url = " + url);
			iGetResultCode = http.getClient().executeMethod(post);
			response = post.getResponseBody();
			post.releaseConnection();
		} catch (HttpException e) {
			log.error(e.getStackTrace());
			throw new XMLBuildException(e.getMessage());
		} catch (IOException e) {
			throw new XMLBuildException(e.getMessage());
		}
		
		if (parse) {
			try {
				SAXParserFactory factory = SAXParserFactory.newInstance();
				factory.setNamespaceAware(true);
				
				parser = new XmlParser(factory);
				//f.setPackage("edu.wbu.swt");
				log.debug("f = " + parser);
				parser.setParent(this);
				parser.setSymbolTable(this.getSymbolTable());
				if (print) 
					log.debug("RESPNSE: " + new String(response));
				parser.setSource(url);
				parser.parse(response, null);
			} catch (Exception e) {
				log.error(e.getStackTrace());
				if (e.getMessage().endsWith(".Html")) {
					//FIXME: this interface stuff should not be part of GET
					//new Browser("text/html", new String(response));
					Display display = new Display();
					Shell shell = new Shell(display);
					shell.setLayout(new FillLayout());
					org.eclipse.swt.browser.Browser browser;
					try {
						browser = new org.eclipse.swt.browser.Browser(shell, SWT.NONE);
					} catch (SWTError e1) {
						System.out.println("Could not instantiate Browser: " + e1.getMessage());
						return;
					}
					browser.setText(new String(response));
					shell.open();
					while (!shell.isDisposed()) {
						if (!display.readAndDispatch())
							display.sleep();
					}
					log.debug("display.dispose");
					display.dispose();
				}
				// TODO Auto-generated catch block
				
				throw new XMLBuildException(e.getMessage());
			}
		}
	}
	@Override
	public void check() throws XMLBuildException {

	}
	
	/**
	 *  The URL of the page to get
	 */
	@attribute(value = "", required = true)
	public void setUrl(String url) {
		this.url = url;
	}
	
	/**
	 *  If the page redirects, go to the redirection
	 */
	@attribute(value = "", required = false, defaultValue="false")
	public void setFollowRedirects(Boolean text) {
		followRedirects = text;
	}
	public void setFollowRedirects(String text) throws XMLBuildException {
		try {
			setFollowRedirects(Boolean.parseBoolean(text));
		} catch (RuntimeException e) {
			throw new XMLBuildException(e.getMessage());
		}
	}
	
	public void addBooleanParameter(BooleanParameter bp) {
		params.add(bp);
	}
	
	public void addParameter(Parameter bp) {
		params.add(bp);
	}
	
	public void addParts(Parts p) throws XMLBuildException {
		parts = p;
	}
	
	public void addEntity(Entity e) {
		this.entity = e;
	}
	
	public void setHttp(String http) {
		this.httpName = http;
	}
	
	/**
	 * Print the response to standard error
	 */
	@attribute(value = "", required = false, defaultValue = "false")
	public void setPrint(Boolean print) {
		this.print = print;
	}
	public void setPrint(String print) throws XMLParseException {
		setPrint(Boolean.parseBoolean(print));
	}
	
	/**
	 * Assuem the response is a browseXML document and execute it
	 */
	@attribute(value = "", required = false, defaultValue = "false")
	public void setParse(Boolean parse) {
		this.parse = parse;
	}
	public void setParse(String parse) throws XMLBuildException {
		setParse(Boolean.parseBoolean(parse));
	}
}
