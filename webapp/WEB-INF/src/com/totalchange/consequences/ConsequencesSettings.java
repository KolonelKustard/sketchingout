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
	public static final int SMTP_SERVER_PORT = 21;
}
