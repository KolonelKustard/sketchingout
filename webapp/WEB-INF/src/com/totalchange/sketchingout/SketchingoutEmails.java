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
	
	public static final String[][] EMAILS = {
		{
			"Bob Geldof",
			"bob@geldof.com",
			"Some email subject",
			"Hi " + SUBST_NAME + ",\n\n" +
			"Please find attached your sketching out pictures.\n\n" +
			"Kind regards,\n\n" +
			"Bob"
		}, 
		{
			"",
			"",
			"",
			""
		}
	}; 
}
