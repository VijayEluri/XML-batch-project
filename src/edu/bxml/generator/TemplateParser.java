package edu.bxml.generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TemplateParser {
	private static Log log = LogFactory.getLog(TemplateParser.class);
	
	static BxmlPattern variableReplacePattern = new BxmlPattern("\\$\\{([^}]*)\\}") {
		@Override
		public String replace(List<String> match, Map env) {
			if (env == null) 
				return match.get(0);
            Object obj = env.get(match.get(1));
            String replace = "";
            if (obj != null)
            	replace = obj.toString();
            return replace;
		}
	};
	
	static String replaceVariables(String line, Map<String, Object> env) {
        return variableReplacePattern.execute(line, env);
	}
	
	/**
	 * Read a file into a string replacing ${varname} style variables from env.  If env is null,
	 * this just does a normal file read into the string.
	 * @param templatePath
	 * @param env
	 * @return
	 * @throws java.io.IOException
	 */
	static public String readFileAsString(File templatePath, Map<String, Object> env) throws java.io.IOException {

		BufferedReader reader = new BufferedReader(new FileReader(templatePath));
		StringBuffer ret = new StringBuffer();
		String line = null;
		Boolean newlines = true;
		for (line = reader.readLine(); line != null;line = reader.readLine()) {
			if (line.startsWith("#nonewline")) {
				newlines = false;
				continue;
			}
			if (line.startsWith("#newline")) {
				newlines = true;
				continue;
			}
			if (line.startsWith("#for")) {
				Matcher matcher = variableReplacePattern.getPattern().matcher(line);
				if (matcher.find()) {
					Map<String, Map<String, String>> variable = (Map<String, Map<String, String>>) env.get(matcher.group(1));
					if (variable == null) {
						throw new java.io.IOException(templatePath.getAbsolutePath() + ": " + matcher.group(1) + " is not in environment");
					}
					List<String> lines = new ArrayList<String>();
					for (line = reader.readLine(); line != null && !line.startsWith("#end for");line = reader.readLine()) {
						lines.add(line);
					}
					for (Map.Entry<String, Map<String, String>>entry: variable.entrySet()) {
						Map<String, Object> env1 = env;
						log.debug("EntryHashMapValue =  " + entry.getValue());
						env1.putAll(entry.getValue());
						
						String key = entry.getKey();
						env1.put("key", key);
						env1.put("Key", Character.toUpperCase(key.charAt(0)) + key.substring(1));
						env1.putAll(entry.getValue());
		
						for (String line1: lines) {
							log.debug("replace '" + line1 + "'   env = " + env1);
							String newLine = replaceVariables(line1, env1);
							System.err.println("newLine = " + newLine);
							ret.append(newLine);
							if (newlines)
								ret.append("\n");
						}
					}
				}
			}
			line = replaceVariables(line, env);
			System.err.println("LINE: " + line);
			if (line != null && !line.startsWith("#"))
				ret.append(line);
				if (newlines)
					ret.append("\n");
		}
		return ret.toString();
	}
	
	public static String readFileAsString(File filePath) throws java.io.IOException {
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}
}
