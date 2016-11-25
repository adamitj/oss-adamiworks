package com.adamiworks.utils.mailsender;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.mail.Message;

import org.simplejavamail.email.Email;

import com.adamiworks.utils.FileUtils;

public class MailSenderMessage {
	private String senderName;
	private String senderEmail;
	private List<String> to;
	private List<String> cc;
	private List<String> bcc;
	private String subject;
	private String body;
	private List<String> attachedFiles;
	private boolean bodyHtml = false;
	private Email email;

	public MailSenderMessage(String senderName, String senderEmail) {
		super();
		this.senderEmail = senderEmail;
		this.senderName = senderName;
		email = new Email();
		email.setFromAddress(senderName, senderEmail);
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		email.setSubject(subject);
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public boolean isBodyHtml() {
		return bodyHtml;
	}

	/**
	 * Points that the body is HTML formatted.
	 * 
	 * @param bodyHtml
	 */
	public void setBodyHtml(boolean bodyHtml) {
		this.bodyHtml = bodyHtml;
	}

	/**
	 * The message body.
	 * 
	 * @param body
	 */
	public void setBody(String body) {
		email.setText(body);
		this.body = body;
	}

	/**
	 * The message body. You can set a HTML content here.
	 * 
	 * @param body
	 */
	public void setHtmlBody(String body) {
		email.setTextHTML(body);
		this.body = body;
		this.bodyHtml = true;
	}

	public void addTo(String to) {
		if (this.to == null) {
			this.to = new ArrayList<String>();
		}
		this.to.add(to);

		if (to != null) {
			email.addRecipient(to, to, Message.RecipientType.TO);
		}
	}

	public void addCc(String cc) {
		if (this.cc == null) {
			this.cc = new ArrayList<String>();
		}
		this.cc.add(cc);

		if (cc != null) {
			email.addRecipient(cc, cc, Message.RecipientType.CC);
		}
	}

	public void addBcc(String bcc) {
		if (this.bcc == null) {
			this.bcc = new ArrayList<String>();
		}
		this.bcc.add(bcc);

		if (bcc != null) {
			email.addRecipient(bcc, bcc, Message.RecipientType.BCC);
		}
	}

	public void addAttachFile(File file) {
		if (this.attachedFiles == null) {
			this.attachedFiles = new ArrayList<String>();
		}
		this.attachedFiles.add(file.getAbsolutePath());

		byte[] fileBytes = FileUtils.readFile(file);
		email.addAttachment(file.getName(), fileBytes, "application/octet-stream");
	}

	public void addAttachFile(String fullFileNamePath) {
		File f = new File(fullFileNamePath);
		this.addAttachFile(f);
	}

	public List<String> getAttachedFiles() {
		return attachedFiles;
	}

	public List<String> getTo() {
		return to;
	}

	public List<String> getCc() {
		return cc;
	}

	public List<String> getBcc() {
		return bcc;
	}

	public Email getEmail() {
		return email;
	}

	public String getSenderName() {
		return senderName;
	}

	public String getSenderEmail() {
		return senderEmail;
	}

}
