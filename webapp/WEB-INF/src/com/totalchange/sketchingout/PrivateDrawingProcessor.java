/*
 * Created on 17-Jun-2004
 */
package com.totalchange.sketchingout;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.Session;

/**
 * @author RalphJones
 */
public class PrivateDrawingProcessor {
	public static final void sendDrawingOn(String fromName, String fromEmail,
		String toEmail, int stage, String distinguishedID) throws MessagingException,
		UnsupportedEncodingException {
		
		// Get SMTP Session
		Session session = SMTPSessionFactory.getSMTPSession();
		
		// Create and send message
		SketchingoutEmail email = new SketchingoutEmail(session, SketchingoutEmails.EMAILS_TO_FRIENDS);
		email.setSenderName(fromName);
		email.setSenderEmail(fromEmail);
		email.setToName(toEmail);
		email.setToEmail(toEmail);
		email.setStage(stage);
		email.setDistinguishedID(distinguishedID);
		email.send();
	}
}
