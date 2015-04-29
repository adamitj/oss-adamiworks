/*
 * Copyright (c) Ian F. Darwin, http://www.darwinsys.com/, 1996-2002.
 * All rights reserved. Software written by Ian F. Darwin and others.
 * $Id: LICENSE,v 1.8 2004/02/09 03:33:38 ian Exp $
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * Java, the Duke mascot, and all variants of Sun's Java "steaming coffee
 * cup" logo are trademarks of Sun Microsystems. Sun's, and James Gosling's,
 * pioneering role in inventing and promulgating (and standardizing) the Java 
 * language and environment is gratefully acknowledged.
 * 
 * The pioneering role of Dennis Ritchie and Bjarne Stroustrup, of AT&T, for
 * inventing predecessor languages C and C++ is also gratefully acknowledged.
 */
package com.adamiworks.utils;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * sender -- send an email message. If you give more than one file, each file
 * will be sent to the same recipient with the same subject, so you generally
 * don't want to.
 * 
 * @author Ian F. Darwin
 * @version $Id: Sender2.java,v 1.8 2004/03/20 20:52:35 ian Exp $
 */
public class EmailUtils {

	/** The message recipient. */
	protected String message_recip;

	/** What's it all about, Alfie? */
	protected String message_subject;

	/** The message CC recipient. */
	protected String message_cc;

	/** The message body */
	protected String message_body;

	/** The JavaMail session object */
	protected Session session;

	/** The JavaMail message object */
	protected Message mesg;

	/** Properties object used to pass props into the MAIL API */
	Properties props = new Properties();

	/** Construct a Sender2 object */
	public EmailUtils() throws MessagingException {
		// Your LAN must define the local SMTP as "mailhost"
		// for this simple-minded version to be able to send mail...
		props.put("mail.smtp.host", "mailhost");
		finish();
	}

	/**
	 * Construct a Sender2 object.
	 * 
	 * @param hostName
	 *            - the name of the host to send to/via.
	 */
	public EmailUtils(String hostName) throws MessagingException {
		props.put("mail.smtp.host", hostName);
		finish();
	}

	private void finish() {
		// Create the Session object
		session = Session.getDefaultInstance(props, null);
		// session.setDebug(true); // Verbose!

		// create a message
		mesg = new MimeMessage(session);
	}

	public void sendFile(String fileName) throws MessagingException {
		// Now the message body.
		setBody(message_body);
		send();
	}

	/**
	 * Send the message
	 */
	public void send() {
		try {
			// Finally, send the message! (use static Transport method)
			Transport.send(mesg);
		} catch (MessagingException ex) {
			while ((ex = (MessagingException) ex.getNextException()) != null) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Stub for providing help on usage You can write a longer help than this,
	 * certainly.
	 */
	protected static void usage(int returnValue) {
		System.err.println("Usage: Sender2 [-t to][-c cc][-f from][-s subj] file ...");
		System.exit(returnValue);
	}

	public void addRecipient(String message_recip) throws MessagingException {
		// TO Address
		InternetAddress toAddress = new InternetAddress(message_recip);
		mesg.addRecipient(Message.RecipientType.TO, toAddress);
	}

	public void addCCRecipient(String message_cc) throws MessagingException {
		// CC Address
		InternetAddress ccAddress = new InternetAddress(message_cc);
		mesg.addRecipient(Message.RecipientType.CC, ccAddress);
	}

	public void setFrom(String sender) throws MessagingException {
		// From Address - this should come from a Properties...
		mesg.setFrom(new InternetAddress(sender));
	}

	public void setSubject(String message_subject) throws MessagingException {
		// The Subject
		mesg.setSubject(message_subject);
	}

	/** Set the message body. */
	public void setBody(String message_body) throws MessagingException {
		mesg.setText(message_body);
		/* I18N: use setText(msgText.getText(), charset) */
	}

	
}
