/*
 * Created on 17-Jun-2004
 */
package com.totalchange.consequences;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * @author RalphJones
 */
public class PrivateDrawingProcessor {
	public static final void sendDrawingOn(String fromName, String fromEmail,
		String toEmail, String distinguishedID) throws MessagingException,
		UnsupportedEncodingException {
			
		// Setup SMTP properties
		Properties props = System.getProperties();
		props.put("mail.smtp.host", ConsequencesSettings.SMTP_SERVER_ADDR);
		
		// Get SMTP session
		Session session = Session.getDefaultInstance(props);
		
		// Setup a new mail message
		MimeMessage msg = new MimeMessage(session);
		
		// Who from
		msg.setFrom(new InternetAddress(ConsequencesSettings.EMAIL_FROM_EMAIL,
			ConsequencesSettings.EMAIL_FROM_NAME));
			
		// Who to
		msg.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
		
		// Set subject and message body
		msg.setSubject("You're being hassled by Consequences");
		msg.setText(fromName + " <" + fromEmail + ">" + " thinks you'll want to " +
			"draw something.  Click this link to do some drawing: " +
			ConsequencesSettings.URL_DRAWING + "?" +
			ConsequencesSettings.URL_PARAM_DRAWING_ID + "=" + distinguishedID);
		
		// Send the message
		Transport.send(msg);
	}
}
