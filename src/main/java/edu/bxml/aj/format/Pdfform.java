package edu.bxml.aj.format;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.AcroFields.Item;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;

import edu.bxml.format.BinaryField;
import edu.bxml.format.BitArrayField;
import edu.bxml.format.Connection;
import edu.bxml.format.Field;
import edu.bxml.format.Sql;
import edu.bxml.io.FilterAJ;
import edu.bxml.io.FilterAJImpl;

public class Pdfform extends FilterAJImpl implements FilterAJ {
	private static Log log = LogFactory.getLog(Pdfform.class);
	private String queryName;
	private String reportName = "";


	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getQueryName() {
		return queryName;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}

	public Query getQuery() {
		return query;
	}

	public void setQuery(Query query) {
		this.query = query;
	}

	private Query query = null;

	HashMap<String, Field> fieldFormats = new HashMap<String, Field>();

	List<BitArrayField> bitArrayList = new ArrayList<BitArrayField>();

	public void addFieldEnd(Field field) throws XMLBuildException {
		String name = field.fieldName != null ? field.fieldName : field
				.getName();
		log.debug("add field named " + name);
		if (field instanceof BitArrayField) {
			Map<String, BinaryField> fields = ((BitArrayField) field).getBitFields();
			for (Entry<String, BinaryField> f:fields.entrySet()) {
				log.debug("add subfield named " + f.getKey());
				fieldFormats.put(f.getKey(), field);
			}
		}
		else
			fieldFormats.put(name, field);
	}
	
	String keyfile;
	String keystorePassword;
	String passphrase;
	String signatureField;
	String reason;
	String location;
	
	boolean sign = false;
	
	public String getKeyfile() {
		return keyfile;
	}

	public void setKeyfile(String keyfile) {
		this.keyfile = keyfile;
	}

	public String getKeystorePassword() {
		return keystorePassword;
	}

	public void setKeystorePassword(String keystorePassword) {
		this.keystorePassword = keystorePassword;
	}

	public String getPassphrase() {
		return passphrase;
	}

	public void setPassphrase(String passphrase) {
		this.passphrase = passphrase;
	}

	public String getSignatureField() {
		return signatureField;
	}

	public void setSignatureField(String signatureField) {
		this.signatureField = signatureField;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}


	@Override
	public void check() throws XMLBuildException {

		sign = false;
		if (keyfile != null || keystorePassword != null || passphrase != null ||signatureField != null) {
			sign = true;
			log.debug("SIGN is true");
			if (keyfile == null || keystorePassword == null || passphrase == null || signatureField == null) {
				throw new XMLBuildException("one of keyfile, keyhstorePassword, passphrase or signatureField is present indicating " +
						"you wish to sign the document, while at the same time one or more of those required fields for signing is missing.", this);
			}
		}
	}

	@Override
	public void execute() throws XMLBuildException {
		// log.debug("SELECT:");

		Query query = (Query) this.getAncestorOfType(Query.class);

		//PrintStream localOut = new PrintStream(this.getOut());

		File arch = null;

		try {
			Sql sql = query.getSql(queryName);

			Connection connection = sql.getConnection();
			java.sql.Connection c = null;
			try {
				c = connection.getConnection();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
	
			Statement stmt = null;
			try {
				log.debug("sql = " + sql.getQuery());
				stmt = c.createStatement();
				final ResultSet rs = stmt.executeQuery(sql.getQuery());

				// java.sql.Connection con = connection.getConnection();
				/* TODO put dataoutput into sub objects */
				//
				// recordCount = 0;
				//
				// // log.debug(sql.query);
				//
				// setMD(rs.getMetaData());
				// checkHeader();
				//
				// log.debug("filename = " + filename);
				// if (filenameField == null) {
				// printHeader(localOut, workingValues);//PROBLEM LINE
				// }
				log.debug("start loop");
				while (rs.next()) {
					printPdf(rs);

					log.debug("data 1 is " + rs.getString(1));
				}
			} catch (SQLException s) {
				s.printStackTrace();
			}
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException cnfe) {
			throw new XMLBuildException("class not found exception: "
					+ cnfe.getMessage(), this);
		} finally {
		}
	}

	public void printPdf(ResultSet rs) throws XMLBuildException {
		PdfReader pdf = null;
		try {
			pdf = new PdfReader(getIn());
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		PdfStamper filledOutForm = null;
		AcroFields acroFields = null;

		try {
			if (!sign) 
				filledOutForm = new PdfStamper(pdf,out);
			else {
				filledOutForm = PdfStamper.createSignature(pdf,out, '\0');
				try {
					sign(filledOutForm, keyfile, keystorePassword, passphrase, signatureField, reason, location);
				} catch (Exception e) {
					e.printStackTrace();
					throw new XMLBuildException(e.getMessage(), this);
				} 
			}
			acroFields = filledOutForm.getAcroFields();
		} catch (DocumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		

		if (acroFields != null) {
			Map<String, Item> fields = acroFields.getFields();

			for (String fieldName : fields.keySet()) {
				int fieldType = acroFields.getFieldType(fieldName);
				Item item = acroFields.getFieldItem(fieldName);
	
				try {
				
					log.debug("field name = " + fieldName + "  type = "
							+ acroFields.getFieldType(fieldName));

					Field formatting = fieldFormats.get(fieldName);
					log.debug("formatting for " + fieldName + " is a ");
					if (formatting == null) { //Not every field needs formatting
						log.debug("null");
					}
					else {
						log.debug(formatting.getClass().getName());
					}
					if (formatting instanceof BitArrayField) {
						Object x = rs.getObject(formatting.getFieldName()); // Get the varbinary string
						x = ((BitArrayField)formatting).format((byte[])x, fieldName);  // Pull out specific bit and format
						if (!x.toString().equals("__off__")) {
							acroFields.setField(fieldName, x.toString());
						}
						log.debug("set " + fieldName + " to " + x);
					}
					else {
						Object x = rs.getObject(fieldName);
						if (x != null) {
							log.debug("Object type is a " + x.getClass().getName());
							if (formatting != null) {
								x = formatting.format(x);
								log.debug("formated " + fieldName
										+ " is " + x);
							}
							if (!x.toString().equals("__off__")) {
								acroFields.setField(fieldName, x.toString());
							}
							log.debug("set " + fieldName + " to " + x);
						} else {
							log.debug("set " + fieldName + " is null");
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DocumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} else {
			log.debug("No form in Pdf file");
		}
		try {
			filledOutForm.close();
			log.debug("Flush pdf out");
			out.flush();
			pdf.close();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sign(PdfStamper stp, String keyfile, String keystorePassword, String passphrase, String signatureField, String reason, String location) 
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, UnrecoverableKeyException, DocumentException {
		log.debug("SIGN it");
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		ks.load(new FileInputStream(keyfile), keystorePassword.toCharArray());
		String alias = (String)ks.aliases().nextElement();
		PrivateKey key = (PrivateKey)ks.getKey(alias, passphrase.toCharArray());
		Certificate[] chain = ks.getCertificateChain(alias);
		
		PdfSignatureAppearance sap = stp.getSignatureAppearance();
		sap.setCrypto(key, chain, null, PdfSignatureAppearance.SELF_SIGNED);
		stp.getSignatureAppearance().setVisibleSignature(signatureField);//"studentSig"
		if (reason != null)
			sap.setReason(reason);
		if (location != null) 
			sap.setLocation(location);
		sap.setCertificationLevel(PdfSignatureAppearance.CERTIFIED_NO_CHANGES_ALLOWED);
		log.debug("SIGN it DONE");
	}
}
