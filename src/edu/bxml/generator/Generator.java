package edu.bxml.generator;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.UndoEdit;

import com.browsexml.core.XMLBuildException;
import com.javalobby.tnt.annotation.attribute;

import edu.bxml.io.FilterAJ;
/**
 * Specify the query that needs formatting
 * @author ritcheyg
 *
 */
@attribute(value = "", required = true)
public class Generator extends FilterAJ {
	private static Log log = LogFactory.getLog(Generator.class);

	SqlAnalyzer analyzer;
	String generatedDir = "generated";
	String templateDir = "template";
	String match = ".*.java";
	String basename;
	XmlEditor xmlEditor;
	String compilerVersion = "1.5";
	String workingPerson;
	
	public String getWorkingPerson() {
		return workingPerson;
	}

	public void setWorkingPerson(String workingPerson) {
		this.workingPerson = workingPerson;
	}

	public String getCompilerVersion() {
		return compilerVersion;
	}

	public void setCompilerVersion(String compilerVersion) {
		this.compilerVersion = compilerVersion;
	}

	public String getGeneratedDir() {
		return generatedDir;
	}

	public void setGeneratedDir(String generatedDir) {
		this.generatedDir = generatedDir;
	}

	public String getTemplateDir() {
		return templateDir;
	}

	public void setTemplateDir(String templateDir) {
		this.templateDir = templateDir;
	}

	public String getMatch() {
		return match;
	}

	public void setMatch(String match) {
		this.match = match;
	}

	public SqlAnalyzer getAnalyzer() {
		return analyzer;
	}

	public void setAnalyzer(SqlAnalyzer analyzer) {
		this.analyzer = analyzer;
	}
		
	@Override
	public void execute() throws XMLBuildException  {
		log.debug("generator...");
		analyzer.execute();
		
		Map<String, Object> env = new HashMap<String, Object>();

		String propername = basename.substring(0,1).toUpperCase() + basename.substring(1);
		//String packageDir = packageName.replaceAll("\\.", "/");
		
		env.put("basename", basename);
		env.put("propername", propername);
		env.put("workingPerson", workingPerson);
		Filter firstFilter = this.analyzer.filters.get(0);
		env.put("searchText", firstFilter.getSearchText());
		
		SqlAnalyzer.translateJavaToGui(firstFilter.getSearchTextType(), firstFilter.getSearchText(), env, "guiSearchText");
		
		log.debug("search text = " + env.get("searchText"));	
		
		log.debug("generator where variables = " + analyzer.getWhereVariables());
		
		Map<String, Map<String, String>> where = analyzer.getWhereVariables();
		env.put("whereVariables", analyzer.getWhereVariables());
		env.put("fromClause", analyzer.getFromclause().get());
		env.put("orderByClause", analyzer.getOrderbyclause().get());
		env.put("selectClause", analyzer.getSelectclause().get());
		for (Filter filter: analyzer.getFilters()) {
			LinkedHashMap<String, Map<String, String>> selectList = SqlAnalyzer.getSelectvariables().get();
			LinkedHashMap<String, Map<String, String>> filterList = new LinkedHashMap<String, Map<String, String>>();
			for (Interface i:filter.getInterfaces()) {
				Map<String, String> fieldMap = selectList.get(i.getName());
				if (fieldMap == null) {
					fieldMap = new HashMap<String, String>();
				}
				Field f = i.getFields().get(0);
				Map<String, String> subMap = selectList.get(f.getFieldName());
				String jdbcType = SqlAnalyzer.getJdbcType(subMap.get("jdbcKey"));
				log.debug("Filter name = " + i.getName() + "  jdbcType = " + jdbcType);
				
		    	fieldMap.put("jdbcType", jdbcType);
		    	
		    	
		    	String javaType = SqlAnalyzer.translateJdbcToJava(jdbcType);	
		    	fieldMap.put("javaType", javaType);
		    	
		    	fieldMap.put("width", i.getWidth());
		    	fieldMap.put("label", i.getLabel());
		    	
		    	String javaName = i.getName();
				
		    	SqlAnalyzer.translateJavaToGui(javaType, javaName, fieldMap);
	
				filterList.put(i.getName(), fieldMap);
				log.debug("filter put " + i.getName() + "   fieldMap = " + fieldMap);
				
				log.debug("filter write to whereVariables");
				for (Field ff: i.getFields()) {
					Map<String, String> fieldVars = where.get(ff.getFieldName());
					Map<String, String> filterVars = filter.getFilterName(ff.getFieldName());
					log.debug("fieldVars = " + fieldVars);
					log.debug("filterVars = " + filterVars);
					fieldVars.putAll(filterVars);
					fieldVars.put("filterGuiName", fieldMap.get("guiName"));
				}
			}
			
			env.put(filter.getName(), filterList);
			log.debug("filter size = " + ((LinkedHashMap<String, Map<String, String>>)env.get("filter")).size());
		}
		log.debug("filter size = " + ((LinkedHashMap<String, Map<String, String>>)env.get("filter")).size());
		for (Select select: analyzer.getSelects()) {
			//env.put(select.getName(), select);
			LinkedHashMap<String, Map<String, String>> selectList = SqlAnalyzer.getSelectvariables().get();
			if (selectList == null) {
				selectList = new LinkedHashMap<String, Map<String, String>>();
				SqlAnalyzer.getSelectvariables().set(selectList);
			}
			
			log.debug("SELECT FOR");
			for (Field field: select.getFields()) {
				log.debug("filed info for '" + field.getFieldName() + "'");
				Map<String, String> fieldMap = selectList.get(field.getFieldName());
				if (fieldMap == null) {
					fieldMap = new HashMap<String, String>();
					log.error(field.getFieldName() + " not found in query");
				}
		    	
				String jdbcType = SqlAnalyzer.getJdbcType(fieldMap.get("jdbcKey"));
		    	fieldMap.put("jdbcType", jdbcType);
		    	log.debug("jdbcType for "  + field.getFieldName() + " is " + jdbcType);
		    	String javaType = SqlAnalyzer.translateJdbcToJava(jdbcType);	
		    	fieldMap.put("javaType", javaType);
		    	log.debug("javaType for "  + field.getFieldName() + " is " + javaType);
		    
				
				fieldMap.putAll(field.getFields());
				
				selectList.put(field.getFieldName(), fieldMap);
			}
			env.put(select.getName(), selectList);
		}
		
		Find find = new Find() {
			@Override
			void process(File file, Map env) {
				log.debug("found " + file);
				try {
					if (xmlEditor != null && file.getCanonicalPath().endsWith("xml")) {
						log.debug("set xmlEditor basename = " + basename);
						xmlEditor.setXmlFile(file);
						xmlEditor.setEnv(env);
						xmlEditor.execute();
						return;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				generate(file, env);
			}
		};
		find.find(new File(templateDir), match, env);
	}
	
	public void addSqlAnalyzer(SqlAnalyzer a) {
		this.analyzer = a;
	}
	
	public void addXmlEditor(XmlEditor e) {
		this.xmlEditor = e;
	}

	public void generate(File file, Map<String, Object> env) {
		String path = null;
		String basePath = null;
		try {
			basePath = new File(templateDir).getCanonicalPath();
			path = file.getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String packageFilePath  = path.substring(basePath.length()+1);
		String packagePath = packageFilePath.substring(0, packageFilePath.lastIndexOf('/'));
		File fileName = new File(generatedDir + "/" + packagePath, env.get("propername") + packageFilePath.substring(packageFilePath.lastIndexOf('/') + 1));
		log.debug("PACKAGE PATH = " + packagePath);
		Boolean exists = fileName.exists();
		try {
			log.debug("destination PATH = " + fileName.getCanonicalPath() + "   exists = " + exists);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (exists) {
			editExistingFile(fileName, file, env);
			return;
		}
		
		
		Map<String, Object> ret = getDocument(file, env);
		CompilationUnit compilationUnit = (CompilationUnit) ret.get("cu");
		AST ast = compilationUnit.getAST();
		ASTRewrite rewriter = ASTRewrite.create(ast);
		Document doc = (Document) ret.get("document");
		log.debug("DOC: "+ doc.get());
		if (!exists) {
			new File(generatedDir + "/" + packagePath).mkdirs();
			printFile(fileName, doc);
		}
	}
	
	public void editExistingFile(File existingFile, File templateFile, Map<String, Object> env) {
		log.debug("Edit Existing file = " + existingFile);
		Map<String, Object> exiting = getDocument(existingFile, null);
		Document existingDocument = (Document) exiting.get("document");
		CompilationUnit existingCU = (CompilationUnit) exiting.get("cu");
		AST existingAst = existingCU.getAST();
		ASTRewrite rewriter = ASTRewrite.create(existingAst);
		log.debug("template File = " + templateFile);
		log.debug("existing File = " + existingFile);
		//if (existingCU.types().size() == 0)
			//return;
		TypeDeclaration existingTypes = (TypeDeclaration) existingCU.types().get(0);
		
		findGenerated(existingCU, existingAst, rewriter, /*remove*/true, /*copy*/false);
		
		log.debug("Check Template file = " + templateFile);
		Map<String, Object> temp = getDocument(templateFile, env);
		Document templateDocument = (Document) temp.get("document");
		CompilationUnit templateCU = (CompilationUnit) temp.get("cu");
		AST templateAst = templateCU.getAST();
		if (templateCU.getProblems().length > 0) {
			log.debug(temp.get("source"));

			for (IProblem x : templateCU.getProblems()) {
				log.error("PARSE*** " + templateFile + ":" + x.getSourceLineNumber() + "  " + x.getMessage());
			}
		}
		ASTRewrite templateRewriter = ASTRewrite.create(templateAst);
		
		Stack stack = findGenerated(templateCU, templateAst, templateRewriter, /*remove*/false, /*copy*/true);
		
		
		ListRewrite lrw = rewriter.getListRewrite(existingTypes, TypeDeclaration.BODY_DECLARATIONS_PROPERTY);
		 while (stack.size() > 0) {
			 lrw.insertLast((ASTNode) stack.pop(), null);
		 }
			
		 
		 TextEdit edits = rewriter.rewriteAST(existingDocument, null);
		 log.debug("edits = " + edits);
			UndoEdit undo = null;
			 try {
			     undo = edits.apply(existingDocument);
			 } catch(MalformedTreeException e) {
			     e.printStackTrace();
			 } catch(BadLocationException e) {
			     e.printStackTrace();
			 }
			 
			 
			 Map options = DefaultCodeFormatterConstants.getEclipseDefaultSettings();
				// initialize the compiler settings to be able to format 1.5 code
				options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_5);
				options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_5);
				options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_5);
				
				// change the option to wrap each enum constant on a new line
				options.put(
					DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_ENUM_CONSTANTS,
					DefaultCodeFormatterConstants.createAlignmentValue(
						true,
						DefaultCodeFormatterConstants.WRAP_ONE_PER_LINE,
						DefaultCodeFormatterConstants.INDENT_ON_COLUMN));
				
				// instanciate the default code formatter with the given options
				final CodeFormatter formatter = ToolFactory.createCodeFormatter(options);
			 //
				//CodeFormatter formatter = new DefaultCodeFormatter();
			 
			 //edits = cf.format(CodeFormatter.K_COMPILATION_UNIT, existingDocument.get(), 0,existingDocument.getLength(),0,null);
				final TextEdit edit = formatter.format(
						CodeFormatter.K_COMPILATION_UNIT, // format a compilation unit
						existingDocument.get(), // source to format
						0, // starting position
						existingDocument.get().length(), // length
						0, // initial indentation
						System.getProperty("line.separator") // line separator
					);
			
				try {
					edit.apply(existingDocument);
				} catch (MalformedTreeException e) {
					e.printStackTrace();
				} catch (BadLocationException e) {
					e.printStackTrace();
				}


		printFile(existingFile, existingDocument);
	}
	
	public void printFile(File toFile, Document document)  {
		try {
			PrintStream x = new PrintStream(toFile);
			x.println(document.get());
			x.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getBasename() {
		return basename;
	}

	public void setBasename(String basename) {
		this.basename = basename;
	}

	public Stack findGenerated(CompilationUnit compilationUnit, AST ast, ASTRewrite rewriter, boolean remove, boolean copy) {
		 Stack stack = new Stack();
		TypeDeclaration typeDecl = (TypeDeclaration) compilationUnit.types().get(0);
		
		ListRewrite lrw = rewriter.getListRewrite(typeDecl, TypeDeclaration.BODY_DECLARATIONS_PROPERTY);
		
		
		 List decls = typeDecl.bodyDeclarations();
		 for (Object dec: decls) {
			 log.debug("dec = " + dec);
			 if (dec instanceof MethodDeclaration) {
				 MethodDeclaration meth = (MethodDeclaration) dec;
				 log.debug("meth = " + meth);
				 if (meth.modifiers() != null && meth.modifiers().size() > 0) {
					 for (Object mod: meth.modifiers()) {
						 if (mod instanceof MarkerAnnotation) {
							 MarkerAnnotation marker = (MarkerAnnotation) mod;
							 log.debug("Marker Name = " + marker.getTypeName().getFullyQualifiedName());
							 if (marker.getTypeName().getFullyQualifiedName().equals("Generated")) {
								 if (copy) {
									 MethodDeclaration dup = (MethodDeclaration) ASTNode.copySubtree(ast, meth);
									 log.debug("copy " + meth.toString());
									 log.debug("dup " + dup.toString());
									 stack.push(dup);
								 }
								 if (remove) {
									 log.debug("remove " + meth);
									 lrw.remove(meth, null);
								 }
							 }
						 }
					 }
				 }
			 }
		 }
		 return stack;
	}
	
	/**
	 * Read a java source file or template and return a Document and CompilationUnit
	 * @param file
	 */
	public Map<String, Object> getDocument(File file, Map<String, Object> env) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);

		String sourceString = null;
		try {
			log.debug("source = " + file.getAbsolutePath());
			
			sourceString = TemplateParser.readFileAsString(file, env);
		} catch (IOException e) {
			e.printStackTrace();
		}
				 
		Document document = new Document(sourceString);

		parser.setSource(document.get().toCharArray());

		
		Map<String, String> options = JavaCore.getOptions();
		JavaCore.setComplianceOptions(compilerVersion /*JavaCore.VERSION_1_5*/, options);
		parser.setCompilerOptions(options);
		
		CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);
		
		
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("document", document);
		ret.put("cu", compilationUnit);
		ret.put("source", sourceString);
		return ret;
	}
}
