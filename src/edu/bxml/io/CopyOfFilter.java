package edu.bxml.io;

/**
 * Cay Horstmann

 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlParser;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;

/**
 * A class must extend filter to participate in a pipeline.  Filter can
 * be used standalone as a basic copy from System.in to System.out.
 * @author geoff.ritchey
 *
 */
@attribute(value = "", required = true)
public class CopyOfFilter extends XmlObject implements Runnable {
	private static Log log = LogFactory.getLog(CopyOfFilter.class);
	protected InputStream in;
	protected OutputStream out;
	protected String text = null;
	List<XmlObject> attributes = new ArrayList<XmlObject>();

	protected String file = null;
	protected String dir = null;
	protected String toFile = null;
	protected String toDir = null;
	protected String archive = null;
	protected File currentFile = null;

	protected List<Filter> commands = new ArrayList<Filter>();

	public CopyOfFilter() {
	}

	public CopyOfFilter(InputStream is, OutputStream os) {
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
		log.debug("file = " + file);
		this.file = file;
	}
	
	public File getFile() {
		return new File(toDir, toFile);
	}
	/**
	 * Standard output will go to the toDir/toFile combination.
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false, defaultValue="output will go to the next class in the pipeline or standard output.")
	public void setToDir(String toDir) {
		log.debug("todir = " + toDir);
		this.toDir = toDir;
	}
	
	/**
	 * Standard output will go to the toDir/toFile combination.
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false, defaultValue="output will go to the next class in the pipeline or standard output.")
	public void setToFile(String toFile) {
		log.debug("toFile = " + toFile);
		this.toFile = toFile;
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
		log.debug("dir = " + dir);
		this.dir = dir;
	}

	public void run() {
		try {
			execute();
		} catch (XMLBuildException e) {
			e.printStackTrace();
		} finally {
			try {
				out.flush();
				if (out instanceof PipedOutputStream)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void execute() throws XMLBuildException {
		File archiveDir = null;
		try {
			archiveDir = new File(archive);
		} catch (RuntimeException e2) {}
		
		List<File> files = new ArrayList<File>();

		boolean found = false;
		if (dir != null) {
			File[] dirListing = (new File(dir)).listFiles();
			if (dirListing != null) {
				for (File file : dirListing) {
					if (!file.getName().matches(this.file))
						continue;
					files.add(file);
				}
			}
		}
		
		Iterator<File> i = files.iterator();
		boolean loop = in != null || i.hasNext();
		currentFile = null;
		while (loop) {
			if (i.hasNext()) {
				currentFile = (File) i.next();
			}
			loop = i.hasNext();
			String toFile = XmlParser.processAttributes(this, this.toFile);
			OutputStream outToFile = null;
			if (this.toDir != null) {
				if (toFile == null && currentFile != null) {
					toFile = currentFile.getName();
				}
				try {
					File fileToFile = null;
					outToFile = new FileOutputStream(fileToFile = new File(toDir, toFile));
					log.debug("file is " + fileToFile.getAbsolutePath());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					throw new XMLBuildException(e.getMessage());
				}
			}
			found = true;

			try {
				if (currentFile != null)
					in = new FileInputStream(currentFile);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}

			try {
				in = getInFilter(in);
				out = getOutFilter(out);
				outToFile = getOutFilter(outToFile);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			for (XmlObject att: attributes) {
				att.execute();
			}
			if (in != null) {
				try {
					 byte[] buf = new byte[4096];
						for (int len = in.read(buf); len > 0; len = in.read(buf)) {
							if (out != null)
								out.write(buf, 0, len);
							if (outToFile != null)
								outToFile.write(buf, 0, len);
						}
						if (out != null)
							finish(out);
						if (outToFile != null)
							finish(outToFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			runChildren();
			closeOut(outToFile);
			archive(archiveDir);
		}
		if (!found) {
			log.debug("dir = " + dir);
			log.debug("file = " + file);
			if (dir == null) {
				log.debug("no file matched " + file + ": dir is null");	
			}
			else 
				log.debug("no file in " + new File(dir).getAbsolutePath() + 
					" matched " + file);
			
			OutputStream outToFile = null;
			try {
				out = new FileOutputStream(new File(toDir, toFile));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			runChildren();
			closeOut(outToFile);
			archive(archiveDir);
			
		}
	}
	
	public void closeOut(OutputStream outToFile) {
		if (outToFile != null) {
			try {
				outToFile.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		if (out != null) {
			try {
				System.err.println("OUT CLOSE: " + out.getClass().getName());
				out.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	
	}

	public void runChildren() throws XMLBuildException {
		for (Filter filter : commands) {
			filter.setInputStream(in);
			filter.setOutputStream(out);
			filter.execute();
		}
	}
	
	public void archive(File archiveDir) {
		if (archive != null) {
			if (!archiveDir.exists()) {
				archiveDir.mkdirs();
			}
			if (archiveDir.exists()) {
				log.debug("archive exists");
				//file.renameTo(new File(archiveDir, file.getName()));
				try {
					FileInputStream inArch = new FileInputStream(file);
					FileOutputStream outArch = new FileOutputStream(
							new File(archiveDir, currentFile.getName()));
					Copy.copyBinaryFile(inArch, outArch);
					inArch.close();
					outArch.close();
					
					log.debug("delete " + currentFile.getAbsolutePath());
					log.debug("delete result = " + currentFile.delete());
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
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
		commands.add(pgp);
	}

	/**
	 * Parse a csv file (any character sequence can 
	 * be used as the delimiter) into separate fields
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false)
	public void addLoad(Load load) {
		commands.add(load);
	}

	/**
	 * Connect classes in a pipeline
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false)
	public void addPipe(Pipe pipe) {
		commands.add(pipe);
	}

	/**
	 * Copy a file 
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false)
	public void addCopy(Copy copy) {
		commands.add(copy);
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
	
	@attribute(value = "", required = false)
	public void addAttribute(Attribute attribute) {
		attributes.add(attribute);
	}

}
