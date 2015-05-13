package edu.bxml.io;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.internal.runtime.PrintStackUtil;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;
import com.browsexml.core.XmlParser;
import com.javalobby.tnt.annotation.attribute;

/**
 * A class must extend filter to participate in a pipeline.  Filter can
 * be used stand alone as a basic copy from System.in to System.out.  All 
 * output of a filter's children will append to the output of the parent
 * filter.
 * @author geoff.ritchey
 *
 */
@attribute(value = "", required = true)
public class FilterAJImpl extends XmlObjectImpl implements FilterAJ, XmlObject, Runnable {
	private static Log log = LogFactory.getLog(FilterAJImpl.class);
	protected InputStream in;
	protected PrintStream out;
	protected String text = null;
	protected boolean lock = false;
	protected Object pojo = null;

	protected boolean isHereFile = false;

	@Override
	public boolean isHereFile() {
		return isHereFile;
	}

	@Override
	public void setHereFile(boolean isHereFile) {
		this.isHereFile = isHereFile;
	}
	
	@Override
	public void setHereFile(String isHereFile) {
		this.isHereFile = Boolean.parseBoolean(isHereFile);
	}

	@Override
	public boolean getLock() {
		return lock;
	}

	@Override
	public void setLock(boolean lock) {
		this.lock = lock;
	}

	protected String file = null;
	protected String dir = null;
	protected String toFile = null;
	public static Log getLog() {
		return log;
	}

	public static void setLog(Log log) {
		FilterAJImpl.log = log;
	}

	protected String toDir = null;
	protected String archive = null;
	protected File currentFile = null;  
	protected File outFile = null;

	protected List<FilterAJ> filters = new ArrayList<FilterAJ>();
	protected List<FilterAJ> filtersMaster = new ArrayList<FilterAJ>();
	
	Boolean closeIn = false;
	Boolean closeOut = false;

	@Override
	public Boolean getCloseIn() {
		return closeIn;
	}

	@Override
	public void setCloseIn(Boolean closeIn) {
		this.closeIn = closeIn;
	}

	@Override
	public Boolean getCloseOut() {
		return closeOut;
	}

	@Override
	public void setCloseOut(Boolean closeOut) {
		this.closeOut = closeOut;
	}

	@Override
	public void closeIn() {
		if (closeIn && in != null)
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	@Override
	public void closeOut() {
		if (closeOut && out != null) {
			out.close();
		}
	}

	@Override
	public InputStream getIn() throws XMLBuildException {
		FilterAJImpl ancestor = this.getAncestorOfType(FilterAJImpl.class);
		log.debug("get IN for " + this.getName());
		closeIn = false;
		if (in != null) { 
			log.debug("in already set ");
			return in;
		}
		
		if (getDir() != null) {
			if (getFile() != null) {
				try {
					closeIn = true;
					in = new FileInputStream(new File(this.getDir(), this.getFile()));
					closeIn =true;
					log.debug("set input to " + getDir() + "/" + getFile());
					log.debug("get IN DONE 1");
					return in;
				} catch (FileNotFoundException e) {
					new XMLBuildException(e.getMessage(), this).printStackTrace();
					return null;
				}
			}
		}
		else if (getDir() == null || getFile() == null){
			//<<HERE file
			if (getText() != null && isHereFile()) {
				log.debug("<<HERE file");
				in = new ByteArrayInputStream(getText().getBytes());
				closeIn =true;
				return in;
			}
		}

		if (ancestor != null) {
			log.debug("ancestor class is " + ancestor.getClass().getName());
			closeIn = false;
			this.in = ancestor.in;
		}
		
		if (in == null) {
			log.debug("System.in");
			closeIn = false;
			in = XmlParser.getOriginalSystemIn();
		}
		log.debug("set input to " + getDir() + "/" + getFile());
		log.debug("get IN DONE 2  input stream is " + in);
		return in;
	}

	@Override
	public PrintStream getOut() {
		log.debug("getOut() to set System.out");
		closeOut = false;
		if (out != null) {
			log.debug("out already set ");
			return out;
		}
		
		FilterAJImpl ancestor = this.getAncestorOfType(FilterAJImpl.class);
		log.debug("get OUT for " + this.getName() + ((this.getPojo()!= null)?("   pojo type is " + this.getPojo().getClass().getName()):""));

		try {
			if (getToDir() != null && getToFile() != null){
				
				outFile = new File(toDir, toFile);
				log.debug("toDir = " + toDir);
				log.debug("toFile = " + toFile);
				log.debug("set output file to " + outFile.getAbsolutePath());
				
				out = new PrintStream(new FileOutputStream(outFile));
// Easy debug put info in output stream
//				out.println("name = " + this.getName() + "   todir = " + toDir +  "   tofile = " + toFile);
//				new Exception("set output file to " + outFile.getAbsolutePath()).printStackTrace(out);
				closeOut = true;
				return out;
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		

		log.debug("out is null");
		if (ancestor != null) {
			log.debug("ancestor class is " + ancestor.getClass().getName());
			closeOut =false;
			this.out = ancestor.out;
		}

		
		if (out == null) {
			closeOut = false;
			out = XmlParser.getOriginalSystemOut();
		}
		return out;
	}

	
	public FilterAJImpl() {
	}

	public FilterAJImpl(InputStream is, PrintStream os) {
		in = is;
		out = os;
	}

	@Override
	public void setInputStream(InputStream in) {
		log.debug(" set in to " + in);
		this.in = in;
	}

	@Override
	public void setOutputStream(PrintStream out) {
		this.out = out;
	}
	
	@Override
	public void finish(OutputStream out) {
		try {
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}

	/**
	 * Standard input will come from the dir/file combination.
	 * 
	 * @param file
	 */
	@Override
	@attribute(value = "", required = false, defaultValue="input will come from another class in the pipeline.")
	public void setFile(String file) {
		this.file = file;
	}
	/**
	 * Standard output will go to the toDir/toFile combination.
	 * 
	 * @param file
	 */
	@Override
	@attribute(value = "", required = false, defaultValue="output will go to the next class in the pipeline or standard output.")
	public void setToDir(String toDir) {
		log.debug("to dir set to " + toDir);
		this.toDir = toDir;
		if (toFile != null) {
			outFile = new File(toDir, toFile);
		}
	}
	
	/**
	 * Standard output will go to the toDir/toFile combination.
	 * 
	 * @param file
	 */
	@Override
	@attribute(value = "", required = false, defaultValue="output will go to the next class in the pipeline or standard output.")
	public void setToFile(String toFile) {
		this.toFile = toFile;
		if (toDir != null) {
			outFile = new File(toDir, toFile);
		}
	}

	@Override
	public File getOutFile() {
		return outFile;
	}

	@Override
	public void setOutFile(File outFile) {
		this.outFile = outFile;
	}

	/**
	 * Move input file to an archive directory.  The archive directory
	 * will be created if it does not exist.
	 * 
	 * @param file
	 */
	@Override
	@attribute(value = "", required = false, defaultValue="archive directory.  The input file, if specified, will be moved to the directory named.")
	public void setArchive(String archive) {
		this.archive = archive;
	}

	/**
	 * Standard input will come from the dir/file combination.
	 * 
	 * @param file
	 */
	@Override
	@attribute(value = "", required = false, defaultValue="input will come from another class in the pipeline.")
	public void setDir(java.lang.String dir) {
		this.dir = dir;
	}

	@Override
	public void run() {
		try {
			log.debug("Starting THREAD: " + getName() + "  " + this.getClass().getName());
			execute();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (XMLBuildException e) {
			e.printStackTrace();
		} 
		finally {
		
			log.debug("Ending THREAD: " + getName() + "  " + this.getClass().getName());

				out.flush();
				if (!(out instanceof PrintStream))
					out.close();
			
			log.debug("Ended THREAD: " + getName() + "  " + this.getClass().getName());
		}
	}

	public void execute() throws XMLBuildException  {
		log.debug("filterAJ execute");
		if (in != null) {
			try {
				 byte[] buf = new byte[4096];
				 log.debug("in != null   dir/file = " + dir + "/" + file);
				 log.debug("filterAJ execute for..." + this.getName());
				 int len = in.read(buf);
				for (; len > 0; len = in.read(buf)) {
					out.write(buf, 0, len);
				} 
				log.debug("filterAJ finish");
				finish(out);
				log.debug("filterAJ finish DONE");

			} 
			catch (IOException e) {
				e.printStackTrace();
				throw new XMLBuildException(e.getMessage(), this);
			}
		}
		filters.clear();
		for (FilterAJ filter : filtersMaster) {
			if (filter.isIff())
				filters.add(filter);
		}
		for (FilterAJ filter : filters) {
			log.debug("Filter = " + filter);
			log.debug("Filter in = " + in);
			log.debug("Filter out = " + out);
			filter.setInputStream(in);
			filter.setOutputStream(out);
			filter.execute();
		}
		log.debug("FilterAJ execute DONE");
	}

	public String getText() {
		return text;
	}

	@Override
	public String getFile() {
		return file;
	}

	@Override
	public String getDir() {
		return dir;
	}

	@Override
	public String getToFile() {
		return toFile;
	}

	@Override
	public String getToDir() {
		return toDir;
	}

	public void setIn(InputStream in) {
		this.in = in;
	}

	public void setOut(PrintStream out) {
		this.out = out;
	}

	public void check() throws XMLBuildException {
	}

	public void setText(String text) {
	}

	public void setFromTextContent(String text) {
		this.text = text;
	}
	
	public InputStream getInFilter(InputStream in) throws IOException {
		return in;
	}
	
	public OutputStream getOutFilter(OutputStream out) throws IOException {
		return out;
	}
	
	public FilterAJImpl(Object pojo) {
		super();
		this.pojo = pojo;
	}

	public File getCurrentFile() {
		return currentFile;
	}

	public void setPojo(Object newInstance) {
		this.pojo = newInstance;
	}
	
	@Override
	public Object getPojo() {
		return pojo;
	}
	
	@Override
	public String getValue() throws XMLBuildException {
		return XmlParser.getValue(this);
	}
	
	@Override
	public void setValue(String value) {
		XmlParser.setValue(this, value);
	}

	@Override
	public void setOutputStream(OutputStream pout) {
		out = new PrintStream(pout);
	}

}
