package edu.bxml.format;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Util {
	private static Log log = LogFactory.getLog(Util.class);
	/**
	 * Move a file f into the directory dir
	 * @param f
	 * @param dir
	 */
	public static void move(File f, String dir, boolean force) {
		move(f, dir, null, force);
	}
	/**
	 * Move a file f into the directory dir
	 * @param f
	 * @param dir
	 * @param destFileName - rename the file while moving
	 */
	public static void move (File f, String dir, String destFileName, boolean force) {
		File directory = null;
		File dest = null;
		
		if (!dir.startsWith("/") && !dir.startsWith(":", 1)) {
			directory = new File(f.getParentFile(), dir);
		}
		else
			directory = new File(dir);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		if (destFileName != null) 
			dest = new File(directory, destFileName);
		else
			dest = new File(directory, f.getName());
		
		log.debug ("move " + f.getAbsolutePath() + " to " + dest);
		log.debug("dest exists = " + dest.exists());
		log.debug("force = " + force);
		if (dest.exists() && dest.isFile() && force) {
			if (dest.delete()) {
				log.debug("destination file removed first");
			}
			else {
				log.error("destination file exists but could not be removed... " + dest.getAbsolutePath());
			}
		}
		boolean b = f.renameTo(dest);
		if (!b) {
			log.debug("file not moved: probably a permission or space problem but can't tell for sure.  From Java specification:");
			log.debug("Many aspects of the behavior of this method are inherently platform-dependent: The rename operation might not be able to move a file from one filesystem to another, it might not be atomic, and it might not succeed if a file with the destination abstract pathname already exists."); 
		}
		else
			log.debug("move " + f + " to " + dest);
	}
	
	/**
	 * Copy a file from location specified by 'from' to the location specified by 'to'
	 * @param from
	 * @param to
	 * @throws IOException
	 */
	public static void copy(File from, File to) throws IOException  {
		log.debug("copy " + from + " to " + to);
        InputStream in = new FileInputStream(from);
        log.debug("open to..");
        OutputStream out = new FileOutputStream(to);
        copy(in, out, true);
	}
	
	public static void copy(InputStream in, OutputStream out, boolean close) throws IOException  {
        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        log.debug("read...");
        while ((len = in.read(buf)) > 0) {
        	log.debug("write...");
            out.write(buf, 0, len);
            log.debug("read.x..");
        }
        if (close) {
        	log.debug("close...");
	        in.close();
	        out.close();
        }
	}
}
