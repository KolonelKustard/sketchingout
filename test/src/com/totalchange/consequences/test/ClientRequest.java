/*
 * Created on 21-Oct-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.totalchange.consequences.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author RalphJones
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class ClientRequest {
	
	protected abstract String getRequest();
	protected abstract void setResponse(String response);
	
	/**
	 * <p>Sends and receives a request.</p>
	 * 
	 * @return The number of milliseconds the request took to process
	 */
	public long processRequest() throws IOException {
		long startTime = System.currentTimeMillis();
		
		URL url = new URL(TestSettings.CONSEQUENCES_URL);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
		try {
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			
			OutputStream out = conn.getOutputStream();
			try {
				
			}
			finally {
				out.close();
			}
			
			InputStream in = conn.getInputStream();
			try {
				
			}
			finally {
				in.close();
			}
		}
		finally {
			conn.disconnect();
		}
		
		return System.currentTimeMillis() - startTime;
	}
}