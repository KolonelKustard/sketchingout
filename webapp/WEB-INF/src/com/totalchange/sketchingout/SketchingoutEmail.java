/*
 * Created on 14-Jan-2005
 */
package com.totalchange.sketchingout;

import java.util.Random;

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
	
	public SketchingoutEmail(String toName, String toEmail, int stage) {
		// Pick a random email from the list
		emailNum = new Random().nextInt(SketchingoutEmails.EMAILS.length);
		
		// Store the details of the recipient
		this.toName = toName;
		this.toEmail = toEmail;
		this.stage = stage;
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

	public static void main(String[] args) {
		SketchingoutEmail email = new SketchingoutEmail("Test Recipient", 
				"test@recipient.com", 3);
		
		System.out.println(email.getFromName());
		System.out.println(email.getFromEmail());
		System.out.println(email.getSubject());
		System.out.println(email.getBody());
	}
}
