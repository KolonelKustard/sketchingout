/*
 * Created on 21-Oct-2004
 */
package com.totalchange.sketchingout.test;

import java.util.logging.Logger;

/**
 * @author RalphJones
 */
public class TestStats {
	private static Logger logger = Logger.getLogger("com.totalchange.consequences.test.TestStats");
	
	public TestStats() {
		logger.info("Created TestStats instance");
	}
	
	public static void startEvent(Object obj, String function) {
		logger.finest(Thread.currentThread().getName() + "." + obj.getClass().getName() + " started function " + function);
	}
	
	public static void failEvent(Object obj, String function, Throwable exception) {
		logger.finest(Thread.currentThread().getName() + "." + obj.getClass().getName() + " FAILED function " + function + ".  ERROR: " + exception.getMessage());
		exception.printStackTrace();
	}
	
	public static void endEvent(Object obj, String function) {
		logger.finest(Thread.currentThread().getName() + "." + obj.getClass().getName() + " ended function " + function);
	}
}
