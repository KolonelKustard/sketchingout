/*
 * Created on 13-Jun-2004
 *
 */
package com.totalchange.consequences;

/**
 * @author RalphJones
 *
 * Configuration settings
 * 
 */
public class ConsequencesSettings {
	/**
	 * The number of seconds to lock a drawing for once it has been requested.  This
	 * only applies to publically retrieved drawings.
	 * 
	 * 3600 = 60 * 60 (1 hour)
	 */
	public static final int DEFAULT_LOCK_SECS = 3600;
	
	/**
	 * The number of seconds to lock a drawing for when it is being passed onto another
	 * user (i.e. it is a private drawing).
	 * 
	 * 86400 = 24 * 60 * 60 (24 hours)
	 */
	public static final int PRIVATE_LOCK_SECS = 86400;
	
	/**
	 * This number indicates the maximum number of stages a drawing goes through before
	 * it's determined to be complete.
	 * 
	 * Currently set to 4.  This means 4 stages: "Head", "Body", "Legs", "Feet".
	 */
	public static final int MAX_NUM_STAGES = 4;
	
	/**
	 * The SMTP server address to use when sending emails
	 */
	public static final String SMTP_SERVER_ADDR = "localhost";
	
	/**
	 * The SMTP server port to use when sending emails
	 */
	public static final String SMTP_SERVER_PORT = "25";
	
	/**
	 * The email From name
	 */
	public static final String EMAIL_FROM_NAME = "Consequences";
	
	/**
	 * The email From address
	 */
	public static final String EMAIL_FROM_EMAIL = "consequences@slagtard.cum";
	
	/**
	 * The root URL to access Consequences
	 */
	public static final String URL_ROOT = "http://localhost:8080/consequences/";
	
	/**
	 * The full URL to access the main drawing page
	 */
	public static final String URL_DRAWING = URL_ROOT + "consequences.jsp";
	
	/**
	 * The user id parameter to be sent to Flash
	 */
	public static final String URL_PARAM_USER_ID = "uid";
	
	/**
	 * The drawing id parameter to be sent to Flash and also to be used by the next
	 * drawing email sent out in a private drawing transaction.  As in gets put in
	 * the GET query string.
	 */
	public static final String URL_PARAM_DRAWING_ID = "did";
	
	/**
	 * When a drawing is requested back, this is the width of the image that will be
	 * returned.
	 */
	public static final int IMG_DEFAULT_WIDTH = 200;
	
	/**
	 * When a drawing is requested back, this is the height of the image that will be
	 * returned.
	 */
	public static final int IMG_DEFAULT_HEIGHT = 400;
}