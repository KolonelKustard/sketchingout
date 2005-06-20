/*
 * Created on 14-Jan-2005
 */
package com.totalchange.sketchingout;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * @author RalphJones
 */
public class SketchingoutEmail {
	public static final int EMAILS_ARRAY_FROM_NAME = 0;
	public static final int EMAILS_ARRAY_FROM_EMAIL = 1;
	public static final int EMAILS_ARRAY_SUBJECT = 2;
	public static final int EMAILS_ARRAY_BODY = 3;
	
	private Session session;
	private MimeMessage msg;
	private ArrayList attachments = new ArrayList();
	private String fromName, fromEmail, senderName, senderEmail, toName, toEmail, subject, body;
	private String distinguishedID;
	private int stage;
	private int lockSecs = 0;
	
	private void substStr(StringBuffer buff, String orig, String subst) {
		int index = buff.indexOf(orig, 0);
		while(index > -1) {
			buff.replace(index, index + orig.length(), subst);
			index = buff.indexOf(orig, index);
		}
	}
	
	private String substStrs(String src) {
		StringBuffer dest = new StringBuffer(src);
		
		// Substitute who from (Sender overrides From)
		if (senderName != null) substStr(dest, SketchingoutEmails.SUBST_FROM_NAME, senderName);
		else substStr(dest, SketchingoutEmails.SUBST_FROM_NAME, fromName);
		
		if (senderEmail != null) substStr(dest, SketchingoutEmails.SUBST_FROM_EMAIL, senderEmail);
		else substStr(dest, SketchingoutEmails.SUBST_FROM_EMAIL, fromEmail);
		
		// Substitute who being sent to
		substStr(dest, SketchingoutEmails.SUBST_TO_NAME, toName);
		substStr(dest, SketchingoutEmails.SUBST_TO_EMAIL, toEmail);
		
		// The url depends on if a distinguished ID was sent in
		if (distinguishedID == null) {
			substStr(dest, SketchingoutEmails.SUBST_URL, SketchingoutSettings.URL_DRAWING);
		}
		else {
			substStr(dest, SketchingoutEmails.SUBST_URL,
					SketchingoutSettings.URL_DRAWING + "?" +
					SketchingoutSettings.URL_PARAM_DRAWING_ID + "=" +
					distinguishedID);
		}
		
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
		
		// See if locked timeout has been set
		if (lockSecs != 0) {
			// Calculate hours and mins from the time
			int hours = lockSecs / (60 * 60);
			int mins = (lockSecs / 60) % (hours * 60);
			
			// Make an hours/mins String
			String timeLeft = "";
			if (hours > 0) {
				timeLeft += hours + "hrs";
			}
			if (mins > 0) {
				if (timeLeft.length() > 0) timeLeft += " ";
				timeLeft += mins + "mins";
			}
			
			// Get the locked timeout as a date/time
			long lockedUntil = System.currentTimeMillis() + (lockSecs * 1000);
			
			// Convert the date/time to a String
			DateFormat df = new SimpleDateFormat("dd MMMM yyyy HH:mm");
			String lockedUntilStr = df.format(new Date(lockedUntil));
			
			// Now substitute the text
			substStr(dest, SketchingoutEmails.SUBST_LOCKED_UNTIL,
				lockedUntilStr + " GMT (" + timeLeft + ")");
			
		}
		
		return dest.toString();
	}
	
	/**
	 * 
	 * @param session
	 */
	public SketchingoutEmail(Session session) {
		this.session = session;
		
		// Create message
		msg = new MimeMessage(session);
	}
	
	/**
	 * Create a new email based upon the provided list of random emails
	 * 
	 * @param session
	 * @param emails
	 */
	public SketchingoutEmail(Session session, String[][] emails) {
		this(session);
		
		// Initially configure the message to use random parts
		int emailNum = new Random().nextInt(emails.length);
		
		setFromName(emails[emailNum][EMAILS_ARRAY_FROM_NAME]);
		setFromEmail(emails[emailNum][EMAILS_ARRAY_FROM_EMAIL]);
		setSubject(emails[emailNum][EMAILS_ARRAY_SUBJECT]);
		setBody(emails[emailNum][EMAILS_ARRAY_BODY]);
	}
	
	/**
	 * Sends the email
	 * 
	 * @throws MessagingException
	 */
	public void send() throws UnsupportedEncodingException, MessagingException {
		// Make the multi part to hold the message parts
		Multipart mp = new MimeMultipart();
		BodyPart bp;
		
		// Set who from and to
		msg.setFrom(new InternetAddress(fromEmail, substStrs(fromName)));
		msg.addRecipient(Message.RecipientType.TO,
				new InternetAddress(toEmail, substStrs(toName)));
		
		// Set subject
		msg.setSubject(substStrs(subject));
		
		// Add the body text
		bp = new MimeBodyPart();
		bp.setText(substStrs(body));
		mp.addBodyPart(bp);
		
		// Add the attachments
		int size = attachments.size();
		for (int num = 0; num < size; num++) {
			DataSource attach = (DataSource) attachments.get(num);
			
			bp = new MimeBodyPart();
			bp.setDataHandler(new DataHandler(attach));
			bp.setFileName(attach.getName());
			mp.addBodyPart(bp);
		}
		
		// Put the parts into the message
		msg.setContent(mp);
		
		// Connect and send
		Transport transport = session.getTransport("smtp");
	    transport.connect(SketchingoutSettings.SMTP_SERVER_ADDR, "", "");
	    transport.sendMessage(msg, msg.getAllRecipients());
	    transport.close();
	}
	
	public void addAttachment(DataSource attachment) {
		attachments.add(attachment);
	}
	
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getFromEmail() {
		return fromEmail;
	}
	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;
	}
	public String getFromName() {
		return fromName;
	}
	public void setFromName(String fromName) {
		this.fromName = fromName;
	}
	public String getSenderEmail() {
		return senderEmail;
	}
	public void setSenderEmail(String senderEmail) {
		this.senderEmail = senderEmail;
	}
	public String getSenderName() {
		return senderName;
	}
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	public int getStage() {
		return stage;
	}
	public void setStage(int stage) {
		this.stage = stage;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getToEmail() {
		return toEmail;
	}
	public void setToEmail(String toEmail) {
		this.toEmail = toEmail;
	}
	public String getToName() {
		return toName;
	}
	public void setToName(String toName) {
		this.toName = toName;
	}
	public String getDistinguishedID() {
		return distinguishedID;
	}
	public void setDistinguishedID(String distinguishedID) {
		this.distinguishedID = distinguishedID;
	}
	public int getLockSecs() {
		return lockSecs;
	}
	public void setLockSecs(int lockSecs) {
		this.lockSecs = lockSecs;
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
		
		// Setup SMTP properties
		Properties props = System.getProperties();
		props.put("mail.smtp.host", smtpHost);
		props.put("mail.smtp.port", smtpPort);
		
		// Get SMTP session
		Session session = Session.getDefaultInstance(props);
		
		// Run through each email making and sending
		System.out.println("Sending " + SketchingoutEmails.EMAILS.length +
				" emails to " + smtpHost + ":" + smtpPort);
		
		for (int num = 0; num < SketchingoutEmails.EMAILS.length; num++) {
			SketchingoutEmail email = new SketchingoutEmail(session);
			email.setFromName(SketchingoutEmails.EMAILS[num][EMAILS_ARRAY_FROM_NAME]);
			email.setFromEmail(SketchingoutEmails.EMAILS[num][EMAILS_ARRAY_FROM_EMAIL]);
			email.setSubject(SketchingoutEmails.EMAILS[num][EMAILS_ARRAY_SUBJECT]);
			email.setBody(SketchingoutEmails.EMAILS[num][EMAILS_ARRAY_BODY]);
			email.setToName(toName);
			email.setToEmail(toEmail);
			email.setStage(new Random().nextInt(SketchingoutSettings.MAX_NUM_STAGES + 1));
			email.send();
		}
		
		System.out.println("Sending " + SketchingoutEmails.EMAILS_TO_FRIENDS.length +
				" emails to " + smtpHost + ":" + smtpPort);
		
		for (int num = 0; num < SketchingoutEmails.EMAILS_TO_FRIENDS.length; num++) {
			SketchingoutEmail email = new SketchingoutEmail(session);
			email.setFromName(SketchingoutEmails.EMAILS_TO_FRIENDS[num][EMAILS_ARRAY_FROM_NAME]);
			email.setFromEmail(SketchingoutEmails.EMAILS_TO_FRIENDS[num][EMAILS_ARRAY_FROM_EMAIL]);
			email.setSubject(SketchingoutEmails.EMAILS_TO_FRIENDS[num][EMAILS_ARRAY_SUBJECT]);
			email.setBody(SketchingoutEmails.EMAILS_TO_FRIENDS[num][EMAILS_ARRAY_BODY]);
			email.setSenderName(toName);
			email.setSenderEmail(toEmail);
			email.setToName(toEmail);
			email.setToEmail(toEmail);
			email.setStage(new Random().nextInt(SketchingoutSettings.MAX_NUM_STAGES + 1));
			email.setDistinguishedID(new RandomGUID().toString());
			email.setLockSecs(SketchingoutSettings.PRIVATE_LOCK_SECS);
			email.send();
		}
		
		System.out.println("Done");
	}
}