package com.adamiworks.utils.mailsender;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MailSenderMessage {
	private List<String> to;
	private List<String> cc;
	private List<String> bcc;
	private String subject;
	private String body;
	private List<String> attachedFiles;
	private boolean bodyHtml = false;

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
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
		this.body = body;
	}

	/**
	 * The message body. You can set a HTML content here.
	 * 
	 * @param body
	 */
	public void setHtmlBody(String body) {
		this.body = body;
		this.bodyHtml = true;
	}

	public void addTo(String to) {
		if (this.to == null) {
			this.to = new ArrayList<String>();
		}
		this.to.add(to);
	}

	public void addCc(String cc) {
		if (this.cc == null) {
			this.cc = new ArrayList<String>();
		}
		this.cc.add(cc);
	}

	public void addBcc(String bcc) {
		if (this.bcc == null) {
			this.bcc = new ArrayList<String>();
		}
		this.bcc.add(bcc);
	}

	public void addAttachFile(File file) {
		if (this.attachedFiles == null) {
			this.attachedFiles = new ArrayList<String>();
		}
		this.attachedFiles.add(file.getAbsolutePath());
	}

	public void addAttachFile(String fullFileNamePath) {
		if (this.attachedFiles == null) {
			this.attachedFiles = new ArrayList<String>();
		}
		this.attachedFiles.add(fullFileNamePath);
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

}
