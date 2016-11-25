package com.adamiworks.utils.mailsender;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.config.ServerConfig;
import org.simplejavamail.mailer.config.TransportStrategy;

/**
 * Class representing an emailMessage SMTP server. This class does send e-mails
 * too.
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
	public static final String MAIL_SMTP_FROM = "mail.smtp.from";
	public static final String MAIL_SMTP_USER = "mail.smtp.user";
	public static final String MAIL_SMTP_PASSWORD = "mail.smtp.password";
	public static final String MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
	public static final String MAIL_SMTP_SSL_REQUIRED = "mail.smtp.ssl.required";
	public static final String MAIL_SMTP_PLAIN_TEXT_OVER_TLS = "mail.smtp.plain.text.over.tls";
	public static final String MAIL_SMTP_SOCKETFACTORY_CLASS = "mail.smtp.socketFactory.class";
	public static final String MAIL_SMTP_CONNECTIONTIMEOUT = "mail.smtp.connectiontimeout";
	public static final String MAIL_SMTP_TIMEOUT = "mail.smtp.timeout";
	public static final String MAIL_SMTP_WRITETIMEOUT = "mail.smtp.writetimeout";
	public static final String MAIL_SOCKET_TIMEOUT = "60000";

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

	public MailSender(boolean authenticatonRequired, String host, int port, boolean ssl, boolean startTLSRequired,
			boolean plainTextOverTLS, String from, String userName, String password) {
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
	}

	public MailSender(Properties props) {
		super();
		this.readProperties(props);
	}

	public void readProperties(Properties props) {
		this.authenticatonRequired = Boolean.valueOf(props.getProperty(MAIL_SMTP_AUTH));
		this.host = props.getProperty(MAIL_SMTP_HOST);
		this.port = Integer.valueOf(props.getProperty(MAIL_SMTP_PORT));
		this.ssl = Boolean.valueOf(props.getProperty(MAIL_SMTP_SSL_REQUIRED));
		this.startTLSRequired = Boolean.valueOf(props.getProperty(MAIL_SMTP_STARTTLS_ENABLE));
		this.from = props.getProperty(MAIL_SMTP_FROM);
		this.userName = props.getProperty(MAIL_SMTP_USER);
		this.password = props.getProperty(MAIL_SMTP_PASSWORD);
		this.plainTextOverTLS = Boolean.valueOf(props.getProperty(MAIL_SMTP_PLAIN_TEXT_OVER_TLS));
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

	public void sendMessages() throws MailSenderException {
		for (MailSenderMessage m : listMailSenderMessage) {
			this.sendMessage(m);
		}
	}

	/**
	 * Sends a message to recipients.
	 * 
	 * @param mailSenderMessage
	 * @throws MailSenderException
	 */
	private void sendMessage(MailSenderMessage mailSenderMessage) throws MailSenderException {
		Mailer mailer;
		ServerConfig sc = new ServerConfig(host, port, from, password);
		if (ssl) {
			mailer = new Mailer(sc, TransportStrategy.SMTP_SSL);
		} else if (startTLSRequired) {
			mailer = new Mailer(sc, TransportStrategy.SMTP_TLS);

		} else {
			mailer = new Mailer(sc, TransportStrategy.SMTP_PLAIN);
		}
		mailer.sendMail(mailSenderMessage.getEmail(), false);
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
