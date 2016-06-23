package com.browsexml.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;


public class XmlParseTest {
	private static Log log = LogFactory.getLog(XmlParseTest.class);
	
    @Test
    public void testMarkerMethod() {

    }
    
    
    //@Test
    public void testSearchReplace() {
    	String filter = ""
    			+ " ${and services.service like '%s':service} "
    			+ " ${and people.last_name like '%s':last} "
    			+ " ${and people.first_name like '%s':first} "
    			+ " ${and people.people_id like '%s':peopleId} "
    			+ " ${and people.people_id is not null:isInPowerCampus} "
    			+ " ${and scans.scantime < dateadd(d,1,'%s'):scantimeMax} "
    			+ " ${and scans.scantime >= '%s':scantimeMin} "
    			+ " ${and notes is not null:hasNotes} "
    			+ " ";
    	
    	Map<String, String> env = new HashMap<String, String>();
    	env.put("service", "tutor");
    	env.put("last", "r%");
    	env.put("first", "g%");
    	env.put("peopleId", "%");
    	env.put("scantimeMax", "2015-05-07");
    	env.put("scantimeMin", "2015-01-01");
    	env.put("isInPowerCampus", "true");
    	
    	String filtered = XmlParser.processMacros(env, filter);

    	log.debug("filtered = " + filtered);
    	
    	assert(filtered.equals(" and services.service like 'tutor'  and people.last_name like 'r%'  and people.first_name like 'g%'  and people.people_id like '%'  and people.people_id is not null  and scans.scantime < dateadd(d,1,'2015-05-07')  and scans.scantime >= '2015-01-01'    "));
    }
}
