package edu.bxml.io;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.browsexml.core.XMLBuildException;


public class TestIo extends TestCase {

    public static Test suite() {

        TestSuite suite = new TestSuite();
        

        suite.addTestSuite(TestIo.class);
        return suite;
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
	
	protected void setUp() {
		new File("build/TestMessage.txt.txt").delete();
		new File("build/test/TestMessage.txt").delete();
		new File("build/test").mkdirs();
	}
	
	protected void tearDown() {
	}
	
	public void testZipEqualsUnzip() {
		/*
<io xmlns='edu.bxml.io' xmlns:io='edu.bxml.io' xmlns:format="edu.bxml.format">
	<pipe dir="." file="2006_06_14_08_38_lcu_bills.dat" 
			toDir="build" >
		<gZip/>
		<gZip unzip="true"/>
	</pipe>	
</io>
		 */
		
		Io io = new Io();
		Pipe pipe = new Pipe();
		pipe.setDir("test/");
		pipe.setFile("TestMessage.txt");
		pipe.setToDir("build/test");
		pipe.setFile("TestMessage.txt");
		
		GZip zip = new GZip();
		GZip unzip = new GZip();
		unzip.setUnzip("true");

		pipe.addGZip(zip);
		pipe.addGZip(unzip);
		io.addPipe(pipe);
		
		try {
			pipe.init(io);
			zip.init(pipe);
			unzip.init(pipe);
			
			zip.check();
			unzip.check();
			pipe.check();
			io.check();
		} catch (XMLBuildException e2) {
			assertTrue("check failed", false);
			e2.printStackTrace();
		}
		
		try {
			io.execute();
		} catch (XMLBuildException e1) {
			e1.printStackTrace();
		}
		assertTrue(Compare.compare("build/test/TestMessage.txt", 
				"test/TestMessage.txt"));
		
	}
	
	public void testPgpEncryptEqualsDecrypt() {
		/*
<io xmlns='edu.bxml.io' xmlns:io='edu.bxml.io' xmlns:format="edu.bxml.format">
	<pipe dir="web" file="test.txt">
		<pgpEncrypt passPhrase = "TestingPassphrase" keyFile="bob-public.pgp.asc"/>
		<pgpEncrypt decrypt="true" 
			passPhrase = "TestingPassphrase" keyFile="bob-secret.pgp.asc"/>
	</pipe>	
</io>
		 */
		Io io = new Io();
		Pipe pipe = new Pipe();
		pipe.setDir("test/");
		pipe.setFile("TestMessage.txt");
		pipe.setToDir("build/test");
		pipe.setToFile("PgpEncryptEqualsUnEncrypt.txt");
		
		pipe.setDir("C:/Users/geoff.ritchey/work/course/test");
		pipe.setFile("TestMessage.txt");
		
		PgpEncrypt encrypt = new PgpEncrypt();
		encrypt.setPassPhrase("TestingPassphrase");
		try {
			encrypt.setKeyFile("test/pgp/bob-public.pgp.asc");
		} catch (XMLBuildException e3) {
			e3.printStackTrace();
		}
		
		PgpEncrypt decrypt = new PgpEncrypt();
		
		decrypt.setDecrypt("true");
		decrypt.setPassPhrase("TestingPassphrase");
		try {
			decrypt.setKeyFile("test/pgp/bob-secret.pgp.asc");
			decrypt.setSignersKeyFile("test/pgp/bob-public.pgp.asc");
		} catch (XMLBuildException e) {
			e.printStackTrace();
		}
		
		io.addPipe(pipe);
		pipe.addPgpEncrypt(encrypt);
		pipe.addPgpEncrypt(decrypt);
		
		try { 
			pipe.init(io);
			decrypt.init(pipe);
			encrypt.init(pipe);

			decrypt.check();
			encrypt.check();
			pipe.check();
			io.check();
		} catch (XMLBuildException e2) {
			assertTrue("check failed", false);
			e2.printStackTrace();
		}
		
		try {
			io.execute();
		} catch (XMLBuildException e1) {
			e1.printStackTrace();
		}
		
		assertTrue(Compare.compare("build/test/PgpEncryptEqualsUnEncrypt.txt", 
				"test/TestMessage.txt"));
				
	}
	
	public void testLoad() {
		/*
<io xmlns='edu.bxml.io' xmlns:io='edu.bxml.io' xmlns:format="edu.bxml.format">
	<io:Filter toDir="\\infodepot\Humongous\QuickBill" toFile="#{MMddyyyy_kkmm}.xml">
		<io:copy dir="web" file="header" xmlns="edu.bxml.format"/>
		<pipe dir="\\Stargate\infiNETDrop\QuickBill" file=".*\.dat\.pgp"
				archive="\\Stargate\infiNETDrop\archive">
			<io:load xmlns="edu.bxml.format"
					delimit="\|" header="false">
				<io:matches expression="^E">
<![CDATA[%{pipe.file}]]>
				</io:matches>
			</io:load>
		</pipe>
	</io:Filter>
</io>
		 */
		Io io = new Io();
		Filter filter = new Filter();
		filter.setToDir("build");
		filter.setToFile("XXX.txt");
		io.addFilter(filter);
		
		Pipe pipe = new Pipe();
		pipe.setName("pipe");
		pipe.setDir("test/pgp");
		pipe.setFile("20080725.txt");
		filter.addPipe(pipe);
		
		Load load = new Load();
		load.setDelimit("\\|");
		load.setHeader("false");
		pipe.addLoad(load);
		
		Matches matches = new Matches();
		matches.setExpression("^E");
		load.addMatches(matches);
		matches.setFromTextContent("${pipe.file.lastModified}");
		matches.setSymbolTable(new ConcurrentHashMap());
		ConcurrentHashMap st = matches.getSymbolTable();
		st.put("pipe", pipe);
		
		try {
			filter.init(io);
			pipe.init(filter);
			load.init(pipe);
			matches.init(load);
		
			matches.check();
			load.check();
			pipe.check();
			filter.check();
			io.check();
			
			io.execute();
		} catch (XMLBuildException e) {
			e.printStackTrace();
		}
		
		
	}
	
	public void testBillingFile() {
		/*
<io 
	<io:Filter toDir="." toFile="{MMddyyyy}.xml">
		<pipe dir="\\Stargate\infiNETDrop\QuickBill" file=".*\.dat\.pgp">
				<!--archive="C:/Users/geoff.ritchey/Desktop/PGP/archive"-->
			<pgpEncrypt decrypt="true" keyFile="C:/pgp262i/LCU.asc" 
				passPhrase = "LCU2006QuickBill"/>
				
				There seems to be a problem where stream decrypt
				doesn't work in a case where message collection 
				decrypt does.
				
				PgpEncrypt (streams) should work but doesn't so resort 
				to Pgp.
		 */
		Io io = new Io();
		Filter filter = new Filter();
		filter.setToDir("build");
		filter.setToFile("XXX.txt");
		io.addFilter(filter);
		
		Pipe pipe = new Pipe();
		pipe.setDir("test/pgp");
		pipe.setFile(".*\\.dat\\.pgp");
		filter.addPipe(pipe);
		
		Pgp decrypt = new Pgp();
		decrypt.setPrivateArmouredKeyFile("test/pgp/LCU.asc");

		decrypt.setPassPhrase("LCU2006QuickBill");
		pipe.addPgp(decrypt);
		
		try {
			filter.init(io);
			pipe.init(filter);
			decrypt.init(pipe);
		
			decrypt.check();
			pipe.check();
			filter.check();
			io.check();
			
			io.execute();
		} catch (XMLBuildException e) {
			e.printStackTrace();
		}
		assertTrue(Compare.compare("build/XXX.txt", "test/pgp/20080725.txt"));
	}
	
	public void testTee() {
		/*
	<pipe name="pipe" dir="test" file="TestMessage.txt" toDir="build/test"> 
		<gZip/>
		<tee toDir="build/test" 
			toFile="%{pipe.currentFile.name}.gz"/>
		<gZip unzip="true"/>
		 */
		ConcurrentHashMap st = new ConcurrentHashMap();
		Io io = new Io();
		Pipe pipe = new Pipe();
		pipe.setDir("test/");
		pipe.setFile("TestMessage.txt");
		pipe.setToDir("build/test");
		pipe.setName("pipe");
		st.put("pipe", pipe);
		
		io.addPipe(pipe);
		

		GZip zip = new GZip();
		pipe.addGZip(zip);
		
		Tee tee = new Tee();
		tee.setToDir("build/test");
		tee.setToFile("%{pipe.currentFile.name}.gz");
		tee.setSymbolTable(st);
		pipe.addTee(tee);
		
		GZip unzip = new GZip();
		unzip.setUnzip("true");
		pipe.addGZip(unzip);
		
		try {
			zip.init(pipe);
			tee.init(pipe);
			unzip.init(pipe);
			pipe.init(io);

			zip.check();
			tee.check();
			unzip.check();
			pipe.check();
			io.check();

			io.execute();
		} catch (XMLBuildException e) {
			e.printStackTrace();
		}
		
		assertTrue(Compare.compare("build/test/TestMessage.txt", "test/TestMessage.txt"));
		
	}

}
