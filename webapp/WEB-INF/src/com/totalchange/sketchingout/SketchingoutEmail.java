/*
 * Created on 14-Jan-2005
 */
package com.totalchange.sketchingout;

import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * @author RalphJones
 */
public class SketchingoutEmail {
	private int emailNum;
	
	private String toName, toEmail;
	private int stage;
	
	private void substStr(StringBuffer buff, String orig, String subst) {
		int index = buff.indexOf(orig, 0);
		while(index > -1) {
			buff.replace(index, index + orig.length(), subst);
			index = buff.indexOf(orig, index);
		}
	}
	
	private String substStrs(String src) {
		StringBuffer dest = new StringBuffer(src);
		
		// Substitute the easy strings
		substStr(dest, SketchingoutEmails.SUBST_NAME, toName);
		substStr(dest, SketchingoutEmails.SUBST_EMAIL, toEmail);
		substStr(dest, SketchingoutEmails.SUBST_URL, SketchingoutSettings.URL_DRAWING);
		
		// Change the stage into a body part
		switch(stage) {
			case 1:
				substStr(dest, SketchingoutEmails.SUBST_BODY_PART, "head");
				substStr(dest, SketchingoutEmails.SUBST_A_OR_SOME, "a");
				substStr(dest, SketchingoutEmails.SUBST_THOSE_ARE_OR_THAT_IS, "that is");
				break;
				
			case 2:
				substStr(dest, SketchingoutEmails.SUBST_BODY_PART, "body");
				substStr(dest, SketchingoutEmails.SUBST_A_OR_SOME, "a");
				substStr(dest, SketchingoutEmails.SUBST_THOSE_ARE_OR_THAT_IS, "that is");
				break;
				
			case 3:
				substStr(dest, SketchingoutEmails.SUBST_BODY_PART, "legs");
				substStr(dest, SketchingoutEmails.SUBST_A_OR_SOME, "some");
				substStr(dest, SketchingoutEmails.SUBST_THOSE_ARE_OR_THAT_IS, "those are");
				break;
				
			case 4:
				substStr(dest, SketchingoutEmails.SUBST_BODY_PART, "feet");
				substStr(dest, SketchingoutEmails.SUBST_A_OR_SOME, "some");
				substStr(dest, SketchingoutEmails.SUBST_THOSE_ARE_OR_THAT_IS, "those are");
				break;
		}
		
		return dest.toString();
	}
	
	public SketchingoutEmail(int emailNum, String toName, String toEmail, int stage) {
		this.emailNum = emailNum; 
		this.toName = toName;
		this.toEmail = toEmail;
		this.stage = stage;
	}
	
	public SketchingoutEmail(String toName, String toEmail, int stage) {
		this(new Random().nextInt(SketchingoutEmails.EMAILS.length), toName,
				toEmail, stage);
	}
	
	public String getToName() {
		return toName;
	}
	
	public String getToEmail() {
		return toEmail;
	}
	
	public int getStage() {
		return stage;
	}
	
	public String getFromName() {
		return SketchingoutEmails.EMAILS[emailNum][SketchingoutEmails.EMAILS_FROM_NAME];
	}
	
	public String getFromEmail() {
		return SketchingoutEmails.EMAILS[emailNum][SketchingoutEmails.EMAILS_FROM_EMAIL];
	}
	
	public String getSubject() {
		return substStrs(
				SketchingoutEmails.EMAILS[emailNum][SketchingoutEmails.EMAILS_SUBJECT]);
	}
	
	/**
	 * <p>Parses the body and performs basic text substitution</p>
	 * 
	 * @return The body text of the email
	 */
	public String getBody() {
		return substStrs(
				SketchingoutEmails.EMAILS[emailNum][SketchingoutEmails.EMAILS_BODY]);
	}

	/**
	 * Tests all the random emails
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String smtpHost = SketchingoutSettings.SMTP_SERVER_ADDR;
		String smtpPort = SketchingoutSettings.SMTP_SERVER_PORT;
		String toName = "Test Person";
		String toEmail = "test@tester.com";
		
		if (args.length >= 1) smtpHost = args[0];
		if (args.length >= 2) smtpPort = args[1];
		if (args.length >= 3) toName = args[2];
		if (args.length >= 4) toEmail = args[3];
		
		System.out.println("Sending " + SketchingoutEmails.EMAILS.length +
				" emails to " + smtpHost + ":" + smtpPort);
		
		// Setup SMTP properties
		Properties props = System.getProperties();
		props.put("mail.smtp.host", smtpHost);
		props.put("mail.smtp.port", smtpPort);
		
		// Get SMTP session
		Session session = Session.getDefaultInstance(props);
		
		// Run through each email making and sending
		for (int num = 0; num < SketchingoutEmails.EMAILS.length; num++) {
			SketchingoutEmail email = new SketchingoutEmail(num, toName, toEmail,
					new Random().nextInt(SketchingoutSettings.MAX_NUM_STAGES));
			
			// Make new SMTP email
			Message msg = new MimeMessage(session);
			
			// Who from
			msg.setFrom(new InternetAddress(email.getFromEmail(), 
					email.getFromName()));
			
			// Who to
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail, toName));
			
			// Set subject and body
			msg.setSubject(email.getSubject());
			msg.setText(email.getBody());
			
			// Send!
			Transport.send(msg);
		}
		
		System.out.println("Done");
	}
}