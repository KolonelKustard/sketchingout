/*
 * Created on 21-Oct-2004
 */
package com.totalchange.sketchingout.test;

import java.util.ArrayList;

/**
 * @author RalphJones
 */
public class SketchingoutTest {
	private ArrayList clients;
	
	private void go() {
		for (int num = 0; num < TestSettings.NUM_CLIENTS; num++) {
			DummyClient client = new DummyClient();
			clients.add(client);
			
			client.start();
		}
	}
	
	private void stop() {
		for (int num = 0; num < clients.size(); num++) {
			DummyClient client = (DummyClient) clients.get(num);
			client.interrupt();
		}
	}
	
	public SketchingoutTest() {
		clients = new ArrayList();
	}

	public static void main(String[] args) throws Exception {
		System.out.println("Creating clients...");
		SketchingoutTest tester = new SketchingoutTest();
		tester.go();
		
		System.out.println("Running...  Type 'q' then press return to end.");
		while (System.in.read() != 'q');
		
		System.out.println("Terminating clients...");
		tester.stop();
		
		System.out.println("Done");
	}
}
