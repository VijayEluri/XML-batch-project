package edu.bxml.http;

/*
 * @(#)InstallCert.java	1.1 06/10/09
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * Use is subject to license terms.
 * 
 * Either configure JSSE to use jssecacerts as its trust store (as explained in the 
 * documentation) or copy it into your $JAVA_HOME/jre/lib/security directory
 * 
 */

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;
import com.javalobby.tnt.annotation.attribute;
public class InstallCert extends XmlObjectImpl implements XmlObject {
	private static Log log = LogFactory.getLog(InstallCert.class);
	String host = null;
	char[] passphrase = "changeit".toCharArray();
	int port = 443;
	
	@Override
	public void check() throws XMLBuildException {
		if (host == null) {
			log.debug("Host must be set.");
		}
	}
	
	/**
	 * Set the host
	 * @param host
	 */
	@attribute(value = "", required = true)
	public void setHost(String host) {
		this.host = host;
	}
	
	/**
	 * Set the passphrase
	 * @param passphrase
	 */
	@attribute(value = "", required = false, defaultValue="changeit")
	public void setPassphrase(String passphrase) {
		this.passphrase = passphrase.toCharArray();
	}
	
	/**
	 * Set the port
	 * @param port
	 */
	@attribute(value = "", required = false, defaultValue="443")
	public void setPort(Integer port) {
		this.port = port;
	}
	public void setPort(String port) {
		setPort(Integer.parseInt(port));
	}
	
	@Override
	public void execute() throws XMLBuildException {

	java.io.File file = new java.io.File("jssecacerts");
	if (file.isFile() == false) {
	    char SEP = java.io.File.separatorChar;
	    java.io.File dir = new java.io.File(System.getProperty("java.home") + SEP
		    + "lib" + SEP + "security");
	    file = new java.io.File(dir, "jssecacerts");
	    if (file.isFile() == false) {
		file = new java.io.File(dir, "cacerts");
	    }
	}
	System.out.println("Loading KeyStore " + file + "...");
	
	KeyStore ks = null;
	SavingTrustManager tm = null;
	SSLSocket socket= null;
	try {
		InputStream in = new FileInputStream(file);
		ks = KeyStore.getInstance(KeyStore.getDefaultType());
		ks.load(in, passphrase);
		in.close();

		SSLContext context = SSLContext.getInstance("TLS");
		TrustManagerFactory tmf =
		    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(ks);
		X509TrustManager defaultTrustManager = (X509TrustManager)tmf.getTrustManagers()[0];
		tm = new SavingTrustManager(defaultTrustManager);
		context.init(null, new TrustManager[] {tm}, null);
		SSLSocketFactory factory = context.getSocketFactory();
		
		System.out.println("Opening connection to " + host + ":" + port + "...");
		socket = (SSLSocket)factory.createSocket(host, port);
		socket.setSoTimeout(10000);
	} catch (KeyManagementException e1) {
		// TODO Auto-generated catch block
		log.error(e1.getStackTrace());
	} catch (FileNotFoundException e1) {
		// TODO Auto-generated catch block
		log.error(e1.getStackTrace());
	} catch (KeyStoreException e1) {
		// TODO Auto-generated catch block
		log.error(e1.getStackTrace());
	} catch (NoSuchAlgorithmException e1) {
		// TODO Auto-generated catch block
		log.error(e1.getStackTrace());
	} catch (CertificateException e1) {
		// TODO Auto-generated catch block
		log.error(e1.getStackTrace());
	} catch (UnknownHostException e1) {
		// TODO Auto-generated catch block
		log.error(e1.getStackTrace());
	} catch (SocketException e1) {
		// TODO Auto-generated catch block
		log.error(e1.getStackTrace());
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		log.error(e1.getStackTrace());
	}
	try {
	    System.out.println("Starting SSL handshake...");
	    socket.startHandshake();
	    socket.close();
	    System.out.println();
	    System.out.println("No errors, certificate is already trusted");
	} catch (SSLException e) {
	    System.out.println();
	    log.error(e.getStackTrace());
	} catch (IOException e) {
	    System.out.println();
	    log.error(e.getStackTrace());
	}
	X509Certificate[] chain = tm.chain;
	if (chain == null) {
	    System.out.println("Could not obtain server certificate chain");
	    return;
	}

	BufferedReader reader =
		new BufferedReader(new InputStreamReader(System.in));

	System.out.println();
	System.out.println("Server sent " + chain.length + " certificate(s):");
	System.out.println();
	MessageDigest sha1= null;
	MessageDigest md5 = null;
	try {
		sha1 = MessageDigest.getInstance("SHA1");
		md5 = MessageDigest.getInstance("MD5");
	} catch (NoSuchAlgorithmException e1) {
		// TODO Auto-generated catch block
		log.error(e1.getStackTrace());
	}
	for (int i = 0; i < chain.length; i++) {
	    X509Certificate cert = chain[i];
	    System.out.println
	    	(" " + (i + 1) + " Subject " + cert.getSubjectDN());
	    System.out.println("   Issuer  " + cert.getIssuerDN());
	    try {
			sha1.update(cert.getEncoded());
			System.out.println("   sha1    " + toHexString(sha1.digest()));
			md5.update(cert.getEncoded());
			System.out.println("   md5     " + toHexString(md5.digest()));
		} catch (CertificateEncodingException e) {
			// TODO Auto-generated catch block
			log.error(e.getStackTrace());
		}
	    System.out.println();
	}

	System.out.println("Enter certificate to add to trusted keystore or 'q' to quit: [1]");
	String line= "";//null;
	/*try {
		line = reader.readLine().trim();
	} catch (IOException e2) {
		// TODO Auto-generated catch block
		log.error(e2.getStackTrace());
	}*/
	int k;
	try {
	    k = (line.length() == 0) ? 0 : Integer.parseInt(line) - 1;
	} catch (NumberFormatException e) {
	    System.out.println("KeyStore not changed");
	    return;
	}

	X509Certificate cert = chain[k];
	String alias = host + "-" + (k + 1);
	try {
		ks.setCertificateEntry(alias, cert);
	} catch (KeyStoreException e1) {
		// TODO Auto-generated catch block
		log.error(e1.getStackTrace());
	}

	try {
		OutputStream out = new FileOutputStream("jssecacerts");
		ks.store(out, passphrase);
		out.close();
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		log.error(e.getStackTrace());
	} catch (KeyStoreException e) {
		// TODO Auto-generated catch block
		log.error(e.getStackTrace());
	} catch (NoSuchAlgorithmException e) {
		// TODO Auto-generated catch block
		log.error(e.getStackTrace());
	} catch (CertificateException e) {
		// TODO Auto-generated catch block
		log.error(e.getStackTrace());
	} catch (IOException e) {
		// TODO Auto-generated catch block
		log.error(e.getStackTrace());
	}

	System.out.println();
	System.out.println(cert);
	System.out.println();
	System.out.println
		("Added certificate to keystore 'jssecacerts' using alias '"
		+ alias + "'");
    }
    
    private static final char[] HEXDIGITS = "0123456789abcdef".toCharArray();
    
    private static String toHexString(byte[] bytes) {
	StringBuilder sb = new StringBuilder(bytes.length * 3);
	for (int b : bytes) {
	    b &= 0xff;
	    sb.append(HEXDIGITS[b >> 4]);
	    sb.append(HEXDIGITS[b & 15]);
	    sb.append(' ');
	}
	return sb.toString();
    }

    private static class SavingTrustManager implements X509TrustManager {
	
	private final X509TrustManager tm;
	private X509Certificate[] chain;
	
	SavingTrustManager(X509TrustManager tm) {
	    this.tm = tm;
	}
    
	public X509Certificate[] getAcceptedIssuers() {
	    throw new UnsupportedOperationException();
	}
    
	public void checkClientTrusted(X509Certificate[] chain, String authType)
		throws CertificateException {
	    throw new UnsupportedOperationException();
	}
    
	public void checkServerTrusted(X509Certificate[] chain, String authType)
		throws CertificateException {
	    this.chain = chain;
	    tm.checkServerTrusted(chain, authType);
	}
    }

}

