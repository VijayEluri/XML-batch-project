package com.bxml.xpath;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Parents {

	public static void main(String[] args) {
		Parents x = new Parents();
		String expression = "/object/nested/parameter/name";
		x.evaluateDocument(new File("build/gensrc/edu/bxml/swt"));

	}

	public void evaluateDocument(File fileRoot) {
		HashMap<String, HashSet<String>> hashMaps = new HashMap<String, HashSet<String>>();
		HashMap<String, File> fileNames = new HashMap<String, File>();
		File[] files = fileRoot.listFiles();
		for (int fileCount = 0; fileCount < files.length; fileCount ++) {
			File xmlDocument = files[fileCount];
		XPathFactory factory = XPathFactory.newInstance();
		XPath xPath = factory.newXPath();
		try {
			InputSource inputSource = new InputSource(new FileInputStream(
					xmlDocument));

			XPathExpression xPathExpression = xPath.compile("/object/name");
			String title = xPathExpression.evaluate(inputSource).trim();
			
			title = title.substring(0, 1).toLowerCase() + title.substring(1);

			fileNames.put(title, xmlDocument);
			
			InputSource inputSource2 = new InputSource(new FileInputStream(
					xmlDocument));
			String expression = "/object/nested/parameter/name";
			Object nodes = xPath.evaluate(expression, inputSource2,
					XPathConstants.NODESET);
			NodeList nodeList = (NodeList) nodes;
			for (int i = 0; i < nodeList.getLength(); i++) {
				String child = nodeList.item(i).getTextContent().trim();
				//System.out.println("child = " + child);
				HashSet m = null;
				if (hashMaps.containsKey(child)) {
					m = hashMaps.get(child);
				}
				else {
					m = new HashSet();
					hashMaps.put(child, m);
				}
				m.add(title);
			}
			

			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		}

		
		

		Iterator myVeryOwnIterator = hashMaps.keySet().iterator();
		while(myVeryOwnIterator.hasNext()) {
			
			String key = (String) myVeryOwnIterator.next();
			System.out.println("file to update(" + key + "): " + fileNames.get(key));
			
			Document doc = parseXmlFile(fileNames.get(key), false);
			
			if (doc != null) {
				Node rootObject = doc.getFirstChild();
				Node parents = doc.createElement("parents");
				rootObject.appendChild(parents);
				
			    HashSet s = hashMaps.get(key);
			    Iterator iter = s.iterator();
			    while (iter.hasNext()) {
					Node parent = doc.createElement("parent");
					parent.setTextContent(iter.next().toString());
					parents.appendChild(parent);
			    }
			  
			    print(fileNames.get(key), doc);
			}
		}
		
		
	}

    public static Document parseXmlFile(File filename, boolean validating) {
        try {
        	if (filename == null) 
        		return null;
            // Create a builder factory
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(validating);

            // Create the builder and parse the file
            Document doc = factory.newDocumentBuilder().parse(filename);
            return doc;
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
        	e.printStackTrace();
        } catch (IOException e) {
        	e.printStackTrace();
        }
        return null;
    }
    
    public static void print(File file, Document doc) {
    	Transformer transformer = null;
    	try {
    		transformer = TransformerFactory.newInstance().newTransformer();
		} catch (TransformerConfigurationException e1) {
			e1.printStackTrace();
		} catch (TransformerFactoryConfigurationError e1) {
			e1.printStackTrace();
		}
    	transformer.setOutputProperty(OutputKeys.INDENT, "yes");

    	StreamResult result = new StreamResult(new StringWriter());
    	DOMSource source = new DOMSource(doc);
    	try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			e.printStackTrace();
		}

    	String xmlString = result.getWriter().toString();
   	try {
			PrintStream p = new PrintStream(file);
			p.println(xmlString);
			p.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

    }

}
