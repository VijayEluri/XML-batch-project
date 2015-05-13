package edu.bxml.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.javalobby.tnt.annotation.attribute;

/**
 *	Diff two comma separated files (separator may be something other than a comma)
 *	assuming they are sorted on a key field.  The key field does not have to be unique.
 * 
 */
@attribute(value = "", required = true)
public class SortedCsvDiff extends Filter{
	private static Log log = LogFactory.getLog(SortedCsvDiff.class);
	private Boolean diffOnly = true;
	private Boolean different = false;
	private Integer key = 0;
	private String otherFile = null;
	private String otherDir = null;
	private String separator = "|";
	private String fileOneInsert = "<";
	private String fileTwoInsert = ">";
	private String fileOneEqualUpdate = "<E";
	private String fileTwoEqualUpdate = ">E";
	private String toDir = ".";
	private String toFile = null;
	private File outFile = null;
	private PrintStream out = System.out;
	private Boolean printUpdateOnly = true;
	
	public Boolean getPrintUpdateOnly() {
		return printUpdateOnly;
	}

	public void setPrintUpdateOnly(String printUpdateOnly) {
		setPrintUpdateOnly(Boolean.parseBoolean(printUpdateOnly));
	}
	
	public void setPrintUpdateOnly(Boolean printUpdateOnly) {
		this.printUpdateOnly = printUpdateOnly;
	}

	public String getFileOneInsert() {
		return fileOneInsert;
	}

	public void setFileOneInsert(String fileOneInsert) {
		this.fileOneInsert = fileOneInsert;
	}

	public String getFileTwoInsert() {
		return fileTwoInsert;
	}

	public void setFileTwoInsert(String fileTwoInsert) {
		this.fileTwoInsert = fileTwoInsert;
	}

	public String getFileOneEqualUpdate() {
		return fileOneEqualUpdate;
	}

	public void setFileOneEqualUpdate(String fileOneEqualUpdate) {
		this.fileOneEqualUpdate = fileOneEqualUpdate;
	}

	public String getFileTwoEqualUpdate() {
		return fileTwoEqualUpdate;
	}

	public void setFileTwoEqualUpdate(String fileTwoEqualUpdate) {
		this.fileTwoEqualUpdate = fileTwoEqualUpdate;
	}

	public String getToDir() {
		return toDir;
	}

	public void setToDir(String toDir) {
		this.toDir = toDir;
	}

	public String getToFile() {
		return toFile;
	}

	public void setToFile(String toFile) {
		this.toFile = toFile;
	}

	public Boolean getDiffOnly() {
		return diffOnly;
	}
	
	public String getSeparator() {
		return separator;
	}

	/**
	 * String to split fields of line by
	 * @param separator
	 */
	@attribute(value = "", required = false, defaultValue="\\")
	public void setSeparator(String separator) {
		this.separator = separator;
	}

	/**
	 * Does not produce any output.  If the files differ, isDifferent will be true;
	 * otherwise it will be false.
	 * @param diffOnly
	 */
	@attribute(value = "", required = false, defaultValue="true")
	public void setDiffOnly(Boolean diffOnly) {
		this.diffOnly = diffOnly;
	}
	public void setDiffOnly(String diffOnly) {
		setDiffOnly(Boolean.parseBoolean(diffOnly));
	}
	
	public Integer getKey() {
		return key;
	}
	
	/**
	 * Specify the column (zero based) where the key is located.
	 * @param key
	 */
	@attribute(value = "", required = false, defaultValue="0 (first column)")
	public void setKey(Integer key) {
		this.key = key;
	}
	public void setKey(String key) {
		setKey(Integer.parseInt(key));
	}
	
	/**
	 * The file to compare
	 * @return
	 */
	@attribute(value = "", required = true)
	public String getOtherFile() {
		return otherFile;
	}
	public void setOtherFile(String otherFile) {
		this.otherFile = otherFile;
	}

	public String getOtherDir() {
		return otherDir;
	}
	/**
	 * Directory of file to compare
	 * @return
	 */
	@attribute(value = "", required = false, defaultValue="same value as dir")
	public void setOtherDir(String otherDir) {
		this.otherDir = otherDir;
	}
	
	public void execute() throws XMLBuildException  {
		BufferedReader in2 = null;
		log.debug("EXECUTE " + this.getClass().getName());
		String line = null;
		String line2 = null;
		File file1 = null;
		File file2 = null;
		BufferedReader in = null;
		
		if (otherDir == null)
			otherDir = dir;
		try {
			
			file1 = new File(dir, file);
			if (toFile != null) {
				outFile = new File(toDir, toFile);
				out = new PrintStream(outFile);
			}
			
			try {
				in = new BufferedReader(
						new InputStreamReader(new FileInputStream(file1)));
			} catch (FileNotFoundException e) {
				log.error("Sorted File not found: " + dir + "/" + file);
				e.printStackTrace();
				in = new BufferedReader(new StringReader(""));
			}

			file2 = new File(otherDir, otherFile);
			try {
				in2 = new BufferedReader(
						new InputStreamReader(new FileInputStream(file2)));
			} catch (FileNotFoundException e) {
				log.error("Sorted File (otherFile) not found: " + otherDir + "/" + otherFile);
				e.printStackTrace();
				in2 = new BufferedReader(new StringReader(""));
			}

			line = in.readLine();
			line2 = in2.readLine();
			String key1 = null;
			String key2 = null;
			String lastKey1 = null;
			String lastKey2 = null;
			
			while (line != null || line2 != null) {

				if (line != null) {
					key1 = line.split("\\Q" + separator + "\\E")[key];
				}
				if (line2 != null) {
					key2 = line2.split("\\Q" + separator + "\\E")[key];
				}
				int compare = 1;
				if (key1 == null  || key2 == null) {
					compare = (key1 == null&&key2==null)?0:(key1==null)?1:-1;
				}
				else {
					compare = (int)Math.signum((double)key1.compareTo(key2));
				}
				different = (compare != 0 || different);
				if (different && diffOnly) {
					break; //while
				}
				switch (compare) {
				case -1:
					if (line != null)
						out.println(fileOneInsert + separator + line);
					line = in.readLine();
					key1 = null;
					break;//for
				case 0:
					if (line != null && !line.equals(line2)) {
						different = true;
						if (!printUpdateOnly)
							out.println(fileOneEqualUpdate + separator + line);
						out.println(fileTwoEqualUpdate + separator + line2);
						log.debug("key1, key2, compare = " + key1 + "  " + key2 + "  " + compare);
					}
					line = in.readLine();
					line2 = in2.readLine();
					break;
				case 1:
					if (line2 != null)
						out.println(fileTwoInsert + separator + line2);
					line2 = in2.readLine();
					key2 = null;
					break;
				}
				if (lastKey1 != null && key1 != null &&  lastKey1.compareTo(key1) > 0) {
					System.err.println("file " + file  + " not sorted on key");
					System.err.println("last key1 = " + lastKey1);
					System.err.println("key1 = " + key1);
					
					break;
				}
				if (lastKey2 != null && key2 != null &&  lastKey2.compareTo(key2) > 0) {
					System.err.println("file " + otherFile  + " not sorted on key");
					System.err.println("last key2 = " + lastKey2);
					System.err.println("key2 = " + key2);
					break;
				}
				lastKey1 = key1;
				lastKey2 = key2;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (!different) {
			System.err.println("No differences");
		}
		try {
			log.debug("in = " + in);
			log.debug("file1 = " + file1);
			if (in != null && file1 != null) {
				log.debug("close in = " + file1.getAbsolutePath());
				in.close();
			}
			log.debug("in2 = " + in2);
			log.debug("file2 = " + file2);
			if (in2 != null && file2 != null) {
				log.debug("close in2 = " + file2.getAbsolutePath());
				in2.close();
			}
			if (out != null && out != System.out) {
				log.debug("close out = " + outFile);
				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Boolean getDifferent() {
		return different;
	}

	public void setDifferent(Boolean different) {
		this.different = different;
	}

	
}
