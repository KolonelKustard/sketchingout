/*
 * Created on 08-Dec-2004
 */
package com.totalchange.sketchingout;

/**
 * @author RalphJones
 *
 * <p>Contains an array of emails</p>
 */
public class SketchingoutEmails {
	public static final String SUBST_NAME = ":name:";
	public static final String SUBST_EMAIL = ":email:";
	public static final String SUBST_BODY_PART = ":body_part:";
	public static final String SUBST_A_OR_SOME = ":a_or_some:";
	public static final String SUBST_THOSE_ARE_OR_THAT_IS = ":those_are_or_that_is:";
	public static final String SUBST_URL = ":url:";
	
	public static final int EMAILS_FROM_NAME = 0;
	public static final int EMAILS_FROM_EMAIL = 1;
	public static final int EMAILS_SUBJECT = 2;
	public static final int EMAILS_BODY = 3;
	public static final String[][] EMAILS = {
		{
			"Bob Geldof",
			"bob.geldof@sketchingout.co.uk",
			"Some email subject",
			"Hi " + SUBST_NAME + ",\n\n" +
			"Please find attached your sketching out picture.  The starving children of Africa love it more than stale bread and rancid water.\n\n" +
			"Kind regards,\n\n" +
			"Bob"
		}, 
		{
			"Test Person",
			"test@sketchingout.co.uk",
			"Test Subject",
			"Test Body"
		},
		{
			"Test Person 2",
			"test2@sketchingout.co.uk",
			"Test Subject to " + SUBST_NAME + " for drawing " + SUBST_BODY_PART,
			"Test multiple: " + SUBST_NAME + ", " + SUBST_NAME + ", " + SUBST_NAME + "\n" +
			"URL to get back to SketchingOut: " + SUBST_URL
		}
	}; 
}
