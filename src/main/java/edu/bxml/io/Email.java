package edu.bxml.io;


import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;
import com.javalobby.tnt.annotation.attribute;

public class Email extends XmlObjectImpl implements XmlObject {
	private static Log log = LogFactory.getLog(Email.class);
	private String host = null;
	private String user = null;
	private String password = null;
	
	private String text = "";

	private String subject = null;
	private String from = null;
	// Add List of Email address to who email needs to be sent to
	private String toList = null;

	
	public String getHost() {
		return host;
	}

	/**
	 * Location of the Email server
	 * @param host
	 */
	@attribute(value = "", required = true)
	public void setHost(String host) {
		this.host = host;
	}

	public String getText() {
		return text;
	}

	/**
	 * Text message in Email body
	 * @param text
	 */
	@attribute(value = "", required = false)
	public void setText(String text) {
		this.text = text;
	}

	public String getSubject() {
		return subject;
	}

	@attribute(value = "", required = false)
	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getFrom() {
		return from;
	}

	/**
	 * email address of sender
	 * @param from
	 */
	@attribute(value = "", required = false, defaultValue="user account used to access host")
	public void setFrom(String from) {
		this.from = from;
	}

	public String getToList() {
		return toList;
	}

	/**
	 * comma separated list of email recipients
	 * @param toList
	 */
	@attribute(value = "", required = true)
	public void setToList(String toList) {
		this.toList = toList;
	}

	public String getFilename() {
		return filename;
	}

	/**
	 * the name of a file attachment
	 * @param filename
	 */
	@attribute(value = "", required = true)
	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getUser() {
		return user;
	}

	private String filename = null;

	public Email() {

	}

	/**
	 * user account to access mail server
	 * @param user
	 */
	@attribute(value = "", required = true)
	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	/**
	 * user account's password
	 * @param password
	 */
	@attribute(value = "", required = false)
	public void setPassword(String password) {
		this.password = password;
	}

	public void postMail() throws MessagingException {
		boolean debug = false;

		if (from == null)
			from = user;
		
		// Set the host smtp address
		Properties props = new Properties();

	    props.put("mail.smtp.starttls.enable", "true"); 
	    props.put("mail.smtp.host", host); 
	    props.put("mail.smtp.user", user); 
	    if (password != null) {
		    props.put("mail.smtp.password", password); 
		    props.put("mail.smtp.auth", "true"); 
	    }
	    props.put("mail.smtp.port", "587"); 
	    

		  Session session = Session.getDefaultInstance(props, null);           
		  
		    // -- Create a new message -- 
		    MimeMessage message = new MimeMessage(session); 
		 
		    message.setFrom(new InternetAddress(from)); 
		    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toList, false)); 
		 
		    MimeMultipart content = new MimeMultipart(); 
		    MimeBodyPart text = new MimeBodyPart(); 
		    //MimeBodyPart html = new MimeBodyPart(); 
		    log.debug("Filename = " + filename);
		    if (filename != null) {

		        MimeBodyPart mbp2 = new MimeBodyPart();
	
		            // attach the file to the message
		         FileDataSource fds = new FileDataSource(filename);
		      mbp2.setDataHandler(new DataHandler(fds));
		      mbp2.setFileName(fds.getName());
		      content.addBodyPart(mbp2);
		    }

		 
		    text.setText(this.text); 
		    text.setHeader("MIME-Version" , "1.0" ); 
		    text.setHeader("Content-Type" , text.getContentType() ); 
		 
//		    html.setContent(htmlBody, "text/html"); 
//		    html.setHeader("MIME-Version" , "1.0" ); 
//		    html.setHeader("Content-Type" , html.getContentType() ); 
		 
		    content.addBodyPart(text); 
		   // content.addBodyPart(html); 
		 
		    message.setContent( content ); 
		    message.setHeader("MIME-Version" , "1.0" ); 
		    message.setHeader("Content-Type" , content.getContentType() ); 
		    message.setHeader("X-Mailer", "My own custom mailer"); 
		    
		    
		 
		    // -- Set the subject -- 
		    message.setSubject(subject); 
		 
		    // -- Set some other header information -- 
		    message.setSentDate(new Date()); 
		 
		    // INFO: only SMTP protocol is supported for now... 
		    Transport transport = session.getTransport("smtp"); 
		    transport.connect(host, user, password); 
		    message.saveChanges(); 
		 
		    // -- Send the message -- 
		    transport.sendMessage(message, message.getAllRecipients()); 
		    transport.close(); 

	}

	/**
	 * SimpleAuthenticator is used to do simple authentication when the SMTP
	 * server requires it.
	 */
	private class SMTPAuthenticator extends javax.mail.Authenticator {
		String username;
		String password;

		public SMTPAuthenticator(String username, String password) {
			super();
			this.username = username;
			this.password = password;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public PasswordAuthentication getPasswordAuthentication() {
			log.debug("username set to " + username);
			log.debug("password set to " + password);
			return new PasswordAuthentication(username, password);
		}
	}

	@Override
	public void check() throws XMLBuildException {
		if (host == null) {
			throw new XMLBuildException("Host must be set", this);
		}
		if (user == null) 
			throw new XMLBuildException("User must be set", this);

		if (toList == null) {
			throw new XMLBuildException("emailList of recipients must be set", this);
		}
	}

	@Override
	public void execute() throws XMLBuildException {
		try {
			postMail();
		} catch (MessagingException em) {
			em.printStackTrace();
			throw new XMLBuildException(em.getMessage(), this);
		}

	}

	@Override
	public void setFromTextContent(String text) throws XMLBuildException {
		this.text = text;
	}

}
