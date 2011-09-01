package edu.bxml.http;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.SWTError;
//import org.eclipse.swt.layout.FillLayout;
//import org.eclipse.swt.widgets.Display;
//import org.eclipse.swt.widgets.Shell;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlParser;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;

//import edu.bxml.swt.ControlObject;
//import edu.bxml.swt.Data;
//import edu.bxml.swt.Interface;

/**
 * Get an http web page
 * 
 */
@attribute(value = "", required = false)
public class Get extends XmlObject {
	private static Log log = LogFactory.getLog(Get.class);
	private String url = null;
	private boolean print = false;
	private boolean newSymbolTable = false;

	private boolean followRedirects = false;

	private GetMethod get = null;

	private Vector<Parameter> params = new Vector<Parameter>();

	private Http http = null;
	private String httpName = null;

	private String file = null;
	
	private String toTempFilePrefix = null;
	private String toTempFileSuffix = null;

	private Vector<WebItem> webItems = new Vector<WebItem>();
//	private Vector<Data> datum = new Vector<Data>();
//	private Vector<Interface> interfaces = new Vector<Interface>();
	private XmlParser f = null;

	private byte[] response = null;

	private boolean parse = true;

	public void setHttp(Http http) {
		this.http = http;
	}

	@Override
	public void execute() throws XMLBuildException {
		String url = this.url;
		if (url == null) {
			return;
		}
		log.debug("url = " + url);

		if (http == null) {
			if (httpName != null) {
				http = (Http) this.getSymbolTable().get(httpName);
			}
			else 
				http = (Http) getAncestorOfType(Http.class);
		}
		if (http == null) {
			throw new XMLBuildException(
					"http was not set.  It must an Ancestor or named by the http attribute.");
		}
		if (url.startsWith("/"))
			url = http.getRoot() + url;
		try {
			url = url + getParameterString();
			get = new GetMethod(url);
			log.debug("getting: " + url);
			// keytool - genkey -keystore key.store -alias browse
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw new XMLBuildException(e.getMessage());
		}
		get.setFollowRedirects(followRedirects);
		// String strGetResponseBody = null;
		// byte[] bGetResponseBody = null;
		// InputStream iGetResponseBody = null;
		int iGetResultCode = 0;
		try {
			
			iGetResultCode = http.getClient().executeMethod(get);
			// iGetResponseBody = get.getResponseBodyAsStream();
			response = get.getResponseBody();
		} catch (HttpException e) {
			throw new XMLBuildException(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			throw new XMLBuildException("Connection refused: There appears to be a problem accessing " + url + ".  Make sure the web service is running!");
		} catch (IllegalArgumentException e) {
			return;//throw new XMLBuildException(e.getMessage());
		}
		// log.debug("response: " + bGetResponseBody);

		java.io.File workFile = null;
		if (toTempFileSuffix != null && toTempFilePrefix != null && file == null) {
			try {
				workFile = java.io.File.createTempFile(toTempFilePrefix, toTempFileSuffix);
				file = workFile.getAbsolutePath();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if (file != null)
			workFile = new java.io.File(file);
		log.debug("File = " + file);
		if (workFile != null) {
			try {
				FileOutputStream fo = new FileOutputStream(workFile);
				fo.write(response);
				fo.close();
			} catch (IOException e) {
				throw new XMLBuildException(e.getMessage());
			}
		} else {

		}
		get.releaseConnection();
		
		if (parse) {
			try {
				SAXParserFactory factory = SAXParserFactory.newInstance();
				factory.setNamespaceAware(true);
				
				f = new XmlParser(factory);
				//f.setPackage("edu.wbu.swt");
				log.debug("f = " + f);
				f.setParent(this);
				HashMap map = this.getSymbolTable();
				
				if (newSymbolTable) {
					HashMap newSymbolTable = new HashMap();
					String root = (String) map.get("_#root");
					log.debug("root = " + root);
					newSymbolTable.put("_#root", root);
					f.setSymbolTable(newSymbolTable);
				}
				else
					f.setSymbolTable(map);

				if (true) 
					log.debug("RESPNSE: " + new String(response));
				f.setSource(url);
				f.parse(response);
				f.execute();
			} catch (Exception e) {
				log.debug("HERE  msg = " + e.getMessage());
				e.printStackTrace();
				String strResponse = new String(response);
				if (strResponse.matches(".*html>\\s*\\z")) {
					//FIXME: this interface stuff should not be part of GET
					//new Browser("text/html", new String(response));
//					Display display = Display.getDefault();
//					Shell shell = new Shell(display);
//					shell.setLayout(new FillLayout());
//					org.eclipse.swt.browser.Browser browser;
//					try {
//						browser = new org.eclipse.swt.browser.Browser(shell, SWT.NONE);
//					} catch (SWTError e1) {
//						System.out.println("Could not instantiate Browser: " + e1.getMessage());
//						return;
//					}
//					browser.setText(strResponse);
//					shell.open();
//					while (!shell.isDisposed()) {
//						if (!display.readAndDispatch())
//							display.sleep();
//					}
					log.debug("display.dispose");
					//display.dispose();
					//System.exit(1);
				}
				throw new XMLBuildException(e.getMessage());
			}
		}
	}

	@Override
	public void check() throws XMLBuildException {
//		ControlObject o = getAncestorOfType(ControlObject.class);
//		if (o != null && o.isShowUrl() == true) {
//			o.setToolTipText(url);
//		}
		if ((toTempFilePrefix == null && toTempFileSuffix != null) ||
		(toTempFilePrefix != null && toTempFileSuffix == null))
			log.debug("temp file prefix and suffix should either both be set or neither set.");
	}

	/**
	 * The URL of the page to get
	 */
	@attribute(value = "", required = true)
	public void setUrl(String url) {
		this.url = url;
	}
	
	/**
	 * All predefined symbols are hidden.
	 * @param newSymbolTable
	 */
	@attribute(value = "", required = false, defaultValue = "false")
	public void setNewSymbolTable(Boolean newSymbolTable) {
		this.newSymbolTable = newSymbolTable;
	}
	public void setNewSymbolTable(String newSymbolTable) {
		setNewSymbolTable(Boolean.parseBoolean(newSymbolTable));
	}
	
	public String getUrl() {
		return url;
	}

	/**
	 * Save the response in a file
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false, defaultValue = "System.out")
	public void setToFile(String file) {
		this.file = file;
	}

	/**
	 * Prefix for a temporary file name
	 * @param toTemp
	 */
	@attribute(value = "", required = false, defaultValue = "no temp file is used")
	public void setTempFilePrefix(String toTemp) {
		this.toTempFilePrefix = toTemp;
	}
	
	/**
	 * Suffix for a temporary file name
	 * @param toTempL
	 */
	@attribute(value = "", required = false, defaultValue = "no temp file is used")
	public void setTempFileSuffix(String toTemp) {
		this.toTempFileSuffix = toTemp;
	}
	
	/**
	 * If the page redirects, go to the redirection
	 */
	@attribute(value = "", required = false, defaultValue = "false")
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
	 * Add a key/value pair to the query string
	 * 
	 * @param p
	 */
	@attribute(value = "", required = false)
	public void addParameter(Parameter p) {
		params.add(p);
	}

	public String getParameterString() throws XMLBuildException {
		if (params.size() == 0)
			return "";
		StringBuffer line = new StringBuffer("");
		for (Parameter p : params) {
			line.append("&" + http.getEncoded(p.getKey()) + "="
					+ http.getEncoded((String) p.getValue()));
		}
		return line.toString();
	}

	public void addWebItem(WebItem w) {
		webItems.add(w);
	}

	/**
	 * Interpret the results of the get; that is, assume the file is a 
	 * browseXML document and execute it.  
	 * @see setNewSymbolTable
	 * @param parse
	 */
	@attribute(value = "", required = false, defaultValue="true")
	public void setParse(Boolean parse) {
		this.parse = parse;
	}
	public void setParse(String parse) throws XMLBuildException {
		this.parse = Boolean.parseBoolean(parse);
	}
	
//	public void addData(Data data) throws XMLBuildException {
//		datum.add(data);
//	}
//	
//	public void addInterface(Interface in) throws XMLBuildException {
//		if (f != null) {
//			f.setSymbolTable(new HashMap<String, Object>());
//		}
//	}
//	
//	public void addInterfaceEnd(Interface in) throws XMLBuildException {
//		in.execute();
//	}
	
	/**
	 * the name of the 'Http' object.  
	 * @param http
	 */
	@attribute(value = "", required = false, defaultValue="required if an Http object is not an ancestor in the xml structure")
	public void setHttp(String http) {
		this.httpName = http;
	}

	/**
	 * return the current toFile with forward slashes as path separator
	 * @return
	 */
	public String getFile() {
		String fileName = file.replaceAll("\\\\", "/");
		return fileName;
	}
}
