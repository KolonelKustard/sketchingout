/*
 * Created on 21-Oct-2004
 */
package com.totalchange.sketchingout.test;

import java.util.logging.Logger;

/**
 * @author RalphJones
 */
public class DummyClient extends Thread {
	private static Logger logger = Logger.getLogger("com.totalchange.consequences.test.DummyClient");
	private static int staticClientNum = 0;

	private TestStats stats;
	private int clientNum;
	
	private ClientDetails clientDetails;
	
	private long lastChangedIdentity;

	/**
	 * <p>Called when sleeping this thread.  Changes the sleep time by variation.</p>
	 *
	 * @param millis Time to sleep for
	 * @param variation Percentage variation allowed
	 */
	private long sleepy(long millis, int variation) {
		double var = (variation / 100) * millis;
		var -= var / 2;

		return millis + (long) var;
	}
	
	/**
	 * Changes identity by connecting and asking for user details based on
	 * a fictitious ID.
	 */
	private void changeIdentity() {
		
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			lastChangedIdentity = 0;
			
			while (true) {
				if ((System.currentTimeMillis() - lastChangedIdentity) > 
						TestSettings.KEEP_IDENTITY_FOR_MILLIS) {
					
					changeIdentity();
					lastChangedIdentity = System.currentTimeMillis();
				}

				sleep(sleepy(TestSettings.SLEEP_MILLIS_DRAWING,
					TestSettings.SLEEP_VARIATION_DRAWING));
			}
		}
		catch (InterruptedException ie) {
			logger.info("Client interrupted: " + ie.getMessage());
		}
	}

	public DummyClient(TestStats stats) {
		this.stats = stats;

		clientNum = staticClientNum++;
		logger.info("Created DummyClient instance: " + clientNum);
	}
}
