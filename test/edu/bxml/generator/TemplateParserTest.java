package edu.bxml.generator;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TemplateParserTest extends TestCase {
	protected final Log log = LogFactory.getLog(getClass());
	
	public TemplateParserTest() {
		
	}
	
	public void testLines() throws Exception {
		Map env = new HashMap<String, String>();
		List<String> lines = new ArrayList<String>();
		lines.add("line1;");
		lines.add("line2;");
		StringBuffer ret = new StringBuffer();
		String templatePath = "template";
		TemplateParser.evalProcessorLine(0, env, lines, ret, templatePath);
		String strRet = ret.toString();
		log.debug("ret = '" + strRet + "'");
		assertTrue(strRet.equals("line1;\n"));
	}
	
	public void testIfTrue() throws Exception {
		Map env = new HashMap<String, String>();
		List<String> lines = new ArrayList<String>();
		lines.add("#if (true)");
		lines.add("line2;");
		lines.add("#end if");
		StringBuffer ret = new StringBuffer();
		String templatePath = "template";
		TemplateParser.evalProcessorLine(0, env, lines, ret, templatePath);
		String strRet = ret.toString();
		log.debug("ret = '" + strRet + "'");
		assertTrue(strRet.equals("line2;\n"));
	}
	
	public void testIfFalse() throws Exception {
		Map env = new HashMap<String, String>();
		List<String> lines = new ArrayList<String>();
		lines.add("#if (false)");
		lines.add("line2;");
		lines.add("#end if");
		StringBuffer ret = new StringBuffer();
		String templatePath = "template";
		TemplateParser.evalProcessorLine(0, env, lines, ret, templatePath);
		String strRet = ret.toString();
		log.debug("ret = '" + strRet + "'");
		assertTrue(strRet.equals(""));
	}
	
	public void testIfElse() throws Exception {
		Map env = new HashMap<String, String>();
		List<String> lines = new ArrayList<String>();
		lines.add("#if (false)");
		lines.add("line2;");
		lines.add("#else");
		lines.add("line3;");
		lines.add("#end if");
		StringBuffer ret = new StringBuffer();
		String templatePath = "template";
		TemplateParser.evalProcessorLine(0, env, lines, ret, templatePath);
		String strRet = ret.toString();
		log.debug("ret = '" + strRet + "'");
		assertTrue(strRet.equals("line3;\n"));
	}
	
	public void testIfTwoElse() throws Exception {
		Map env = new HashMap<String, String>();
		List<String> lines = new ArrayList<String>();
		lines.add("#if (false)");
		lines.add("line1;");
		lines.add("line2;");
		lines.add("#else");
		lines.add("line3;");
		lines.add("line4;");
		lines.add("#end if");
		StringBuffer ret = new StringBuffer();
		String templatePath = "template";
		TemplateParser.evalProcessorLine(0, env, lines, ret, templatePath);
		String strRet = ret.toString();
		log.debug("ret = '" + strRet + "'");
		assertTrue(strRet.equals("line3;\nline4;\n"));
	}
	
	public void testIfNested() throws Exception {
		Map env = new HashMap<String, String>();
		List<String> lines = new ArrayList<String>();
		lines.add("#if (false)");
		lines.add("line1;");
		lines.add("line2;");
		lines.add("#else");
		lines.add("#if (false)");
		lines.add("line3;");
		lines.add("#else");
		lines.add("line4;");
		lines.add("#end if");
		lines.add("#end if");
		StringBuffer ret = new StringBuffer();
		String templatePath = "template";
		TemplateParser.evalProcessorLine(0, env, lines, ret, templatePath);
		String strRet = ret.toString();
		log.debug("ret = '" + strRet + "'");
		assertTrue(strRet.equals("line4;\n"));
	}
	
	public void testIfNestedBodies() throws Exception {
		Map env = new HashMap<String, String>();
		env.put("test", "value");
		List<String> lines = new ArrayList<String>();
		lines.add("#if (false)");
		lines.add("line1;");
		lines.add("line2;");
		lines.add("#else");
		lines.add("line2.1;");
		lines.add("#if (false)");
		lines.add("line3;");
		lines.add("#else");
		lines.add("line4;");
		lines.add("#end if 1");
		lines.add("line5 ${test};");
		lines.add("#end if 2");
		StringBuffer ret = new StringBuffer();
		String templatePath = "template";
		TemplateParser.evalProcessorLine(0, env, lines, ret, templatePath);
		String strRet = ret.toString();
		log.debug("ret = '" + strRet + "'");
		assertTrue(strRet.equals("line2.1;\nline4;\nline5 value;\n"));
	}
	
	public void testFor() throws Exception {
		Map env = new HashMap<String, String>();
		List<String> lines = new ArrayList<String>();
		Map<String, Map<String, String>> variable = new LinkedHashMap<String, Map<String, String>>();
		Map<String, String> forVar1 = new HashMap<String, String>();
		forVar1.put("count", "1");
		variable.put("env1", forVar1);
		
		Map<String, String> forVar2 = new HashMap<String, String>();
		forVar2.put("count", "2");
		variable.put("env2", forVar2);
		
		env.put("test", variable);
		lines.add("#for ${test}");
		lines.add("line${count};");
		lines.add("#end for");
		
		StringBuffer ret = new StringBuffer();
		String templatePath = "template";
		TemplateParser.evalProcessorLine(0, env, lines, ret, templatePath);
		String strRet = ret.toString();
		log.debug("ret = '" + strRet + "'");
		assertTrue(strRet.equals("line1;\nline2;\n"));
	}
	
	public void testProcess() throws Exception {
		List<String> lines = new ArrayList<String>();
		Map env = new HashMap<String, String>();
		Map<String, Map<String, String>> variable = new LinkedHashMap<String, Map<String, String>>();
		
		Map<String, String> forVar1 = new HashMap<String, String>();
		forVar1.put("count", "1");
		variable.put("env1", forVar1);
		
		Map<String, String> forVar2 = new HashMap<String, String>();
		forVar2.put("count", "2");
		variable.put("env2", forVar2);
		
		env.put("test", variable);
		
		lines.add("start line 1;");
		lines.add("start line 2;");
		lines.add("#for ${test}");
		lines.add("line${count};");
		lines.add("#end for");
		lines.add("end line 1;");
		lines.add("end line 2;");
		String ret = TemplateParser.processLines(lines, env, "template");
		log.debug("ret = '" + ret + "'");
		assertTrue(ret.equals("start line 1;\nstart line 2;\nline1;\nline2;\nend line 1;\nend line 2;\n"));
	}
	
	public void testContinueUndefined() throws Exception {
		List<String> lines = new ArrayList<String>();
		Map env = new HashMap<String, String>();
		Map<String, Map<String, String>> variable = new LinkedHashMap<String, Map<String, String>>();
		
		Map<String, String> forVar1 = new HashMap<String, String>();
		forVar1.put("count", "1");
		variable.put("env1", forVar1);
		
		Map<String, String> forVar2 = new HashMap<String, String>();
		forVar2.put("count", "2");
		variable.put("env2", forVar2);
		env.put("filter", variable);

		
		lines.add("#for ${filter}");
		lines.add("#if (typeof(guiName) === 'undefined' || (guiName == null) || guiName.equals(\"\")) continue;");
		lines.add("	private ${guiType} ${guiName};");
		lines.add("#end for");
		String ret = TemplateParser.processLines(lines, env, "template");
		log.debug("ret = '" + ret + "'");
		assertTrue(ret.equals(""));
	}
	
	public void testProcessContinue() throws Exception {
		List<String> lines = new ArrayList<String>();
		Map env = new HashMap<String, String>();
		Map<String, Map<String, String>> variable = new LinkedHashMap<String, Map<String, String>>();
		
		Map<String, String> forVar1 = new HashMap<String, String>();
		forVar1.put("count", "1");
		variable.put("env1", forVar1);
		
		Map<String, String> forVar2 = new HashMap<String, String>();
		forVar2.put("count", "2");
		variable.put("env2", forVar2);
		
		env.put("test", variable);
		
		lines.add("start line 1;");
		lines.add("start line 2;");
		lines.add("#for ${test}");
		lines.add("#if (${count}==2) continue");
		lines.add("line${count};");
		lines.add("again${count};");
		lines.add("#end for");
		lines.add("end line 1;");
		lines.add("end line 2;");
		String ret = TemplateParser.processLines(lines, env, "template");
		log.debug("ret = '" + ret + "'");
		assertTrue(ret.equals("start line 1;\nstart line 2;\nline1;\nagain1;\nend line 1;\nend line 2;\n"));
	}
	
	public void testSet() throws Exception {
		List<String> lines = new ArrayList<String>();
		Map env = new HashMap<String, String>();
		Map<String, Map<String, String>> variable = new LinkedHashMap<String, Map<String, String>>();
		
		Map<String, String> forVar1 = new HashMap<String, String>();
		forVar1.put("interfaceType", "person");
		forVar1.put("value", "1");
		forVar1.put("Key", "Person");
		forVar1.put("key", "person");
		forVar1.put("javaType", "Integer");
		variable.put("env1", forVar1);
		
		env.put("whereVariables", variable);
		
		lines.add("#for ${whereVariables}");
		lines.add("#set rhs }");
		lines.add("	var ret = \"XXX\";");
		lines.add("	switch(interfaceType) {");
		lines.add("	case \"person\":	");
		lines.add("		ret = \"clientFactory.getWorkingPersonId()\";");
		lines.add("		break;");
		lines.add("	case \"const\": 	");
		lines.add("		ret = \"${value}\";");
		lines.add("		break; ");
		lines.add("	default: ");
		lines.add("		ret = \"this.display.get${Key}()\";");
		lines.add("	};");
		lines.add("	ret;");
		lines.add("}");
		lines.add("    final ${javaType} ${key}=${rhs};");
		lines.add("#end for");
		String ret = TemplateParser.processLines(lines, env, "template");
		log.debug("ret = '" + ret + "'");
		assertTrue(ret.equals("    final Integer person=clientFactory.getWorkingPersonId();\n"));
	}
	
	public void testIfSet() throws Exception {
		List<String> lines = new ArrayList<String>();
		Map env = new HashMap<String, String>();
		Map<String, Map<String, String>> variable = new LinkedHashMap<String, Map<String, String>>();
		
		Map<String, String> forVar1 = new HashMap<String, String>();
		forVar1.put("interfaceType", "person");
		forVar1.put("value", "1");
		forVar1.put("Key", "Person");
		forVar1.put("key", "person");
		forVar1.put("javaType", "Integer");
		variable.put("env1", forVar1);
		
		env.put("whereVariables", variable);
		
		lines.add("#for ${whereVariables}");
		lines.add("#if (false) continue");
		lines.add("#set rhs }");
		lines.add("	var ret = \"XXX\";");
		lines.add("	switch(interfaceType) {");
		lines.add("	case \"person\":	");
		lines.add("		ret = \"clientFactory.getWorkingPersonId()\";");
		lines.add("		break;");
		lines.add("	case \"const\": 	");
		lines.add("		ret = \"${value}\";");
		lines.add("		break; ");
		lines.add("	default: ");
		lines.add("		ret = \"this.display.get${Key}()\";");
		lines.add("	};");
		lines.add("	ret;");
		lines.add("}");
		lines.add("    final ${javaType} ${key}=${rhs};");
		lines.add("#end for");
		String ret = TemplateParser.processLines(lines, env, "template");
		log.debug("ret = '" + ret + "'");
		assert(ret.equals("    final Integer person=clientFactory.getWorkingPersonId();\n"));
	}
}
