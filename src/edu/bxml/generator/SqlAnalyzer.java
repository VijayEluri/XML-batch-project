package edu.bxml.generator;

import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
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
/**
 * Specify the query that needs formatting
 * @author ritcheyg
 *
 */
@attribute(value = "", required = true)
public class SqlAnalyzer extends FilterAJ {
	private static Log log = LogFactory.getLog(SqlAnalyzer.class);
	
	String queryName;

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
	static public final ThreadLocal<Map<String, Map<String, String>>> whereVariables = new ThreadLocal();
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
		return variable.get();
	}

	public static void setVariable(boolean var) {
		variable.set(var);
	}

	public Map<String, Map<String, String>> getWhereVariables() {
		return whereVariables.get();
	}

	public void setWhereVariables(Map<String, Map<String, String>> whereVariables) {
		this.whereVariables.set(whereVariables);
	}

	@Override
	public void execute() throws XMLBuildException  {
		log.debug("analyizer...");
		variable.set(false);
		mapTables.set(new HashMap<String, Map>());
		tables.set(new ArrayList<Table>());
		aliases.set( new HashMap<String, String>());
		whereVariables.set(new HashMap<String, Map<String, String>>());
		CCJSqlParserManager parserManager = new CCJSqlParserManager();
		Generator generator = getAncestorOfType(Generator.class);
		Query query = getAncestorOfType(Query.class);
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
			select = (Select) parserManager.parse(new StringReader(sql.getQuery()));
		} catch (JSQLParserException e) {
			e.printStackTrace();
			return null;
		}
		
		//sql.getRawQuery() to see without variable substitution
		ResultSetMetaData types = getTypes(sql, null);
		log.debug("sql = " + select.toString());
		try {
			for (int i = 1; i < types.getColumnCount()+1; i++) {
				log.debug("column " + types.getTableName(i) + "." + types.getColumnName(i) + " as " + types.getColumnLabel(i) + "   " + types.getColumnTypeName(i));
				
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
		StringBuffer fromClause = new StringBuffer();
		log.debug("plain from class = " + plain.getFromItem().getClass().getName());
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
		log.debug("table is " + table.getName() + "   is also " + table.getAlias() + "  whole = " + table.getWholeTableName());
		List<Join> joins = plain.getJoins();
		for (Join join: joins) {
			Table right = (Table) join.getRightItem();
			tables.add(right);
			if (right.getAlias() != null) {
				aliases.put(right.getAlias(), right.getName());
			}
			
			log.debug("join table is " + right.getName() + "  is also " + right.getAlias() + "  whole = " + right.getWholeTableName());
		}
			List<Join> joinList = plain.getJoins();
			for (Join join: joinList) {
				fromClause.append(" ").append(join);
				System.out.println(join);
			}
		 System.out.println(" where (1=1) ");
		 System.out.println("</sql>");
		 
		for (Table t:tables) {
			String query = "select * from " + t.getName() + " where (1=0)";
			log.debug("TABLE TYPES :");
			ResultSetMetaData types = getTypes(sql, query);
			// columns is name, type
			Map<String, String> columns = new HashMap<String, String>();
			try {
				for (int i = 1; i < types.getColumnCount()+1; i++) {
					log.debug("column " + types.getTableName(i) + "." + types.getColumnName(i) + " as " + types.getColumnLabel(i) + "   " + types.getColumnTypeName(i));
					columns.put(types.getColumnName(i), types.getColumnTypeName(i));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
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
	
	static Pattern evalPattern = Pattern.compile("(\\S+)\\s*(\\S+)\\s*#\\{([^,]*),jdbcType=([^}]*)}");
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
        	Map<String, String> env = new HashMap<String, String>();
        	env.put("jdbcType", matcher.group(4));
        	env.put("compare", matcher.group(2));
        	env.put("jdbcKey", matcher.group(1));
        	env.put("value", translateJdbcToJava(matcher.group(4)));
        	env.put("key", matcher.group(3));
        	whereVariables.get().put(matcher.group(3), env);
        }
        System.err.println("list = " + list);
		return list;
	}
	
	public void printWhere(Expression where, Document doc, Element sql) {
		variable.set(true);
		System.err.println("MMM TOP where is a " + where.getClass().getName());
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
							for (String var: vars) {
								ifElement.setAttribute("test", var + "!=null");
							}
							ifElement.appendChild(doc.createTextNode(" AND " + left));
							sql.appendChild(ifElement);
						}
						System.out.print("<if ");
						for (String var: vars) {
							System.out.print("test=\"" + var + " != null\" ");
						}
						System.out.println(">");
					}
					System.out.println(" AND " + left );
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
							for (String var: vars) {
								ifElement.setAttribute("test", var + "!=null");
							}
							ifElement.appendChild(doc.createTextNode(" AND " + right));
							sql.appendChild(ifElement);
						}
						System.out.print("<if ");
						for (String var: vars) {
							System.out.print("test=\"" + var + " != null\" ");
						}
						System.out.println(">");
					}
					System.out.println(" AND " + right );
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
	
	static public String getJdbcType(String ex) {
		String ret = null;
		String[] parts = ex.split("\\.");
		if (parts.length == 1) {
			String field = parts[0];
			for (Map<String, String> mapTable: mapTables.get().values()) {
				String type = filterType(mapTable.get(field));
				if (type != null) {
					return type.toUpperCase();
				}
			}
		}
		else {
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
	 * Find a literal dot (.) or underscore (_) character followed by any character and replace both with 
	 * the second character's upper case value
	 */
	static BxmlPattern camelPattern = new BxmlPattern("[._](.)") {
		@Override
		public String replace(List<String> match, Map env) {
			return match.get(1).toUpperCase();
		}
	};
	
	public static String camelCase(String name) {
		return camelPattern.execute(name, null);
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
}
