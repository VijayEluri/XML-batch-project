package com.browsexml.core;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.httpclient.URI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.browsexml.core.annotation.Add;
import com.browsexml.core.annotation.Ancestor;
import com.browsexml.core.annotation.Check;
import com.browsexml.core.annotation.EndDocument;
import com.browsexml.core.annotation.Finally;
import com.browsexml.core.annotation.SymbolTable;
import com.browsexml.core.annotation.Value;
import com.browsexml.core.annotation.Wrapper;
import com.javalobby.tnt.annotation.attribute;

import edu.bxml.http.Get;
import edu.bxml.http.Http;
import edu.bxml.io.FilterAJ;
import edu.bxml.io.FilterAJImpl;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InterfaceMaker;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * Format a query's output
 */
@attribute("")
public class XmlParser {
	private static Log log = LogFactory.getLog(XmlParser.class);
	String packageName = null;
	boolean namespaceFilter = false;
	boolean traceNoSuchMethod = true;
	boolean traceNoSuchClass = true;

	private static final Pattern predefines = Pattern.compile("\\#\\{[^\\{\\}]*\\}");

	QueryReader handler = new QueryReader();

	private String requestMethod = "GET";
	private String[] cookies = null;

	public String query = null;

	public String key = null;
	String header = "";

	Stack<XmlObject> object = new Stack<XmlObject>();
	List<FilterAJ> createOrder = new LinkedList<FilterAJ>();
	XmlObject currentObject = null;
	XmlObject parentObject = null;
	String source = null;
	String context = null;
	String webContextRoot = null;

	SAXParser saxParser = null;

	static Pattern macroPattern = Pattern.compile("\\$\\{(\\S+?)\\}");
	static Pattern attributePattern = Pattern.compile("\\%\\{(\\S+?)\\}");

	ConcurrentHashMap<String, Object> symbolTable;

	public void setSymbolTable(ConcurrentHashMap<String, Object> symbolTable) {
		if (symbolTable == null)
			return;
		this.symbolTable = symbolTable;
	}

	public ConcurrentHashMap<String, Object> getSymbolTable() {
		return symbolTable;
	}

	XmlObject root = null;

	public XmlObject getRoot() {
		return root;
	}

	public void setRoot(XmlObject root) {
		this.root = root;
	}

	URLClassLoader urlClassLoader = null;
	Vector<URL> urls = new Vector<URL>();

	static ScriptEngineManager sem = new ScriptEngineManager();
	static ScriptEngine javascriptEngine = sem.getEngineByName("ECMAScript");

	static public String processMacros(Object pojo, String text) {
		FilterAJ filter = XmlParser.pojoToWrapper.get(pojo);
		if (filter == null) {
			return null;
		}
		ConcurrentHashMap st = filter.getSymbolTable();
		return processMacros(st, text);
	}

	/**
	 * Replace strings of the form ${name} with the symbol-table value for name.
	 * Name should be the name of a previously defined property or a system
	 * property.
	 * 
	 * @param st
	 *            - the symbol table
	 * @param text
	 *            - the text which may have embedded properties needing
	 *            replacement
	 * @return
	 */
	static public String processMacros(Map st, String text) throws XMLBuildException {
		log.debug("text = " + text);
		if (text.startsWith("javascript:")) {
			text = text.substring(11);
			log.debug("javascript");

			try {
				javascriptEngine.setBindings(new SimpleBindings(st), ScriptContext.ENGINE_SCOPE);
			} catch (Exception e1) {
				log.debug("script engine = " + javascriptEngine);
				log.debug("symold table = " + st);
				e1.printStackTrace();
			}
			try {
				log.debug("eval " + text);
				String result = "" + javascriptEngine.eval(text);
				log.debug("eval " + text + " = " + result);
				return result;
			} catch (ScriptException ex) {
				log.error("text = " + text, ex);
				throw new XMLBuildException(ex.getMessage());
			} catch (NullPointerException ex) {
				log.error("text = " + text, ex);
				throw new XMLBuildException(ex.getMessage());
			}
		} else {
			Matcher myMatcher = macroPattern.matcher(text);
			String origVar = null;
			String[] var = null;
			while (myMatcher.find()) {
				origVar = myMatcher.group(1);
				var = origVar.split("->"); // Use arrow instead of dot (.) to
											// denote structure elements
				Object x = st.get("_#" + var[0]);
				if (x == null) {
					x = st.get(var[0]);
				}
				log.debug("PROCESS MACRO: " + var[0] + " = " + x);
				if (x == null) {
					x = System.getProperty(var[0]);
					if (x == null) {
						x = System.getenv(var[0]);
					}
				}
				String val = (x != null) ? x.toString() : "";
				if (var.length > 1) {
					val = "" + getAttributes((XmlObject) x, var[1]);
				}
				String replace = "\\$\\{" + origVar + "\\}";
				val = val.replaceAll("\\\\", "\\\\\\\\");
				log.debug("replace = " + replace);
				log.debug("val = " + val);
				text = text.replaceAll(replace, val);
			}
			log.debug("return text = " + text);
			return text;
		}
	}

	/**
	 * processMacros using getters on any object(s) to set values
	 * 
	 * @param x
	 * @param text
	 * @return
	 */
	static public String processAttributes(XmlObject object, String text) throws XMLBuildException {
		if (text == null) {
			return text;
		}
		ConcurrentHashMap st = object.getSymbolTable();
		Matcher myMatcher = attributePattern.matcher(text);
		String[] var = null;
		while (myMatcher.find()) {
			String match = myMatcher.group(1);
			log.debug("match = " + match);
			Object current = getAttributeFromPath(object, match);
			String val = (current != null) ? current.toString() : "";
			text = text.replaceAll("\\%\\{" + match + "\\}", val);
			log.debug("text = " + text);

		}
		return text;
	}

	/**
	 * Take a string of the form object.getValue and return the objects value or
	 * null if it doesn't exist in the objects symbol table
	 * 
	 * @param object
	 * @param path
	 * @return
	 * @throws XMLBuildException
	 */
	static public Object getAttributeFromPath(XmlObject object, String path) throws XMLBuildException {
		String[] var = path.split("\\.");
		Object x = null;
		if (var[0].equals("this"))
			x = object;
		else
			x = object.getSymbolTable().get(var[0]);

		Object current = x;
		for (int i = 1; i < var.length; i++) {
			current = getAttributes(current, var[i]);
		}
		return current;
	}

	public static String properName(String name) {
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	public static Object getAttributes(Object object, String attribute) throws XMLBuildException {
		String functionName = "get" + properName(attribute);

		if (object == null) {
			throw new XMLBuildException(functionName + "  attempted call on null object", null);
		}
		Class<?> c = object.getClass();
		try {
			log.debug("get function = " + functionName);
			Method m = c.getMethod(functionName);
			log.debug("invoke function = " + functionName);
			return m.invoke(object);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		throw new XMLBuildException("invoke function getMethod failed: " + functionName, null);
	}

	/**
	 * Iterate through all the attributes of the current object and call
	 * set[attributeName] on the currentObject.
	 * 
	 * @param attrs
	 */
	public static void setAttributes(Attributes attrs, XmlObject currentObject, Map symbolTable, Locator locator)
			throws SAXParseException {
		Class<?> c = currentObject.getClass();
		for (int i = 0; i < attrs.getLength(); i++) {
			String attName = attrs.getQName(i);
			if (attName.contains("schemaLocation"))
				continue;
			String functionName = "set" + properName(attName);
			Class<?>[] parameterTypes = new Class[1];
			Object[] arguments = new Object[1];
			parameterTypes[0] = String.class;
			String message = "";
			try {
				arguments[0] = replacePoundMacros(attrs.getValue(i));
				functionName = functionName.replace(':', '_');
				Method m = c.getMethod(functionName, parameterTypes);
				if (attrs.getValue(i).startsWith("javascript:") || attrs.getValue(i).contains("${")
						|| attrs.getValue(i).contains("#{")) {
					log.debug("putting " + m.getName() + "  " + attrs.getValue(i) + " in object "
							+ currentObject.getName());
					currentObject.getVariableParameters().put(m, attrs.getValue(i));
				}
				m.invoke(currentObject, arguments);
			}
			// catch (XMLBuildException be) {
			// String source = null;
			// if (locator == null) {
			// locator = currentObject.getLocator();
			// source = currentObject.getSource();
			// }
			// if (locator != null) {
			// be.printStackTrace();
			// message = be.getMessage();
			// log.debug("location = " + locator.getLineNumber() + " column: "
			// + locator.getColumnNumber());
			// }
			// }
			catch (NoSuchMethodException nsme) {
				String source = null;
				String linenumber = currentObject.getLineNumber();
				source = currentObject.getSource();
				nsme.printStackTrace();
				message = nsme.getMessage();
				log.debug("source = " + source);
				log.debug("location = " + linenumber);

			} catch (InvocationTargetException ite) {
				ite.printStackTrace();
				if (ite.getMessage() == null)
					throw new SAXParseException(ite.getCause().getMessage(), locator);

			} catch (IllegalAccessException iae) {
				iae.printStackTrace();
				message = iae.getMessage();
			}
		}
	}

	public static void addToParent(Object parent, Object current, Locator locator, boolean endTag)
			throws SAXParseException {
		Class c = parent.getClass();
		Class[] parameterTypes = new Class[1];
		Object[] arguments = new Object[1];

		String endString = "";
		if (endTag)
			endString = "End";
		boolean success = false;
		Exception ex = null;
		String name = null;
		log.debug("Add to parent");
		if (endTag) {
			if (parent instanceof FilterAJ && current instanceof FilterAJ) {
				Object parentPojo = ((FilterAJ) parent).getPojo();
				Object currentPojo = ((FilterAJ) current).getPojo();
				if (parentPojo != null) {
					for (Method method : parentPojo.getClass().getMethods()) {
						if (method.isAnnotationPresent(Add.class)) {
							log.debug("Annotation Add found on " + method);
							if (currentPojo == null) {
								parameterTypes[0] = current.getClass();
								arguments[0] = current;
							} else {
								parameterTypes[0] = currentPojo.getClass();
								arguments[0] = currentPojo;
							}
							try {
								log.debug("invoke parent = " + parentPojo.getClass().getName() + "  method name + "
										+ method.getName() + "   arguments: " + arguments[0].getClass().getName()
										+ "  type: " + parameterTypes[0]);

								method.invoke(parentPojo, arguments);
								log.debug("success");
								return;
							} catch (IllegalArgumentException e) {
								log.debug(e.getMessage(), e);
							} catch (IllegalAccessException e) {
								log.debug(e.getMessage(), e);
							} catch (InvocationTargetException e) {
								log.debug(e.getMessage(), e);
							}
						}
					}
				}
			}
		}
		arguments[0] = current;
		for (Method m : c.getMethods()) {
			if (m.getName().equals("add") && m.getParameterTypes().length == 1) {
				try {
					log.debug("Call " + m);
					m.invoke(parent, arguments);
					log.debug("Call SUCCESS");
					success = true;
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		for (Class t = current.getClass(); !success && t != null; t = t.getSuperclass()) {
			try {
				parameterTypes[0] = t;
				name = "add" + properName(t.getSimpleName()) + endString;
				log.debug("calling " + name + " on " + c.getName() + " with parameter " + t.getName());
				Method m = c.getMethod(name, parameterTypes);
				m.invoke(parent, arguments);
				success = true;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// for (Method m:c.getMethods()) {
				// log.debug("method: " + m.getName());
				// }
			} catch (InvocationTargetException e) {
				if (e.getMessage() == null) {
					e.printStackTrace();
					throw new SAXParseException(e.getCause().getMessage(), locator);
				}
				//
			}
		}

		if (ex != null && !success) {
			log.debug("location = " + locator.getLineNumber() + "  column: " + locator.getColumnNumber());
			new SAXParseException("No Such Method Exception: " + name, locator).printStackTrace();
		}
	}

	public static void updateVariables(XmlObject x) throws XMLBuildException {
		Set c = x.getVariableParameters().entrySet();
		log.debug("size of variable parameters = " + c.size());
		Iterator itr = c.iterator();

		while (itr.hasNext()) {
			Object[] arguments = new Object[1];
			Entry<Method, String> hmItem = (Entry<Method, String>) itr.next();

			arguments[0] = XmlParser.processMacros(x.getSymbolTable(), XmlParser.replacePoundMacros(hmItem.getValue()));
			log.debug("aspect before execute " + x.getName() + ": " + x.getClass().getName() + ": " + hmItem.getValue()
					+ " = " + arguments[0]);
			try {
				hmItem.getKey().invoke(x, arguments);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Replace date strings of the form {SimpleDateFormat pattern} with the
	 * date. Example: #{yyyymmdd} gets replaced with 20060801 on August 1, 2006.
	 * 
	 * @param filename
	 * @return
	 */
	public static String replacePoundMacros(String text) {
		Matcher m = predefines.matcher(text);
		while (m.find()) {
			String replace = m.group();
			String format = replace.substring(2, replace.length() - 1);
			replace = "\\#\\{" + format + "\\}";

			SimpleDateFormat f = new SimpleDateFormat(format);
			String replacement = f.format(new Date());
			text = text.replaceAll(replace, replacement);
			m = predefines.matcher(text);
		}
		return text;
	}

	
	/** root executed once to kick things off... is this not needed?
	 * */
	public void execute() throws XMLBuildException {
		log.debug("ROOT EXECUTE g  is a " + root.getClass().getName());
		if (root != null) {
			log.debug("root not null ");
			//root.execute();
			log.debug("done execute");
		} else {
			new Exception().printStackTrace();
		}

	}
	
	
	public XmlParser() {

	}

	public XmlParser(SAXParserFactory factory) {
		try {
			saxParser = factory.newSAXParser();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	public XmlParser(byte[] data, SAXParserFactory factory, Map env) throws Exception {
		symbolTable = new ConcurrentHashMap<String, Object>();
		saxParser = factory.newSAXParser();
		if (env != null)
			symbolTable.put("_#env", env);
		parse(data);
	}

	public XmlParser(String xmlFile, SAXParserFactory factory, Map env)
			throws XMLBuildException, IOException, SAXParseException, SAXException, ParserConfigurationException {
		saxParser = factory.newSAXParser();
		symbolTable = new ConcurrentHashMap<String, Object>();
		if (env != null)
			symbolTable.put("_#env", env);
		parse(xmlFile);
	}

	public XmlParser(URL xmlFile, SAXParserFactory factory, Map env)
			throws XMLBuildException, IOException, SAXParseException, SAXException, ParserConfigurationException {
		saxParser = factory.newSAXParser();
		symbolTable = new ConcurrentHashMap<String, Object>();
		if (env != null)
			symbolTable.put("_#env", env);
		parse(xmlFile);
	}

	public XmlParser(String xmlFile, SAXParserFactory factory, String[] args)
			throws XMLBuildException, IOException, SAXParseException, SAXException, ParserConfigurationException {
		HashMap<String, String> env = new HashMap<String, String>();

		for (int i = 1; i < args.length; i++) {
			String arg = args[i];
			log.debug("arg = " + arg);
			String[] pair = arg.split("=");
			if (pair.length == 2) {
				env.put(pair[0], pair[1]);
			}
		}
		saxParser = factory.newSAXParser();
		if (symbolTable == null) {
			symbolTable = new ConcurrentHashMap<String, Object>();
		}
		if (env != null)
			symbolTable.put("_#env", env);
		parse(xmlFile);
	}

	public String[] getCookies() {
		return cookies;
	}

	public String getCookieString() {
		if (cookies == null) {
			return null;
		}
		StringBuffer cookie = null;
		for (int i = 0; i < cookies.length; i++)
			cookie.append(";").append(cookies[i]);
		return cookie.substring(1);
	}

	public void setParent() {
		currentObject = object.peek();
		parentObject = object.peek();
	}

	public void setParent(XmlObject p) {
		object.push(p);
		parentObject = p;
	}

	public void setRequestMethod(String method) {
		this.requestMethod = method;
	}

	public void parse(byte[] data) throws Exception {
		InputStream in = null;
		in = new ByteArrayInputStream(data);
		parse(in);
	}

	public void setSource(String file) {
		if (file == null) {
			new Exception().printStackTrace();
		} else
			source = file;
	}

	public String getContext() {
		return context;
	}

	public void parse(URL xmlFile)
			throws ConnectException, XMLBuildException, IOException, SAXParseException, SAXException {
		log.debug("URL file = " + xmlFile);
		parse(xmlFile.openStream());
	}

	public void doFinally() {
		log.debug("FINALLY");
		for (FilterAJ key : createOrder) {
			log.debug("static end = " + key);

			FilterAJ wrapper = null;
			try {
				wrapper = XmlParser.getWrapper(key);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			if (wrapper != null && wrapper.isIff()) {
				try {
					Object currentPojo = wrapper.getPojo();

					for (Method m : currentPojo.getClass().getMethods()) {
						if (m.isAnnotationPresent(Finally.class)) {
							log.debug("FINALLY found");
							m.invoke(currentPojo, (Object[]) null);
							break;
						}
					}
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}

		}
	}

	public void parse(String xmlFile)
			throws ConnectException, XMLBuildException, IOException, SAXParseException, SAXException {
		source = xmlFile;

		URI x = new URI(xmlFile, false);
		// URL n = new URL(xmlFile);
		// context = n.getRef();

		log.debug("context = " + context);
		String scheme = x.getScheme();
		if (scheme != null && "bxmls:https".indexOf(scheme) > -1) {
			// try {
			Http h = new Http();
			h.init(null);
			String certs = System.getProperty("java.home") + "/lib/security/cacerts";
			h.setTrustStore(certs);
			h.setTimeout("30000");
			h.setCookiePolity("RFC_2109");
			h.setEncode("UTF-8");
			h.setSymbolTable(this.symbolTable);

			h.setRoot(xmlFile);
			Get g = new Get();
			g.setSymbolTable(this.symbolTable);
			g.setParent(h);
			g.init(h);
			g.setFollowRedirects("true");
			g.setUrl(xmlFile);
			g.setSource(xmlFile);
			g.execute();
			doFinally();
			// }
			// catch (Exception e) {throw new
			// XMLBuildException(e.getMessage());};
		} else {
			InputStream in = new FileInputStream(xmlFile);
			parse(in);
			in.close();
		}
	}

	public void parse(InputStream in) throws IOException, SAXParseException, SAXException {

		try {

			try {
				handler.setParser(this);
				// log.debug("saxParser = " + saxParser);
				// log.debug("handler = " + handler);
				// log.debug("in = " + in);
				saxParser.parse(in, handler);
			} catch (IOException ioe) {
				ioe.printStackTrace();
				throw ioe;
			} catch (SAXParseException spe) {
				log.warn(spe.getMessage());
				log.warn("at " + spe.getSystemId() + "  line: " + spe.getLineNumber() + "  column: "
						+ spe.getColumnNumber());
				log.warn("");
				spe.printStackTrace();
				throw spe;
			} catch (SAXException t) {
				log.debug(t.getMessage());
				throw t;
			}
		}

		finally {
			doFinally();
		}
		/* TODO put formatString into sub classes */
		// if (formatString.length()>0) // get rid of initial delimiter
		// formatString = formatString.substring(select.delimit.length());
	}

	public void setPackage(String packageName) {
		this.namespaceFilter = true;
		this.packageName = packageName;
		log.debug("package name just set to " + packageName);
	}

	public static <T> T[] concat(T[] first, T[] second) {
		T[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	static Stack<PrintStream> oStack = new Stack<PrintStream>();
	static Stack<InputStream> iStack = new Stack<InputStream>();
	static PrintStream currentOut = System.out;
	static PrintStream sysOut = System.out;
	static InputStream currentIn = System.in;
	static InputStream sysIn = System.in;

	public static void setSysOut(PrintStream out) {
		sysOut = out;
	}

	public static Boolean endDocument(Object currentPojo) throws SAXException, IOException {
		log.debug("STATIC END DOC " + currentPojo.getClass().getName(), new Exception());

		FilterAJ wrapper = (FilterAJ) pojoToWrapper.get(currentPojo);
		if (wrapper != null) {
			log.debug("STATIC END DOC  name=" + wrapper.getName());
		} else {
			log.debug("STATIC END DOC  wrapper=null");
		}

		if (wrapper != null) {
			log.debug("POJO = " + currentPojo.getClass().getName());
			log.debug("SUPER = " + currentPojo.getClass().getSuperclass().getName());
			/**
			 * The static call is called by client objects explicitly; so ignore
			 * the 'parentAllowsEndDoc' annotation
			 */
			// if (!parentAllowsEndDocCall(wrapper)) {
			// log.debug("Parent does not allow end document call");
			// return;
			// }
			try {
				log.debug("Name = " + wrapper.getName());
			} catch (Exception e1) {

				e1.printStackTrace();
				System.exit(1);
			}

			Method endDocument = null;
			Method check = null;

			for (Method m : currentPojo.getClass().getMethods()) {
				if (m.isAnnotationPresent(EndDocument.class)) {
					endDocument = m;
					for (Class<?> x : currentPojo.getClass().getInterfaces()) {
						log.debug("INTERFACE = " + x);
					}

					log.debug("ANNOTATION on " + m.toString());

					log.debug("current pojo = " + currentPojo);
					try {
						log.debug("current pojo name = " + XmlParser.getWrapper(currentPojo).getName());
					} catch (Exception e) {
						e.printStackTrace();
					}
					log.debug("end document: wrappers = " + pojoToWrapper);
				}
				if (m.isAnnotationPresent(Check.class)) {
					check = m;
				}
			}

			Boolean checkPassed = true;
			if (check != null) {
				try {
					check.invoke(currentPojo, (Object[]) null);
				} catch (IllegalAccessException e) {
					checkPassed = false;
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					checkPassed = false;
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					checkPassed = false;
					e.printStackTrace();
				}
			}

			log.debug("check passed = " + checkPassed);
			if (checkPassed && endDocument != null) {
				log.debug("check iff");
				if (!wrapper.isIff()) {
					log.debug("skip " + wrapper.getName() + " do to iff evaluation");
					return true;
				}
				log.debug("iff evaluate normal");

				Boolean redirectOut = (wrapper.getToDir() != null) && (wrapper.getToFile() != null);
				Boolean redirectIn = (wrapper.getDir() != null) && (wrapper.getFile() != null);
				log.debug("redirect In = " + redirectIn);
				boolean pushedInstream = false;
				boolean pushedOutstream = false;
				try {
					
					replaceAllVariables(wrapper);

					if (redirectOut) {
						oStack.push(currentOut);
						pushedOutstream = true;
						currentOut = wrapper.getOut();
						System.setOut(currentOut);
					}

					if (redirectIn) {
						iStack.push(currentIn);
						pushedInstream = true;
						currentIn = wrapper.getIn();
						System.setIn(currentIn);
					}
					// log.debug("execute pojo name is " + wrapper.getName());
					// log.debug("current in = " + currentIn);

					endDocument.invoke(currentPojo, (Object[]) null);

				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} finally {
					if (redirectIn) {
						if (pushedInstream) {
							System.setIn(currentIn = iStack.pop());
						}
						wrapper.closeIn();
					}
					if (redirectOut) {
						if (pushedOutstream) {
							System.setOut(currentOut = oStack.pop());
						}
						wrapper.closeOut();
					}
				}
			}
		}

		return false;
	}

	/*
	 * Replace all parameters of form ${name} with the actual values in
	 * preparation for a call to the @EndDocument (previously execute) method
	 */
	public static void replaceAllVariables(XmlObject x) {
		if (x == null)
			return;
		Set c = x.getVariableParameters().entrySet();
		log.debug("ReplaceAllVariables for " + x.getName());
		log.debug("size of variable parameters = " + c.size());
		log.debug("variable parameters = " + c);
		Iterator itr = c.iterator();

		// Call all setter methods
		while (itr.hasNext()) {
			Object[] arguments = new Object[1];
			Entry<Method, String> hmItem = (Entry<Method, String>) itr.next();

			arguments[0] = XmlParser.processMacros(x.getSymbolTable(), XmlParser.replacePoundMacros(hmItem.getValue()));
			log.debug("aspect before execute " + x.getName() + ": " + x.getClass().getName() + ": " + hmItem.getValue()
					+ " = " + arguments[0]);
			log.debug("method to invoke is " + hmItem.getKey().getName());
			try {
				hmItem.getKey().invoke(x, arguments);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	public static void replaceAllVariablesForPojo(Object pojo) {
		FilterAJ wrapper = null;
		try {
			wrapper = getWrapper(pojo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (wrapper != null)
			replaceAllVariables(wrapper);
	}

	public static FilterAJ getWrapper(Object pojo) throws XMLBuildException {
		FilterAJ wrapper = pojoToWrapper.get(pojo);
		if (wrapper == null) {
			throw new XMLBuildException(pojo.getClass().getName() + " is not a wrapped pojo");
		}
		return wrapper;
	}

	public static Map<Object, FilterAJ> getPojoToWrapper() {
		return pojoToWrapper;
	}

	public static void setPojoToWrapper(Map<Object, FilterAJ> pojoToWrapper) {
		XmlParser.pojoToWrapper = pojoToWrapper;
	}

	public static <S, W> W createWrapper(final S pojo, final Class<W> wrapperClass) {

		log.debug("create wrapper:  pojo type = " + pojo.getClass().getName() + "  wrapper class = "
				+ wrapperClass.getName());
		InterfaceMaker m = new InterfaceMaker();
		m.add(pojo.getClass());

		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(wrapperClass);

		Class[] interfaceArray = concat(wrapperClass.getInterfaces(), new Class<?>[] { m.create() });
		enhancer.setInterfaces(interfaceArray);

		enhancer.setCallback(new MethodInterceptor() {

			@Override
			public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy)
					throws Throwable {

				if ("getPojo".equals(method.getName())) {
					return pojo;
				}

				Object ret = null;
				List<Method> list = Arrays.asList(wrapperClass.getMethods());
				if (list.contains(method)) {
					ret = methodProxy.invokeSuper(proxy, args);
				}

				try {
					Method pojoMethod = pojo.getClass().getMethod(method.getName(), method.getParameterTypes());
					ret = pojoMethod.invoke(pojo, args);
				} catch (Exception e) {
					// This is not an error; the pojo simply didn't implement a
					// callable wrapper method
					// e.printStackTrace();
				}

				return ret;

			}

		});

		W ret = (W) enhancer.create();
		pojoToWrapper.put((Object) pojo, (FilterAJ) ret);
		return ret;
	}

	static Map<Object, FilterAJ> pojoToWrapper = new HashMap<Object, FilterAJ>();

	class QueryReader extends DefaultHandler {
		StringBuffer textBuffer;
		Locator locator;
		XmlParser parser = null;

		public void setParser(XmlParser x) {
			this.parser = x;
		}

		public void processingInstruction(String target, String data) {
			if (target.equals("classpath")) {
				String[] paths = data.split(";");
				for (String path : paths) {
					try {
						urls.add(new URL(path));
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
					if (urls == null) {
						urlClassLoader = new URLClassLoader((URL[]) urls.toArray());
					}
				}
			}
			if (target.equals("package")) {
				packageName = data.trim();
			}
		}

		public void setDocumentLocator(Locator locator) {
			this.locator = locator;
		}

		public void startDocument() throws SAXException {
			// SplashScreen s = SplashScreen.getSplashScreen();
			// if (s != null)
			// s.close();
		}

		public void endDocument() throws SAXException {
			log.debug("END DOCUMENT");
			XmlObject skipParent = null;
			for (FilterAJ key : createOrder) {
				if (skipParent != null) { // Skip all children of iff="false"
											// attribute
					log.debug("skipping...  skip parent name = " + skipParent.getName() + "    key parent name = "
							+ key.getParent().getName());
					if (key.hasAncestorWithName(skipParent.getName())) {
						log.debug("CONTINUE SKIPPING");
						continue;
					}
					log.debug("SKIPPING has finished on " + key.getName());
					skipParent = null;
				}
				log.debug("object is FILTERAJ = " + key);
				if (!parentAllowsEndDocCall(key)) {
					continue;
				}
				log.debug("static end = " + key);
				try {
					FilterAJ wrapper = null;
					try {
						wrapper = XmlParser.getWrapper(key);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (wrapper != null && wrapper.isIff()) {
						XmlParser.endDocument(key.getPojo());
					} else {
						skipParent = key;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}

		public void startElement(String namespaceURI, String sName, String qName, Attributes attrs)
				throws SAXException {
			// qName = qName.toLowerCase();

			// log.debug("Start qName = " + qName);
			textBuffer = null;

			Class c = null;
			String name = properName(sName);

			try {
				log.debug("namespace URI = " + namespaceURI);
				log.debug("package = " + packageName);
				if (packageName == null) {
					Map env = (Map) symbolTable.get("_#env");
					if (env != null)
						packageName = (String) env.get("#ct_package");
				}
				log.debug("package = " + packageName);
				String fullName = "";
				if (packageName != null)
					fullName = packageName + "." + name;
				else if (namespaceFilter) {
					fullName = packageName + "." + sName;
				} else
					fullName = namespaceURI + "." + name;

				log.debug("fullName = " + fullName);

				String newFullName = (String) symbolTable.get("#ct_" + fullName);
				if (newFullName != null) {
					fullName = newFullName;
				}
				log.debug("fullName = " + fullName);
				c = Class.forName(fullName);
				log.debug("class c = " + c);
				Object o = null;
				try {
					o = c.newInstance();
				} catch (IllegalAccessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					log.debug(c.getName() + ": the class or its nullary constructor is not accessible");
					log.debug("Make sure " + c.getName() + " has a public nullary constructor.");
					e1.printStackTrace();
					System.exit(-1);
				}

				try {
					currentObject = (XmlObject) o;
				} catch (java.lang.ClassCastException cce) {
					log.debug("Create WRAPPER (cast exception)");
					currentObject = (XmlObject) createWrapper(o, FilterAJImpl.class);
					currentObject.setTypeName(o.getClass().getName());

				}
				String cname = attrs.getValue("name");
				currentObject.setName(cname);
				log.debug("object name = " + currentObject.getClass().getName() + "  " + cname);
				currentObject.setLocator(locator);
				currentObject.setSource(source);
				currentObject.setParser(this.parser);
				if (root == null) {
					root = currentObject;
				}

			} catch (InstantiationException e) {
				log.warn(c.getName()
						+ ": this Class represents an abstract class, an interface, an array class, a primitive type, or void; or the class has no nullary constructor");
				e.printStackTrace();
				System.exit(1);
			} catch (ClassNotFoundException e) {
				if (traceNoSuchClass) {
					e.printStackTrace();
					log.debug("could not load " + name);
					throw new SAXException("Class not found: " + e.getMessage());
				}
			}
			// log.debug("BEGIN: " + qName + ": " + currentObject);
			currentObject.setSymbolTable(symbolTable);

			if (parentObject != null) {
				addToParent(parentObject, currentObject, locator, false);
			}

			if (currentObject != null) {
				try {
					currentObject.setParent(parentObject);

					if (currentObject.processRawAttributes(attrs))
						setAttributes(attrs, currentObject, symbolTable, handler.locator);

					String cname = currentObject.getName();
					log.debug("cname = " + cname);
					if (cname != null) {
						if (null != symbolTable.put(cname, currentObject)) {
							currentObject.setLocator(locator);
							currentObject.setSource(source);
							throw new XMLBuildException(cname + ": multiply defined.", null);
						} else {
							log.debug(cname + " cname null on put to symboltable");
							if (currentObject instanceof FilterAJ) {
								log.debug("instanceof FIlterAJ");
							} else {
								log.debug("does not implement FilterAJ");
							}
						}
					}

					currentObject.init(parentObject);

					if (currentObject instanceof FilterAJ) {
						Object currentPojo = ((FilterAJ) currentObject).getPojo();
						if (currentPojo != null) {
							log.debug("Looking for fields symbolTable and Ancestor on "
									+ currentPojo.getClass().getName());
							for (Field f : currentPojo.getClass().getDeclaredFields()) {
								log.debug("get annotations for field " + f.getName());
								f.setAccessible(true);
								if (f.isAnnotationPresent(SymbolTable.class)) {
									try {
										log.debug("annotation set symbolTable to " + symbolTable);
										f.set(currentPojo, symbolTable);
									} catch (IllegalArgumentException e) {
										e.printStackTrace();
									} catch (IllegalAccessException e) {
										e.printStackTrace();
									}
								}
								if (f.isAnnotationPresent(Ancestor.class)) {
									Object ancestor = currentObject.getAncestorOfType(f.getType());
									try {
										log.debug("annotation set ancestor to " + ancestor);
										f.set(currentPojo, ancestor);
									} catch (IllegalArgumentException e) {
										e.printStackTrace();
									} catch (IllegalAccessException e) {
										e.printStackTrace();
									}
									if (ancestor == null) {
										throw new XMLBuildException("A class of type " + f.getType()
												+ " must be an ancestor of " + currentObject.getName()
												+ " which is of type " + currentPojo.getClass().getName()
												+ "   with a type name of " + currentObject.getTypeName(),
												currentObject);
									}
								}
								log.debug("check wrapper class present =  " + f.isAnnotationPresent(Wrapper.class));
								if (f.isAnnotationPresent(Wrapper.class)) {
									try {
										log.debug("set Wrapper annotation for " + currentPojo);
										f.set(currentPojo, XmlParser.getWrapper(currentPojo));
									} catch (IllegalArgumentException e) {
										e.printStackTrace();
									} catch (IllegalAccessException e) {
										e.printStackTrace();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
								log.debug("loop end");
							}
						}
					}

				} catch (XMLBuildException e) {
					e.printStackTrace();
					throw new SAXException(e.getMessage());
				}
			}
			object.push(currentObject);
			if (currentObject instanceof FilterAJ) {
				createOrder.add((FilterAJ) currentObject);
			}
			parentObject = currentObject;
			currentObject = null;
		}

		public void endElement(String namespaceURI, String sName, String qName) throws SAXException {
			qName = qName.toLowerCase();
			// log.debug("End qName = " + qName);
			// log.debug("buffer = " + textBuffer);
			String strBuffer = null;

			// FIXME -- processMacros disabled for now
			// if (textBuffer != null) {
			// try {
			// strBuffer = processMacros(symbolTable,textBuffer.toString());
			// } catch (XMLBuildException e1) {
			// e1.printStackTrace();
			// }
			// }
			// END
			if (textBuffer != null)
				strBuffer = textBuffer.toString();
			if (qName.equals("sql")) {
				query = strBuffer;
			}
			textBuffer = null;
			// log.debug("End " + qName);

			XmlObject o = object.pop();

			try {
				if (strBuffer != null) {
					if (strBuffer.contains("${") || strBuffer.contains("#{")) {
						try {
							Class<?>[] parameterTypes = new Class[1];
							parameterTypes[0] = String.class;
							o.getVariableParameters().put(o.getClass().getMethod("setValue", parameterTypes),
									strBuffer);
						} catch (SecurityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (NoSuchMethodException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					o.setFromTextContent(strBuffer);
					if (o instanceof FilterAJ) {
						log.debug("looking for VALUE FilterAJ  = " + o.getClass().getName());
						Object currentPojo = ((FilterAJ) o).getPojo();
						if (currentPojo != null) {
							setValue((FilterAJ) o, strBuffer);
						}
					}
				}
				o.check();
			} catch (XMLBuildException e) {
				e.printStackTrace();
				throw new SAXParseException(e.getMessage(), locator);
			}
			if (object.isEmpty())
				parentObject = null;
			else {
				parentObject = object.peek();
				addToParent(parentObject, o, locator, true);
			}
		}

		public void characters(char buf[], int offset, int len) throws SAXException {
			String s = new String(buf, offset, len);
			if (textBuffer == null) {
				textBuffer = new StringBuffer(s);
			} else {
				textBuffer.append(s);
			}
		}
	}

	public static void setValue(FilterAJ wrapper, String strBuffer) {
		Object currentPojo = ((FilterAJ) wrapper).getPojo();
		log.debug("looking for VALUE pojo  = " + currentPojo.getClass().getName());
		for (Field m : currentPojo.getClass().getFields()) {
			if (m.isAnnotationPresent(Value.class)) {
				log.debug("VALUE ANNOTATION on " + m.toString());

				try {
					m.setAccessible(true);
					m.set(currentPojo, toObject(m.getType(), strBuffer));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}

			}
		}
	}

	public static Object toObject(Class clazz, String value) {
		if (Boolean.class == clazz)
			return Boolean.parseBoolean(value);
		if (Byte.class == clazz)
			return Byte.parseByte(value);
		if (Short.class == clazz)
			return Short.parseShort(value);
		if (Integer.class == clazz)
			return Integer.parseInt(value);
		if (Long.class == clazz)
			return Long.parseLong(value);
		if (Float.class == clazz)
			return Float.parseFloat(value);
		if (Double.class == clazz)
			return Double.parseDouble(value);
		return value;
	}

	public static String getValue(FilterAJ wrapper) {
		Object currentPojo = ((FilterAJ) wrapper).getPojo();
		log.debug("looking for VALUE pojo  = " + currentPojo.getClass().getName());
		for (Field m : currentPojo.getClass().getFields()) {
			if (m.isAnnotationPresent(Value.class)) {
				log.debug("VALUE ANNOTATION on " + m.toString());
				try {
					m.setAccessible(true);
					return (String) m.get(currentPojo);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}

			}
		}
		return null;
	}

	/**
	 * Get the integer value of a named field from a supplied class. Named
	 * fields can be separated by a pipe (|) symbol to 'or' together multiple
	 * field values. An integer may be used instead of a field name for that
	 * literal number to be included in the result.
	 * 
	 * @param c
	 *            is the class that the field will be found in
	 * @param styleString
	 *            is the name of the field to look for
	 * @return the integer value of the field in the class.
	 */
	static public int getFieldValues(Class c, String styleString) {
		int style = 0;

		if (styleString == null)
			return 0;

		int value = 0;
		String[] strings = styleString.split("\\|");
		for (int i = 0; i < strings.length; i++) {
			value = 0;
			try {
				value = Integer.parseInt(strings[i]);
			} catch (NumberFormatException n) {
				Field f = null;
				try {
					f = c.getField(strings[i]);
					value = f.getInt(c);
				} catch (NoSuchFieldException nsfe) {
				} catch (IllegalAccessException iae) {
				}
			}
			style |= value;
		}
		return style;
	}

	public void setTraceNoSuchClass(boolean traceNoSuchClass) {
		this.traceNoSuchClass = traceNoSuchClass;
	}

	public void setTraceNoSuchMethod(boolean traceNoSuchMethod) {
		this.traceNoSuchMethod = traceNoSuchMethod;
	}

	public static String parseWebContextRoot(URL url) {
		if (url == null)
			return null;
		String protocol = url.getProtocol();
		String path = url.getPath();
		int index = path.lastIndexOf('/');
		if (index == 0)
			index = path.length();
		String root = protocol + "://" + url.getHost() + ":" + url.getPort() + path.substring(0, index);
		log.debug("root = " + root);
		if (root.indexOf("null") > 0)
			return null;
		return root;
	}

	public static PrintStream getOriginalSystemOut() {
		return sysOut;
	}

	public static InputStream getOriginalSystemIn() {
		return sysIn;
	}

	public static boolean parentAllowsEndDocCall(Object object) {
		XmlObject parent = ((FilterAJ) object).getParent();

		if (parent instanceof FilterAJ) {
			Object parentPojo = ((FilterAJ) parent).getPojo();
			for (Method pm : parentPojo.getClass().getMethods()) {
				if (pm.isAnnotationPresent(EndDocument.class)) {
					EndDocument singleAnnotation = pm.getAnnotation(EndDocument.class);
					if (singleAnnotation.overrideImmediateChildren()) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Lookup an object in the symbol table without using a reference to the
	 * symbol table. Object should be the pojo needing to lookup another object.
	 * 
	 * Connection connection = (Connection) lookup(this, "theConnection");
	 * 
	 * @param ob
	 * @param key
	 * @return
	 */
	public static Object lookup(Object ob, String key) {
		FilterAJ filter = XmlParser.pojoToWrapper.get(ob);
		if (filter == null)
			return null;
		ConcurrentHashMap st = filter.getSymbolTable();
		FilterAJ o = (FilterAJ) st.get(key);
		if (o != null) {
			return o.getPojo();
		}
		return null;
	}

}
