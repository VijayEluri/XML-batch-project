package edu.bxml.io;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
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
public class FilterAJ extends XmlObject implements Runnable {
	private static Log log = LogFactory.getLog(FilterAJ.class);
	protected InputStream in;
	protected OutputStream out;
	protected String text = null;
	protected boolean lock = false;

	public boolean getLock() {
		return lock;
	}

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
		FilterAJ.log = log;
	}

	protected String toDir = null;
	protected String archive = null;
	protected File currentFile = null;  
	protected File outFile = null;

	protected List<Filter> filters = new ArrayList<Filter>();
	

	public InputStream getIn() {
		return in;
	}

	public OutputStream getOut() {
		if (out == null) {
			try {
				if (outFile == null) {
					if (toDir == null && toFile == null) {
						out = System.out;
						return out;
					}
					else {
						outFile = new File(toFile, toDir);
					}
				}
				out = new FileOutputStream(outFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return out;
	}

	public FilterAJ() {
	}

	public FilterAJ(InputStream is, OutputStream os) {
		in = is;
		out = os;
	}

	public void setInputStream(InputStream in) {
		log.debug(" set in to " + in);
		this.in = in;
	}

	public void setOutputStream(OutputStream out) {
		this.out = out;
	}
	
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
	@attribute(value = "", required = false, defaultValue="input will come from another class in the pipeline.")
	public void setFile(String file) {
		this.file = file;
	}
	/**
	 * Standard output will go to the toDir/toFile combination.
	 * 
	 * @param file
	 */
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
	@attribute(value = "", required = false, defaultValue="output will go to the next class in the pipeline or standard output.")
	public void setToFile(String toFile) {
		this.toFile = toFile;
		if (toDir != null) {
			outFile = new File(toDir, toFile);
		}
	}

	public File getOutFile() {
		return outFile;
	}

	public void setOutFile(File outFile) {
		this.outFile = outFile;
	}

	/**
	 * Move input file to an archive directory.  The archive directory
	 * will be created if it does not exist.
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false, defaultValue="archive directory.  The input file, if specified, will be moved to the directory named.")
	public void setArchive(String archive) {
		this.archive = archive;
	}

	/**
	 * Standard input will come from the dir/file combination.
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false, defaultValue="input will come from another class in the pipeline.")
	public void setDir(java.lang.String dir) {
		this.dir = dir;
	}

	public void run() {
		try {
			log.debug("Starting THREAD: " + getName() + "  " + this.getClass().getName());
			execute();
		} catch (XMLBuildException e) {
			e.printStackTrace();
		} finally {
			log.debug("Ending THREAD: " + getName() + "  " + this.getClass().getName());
			try {
				out.flush();
				if (!(out instanceof FileOutputStream))
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void execute() throws XMLBuildException  {

		if (in != null) {
			try {
				 byte[] buf = new byte[4096];
				for (int len = in.read(buf); len > 0; len = in.read(buf)) {
					out.write(buf, 0, len);
				}
				finish(out);

			} catch (IOException e) {
				e.printStackTrace();
				throw new XMLBuildException(e.getMessage());
			}
		}
		for (Filter filter : filters) {
			log.debug("Filter = " + filter);
			log.debug("Filter in = " + in);
			log.debug("Filter out = " + out);
			filter.setInputStream(in);
			filter.setOutputStream(out);
			filter.execute();
		}
	}

	public String getText() {
		return text;
	}

	public String getFile() {
		return file;
	}

	public String getDir() {
		return dir;
	}

	public String getToFile() {
		return toFile;
	}

	public String getToDir() {
		return toDir;
	}

	public void setIn(InputStream in) {
		this.in = in;
	}

	public void setOut(OutputStream out) {
		this.out = out;
	}

	public void check() throws XMLBuildException {
	}

	public void setText(String text) {
	}

	public void setFromTextContent(String text) {
		this.text = text;
	}

	/**
	 * Decrypt/Encrypt a PGP document.
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false)
	public void addPgp(Pgp pgp) {
		filters.add(pgp);
	}

	/**
	 * Parse a csv file (any character sequence can 
	 * be used as the delimiter) into separate fields
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false)
	public void addLoad(Load load) {
		filters.add(load);
	}

	/**
	 * Connect classes in a pipeline
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false)
	public void addPipe(Pipe pipe) {
		filters.add(pipe);
	}

	/**
	 * Copy a file 
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false)
	public void addCopy(Copy copy) {
		filters.add(copy);
	}
	
	/**
	 * Print a message
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false)
	public void addPrint(Print copy) {
		filters.add(copy);
	}
	
	public InputStream getInFilter(InputStream in) throws IOException {
		return in;
	}
	
	public OutputStream getOutFilter(OutputStream out) throws IOException {
		return out;
	}
	
	public File getCurrentFile() {
		return currentFile;
	}

}
