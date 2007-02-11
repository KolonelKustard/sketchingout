/*
 * Created on 20-Jun-2004
 */
package com.totalchange.sketchingout;

import java.util.Properties;

import javax.mail.Session;

/**
 * @author RalphJones
 *
 * <p>Simple factory class to create an SMTP session for sending messages</p>
 *
 */
public class SMTPSessionFactory {
	public static final Session getSMTPSession() {
		// Setup SMTP properties
		Properties props = System.getProperties();
		props.put("mail.smtp.host", SketchingoutSettings.SMTP_SERVER_ADDR);
		props.put("mail.smtp.port", SketchingoutSettings.SMTP_SERVER_PORT);
		
		// Get SMTP session
		return Session.getDefaultInstance(props);
	}
}
