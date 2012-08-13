package edu.bxml.generator;

import java.io.File;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;

public abstract class Find {
	private static Log log = LogFactory.getLog(Find.class);
	String dir;
	String fileMatch;
	
	abstract void process(File file, Map env);
	
	public void find(File dir, String match, Map env) throws XMLBuildException {
		String files[] = dir.list();
		for (int i = 0; i < files.length; i++) {
			log.debug("file listing: " + files[i]);
			File newFile = new File(dir, files[i]);
			if (newFile.isDirectory()) {
				log.debug("DIR: " + newFile);
				find(newFile, match, env);
			}
			else {
				if (files[i].matches(match)) {
					process(newFile, env);
				}
			}
		}
	}
}
