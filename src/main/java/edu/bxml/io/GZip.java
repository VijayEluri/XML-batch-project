package edu.bxml.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.browsexml.core.XMLBuildException;
import com.javalobby.tnt.annotation.attribute;

/**
 * Compress or Decompress a file
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class GZip extends Filter {
	boolean unzip = false;
	
	public InputStream getInFilter(InputStream in) throws IOException {
		if (unzip && in != null) {
			return new GZIPInputStream(in); 
		}
		return in;
	}
	
	public OutputStream getOutFilter(OutputStream out) throws IOException {
		if (!unzip && out != null) {
			GZIPOutputStream z = new GZIPOutputStream(out);
			return z; 
		}
		return out;
	}
	
	public void finish(OutputStream out) {
		if (out instanceof GZIPOutputStream)
			try {
				((GZIPOutputStream) out).finish();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	/**
	 * Should we uncompress the file. If false or missing a compression will be
	 * done.
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false, defaultValue = "false")
	public void setUnzip(Boolean unzip) {
		this.unzip = unzip;
	}
	public void setUnzip(String unzip) {
		setUnzip(Boolean.parseBoolean(unzip));
	}
}
