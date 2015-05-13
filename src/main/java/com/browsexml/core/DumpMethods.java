package com.browsexml.core;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionException;
import javax.xml.xpath.XPathFunctionResolver;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.javalobby.tnt.annotation.attribute;


public class DumpMethods {
	private static Log log = LogFactory.getLog(DumpMethods.class);
	String packageName = null;
	HashSet<Class> done = new HashSet<Class>();
	LinkedList<String> imports = new LinkedList<String>();
	LinkedList<String> elements = new LinkedList<String>();
	LinkedList<String> types = new LinkedList<String>();

	public static void main(String args[]) {

		DumpMethods dm = new DumpMethods();
		System.err.println("Dir = " + new File(".").getAbsolutePath());
		Class c = null;
		try {
			c = Class.forName(args[0] + "." + args[1]);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
			System.exit(1);
		}

		String packageName = args[0];
		dm.setPackageName(packageName);
		StringBuffer out = new StringBuffer(dm.dump(c));
		
		try {
			Class[] classes = getClasses(packageName);
			for (Class clazz: classes) {
				HashSet done = dm.getDone();
				if (!done.contains(clazz)) {
					dm.dump(clazz);
					done.add(clazz);
				}
				
			}
		} catch (ClassNotFoundException e2) {
			e2.printStackTrace();
		}
		
		
		StringBuffer head = new StringBuffer();
		head.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		head.append("<xs:schema id=\"query\" targetNamespace=\"" + packageName
				+ "\" " + "xmlns=\"" + packageName
				+ "\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" "
				+ "attributeFormDefault=\"qualified\" "
				+ "elementFormDefault=\"qualified\" ");
		int i = 0;
		for (String pack : dm.getImports()) {
			head.append(" xmlns:app" + i++ + "=\"" + pack + "\"");
		}
		head.append(" > ");

		for (String pack : dm.getImports()) {
			head.append("<xs:import namespace=\"" + pack
					+ "\" schemaLocation=\"" + pack.replace('.', '_')
					+ ".xsd\"/>");
		}
		for (String type : dm.getTypes()) {
			head.append(type);
		}
		for (String element : dm.getElements()) {
			head.append(element);
		}

		head.append("</xs:schema>");
		
		try {
			System.out.println(dm.format(head.toString()));
		} catch (Throwable e) {
			System.err.println(e);
		}
	}

	public HashSet<Class> getDone() {
		return done;
	}

	public void setDone(HashSet<Class> done) {
		this.done = done;
	}

	public String doRest() {
		StringBuffer out = new StringBuffer();
		try {
			Class[] classes = getClasses(packageName);
			for (Class c: classes) {
				if (!done.contains(c)) {
					done.add(c);
					
					dump(c);
				}
				
			}
		} catch (ClassNotFoundException e2) {
			e2.printStackTrace();
		}
		return out.toString();
	}
	
	public LinkedList<String> getImports() {
		return imports;
	}

	public void setImports(LinkedList<String> imports) {
		this.imports = imports;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	@SuppressWarnings("unchecked")
	public String dump(Class c) {
		Boolean defined = false;
		if (!done.contains(c)) {
			done.add(c);
		} else {
			defined = true;
		}
		StringBuffer ret = new StringBuffer();
		StringBuffer typeDef = new StringBuffer();

		Class object = c.getSuperclass();

		String substitutionGroup = null;
		String base = null;
		String classPackage = c.getPackage().getName();

		if (object != null) {
			//if (!"com.browsexml.core.XmlObject".equals(object.getName())) {
			if (isBrowseXml(object))
				if (!classPackage.equals(object.getPackage().getName())) {
					substitutionGroup = remoteRef(object);
					base = remoteBase(object);
				} else {
					substitutionGroup = getProperName(object.getSimpleName());
					base = object.getSimpleName() + "Type";
				}
			//}
		}

		if (!classPackage.equals(packageName)) {
			int index = imports.indexOf(classPackage);
			if (index < 0) {
				imports.add(classPackage);
				index = imports.indexOf(classPackage);
			}
			return "<xs:element ref=\"" + remoteRef(c) + "\" />";
		}
		String name = c.getSimpleName();
		String type = name + "Type";
		if (name.length() == 0)
			return "";
		name = Character.toLowerCase(name.charAt(0)) + ((name.length() > 0)?name.substring(1):"");
		ret.append("<xs:element ref=\"" + name + "\"/>");

		StringBuffer elementDef = new StringBuffer();

		if (defined)
			return ret.toString();

		elementDef.append("<xs:element name=\"" + name + "\" type=\"" + type
				+ "\" ");
		if (substitutionGroup != null) {
			elementDef
					.append("substitutionGroup=\"" + substitutionGroup + "\"");
		}
		elementDef.append(">");

		elementDef.append("<xs:annotation>");
		elementDef.append("<xs:documentation>");
		elementDef.append("<![CDATA[ ");

		try {
			elementDef.append(getDocumentation((classPackage + "/" + name)
					.replace('.', '/'), null));
		} catch (Exception e) {
			//e.printStackTrace();
			System.err.println(e.getMessage());
		}

		elementDef.append("]]>");
		elementDef.append("</xs:documentation>");
		elementDef.append("</xs:annotation>");

		typeDef.append("<xs:complexType name=\"" + type + "\" ");
		Boolean getTextRedefined = true;
		try {
			getTextRedefined = !"com.browsexml.core.XmlObject".equals(c.getDeclaredMethod("setFromTextContent", String.class).getDeclaringClass().getName());
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			getTextRedefined = false;
		}
		if (getTextRedefined) {
			typeDef.append("mixed=\"true\" ");
		}
		typeDef.append(">");
		
		if (base != null) {
			typeDef.append("<xs:complexContent>");
			typeDef.append("<xs:extension base=\"" + base + "\">");
		}
		typeDef.append("<xs:choice minOccurs=\"0\" maxOccurs=\"unbounded\">");

		Method m[] = c.getDeclaredMethods();

		for (int i = 0; i < m.length; i++) {

			Annotation[] a = m[i].getDeclaredAnnotations();
			for (int ij = 0; ij < a.length; ij++) {
				if (a[ij] instanceof attribute) {
					if (m[i].getName().startsWith("add")) {
						typeDef.append(dump(m[i].getParameterTypes()[0]));
					}
				}
			}
		}
		typeDef.append("</xs:choice>");

		for (int i = 0; i < m.length; i++) {

			Annotation[] a = m[i].getDeclaredAnnotations();
			for (int ij = 0; ij < a.length; ij++) {
				if (a[ij] instanceof attribute) {
					if (m[i].getName().startsWith("set")) {
						String mName = m[i].getName().substring(3);
						mName = getProperName(mName);
						typeDef
								.append("<xs:attribute name=\""
										+ mName
										+ "\" form=\"unqualified\" ");
						
						Class clazz = String.class;
						if (m[i].getParameterTypes().length == 1) {
							clazz = m[i].getParameterTypes()[0];
						}
		
						Boolean foundType = false;
						if ("java.lang.String".equals(clazz.getName())) {
								typeDef.append("type=\"xs:string\" ");
								foundType = true;
						} 
						else if ("java.lang.Long".equals(clazz.getName())) {
							typeDef.append("type=\"xs:long\" ");
							foundType = true;
						}
						else if ("java.lang.Integer".equals(clazz.getName())) {
							typeDef.append("type=\"xs:integer\" ");
							foundType = true;
						}
						else if ("java.lang.Boolean".equals(clazz.getName())) {
							typeDef.append("type=\"xs:boolean\" ");
							foundType = true;
						}
						else if ("java.lang.Float".equals(clazz.getName())) {
							typeDef.append("type=\"xs:float\" ");
							foundType = true;
						}
						else if ("java.lang.Character".equals(clazz.getName())) {
							typeDef.append("type=\"xs:string \" ");
							foundType = true;
						}
						typeDef.append(">");

						typeDef.append("<xs:annotation>");
						typeDef.append("<xs:documentation>");
						typeDef.append("<![CDATA[ ");

						try {
							typeDef.append(getDocumentation(
									(classPackage + "/" + name).replace('.',
											'/'), mName));
						} catch (Exception e) {
							//e.printStackTrace();
							System.err.println(e.getMessage());
						}

						typeDef.append("]]>");
						typeDef.append("</xs:documentation>");
						typeDef.append("</xs:annotation>");


						if (!foundType) {
							typeDef.append("<xs:simpleType> "); 
							typeDef.append("<xs:restriction base=\"xs:string\"> "); 
							       
								Set enumerations = null;
								try {
									enumerations = EnumSet.allOf(clazz);
								} catch (Exception e) {
									System.err.println("CLASS is " + clazz.getClass().getName());
									e.printStackTrace();
								}   
								Iterator itt = enumerations.iterator();
								while (itt.hasNext()) {  
									Object o = itt.next();
									typeDef.append("<xs:enumeration value=\"" + o.toString() + "\" />");  
								}
								typeDef.append("</xs:restriction> "); 
								typeDef.append("</xs:simpleType> "); 
						}
						typeDef.append("</xs:attribute>");

					}
				}

			}
		}

		if (base != null) {
			typeDef.append("</xs:extension>");
			typeDef.append("</xs:complexContent>");
		}
		typeDef.append("</xs:complexType>");
		elementDef.append("</xs:element>");

		if (base != null) {
			dump(object);
		}

		types.add(typeDef.toString());
		elements.add(elementDef.toString());
		return ret.toString();
	}
	
	public boolean isBrowseXml(Class object) {
		String x = object.getPackage().getName();
		return (x.startsWith("edu.bxml") || x.startsWith("com.browsexml"));
	}

	public LinkedList<String> getTypes() {
		return types;
	}

	public void setTypes(LinkedList<String> types) {
		this.types = types;
	}

	public LinkedList<String> getElements() {
		return elements;
	}

	public void setElements(LinkedList<String> elements) {
		this.elements = elements;
	}

	public String format(String unformattedXml) {
		try {
			final Document document = parseXmlFile(unformattedXml);

			OutputFormat format = new OutputFormat(document);
			format.setLineWidth(65);
			format.setIndenting(true);
			format.setIndent(2);
			Writer out = new StringWriter();
			XMLSerializer serializer = new XMLSerializer(out, format);
			serializer.serialize(document);

			return out.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Document parseXmlFile(String in) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(in));
			return db.parse(is);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	String getProperName(String name) {
		return Character.toLowerCase(name.charAt(0)) + name.substring(1);
	}

	public static String getDocumentation(String className, String attributeName)
			throws Exception {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();

		Document document = builder.parse(new File("build/gensrc/" + className
					+ ".xml"));


		XPath xpath = XPathFactory.newInstance().newXPath();
		xpath.setNamespaceContext(new PersonalNamespaceContext());
		xpath.setXPathFunctionResolver(new TrimContext());

		String expression = null;
		if (attributeName == null)
			expression = "/object/description";
		else {
			expression = "/object/parameters/parameter[pre:trim(attribute)=\""
					+ attributeName + "\"]/description";
		}

		Node widgetNode = (Node) xpath.evaluate(expression, document,
				XPathConstants.NODE);

		return widgetNode.getTextContent();

	}

	public String remoteRef(Class c) {
		String classPackage = c.getPackage().getName();
		int index = imports.indexOf(classPackage);
		if (index < 0) {
			imports.add(classPackage);
			index = imports.indexOf(classPackage);
		}
		return "app" + index + ":" + getProperName(c.getSimpleName());
	}

	public String remoteBase(Class c) {
		String classPackage = c.getPackage().getName();
		int index = imports.indexOf(classPackage);
		if (index < 0) {
			imports.add(classPackage);
			index = imports.indexOf(classPackage);
		}
		return "app" + index + ":" + c.getSimpleName() + "Type";
	}

	public static Class[] getClasses(String pckgname)
			throws ClassNotFoundException {
		ArrayList<Class> classes = new ArrayList<Class>();
		// Get a File object for the package
		File directory = null;
		try {
			ClassLoader cld = Thread.currentThread().getContextClassLoader();
			if (cld == null) {
				throw new ClassNotFoundException("Can't get class loader.");
			}
			String path = pckgname.replace('.', '/');
			URL resource = cld.getResource(path);
			if (resource == null) {
				throw new ClassNotFoundException("No resource for " + path);
			}
			directory = new File(resource.getFile());
		} catch (NullPointerException x) {
			throw new ClassNotFoundException(pckgname + " (" + directory
					+ ") does not appear to be a valid package");
		}
		if (directory.exists()) {
			// Get the list of the files contained in the package
			String[] files = directory.list();
			for (int i = 0; i < files.length; i++) {
				// we are only interested in .class files
				if (files[i].endsWith(".class")) {
					// removes the .class extension
					classes.add(Class.forName(pckgname + '.'
							+ files[i].substring(0, files[i].length() - 6)));
				}
			}
		} else {
			throw new ClassNotFoundException(pckgname
					+ " does not appear to be a valid package");
		}
		Class[] classesA = new Class[classes.size()];
		classes.toArray(classesA);
		return classesA;
	}

}

class PersonalNamespaceContext implements NamespaceContext {

	public String getNamespaceURI(String prefix) {
		if (prefix == null)
			throw new NullPointerException("Null prefix");
		else if ("pre".equals(prefix))
			return "http://www.example.com/books";
		else if ("xml".equals(prefix))
			return XMLConstants.XML_NS_URI;
		return XMLConstants.NULL_NS_URI;
	}

	// This method isn't necessary for XPath processing.
	public String getPrefix(String uri) {
		throw new UnsupportedOperationException();
	}

	// This method isn't necessary for XPath processing either.
	public Iterator getPrefixes(String uri) {
		throw new UnsupportedOperationException();
	}

}

class trim implements XPathFunction {
	public Object evaluate(List args) throws XPathFunctionException {
		if (args.size() != 1) {
			throw new XPathFunctionException(
					"Wrong number of arguments to trim()");
		}
		Object o = args.get(0);
		String toTrim = null;
		if (o instanceof String)
			toTrim = (String) args.get(0);
		else if (o instanceof NodeList) {
			NodeList list = (NodeList) o;
			Node node = list.item(0);
			toTrim = node.getTextContent();
		} else {
			throw new XPathFunctionException("Could not convert argument type");
		}

		if (toTrim == null)
			return "";
		return toTrim.trim();
	}
}

class TrimContext implements XPathFunctionResolver {

	private static final QName name = new QName("http://www.example.com/books",
			"trim");

	public XPathFunction resolveFunction(QName name, int arity) {
		if (name.equals(TrimContext.name) && arity == 1) {
			return new trim();
		}
		return null;
	}

}
