package edu.bxml.format;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;

import edu.bxml.io.FilterAJ;
import edu.bxml.io.FilterAJImpl;

public class PdfSigner extends FilterAJImpl implements FilterAJ {
	private static Log log = LogFactory.getLog(PdfSigner.class);
	String keyfile;
	String keystorePassword;
	String location;
	String reason;
	String signatureField;
	
	public String getSignatureField() {
		return signatureField;
	}

	public void setSignatureField(String signatureField) {
		this.signatureField = signatureField;
	}

	public String getKeystorePassword() {
		return keystorePassword;
	}

	public void setKeystorePassword(String keystorePassword) {
		this.keystorePassword = keystorePassword;
	}

	String passphrase;

	public String getKeyfile() {
		return keyfile;
	}

	public void setKeyfile(String keyfile) {
		this.keyfile = keyfile;
	}

	public String getPassphrase() {
		return passphrase;
	}

	public void setPassphrase(String passphrase) {
		this.passphrase = passphrase;
	}

	@Override
	public void execute() throws XMLBuildException {
		try {
			sign(keyfile, passphrase);
		} catch (Exception e) {
			e.printStackTrace();
			throw new XMLBuildException(e.getMessage(), this);
		}
	}
	
	public void sign(String keyfile, String passphrase) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, UnrecoverableKeyException, DocumentException, XMLBuildException {
		
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		ks.load(new FileInputStream(keyfile), keystorePassword.toCharArray());
		String alias = (String)ks.aliases().nextElement();
		PrivateKey key = (PrivateKey)ks.getKey(alias, passphrase.toCharArray());
		Certificate[] chain = ks.getCertificateChain(alias);
		PdfReader reader = new PdfReader(this.getIn());
		OutputStream fout = this.getOut();
		PdfStamper stp = PdfStamper.createSignature(reader, fout, '\0');
		PdfSignatureAppearance sap = stp.getSignatureAppearance();
		sap.setCrypto(key, chain, null, PdfSignatureAppearance.SELF_SIGNED);
		stp.getSignatureAppearance().setVisibleSignature(signatureField);//"studentSig"
		sap.setReason(reason);
		sap.setLocation(location);
		sap.setCertificationLevel(PdfSignatureAppearance.CERTIFIED_NO_CHANGES_ALLOWED);
		// comment next line to have an invisible signature
		//sap.setVisibleSignature(rect, 1, null);
		stp.close();
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
}
