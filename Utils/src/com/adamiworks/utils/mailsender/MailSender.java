package com.adamiworks.utils.mailsender;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Class representing an email SMTP server. This class does send e-mails too.
 * 
 * @author Tiago
 *
 */
public class MailSender {
	public static final String MAIL_SMTP_SOCKETFACTORY_CLASSNAME = "javax.net.ssl.SSLSocketFactory";
	//
	public static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
	public static final String MAIL_SMTP_HOST = "mail.smtp.host";
	public static final String MAIL_SMTP_PORT = "mail.smtp.port";
	public static final String MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
	public static final String MAIL_SMTP_SOCKETFACTORY_CLASS = "mail.smtp.socketFactory.class";
	public static final String MAIL_SMTP_CONNECTIONTIMEOUT = "mail.smtp.connectiontimeout";
	public static final String MAIL_SMTP_TIMEOUT = "mail.smtp.timeout";
	public static final String MAIL_SMTP_WRITETIMEOUT = "mail.smtp.writetimeout";
	public static final String MAIL_SOCKET_TIMEOUT = "15000";
	//
	private boolean authenticatonRequired;
	private boolean startTLSRequired;
	private boolean ssl;
	private boolean plainTextOverTLS;
	private String host;
	private int port;
	//
	private String from;
	private String userName;
	private String password;
	private List<MailSenderMessage> listMailSenderMessage;

	//
	// Objetos de controle
	private MailcapCommandMap mc;
	private Session session;

	public MailSender(boolean authenticatonRequired, String host, int port, boolean ssl, boolean startTLSRequired, boolean plainTextOverTLS, String from,
			String userName, String password) {
		super();
		this.authenticatonRequired = authenticatonRequired;
		this.host = host;
		this.port = port;
		this.ssl = ssl;
		this.startTLSRequired = startTLSRequired;
		this.from = from;
		this.userName = userName;
		this.password = password;
		this.plainTextOverTLS = plainTextOverTLS;

		// Inicializa sessão
		Properties props = new Properties();

		props.put(MAIL_SMTP_AUTH, String.valueOf(authenticatonRequired));
		props.put(MAIL_SMTP_HOST, host);
		props.put(MAIL_SMTP_PORT, String.valueOf(port));
		props.put(MAIL_SMTP_STARTTLS_ENABLE, String.valueOf(startTLSRequired));
		props.put(MAIL_SMTP_CONNECTIONTIMEOUT, MAIL_SOCKET_TIMEOUT);
		props.put(MAIL_SMTP_TIMEOUT, MAIL_SOCKET_TIMEOUT);
		props.put(MAIL_SMTP_WRITETIMEOUT, MAIL_SOCKET_TIMEOUT);

		if (plainTextOverTLS) {
			props.put(MAIL_SMTP_SOCKETFACTORY_CLASS, MAIL_SMTP_SOCKETFACTORY_CLASSNAME);
		}

		session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(userName, password);
			}
		});

		mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
		mc.addMailcap("multipart/*; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
		mc.addMailcap("message/rfc822; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
	}

	public MailSender() {
		super();
	}

	/**
	 * Add a new message to be sent
	 * 
	 * @param mailSenderMessage
	 */
	public void addMessage(MailSenderMessage mailSenderMessage) {
		if (this.listMailSenderMessage == null) {
			this.listMailSenderMessage = new ArrayList<MailSenderMessage>();
		}

		this.listMailSenderMessage.add(mailSenderMessage);
	}

	public void sendMessages() throws MessagingException {
		for (MailSenderMessage m : listMailSenderMessage) {
			this.sendMessage(m);
		}
	}

	/**
	 * Sends a message to recipients.
	 * 
	 * @param mailSenderMessage
	 * @throws MessagingException
	 */
	private void sendMessage(MailSenderMessage mailSenderMessage) throws MessagingException {
		// Only cache DNS lookups for 10 seconds
		java.security.Security.setProperty("networkaddress.cache.ttl", "10");

		Message message = new MimeMessage(session);

		message.setFrom(new InternetAddress(from));

		// TO address
		if (mailSenderMessage.getTo() != null && mailSenderMessage.getTo().size() > 0) {
			for (String to : mailSenderMessage.getTo()) {
				if (to != null) {
					message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
				}
			}
		}

		// CC address
		if (mailSenderMessage.getCc() != null && mailSenderMessage.getCc().size() > 0) {
			for (String cc : mailSenderMessage.getCc()) {
				if (cc != null) {
					message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc));
				}
			}
		}

		// BCC address
		if (mailSenderMessage.getBcc() != null && mailSenderMessage.getBcc().size() > 0) {
			for (String bcc : mailSenderMessage.getBcc()) {
				if (bcc != null) {
					message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc));
				}
			}
		}

		// Adds attached files
		if (mailSenderMessage.getAttachedFiles() != null && mailSenderMessage.getAttachedFiles().size() > 0) {
			for (String filename : mailSenderMessage.getAttachedFiles()) {
				BodyPart messageBodyPart = new MimeBodyPart();
				Multipart multipart = new MimeMultipart();
				messageBodyPart = new MimeBodyPart();
				multipart.addBodyPart(messageBodyPart);

				DataSource source = new FileDataSource(filename);
				messageBodyPart.setDataHandler(new DataHandler(source));
				messageBodyPart.setFileName(filename);

				multipart.addBodyPart(messageBodyPart);

				message.setContent(multipart);
			}
		}

		message.setSubject(mailSenderMessage.getSubject());

		if (mailSenderMessage.isBodyHtml()) {
			mc.addMailcap("text/html; x-java-content-handler=com.sun.mail.handlers.text_html");
			mc.addMailcap("text/xml; x-java-content-handler=com.sun.mail.handlers.text_xml");
			message.setContent(mailSenderMessage.getBody(), "text/html; charset=utf-8");
		} else {
			mc.addMailcap("text/plain; x-java-content-handler=com.sun.mail.handlers.text_plain");
			message.setText(mailSenderMessage.getBody());
		}

		CommandMap.setDefaultCommandMap(mc);

		Transport.send(message);
	}

	public boolean isAuthenticatonRequired() {
		return authenticatonRequired;
	}

	public void setAuthenticatonRequired(boolean authenticatonRequired) {
		this.authenticatonRequired = authenticatonRequired;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isStartTLSRequired() {
		return startTLSRequired;
	}

	public void setStartTLSRequired(boolean startTLSRequired) {
		this.startTLSRequired = startTLSRequired;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public boolean isPlainTextOverTLS() {
		return plainTextOverTLS;
	}

	public void setPlainTextOverTLS(boolean plainTextOverTLS) {
		this.plainTextOverTLS = plainTextOverTLS;
	}

	public boolean isSsl() {
		return ssl;
	}

	public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}

}
