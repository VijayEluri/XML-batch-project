package edu.bxml.generator;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import junit.framework.TestCase;
import net.sf.jsqlparser.schema.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.bxml.aj.format.Query;
import edu.bxml.format.Connection;
import edu.bxml.format.Properties;
import edu.bxml.format.Sql;

public class EvaluationQueryTest extends TestCase {
	protected final Log log = LogFactory.getLog(getClass());
	
	public EvaluationQueryTest() {
		
	}
	
	public void testLines() throws Exception {

		java.util.Properties p = new java.util.Properties();
		
		Query query = new Query();
		ConcurrentHashMap s = new ConcurrentHashMap();
		query.setSymbolTable(s);
		
		Properties properties = new Properties();
		properties.setSymbolTable(s);
		properties.setParent(query);

		properties.setFile("../forms/forms.properties");
		properties.check(); //load file
		
		p.load(new FileInputStream("../forms/forms.properties"));
		
		query.addProperties(properties);
		
		Connection connection = new Connection();
		connection.setParent(query);
		
		connection.setName("Campus6");
		//connection.setJndi(p.getProperty("jndi"));
		connection.setLogin(p.getProperty("login"));
		connection.setPassword(p.getProperty("password"));
		connection.setClass("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		connection.setUrl(p.getProperty("url"));
		
		query.addConnection(connection);

		Sql sql = new Sql();
		sql.setParent(connection);
		
		sql.setName("evaluationQuery");
		sql.setFromTextContent("" +
				"select evaluation.id, evaluationtemplate.description, lastName, firstName, evaluation.createDate\n" + 
				"from evaluation\n" + 
				"        join evaluationtemplate on evaluationtemplate.id = evaluation.template\n" + 
				"        join person on person.id = evaluation.evaluatee\n" + 
				"where (1=1)\n" + 
				"	and evaluator = 1\n" + 
				"	and lastName like 'ritchey'\n" + 
				"	and firstName like 'geoff'\n" + 
				"	and evaluationtemplate.description like '%'\n" + 
				"	and evaluation.active = 1\n" + 
				"	and evaluationtemplate.active = 1\n" + 
				"	and evaluation.createDate >= '2001-01-01'\n" + 
				"order by evaluationtemplate.description");
		
		connection.addSql(sql);
		connection.execute();
		
		Generator generator = new Generator();
		generator.setParent(query);
		
		generator.setName("evaluation");
		generator.setBasename("evaluation");
		generator.setGeneratedDir("src/main");
		generator.setCompilerVersion("1.5");
		generator.setTemplateDir("template");
		generator.setMatch("(.*\\.java)|(.*\\.xml)");
		generator.setRemove(false);
		
		SqlAnalyzer analyzer = new SqlAnalyzer();
		analyzer.setSymbolTable(s);
		analyzer.setParent(generator);
		
		analyzer.setName("analyze");
		analyzer.setQueryName("evaluationQuery");
		analyzer.setWorkingPerson("evaluator");
		generator.addSqlAnalyzer(analyzer);
		
		Constants constants = new Constants();
		constants.setParent(analyzer);
		constants.setSymbolTable(s);
		analyzer.addConstants(constants);
		
		Item item = new Item();
		item.setParent(constants);
		item.setSymbolTable(s);
		item.setFromTextContent("evaluationActive");
		constants.addItemEnd(item);
		
		Item item2 = new Item();
		item2.setParent(constants);
		item2.setSymbolTable(s);
		item2.setFromTextContent("evaluationtemplateActive");
		constants.addItemEnd(item2);
		
		Filter filter = new Filter();
		filter.setParent(analyzer);
		filter.setSymbolTable(s);
		analyzer.addFilter(filter);
		filter.setName("filter");
		filter.setSearchText("nameSearch");
		
		Interface inter = new Interface();
		inter.setParent(filter);
		inter.setSymbolTable(s);
		filter.addInterface(inter);
		
		inter.setName("nameSearch");
		inter.setLabel("Name");
		inter.setOperator("like");
		inter.setWidth("131px");
		inter.setSeparator(",");
		
		Field field = new Field();
		field.setParent(inter);
		field.setSymbolTable(s);
		field.setFieldName("lastName");
		inter.addField(field);
		
		Field field2 = new Field();
		field2.setParent(inter);
		field2.setSymbolTable(s);
		field2.setFieldName("firstName");
		inter.addField(field2);

		Interface inter2 = new Interface();
		inter2.setParent(filter);
		inter2.setSymbolTable(s);
		filter.addInterface(inter2);
		inter2.setName("descriptionSearch");
		inter2.setLabel("Evaluation");
		inter2.setOperator("like");
		inter2.setWidth("103px");
	
		Field field3 = new Field();
		field3.setParent(inter2);
		field3.setSymbolTable(s);
		field3.setFieldName("evaluationtemplateDescription");
		inter2.addField(field3);
		
		
		Interface inter3 = new Interface();
		inter3.setParent(filter);
		inter3.setSymbolTable(s);
		filter.addInterface(inter3);
		inter3.setName("dateSearch");
		inter3.setLabel("After Date");
		inter3.setOperator(">=");
		inter3.setWidth("113px");
	
		Field field4 = new Field();
		field4.setParent(inter3);
		field4.setSymbolTable(s);
		field4.setFieldName("evaluationCreateDate");
		field4.setAppendWildcard(false);
		inter3.addField(field4);
		
		Select select = new Select();
		select.setParent(generator);
		select.setSymbolTable(s);
		select.setName("select");
		
		Field field5 = new Field();
		field5.setParent(select);
		field5.setFieldName("evaluationId");
		field5.setLabel("Id");
		field5.setKey(true);
		field5.setVisible(false);
		select.addField(field5);
		
		
		Field field6 = new Field();
		field6.setParent(select);
		field6.setFieldName("evaluationtemplateDescription");
		field6.setLabel("Evaluation");
		field6.setSortable(true);
		select.addField(field6);
		
		Field field7 = new Field();
		field7.setParent(select);
		field7.setFieldName("lastName");
		field7.setLabel("Last Name");
		field7.setWidth(20);
		select.addField(field7);
		
		Field field8 = new Field();
		field8.setParent(select);
		field8.setFieldName("firstName");
		field8.setLabel("First Name");
		field8.setWidth(20);
		select.addField(field8);
		
		Field field9 = new Field();
		field9.setParent(select);
		field9.setFieldName("evaluationCreateDate");
		field9.setLabel("Date");
		field9.setWidth(20);
		select.addField(field9);
		
		analyzer.addSelect(select);
	
		generator.execute();
		
		
		log.debug("st = " + query.getSymbolTable());
		log.debug("Alias size = " + analyzer.getAliases().size());
		
		assert( analyzer.getAliases().size() == 0); // no aliases in query
		for (Entry<String, String> alias:analyzer.getAliases().entrySet()) {
			log.debug("ALIAS: " + alias.getKey() + " = " + alias.getValue());
		}
		
		for (Entry<String, Map<String, String>> whereEnvs: analyzer.getWhereVariables().entrySet()) {
			log.debug("WHERE: " + whereEnvs.getKey() + " = " + whereEnvs.getValue());
		}
 
		assert(analyzer.getWhereVariables().get("evaluator") != null);
		assert(analyzer.getWhereVariables().get("lastName") != null);
		assert(analyzer.getWhereVariables().get("firstName") != null);
		assert(analyzer.getWhereVariables().get("evaluationtemplateDescription") != null);
		assert(analyzer.getWhereVariables().get("evaluationActive") != null);
		assert(analyzer.getWhereVariables().get("evaluationtemplateActive") != null);
		assert(analyzer.getWhereVariables().get("evaluationCreateDate") != null);
		
		//"from evaluationbody \n" + 
		//"	left join evaluationdetails on evaluationdetails.evaluationbody = evaluationbody.id\n" + 
		//"	left join evaluation on evaluation.id = evaluationdetails.evaluation\n" + 
		assert(analyzer.getTables().size() == 3);
		for (Table table: analyzer.getTables()) {
			log.debug("TABLE: " + table.getName());
		}
		
		//"select evaluation.id, evaluationtemplate.description, lastName, firstName, evaluation.createDate\n" + 
		assert(analyzer.getSelectvariables().get().get("evaluationId") != null);
		assert(analyzer.getSelectvariables().get().get("evaluationtemplateDescription") != null);
		assert(analyzer.getSelectvariables().get().get("lastName") != null);
		assert(analyzer.getSelectvariables().get().get("firstName") != null);
		assert(analyzer.getSelectvariables().get().get("evaluationCreateDate") != null);
		
		assert(analyzer.getSelectvariables().get().get("evaluationId").get("visible") != null);
		log.debug("evaluationId.visible = " + analyzer.getSelectvariables().get().get("evaluationId").get("visible"));
		assert(analyzer.getSelectvariables().get().get("evaluationtemplateDescription").get("visible") != null);
		assert(analyzer.getSelectvariables().get().get("lastName").get("visible") != null);
		assert(analyzer.getSelectvariables().get().get("firstName").get("visible") != null);
		assert(analyzer.getSelectvariables().get().get("evaluationCreateDate").get("visible") != null);
		
		for(Entry<String, Map<String, String>> sel : analyzer.getSelectvariables().get().entrySet()) {
			log.debug("SELECT: " + sel.getKey() + " = " + sel.getValue());
		}
		
		Map<String, String> evalBodyEnv = analyzer.getWhereVariables().get("evaluationbodyEvaluation");
		log.debug("evalBodyEnv = " + evalBodyEnv);
		
//		#for ${select}
//		#if (!${visible}) continue
		
		List<String> lines = new ArrayList<String>();
		Map<String, Object> env = generator.getEnv();
		assert(env.get("guiName") == null);
		log.debug("env = " + env);
		
		lines.add("#for ${select}");
		lines.add("#if (!${visible}) continue");
		lines.add("//key = ${key}");
		lines.add("#end for");
		String ret = TemplateParser.processLines(lines, env, "template");
		log.debug("ret = '" + ret + "'");
		assert(ret.equals(
				"//key = evaluationtemplateDescription\n" + 
				"//key = lastName\n" + 
				"//key = firstName\n" + 
				"//key = evaluationCreateDate\n" + 
				""));
		
		assert(env.get("guiName") == null);
		
		List<String> lines2 = new ArrayList<String>();
		lines2.add("#for ${filter}");
		lines2.add("#if ((typeof(guiName) === 'undefined')  || (guiName == null) || guiName.equals(\"\")) continue;");
		lines2.add("// '${guiName}'");
		lines2.add("#end for");
		lines2.add("");
		String ret2 = TemplateParser.processLines(lines2, env, "template");
		log.debug("ret2 = '" + ret2 + "'");
		assert(env.get("guiName") == null);
		
		List<String> lines1 = new ArrayList<String>();
		lines1.add("#for ${whereVariables}");
		lines1.add("//where = ${key}");
		lines1.add("#if (typeof(filterGuiName) === 'undefined') continue;");
		lines1.add("	@Override");
		lines1.add("	public ${javaType} get${Key}() {");
		lines1.add("		${javaType} ret = ${filterGuiName}.getValue();");
		lines1.add("#if (\"${separator}\".equals(\"\")) ");
		lines1.add("		return ret;");
		lines1.add("#else");
		lines1.add("		String[] rets = ret.split(\"${separator}\");");
		lines1.add("		if (rets.length > ${index}) {");
		lines1.add("			 return rets[${index}] + \"%\";");
		lines1.add("		}");
		lines1.add("		return \"%\";");
		lines1.add("#end if");
		lines1.add("	}");
		lines1.add("#end for");

		String ret1 = TemplateParser.processLines(lines1, env, "template");
		log.debug("ret1 = '" + ret1 + "'");
		
		assert(env.get("guiName") == null);
		
		
		
		
	}
}
