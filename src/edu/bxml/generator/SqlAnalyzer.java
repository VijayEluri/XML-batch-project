package edu.bxml.generator;

import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.browsexml.core.XMLBuildException;
import com.javalobby.tnt.annotation.attribute;

import edu.bxml.aj.format.Query;
import edu.bxml.format.Sql;
import edu.bxml.io.FilterAJ;
import edu.bxml.io.FilterAJImpl;

/**
 * Specify the query that needs formatting
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class SqlAnalyzer extends FilterAJImpl implements FilterAJ {
	private static Log log = LogFactory.getLog(SqlAnalyzer.class);

	String queryName;
	static String workingPerson;

	public String getWorkingPerson() {
		return workingPerson;
	}

	public void setWorkingPerson(String workingPerson) {
		this.workingPerson = workingPerson;
	}

	public String getQueryName() {
		return queryName;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}

	static public final ThreadLocal<Map<String, String>> aliases = new ThreadLocal();
	static public final ThreadLocal<List<Table>> tables = new ThreadLocal();
	static public final ThreadLocal<Map<String, Map>> mapTables = new ThreadLocal();
	static public final ThreadLocal<Boolean> variable = new ThreadLocal();
	static public final ThreadLocal<LinkedHashMap<String, Map<String, String>>> whereVariables = new ThreadLocal();
	static public final ThreadLocal<LinkedHashMap<String, Map<String, String>>> selectVariables = new ThreadLocal();
	static public final ThreadLocal<String> fromClause = new ThreadLocal();
	static public final ThreadLocal<String> selectClause = new ThreadLocal();
	static public final ThreadLocal<String> orderByClause = new ThreadLocal();

	public static ThreadLocal<String> getFromclause() {
		return fromClause;
	}

	public Map<String, String> getAliases() {
		return aliases.get();
	}

	public void setAliases(Map<String, String> aliases) {
		this.aliases.set(aliases);
	}

	public List<Table> getTables() {
		return tables.get();
	}

	public void setTables(List<Table> tables) {
		this.tables.set(tables);
	}

	static public Map<String, Map> getMapTables() {
		return mapTables.get();
	}

	public void setMapTables(Map<String, Map> mapTabs) {
		this.mapTables.set(mapTabs);
	}

	public static boolean isVariable() {
		if (variable.get() == null)
			return false;
		return variable.get();
	}

	public static void setVariable(boolean var) {
		variable.set(var);
	}

	public Map<String, Map<String, String>> getWhereVariables() {
		return whereVariables.get();
	}

	public void setWhereVariables(
			LinkedHashMap<String, Map<String, String>> whereVariables) {
		this.whereVariables.set(whereVariables);
	}

	@Override
	public void execute() throws XMLBuildException {
		log.debug("analyizer...");
		variable.set(false);
		mapTables.set(new HashMap<String, Map>());
		tables.set(new ArrayList<Table>());
		aliases.set(new HashMap<String, String>());
		whereVariables.set(new LinkedHashMap<String, Map<String, String>>());
		CCJSqlParserManager parserManager = new CCJSqlParserManager();
		Generator generator = getAncestorOfType(Generator.class);
		Query query = getAncestorOfType(Query.class);
		log.debug("query = " + query);
		log.debug("queryName = " + queryName);
		Sql sql = query.getSql(queryName);
		PlainSelect select = parse(parserManager, sql);
		log.debug("analyzer where variables = " + whereVariables.get());

		selectClause.set(getSelectItems(select.getSelectItems()));

		orderByClause.set(getOrderBy(select.getOrderByElements()));
	}

	public static ThreadLocal<String> getSelectclause() {
		return selectClause;
	}

	public static ThreadLocal<String> getOrderbyclause() {
		return orderByClause;
	}

	public PlainSelect parse(CCJSqlParserManager parserManager, Sql sql) {
		Select select = null;

		try {
			select = (Select) parserManager.parse(new StringReader(sql
					.getQuery()));
		} catch (JSQLParserException e) {
			e.printStackTrace();
			return null;
		}

		// sql.getRawQuery() to see without variable substitution
		ResultSetMetaData types = getTypes(sql, null);
		log.debug("sql = " + select.toString());
		try {
			for (int i = 1; i < types.getColumnCount() + 1; i++) {
				log.debug("column " + types.getTableName(i) + "."
						+ types.getColumnName(i) + " as "
						+ types.getColumnLabel(i) + "   "
						+ types.getColumnTypeName(i));

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		PlainSelect plain = (PlainSelect) select.getSelectBody();
		// System.out.println("select = " + select);
		// System.out.println("s = " + plain.getSelectItems());
		// log.debug("where is a " +
		// plain.getWhere().getClass().getName());

		printMapper(sql, plain);
		return plain;
	}

	public void printMapper(Sql sql, PlainSelect plain) {
		for (Object s : plain.getSelectItems()) {
			log.debug("select item " + s.getClass().getCanonicalName());
			SelectExpressionItem item = (SelectExpressionItem) s;
			log.debug("expression = "
					+ item.getExpression().getClass().getName());
			Column c = (Column) item.getExpression();
			log.debug("column = " + c.getWholeColumnName() + ":"
					+ c.getColumnName() + "   table = " + c.getTable());
			camelCase(c);

		}
		StringBuffer fromClause = new StringBuffer();
		log.debug("plain from class = "
				+ plain.getFromItem().getClass().getName());
		System.out.println("<sql id=\"PagedListBody\">");

		Table table = (Table) plain.getFromItem();

		List<Table> tables = this.tables.get();
		Map<String, String> aliases = this.aliases.get();

		tables.add(table);
		fromClause.append("from " + table);
		System.out.println("from " + table);

		if (table.getAlias() != null) {
			aliases.put(table.getAlias(), table.getName());
		}
		log.debug("table is " + table.getName() + "   is also "
				+ table.getAlias() + "  whole = " + table.getWholeTableName());
		List<Join> joins = plain.getJoins();
		if (joins != null) {
			for (Join join : joins) {
				Table right = (Table) join.getRightItem();
				tables.add(right);
				if (right.getAlias() != null) {
					aliases.put(right.getAlias(), right.getName());
				}

				log.debug("join table is " + right.getName() + "  is also "
						+ right.getAlias() + "  whole = "
						+ right.getWholeTableName());
			}
			List<Join> joinList = plain.getJoins();
			for (Join join : joinList) {
				fromClause.append(" ").append(join);
				System.out.println(join);
			}
		}
		System.out.println(" where (1=1) ");
		System.out.println("</sql>");

		for (Table t : tables) {
			String query = "select * from " + t.getName() + " where (1=0)";
			log.debug("TABLE TYPES :");
			ResultSetMetaData types = getTypes(sql, query);
			// columns is name, type
			Map<String, String> columns = new HashMap<String, String>();
			try {
				for (int i = 1; i < types.getColumnCount() + 1; i++) {
					log.debug("column " + types.getTableName(i) + "."
							+ types.getColumnName(i) + " as "
							+ types.getColumnLabel(i) + "   "
							+ types.getColumnTypeName(i));
					columns.put(types.getColumnName(i),
							types.getColumnTypeName(i));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			log.debug("maptables put " + t.getName() + "  value = " + columns);
			mapTables.get().put(t.getName(), columns);
		}
		printWhere(plain.getWhere(), null, null);
		this.fromClause.set(fromClause.toString());
	}

	public static String translateJdbcToJava(String jdbc) {
		if (jdbc.equals("INTEGER")) {
			return "Integer";
		}
		if (jdbc.equals("DATETIME")) {
			return "Date";
		}
		if (jdbc.equals("BIT")) {
			return "Boolean";
		}
		if (jdbc.equals("VARCHAR")) {
			return "String";
		}
		return null;
	}

	public static void translateJavaToGui(String javaType, String javaName,
			Map env) {
		translateJavaToGui(javaType, javaName, env, null);
	}
	public static void translateJavaToGui(String javaType, String javaName,
			Map env, String varName) {
		String name = "guiName";
		if (varName != null) {
			name = varName;
		}
		env.put("baseGuiName", uname(javaName));
		if (javaType.equals("String")) {
			env.put("guiType", "TextBox");
			env.put(name, "txt" + uname(javaName));
		}
		if (javaType.equals("Date")) {
			env.put("guiType", "DateBox");
			env.put(name, "dt" + uname(javaName));

		}
		if (javaType.equals("Boolean")) {
			env.put("guiType", "CheckBox");
			env.put(name, "ck" + uname(javaName));
		}
	}

	static Pattern evalPattern = Pattern
			.compile("(\\S+)\\s*(\\S+)\\s*#\\{([^,]*),jdbcType=([^}]*)}");

	public static List<String> eval(String expression) {
		log.debug("Expression = '" + expression + "'");
		if (!expression.contains("#{"))
			return null;
		List<String> list = new ArrayList<String>();

		// 1 = dbName
		// 2 = compare
		// 3 = key
		// 4 = jdbcType
		Matcher matcher = evalPattern.matcher(expression);

		while (matcher.find()) {
			list.add(matcher.group(3));
			Map<String, String> env = whereVariables.get()
					.get(matcher.group(3));
			if (env == null) {
				env = new HashMap<String, String>();
			}
			env.put("jdbcType", matcher.group(4));
			env.put("compare", matcher.group(2));
			env.put("jdbcKey", matcher.group(1));
			String javaType = translateJdbcToJava(matcher.group(4));
			env.put("javaType", javaType);
			String key = matcher.group(3);
			env.put("key", key);
			env.put("Key", Character.toUpperCase(key.charAt(0)) + key.substring(1));
			String interfaceType = "variable";
			if (workingPerson != null && key != null
					&& workingPerson.equals(key)) {
				interfaceType = "person";
			}
			translateJavaToGui(javaType, key, env);

			Set<String> s = null;
			if (constants == null) {
				s = new HashSet();
				log.warn("No constants are defined for sqlAnalyzer.  Constants tell which elements of your where clause should not be set by the interface.");
			}
			else {
				s = constants.getItems();
			}
			log.debug("constants =  " + s);
			log.debug("key = '" + key + "'");
			if (key != null && s.contains(key)) {
				interfaceType = "const";
			}
			env.put("interfaceType", interfaceType);
			whereVariables.get().put(matcher.group(3), env);
			String value = env.get("value");
			if (javaType.equals("Boolean")) {
				try {
					Integer v = Integer.parseInt(value);
					if (v == 0) {
						env.put("value", "false");
					} else {
						env.put("value", "true");
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
			log.debug("WHERE Value for " + matcher.group(3) + " is "
					+ env.get("value"));
			log.debug("WHERE Value for " + matcher.group(3) + " is "
					+ whereVariables.get().get(matcher.group(3)).get("value"));
		}
		System.err.println("list = " + list);
		return list;
	}

	public void printWhere(Expression where, Document doc, Element sql) {
		variable.set(true);
		log.debug("where = " + where);
		log.debug("MMM TOP where is a " + where.getClass().getName());
		if (where instanceof AndExpression) {
			AndExpression and = (AndExpression) where;
			if (and.getLeftExpression() instanceof AndExpression) {
				printWhere(and.getLeftExpression(), doc, sql);
			} else {
				String left = and.getLeftExpression().toString();
				if (left != null) {
					List<String> vars = eval(left);
					if (vars != null) {
						if (sql != null) {
							Element ifElement = doc.createElement("if");
							for (String var : vars) {
								ifElement.setAttribute("test", var + "!=null");
							}
							ifElement.appendChild(doc.createTextNode(" AND "
									+ left));
							sql.appendChild(ifElement);
						}
						System.out.print("<if ");
						for (String var : vars) {
							System.out.print("test=\"" + var + " != null\" ");
						}
						System.out.println(">");
					}
					System.out.println(" AND " + left);
					if (vars != null) {
						System.out.println("</if>");
					}
				}
			}
			if (and.getRightExpression() instanceof AndExpression) {
				printWhere(and.getRightExpression(), doc, sql);
			} else {
				String right = and.getRightExpression().toString();
				if (right != null) {
					List<String> vars = eval(right);
					if (vars != null) {
						if (sql != null) {
							Element ifElement = doc.createElement("if");
							for (String var : vars) {
								ifElement.setAttribute("test", var + "!=null");
							}
							ifElement.appendChild(doc.createTextNode(" AND "
									+ right));
							sql.appendChild(ifElement);
						}
						System.out.print("<if ");
						for (String var : vars) {
							System.out.print("test=\"" + var + " != null\" ");
						}
						System.out.println(">");
					}
					System.out.println(" AND " + right);
					if (vars != null) {
						System.out.println("</if>");
					}
				}
			}
			where = and.getLeftExpression();
		}

	}

	public ResultSetMetaData getTypes(Sql sql, String query) {

		if (query == null)
			query = sql.getQuery();

		edu.bxml.format.Connection connection = sql.getConnection();
		java.sql.Connection c = null;
		try {
			c = connection.getConnection();
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Statement x = null;
		try {
			x = c.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ResultSet rs = null;
		try {
			log.debug("query = " + query);
			rs = x.executeQuery(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ResultSetMetaData md = null;
		try {
			md = rs.getMetaData();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return md;
	}

	/**
	 * Given a variable name from a query, look up the name in the query
	 * metadata and return its database type
	 * 
	 * @param ex
	 * @return
	 */
	static public String getJdbcType(String ex) {
		log.debug("jdbcType for " + ex);
		String ret = null;
		String[] parts = ex.split("\\.");
		if (parts.length == 1) {
			String field = parts[0];
			for (Map<String, String> mapTable : mapTables.get().values()) {
				String type = filterType(mapTable.get(field));
				if (type != null) {
					return type.toUpperCase();
				}
			}
		} else {
			log.debug("talbe name is  " + parts[0]);
			log.debug("mapTables is  " + mapTables);
			Map<String, String> mapTable = mapTables.get().get(parts[0]);
			if (mapTable == null) {
				String name = aliases.get().get(parts[0]);
				if (name != null) {
					mapTable = mapTables.get().get(name);
				}
			}
			if (mapTables != null) {
				log.debug("mapTable is  " + mapTable);
				String type = filterType(mapTable.get(parts[1]));
				if (type != null) {
					return type.toUpperCase();
				}
			}
		}
		return ret;
	}

	/**
	 * Find a literal dot (.) or underscore (_) character followed by any
	 * character and replace both with the second character's upper case value
	 */
	static BxmlPattern camelPattern = new BxmlPattern("[._](.)") {
		@Override
		public String replace(List<String> match, Map env) {
			return match.get(1).toUpperCase();
		}
	};

	/**
	 * Comes when analysing the where clause: called with a name and value from
	 * the sql query
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	public static String camelCase(String toCamel, String value) {

		String name = camelPattern.execute(toCamel, null);
		log.debug("CAMEL name = " + name + "  value = " + value);
		Map<String, String> env = null;
		env = whereVariables.get().get(name);

		if (env == null) {
			env = new HashMap<String, String>();
			whereVariables.get().put(name, env);
		}
		log.debug("name  =" + name + "    env = " + env);
		// Database quotes converted to java quotes for strings
		value = value.replaceAll("'", "\\\\\"");
		env.put("value", value);
		log.debug("camelCase value = " + value);
		String interfaceType = "const";
		log.debug("interfaceType = " + interfaceType);
		env.put("interfaceType", interfaceType);
		log.debug("WHERE Value for " + name + " is "
				+ whereVariables.get().get(name).get("value"));
		return name;
	}

	public static String camelCase(Column column) {

		String name = camelPattern.execute(column.getWholeColumnName(), null);
		log.debug("CAMEL name = " + name);

		LinkedHashMap<String, Map<String, String>> selectList = SqlAnalyzer
				.getSelectvariables().get();
		if (selectList == null) {
			selectList = new LinkedHashMap<String, Map<String, String>>();
			SqlAnalyzer.getSelectvariables().set(selectList);
		}

		Map<String, String> env = selectList.get(name);
		if (env == null) {
			env = new HashMap<String, String>();
			selectList.put(name, env);
		}
		log.debug("name  =" + name + "    env = " + env);
		// Database quotes converted to java quotes for strings
		env.put("jdbcKey", column.getWholeColumnName());
		return name;
	}

	public static ThreadLocal<LinkedHashMap<String, Map<String, String>>> getSelectvariables() {
		return selectVariables;
	}

	public static String filterType(String type) {
		log.debug("filter " + type);

		if (type == null)
			return null;
		type = type.toUpperCase();
		if (type.equals("INT")) {
			log.debug("filter returns INTEGER");
			return "INTEGER";
		}
		return type;
	}

	public String getSelectItems(List<SelectExpressionItem> list) {
		StringBuffer linebuffer = new StringBuffer();
		Iterator iterator = list.iterator();
		while (iterator.hasNext()) {
			linebuffer.append(",").append(iterator.next().toString());
		}
		return "	select " + linebuffer.toString().substring(1);
	}

	public String getOrderBy(List list) {
		if (list == null)
			return null;
		StringBuffer linebuffer = new StringBuffer();
		Iterator iterator = list.iterator();
		while (iterator.hasNext()) {
			linebuffer.append(",").append(iterator.next().toString());
		}
		return "	order by " + linebuffer.toString().substring(1);
	}

	static Constants constants;

	public void addConstants(Constants c) {
		constants = c;
	}

	Concat concat;

	public void addConcat(Concat c) {
		concat = c;
	}

	List<edu.bxml.generator.Select> selects = new ArrayList<edu.bxml.generator.Select>();

	public List<edu.bxml.generator.Select> getSelects() {
		return selects;
	}

	List<edu.bxml.generator.Filter> filters = new ArrayList<edu.bxml.generator.Filter>();

	public List<edu.bxml.generator.Filter> getFilters() {
		return filters;
	}

	public void addFilter(edu.bxml.generator.Filter filter) {
		filters.add(filter);
	}

	public void addSelect(edu.bxml.generator.Select s) {
		selects.add(s);
	}
}
