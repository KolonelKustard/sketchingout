/*
 * Created on 13-Jun-2004
 *
 */
package com.totalchange.sketchingout;

/**
 * @author RalphJones
 *
 * Configuration settings
 *
 */
public class SketchingoutSettings {
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
	 * This number is the version that is appended to a drawing.  The point of this is
	 * that if the layout is changed at some point in the future then previous drawings
	 * need not be screwed up.
	 */
	public static final int PRESENT_DRAWING_VERSION = 1;

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
	public static final String EMAIL_FROM_NAME = "Sketching Out";

	/**
	 * The email From address
	 */
	public static final String EMAIL_FROM_EMAIL = "sketcher@sketchingout.co.uk";
	
	/**
	 * Where to send a copy of all finished drawings to
	 */
	public static final String COMPLETE_DRAWING_TO_EMAIL = "sketchingout@totalchange.com";

	/**
	 * The root URL to access Sketching Out
	 */
	public static final String URL_ROOT = "http://localhost:8080/sketchingout/";
	
	/**
	 * The file system root to access Sketching Out
	 */
	public static final String FS_ROOT = "";

	/**
	 * The full URL to access the main drawing page
	 */
	public static final String URL_DRAWING = URL_ROOT + "index.jsp";

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
	 * Where on the file system to find the truetype SketchingOut font
	 */
	public static final String SKETCHING_OUT_FONT = FS_ROOT + "DeannasHand.ttf";
	
	/**
	 * The location on the file system in which to store completed drawings
	 */
	public static final String FS_DRAWING_STORE = FS_ROOT + "drawings/";
	
	/**
	 * The url in which completed drawings can be found
	 */
	public static final String URL_DRAWING_STORE = URL_ROOT + FS_DRAWING_STORE;
}