package edu.bxml.generator;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
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
		
		log.debug("generator where variables = " + analyzer.getWhereVariables());
		
		env.put("whereVariables", analyzer.getWhereVariables());
		env.put("fromClause", analyzer.getFromclause().get());
		env.put("orderByClause", analyzer.getOrderbyclause().get());
		env.put("selectClause", analyzer.getSelectclause().get());
		
		Find find = new Find() {
			@Override
			void process(File file, Map env) {
				log.debug("found " + file);
				try {
					if (file.getCanonicalPath().endsWith("xml")) {
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
		if (exists) {
			try {
				log.debug("destination PATH = " + fileName.getCanonicalPath() + "   exists = " + exists);
				editExistingFile(fileName, file, env);
				return;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
		Map<String, Object> x = getDocument(existingFile, null);
		Document existingDocument = (Document) x.get("document");
		CompilationUnit existingCU = (CompilationUnit) x.get("cu");
		AST existingAst = existingCU.getAST();
		ASTRewrite rewriter = ASTRewrite.create(existingAst);
		TypeDeclaration existingTypes = (TypeDeclaration) existingCU.types().get(0);
		
		findGenerated(existingCU, existingAst, rewriter, /*remove*/true, /*copy*/false);
		
		x = getDocument(templateFile, env);
		Document templateDocument = (Document) x.get("document");
		CompilationUnit templateCU = (CompilationUnit) x.get("cu");
		AST templateAst = templateCU.getAST();
		ASTRewrite templateRewriter = ASTRewrite.create(templateAst);
		
		Stack stack = findGenerated(templateCU, templateAst, templateRewriter, /*remove*/false, /*copy*/true);
		
		
		ListRewrite lrw = rewriter.getListRewrite(existingTypes, TypeDeclaration.BODY_DECLARATIONS_PROPERTY);
		 while (stack.size() > 0) {
			 lrw.insertLast((ASTNode) stack.pop(), null);
		 }
		
		 TextEdit edits = rewriter.rewriteAST(existingDocument, null);
		 System.err.println("edits = " + edits);
			UndoEdit undo = null;
			 try {
			     undo = edits.apply(existingDocument);
			 } catch(MalformedTreeException e) {
			     e.printStackTrace();
			 } catch(BadLocationException e) {
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
									 log.debug("remove " + meth);
									 stack.push(dup);
								 }
								 if (remove) {
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
		CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("document", document);
		ret.put("cu", compilationUnit);
		return ret;
	}
}
