package edu.bxml.generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TemplateParser {
	private static Log log = LogFactory.getLog(TemplateParser.class);
	
	static final String newline = System.getProperty("line.separator");
	
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
		log.debug("replace value [replaceVariables] = " + line);
		log.debug("replace value [replaceVariables] = " + env.get("value"));
        return variableReplacePattern.execute(line, env);
	}
	
	static public String javascript(String text, Map env) {
		String result = null;
		log.debug("javascript");
	    ScriptEngineManager sem = new ScriptEngineManager();
	    ScriptEngine e = sem.getEngineByName("ECMAScript");
	    e.setBindings(new SimpleBindings(env), ScriptContext.ENGINE_SCOPE );
	    log.debug("env = " + env);
	    try {
	    	log.debug("eval " + text);
	    	result = e.eval(text).toString();
	    } catch (ScriptException ex) {
	      ex.printStackTrace();
	    }
	    return result;
    }
    
	static public int evalProcessorLine(int linenumber, Map env, List<String> lines, StringBuffer ret, String templatePath) {
		String line = lines.get(linenumber);
		log.debug("PROCESSOR  '" + line + "'");
		int forEnd = linenumber;
		if (line.startsWith("#for")) {
			int substring = 0;
			Matcher matcher = variableReplacePattern.getPattern().matcher(line);
			if (matcher.find()) {
				Map<String, Map<String, String>> variable = (LinkedHashMap<String, Map<String, String>>) env.get(matcher.group(1));
				if (variable == null) {
					try {
						throw new java.io.IOException(templatePath + ": " + matcher.group(1) + " is not in environment");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				log.debug("#for " + variable);


				int i = linenumber + 1;
				int forStart = i;
				line = lines.get(i++);
				for (; line != null && !line.startsWith("#end for");line = lines.get(i++)) {
				}
				forEnd = i-1;
				if (line.startsWith("#end for")) {
					String[] values = line.substring(8).trim().split(" +");
					if (values.length == 2 && values[0].equals("substring")) {
						try {
							substring = Integer.parseInt(values[1]);
							log.debug("substring = " + substring);
							log.debug("line = " + line);
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
					}
				}
				log.debug("substring = " + substring);
				
				// Loop through all whereVariables
				StringBuffer ret1 = new StringBuffer();
				for (Map.Entry<String, Map<String, String>>entry: variable.entrySet()) {
					Map<String, Object> env1 = env;
					log.debug("for variable = " + entry.getKey());
					log.debug("EntryHashMapValue =  " + entry.getValue());
					env1.putAll(entry.getValue());
					
					String key = entry.getKey();
					env1.put("key", key);
					env1.put("Key", Character.toUpperCase(key.charAt(0)) + key.substring(1));
					env1.putAll(entry.getValue());
	
					StringBuffer forLine = new StringBuffer("");
					int index = forStart;
					String line1 = null;
					
					for ( ; index < forEnd; index++) {
						line1 = lines.get(index);
						
						if (line1.startsWith("#")) {
							env.remove("_nextFor");
							index = evalProcessorLine(index, env, lines, ret1, templatePath);
							String nextFor = (String) env.get("_nextFor");
							env.remove("_nextFor");
							if (nextFor != null && nextFor.equals("true")) {
								break;
							}
							line1 = lines.get(index);
						}
						
						String newLine = replaceVariables(line1, env1);
						log.debug("newLine = " + newLine);
						ret1.append(newLine);
						Boolean newlines = (Boolean) env.get("newlines");
						if (newlines==null || newlines)
							ret1.append("\n");
					}
					ret1.append(forLine);
				}
				ret.append(ret1.substring(substring));
			}
			linenumber = forEnd;
		}
		else if (line.startsWith("#if") || line.startsWith("**NULL**#if")) {
			int start = 4;
			if (line.startsWith("**NULL**")) {
				start += 8;	
			}
			String javascript = line.substring(start, line.lastIndexOf(')')+1);
			log.debug("#if javascript = " + javascript);
			String result = javascript(replaceVariables(javascript.toString(), env), env);
			log.debug("#if result = " + result);
			String rest = line.substring(line.lastIndexOf(')')+1);
			log.debug("rest = " + rest);
			if (rest.contains("continue")) {
				env.put("_nextFor", result.toString());
				return linenumber+1;
			}

				log.debug("BODY");
				StringBuffer trueBody = new StringBuffer();
				StringBuffer falseBody = new StringBuffer();
				Boolean truePart = true;
				int i = linenumber + 1;
				for (line = lines.get(i++); i < lines.size() && !line.startsWith("#end if");line = lines.get(i++)) {
					log.debug("pre #if body line = " + line);
					if (line.startsWith("**NULL**")) {
						line = line.substring(8);
					}
					if (line.startsWith("#else")) {
						truePart = false;
						continue;
					}
					if (line.startsWith("#end if"))
						break;
					
					if (line.startsWith("#")) {
						StringBuffer ret1 = new StringBuffer();
						i = evalProcessorLine(i-1, env, lines, ret1, templatePath);
						if (truePart) 
							trueBody.append(ret1);
						else
							falseBody.append(ret1);
						if (i < lines.size()) {
							continue;
						}
						break;
					}
					
					log.debug("#if body line = " + line);
					if (truePart)
						trueBody.append(line).append(newline);
					else
						falseBody.append(line).append(newline);
					if (start == 5) {
						for (; i >0; ) {
							lines.set(--i, "**NULL**" + lines.get(i));
						}
					}
				}
				String newline = System.getProperty("line.separator");

				if (Boolean.parseBoolean(result)) {
					ret.append(replaceVariables(trueBody.toString(), env));
				}
				else {
					ret.append(replaceVariables(falseBody.toString(), env));
				}
			
			return i;
		}
		else if (line.startsWith("#nonewline")) {
			log.debug("#nonewline");
			env.put("newlines", false);
			return linenumber + 1;
		}
		else if (line.startsWith("#newline")) {
			log.debug("#newline");
			env.put("newlines", true);
			return linenumber + 1;
		}
		else if (line.startsWith("#set") || line.startsWith("**NULL**#set")) {
			log.debug("#set");
			log.debug("process #set");
			line = replaceVariables(line, env);
			int start = 5;
			if (line.startsWith("**NULL**")) {
				start += 8;	
			}
			log.debug("start = " + start);
			int end = line.substring(start).indexOf(' ');
			log.debug("end = " + end);
			String name = line.substring(start, start+end);
			String endMarker = line.substring(start+end+1);
			log.debug("end Marker = '" + endMarker + "'");
			StringBuffer javascript = new StringBuffer();

			int i = linenumber+1;
			for (line = lines.get(i++); i < lines.size() && !line.startsWith(endMarker);line = lines.get(i++)) {
				if (line.startsWith("**NULL**")) {
					line = line.substring(8);
					if (line.startsWith(endMarker))
						break;
				}
				log.debug("javascript line = " + line);
				javascript.append(line);
			}
			
			String value = javascript(replaceVariables(javascript.toString(), env), env);
			log.debug("rhs name = " + name);
			log.debug("rhs value = " + value);
			env.put(name, value);
			return i;
		}
		else {
			line = replaceVariables(line, env);
			ret.append(line);
			Boolean newlines = (Boolean) env.get("newlines");
			if (newlines==null || newlines)
				ret.append("\n");
		}
		return linenumber + 1;
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

		if (env == null) {
			env = new HashMap<String, Object>();
		}
		
		BufferedReader reader = new BufferedReader(new FileReader(templatePath));
		
		List<String> lines = new ArrayList<String>();
		log.debug("template file is " + templatePath);
		for (String line = reader.readLine(); line != null;line = reader.readLine()) {
			log.debug("raw line = " + line);
			lines.add(line);
		}
		return processLines(lines, env, templatePath.getAbsolutePath());
	}
	
	static public String processLines(List<String> lines, Map<String, Object> env, String templatePath) {
		StringBuffer ret = new StringBuffer();
		for (int i = 0; i < lines.size(); ) {
			String line = lines.get(i);
			if (line.startsWith("#")) {
				StringBuffer ret1 = new StringBuffer();
				i = evalProcessorLine(i, env, lines, ret1, templatePath);
				ret.append(ret1);
			}
			else {
				ret.append(replaceVariables(line, env)).append(newline);
				i++;
			}
			
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
