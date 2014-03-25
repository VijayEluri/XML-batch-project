package edu.bxml.generator;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import junit.framework.TestCase;
import net.sf.jsqlparser.schema.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;

import edu.bxml.aj.format.Query;
import edu.bxml.format.Connection;
import edu.bxml.format.Properties;
import edu.bxml.format.Sql;

public class QueryTest extends TestCase {
	protected final Log log = LogFactory.getLog(getClass());
	
	public QueryTest() {
		
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
		
		sql.setName("evalQuery");
		sql.setFromTextContent("" +
				"select evaluationbody.id, evaluationbody.description, evaluationdetails.id as detailsId, evaluationdetails.score, evaluationdetails.comment, evaluationbody.comment_score_or_header, evaluation.evaluatee, evaluation.createDate\n" + 
				"from evaluationbody \n" + 
				"	left join evaluationdetails on evaluationdetails.evaluationbody = evaluationbody.id\n" + 
				"	left join evaluation on evaluation.id = evaluationdetails.evaluation\n" + 
				"where (1=1)\n" + 
				"	and evaluationbody.evaluation = 1 \n" + 
				"	and evaluation.evaluatee = 7 \n" + 
				"	and evaluation.evaluator = 7\n" + 
				"order by evaluation.id");  //{order by "order"} AND {order by [order]} both cause problems
		
		connection.addSql(sql);
		connection.execute();
		
		Generator generator = new Generator();
		generator.setParent(query);
		
		assertTrue(generator.getAncestorOfType(Query.class) != null);
		log.debug("query1 = " + generator.getAncestorOfType(Query.class));
		
		generator.setName("evalBody");
		generator.setBasename("evaluationBody");
		generator.setGeneratedDir("src/main");
		generator.setCompilerVersion("1.5");
		generator.setTemplateDir("template");
		generator.setMatch("(.*\\.java)|(.*\\.xml)");
		
		SqlAnalyzer a = new SqlAnalyzer();
		generator.addSqlAnalyzer(a);
		a.setQueryName("evalQuery");
		a.setParent(generator);
		boolean ex = false;
		log.debug("query2 = " + generator.getAncestorOfType(Query.class));
		try {
			generator.execute();
		} catch (XMLBuildException e) {
			ex = true;
		}
		assert(ex = true); // must have SqlAnalyzer
		
		SqlAnalyzer analyzer = new SqlAnalyzer();
		analyzer.setSymbolTable(s);
		analyzer.setParent(generator);
		
		analyzer.setName("analyze");
		analyzer.setQueryName("evalQuery");
		analyzer.setWorkingPerson("evaluationEvaluator");
		generator.addSqlAnalyzer(analyzer);
		
		Select select = new Select();
		select.setParent(generator);
		select.setName("select");
		
		Field field = new Field();
		field.setParent(select);
		field.setFieldName("evaluationbodyId");
		field.setLabel("Id");
		field.setKey(true);
		field.setVisible(false);
		select.addField(field);
		
		Field field1 = new Field();
		field1.setParent(select);
		field1.setFieldName("evaluationbodyDescription");
		field1.setLabel("Item");
		field1.setWidth(20);
		select.addField(field1);
		
		Field field2 = new Field();
		field2.setParent(select);
		field2.setFieldName("evaluationdetailsId");
		field2.setLabel("detId");
		field2.setVisible(false);
		select.addField(field2);
		
		Field field3 = new Field();
		field3.setParent(select);
		field3.setFieldName("evaluationdetailsScore");
		field3.setLabel("Score");
		field3.setWidth(10);
		select.addField(field3);
		
		Field field4 = new Field();
		field4.setParent(select);
		field4.setFieldName("evaluationdetailsComment");
		field4.setLabel("Comment");
		field4.setWidth(20);
		select.addField(field4);

		Field field5 = new Field();
		field5.setParent(select);
		field5.setFieldName("evaluationbodyCommentScoreOrHeader");
		field5.setLabel("Type");
		field5.setVisible(false);
		select.addField(field5);
		
		Field field6 = new Field();
		field6.setParent(select);
		field6.setFieldName("evaluationEvaluatee");
		field6.setLabel("Evaluated");
		field6.setVisible(false);
		select.addField(field6);
		
		Field field7 = new Field();
		field7.setParent(select);
		field7.setFieldName("evaluationCreateDate");
		field7.setLabel("Date");
		field7.setWidth(10);
		select.addField(field7);
		
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
		//"where (1=1)\n" + 
		//"	and evaluationbody.evaluation = 1 \n" + 
		//"	and evaluation.evaluatee = 7 \n" + 
		//"	and evaluation.evaluator = 7\n" + 
		assert(analyzer.getWhereVariables().get("evaluationbodyEvaluation") != null);
		assert(analyzer.getWhereVariables().get("evaluationEvaluatee") != null);
		assert(analyzer.getWhereVariables().get("evaluationEvaluator") != null);
		
		//"from evaluationbody \n" + 
		//"	left join evaluationdetails on evaluationdetails.evaluationbody = evaluationbody.id\n" + 
		//"	left join evaluation on evaluation.id = evaluationdetails.evaluation\n" + 
		assert(analyzer.getTables().size() == 3);
		for (Table table: analyzer.getTables()) {
			log.debug("TABLE: " + table.getName());
		}
		
		assert(analyzer.getSelectvariables().get().get("evaluationbodyId") != null);
		assert(analyzer.getSelectvariables().get().get("evaluationbodyDescription") != null);
		assert(analyzer.getSelectvariables().get().get("evaluationdetailsId") != null);
		assert(analyzer.getSelectvariables().get().get("evaluationdetailsScore") != null);
		assert(analyzer.getSelectvariables().get().get("evaluationdetailsComment") != null);
		assert(analyzer.getSelectvariables().get().get("evaluationbodyCommentScoreOrHeader") != null);
		assert(analyzer.getSelectvariables().get().get("evaluationEvaluatee") != null);
		assert(analyzer.getSelectvariables().get().get("evaluationCreateDate") != null);
		for(Entry<String, Map<String, String>> sel : analyzer.getSelectvariables().get().entrySet()) {
			log.debug("SELECT: " + sel.getKey() + " = " + sel.getValue());
		}
		
		Map<String, String> evalBodyEnv = analyzer.getWhereVariables().get("evaluationbodyEvaluation");
		log.debug("evalBodyEnv = " + evalBodyEnv);
		
		assert("EvaluationbodyEvaluation".equals(evalBodyEnv.get("Key")));
		assert("variable".equals(evalBodyEnv.get("interfaceType")));
		assert("1".equals(evalBodyEnv.get("value")));
		assert("evaluationbodyEvaluation".equals(evalBodyEnv.get("key")));
		assert("EvaluationbodyEvaluation".equals(evalBodyEnv.get("baseGuiName")));
		assert("Integer".equals(evalBodyEnv.get("javaType")));
		assert("INTEGER".equals(evalBodyEnv.get("jdbcType")));
		assert("=".equals(evalBodyEnv.get("compare")));
		assert("evaluationbody.evaluation".equals(evalBodyEnv.get("jdbcKey")));
		
		Map<String, Object> env = generator.getEnv();
		log.debug("env = " + env);
		//env = {basename=evaluationBody, propername=EvaluationBody, workingPerson=null}
		assert("evaluationBody".equals(env.get("basename")));
		assert("EvaluationBody".equals(env.get("propername")));
		assert("evaluationEvaluator".equals(env.get("workingPerson")));
		
		
		assert(env.get("filter") == null);
		assert(env.get("whereVariables") != null);
		
		// Make sure 'select' variables are in environment
		assert(env.get(select.getName()) != null);
		LinkedHashMap<String, Map<String, String>> selectList = (LinkedHashMap<String, Map<String, String>>) env.get(select.getName());
		for (Entry<String, Map<String, String>> selectItem: selectList.entrySet()) {
			Map<String, String> item = selectItem.getValue();
			log.debug("item = " + item);
			assert(null != item.get("visible"));		
			assert(null != item.get("sortable"));		
			assert(null != item.get("label"));		
		}
	}
}
