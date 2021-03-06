package edu.bxml.io;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;

public interface FilterAJ extends HasPojo, Runnable, XmlObject {

	boolean isHereFile();
	void setHereFile(boolean isHereFile);
	boolean getLock();
	void setLock(boolean lock);
	Boolean getCloseIn();
	void setCloseIn(Boolean closeIn);
	Boolean getCloseOut();
	void setCloseOut(Boolean closeOut);
	void closeIn();
	void closeOut();
	InputStream getIn() throws XMLBuildException;
	PrintStream getOut();
	void setInputStream(InputStream in);
	void finish(OutputStream out);
	void setFile(String file);
	void setToDir(String toDir);
	void setToFile(String toFile);
	File getOutFile();
	void setOutFile(File outFile);
	void setArchive(String archive);
	void setDir(String dir);
	boolean isIff();
	String getFile();
	String getDir();
	String getToFile();
	String getToDir();
	void setOutputStream(PrintStream out);
	void setOutputStream(OutputStream pout);
	void setHereFile(String isHereFile);
	File getInFile();
}
