/*
 * Created on 08-Feb-2005
 */
package com.totalchange.sketchingout.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.totalchange.sketchingout.XMLWriter;

/**
 * <p>This class should be implemented by all request making classes.  It provides
 * 2 functions.  The first returns an XMLWriter instance that can be used to
 * construct the request as a byte array.  The request function then passes the
 * bytes to the server and returns the DOM formatted response.</p>
 * 
 * @author RalphJones
 */
public abstract class Request {
	private ByteArrayOutputStream bytes = new ByteArrayOutputStream();
	private XMLWriter xml;
	
	public Request() {
		xml = new XMLWriter(new OutputStreamWriter(bytes));
		try {
			xml.startElement("request");
		}
		catch (IOException ie) {}
	}
	
	/**
	 * <p>Gets the XMLWriter instance that is used to construct an in
	 * memory request</p>
	 * 
	 * @return
	 */
	protected XMLWriter getXMLWriter() {
		return xml;
	}
	
	/**
	 * <p>This abstract method should be called once the request has been formed
	 * using the XMLWriter.  It will perform the request, recording stats on the
	 * calling thread and object.  The result is an XML DOM Document that contains
	 * the response from the server which the implementing class can use to
	 * construct the response.</p>
	 * 
	 * @param function The symbolic name of the request/response function
	 * @return The XML result returned by the server in DOM format
	 */
	protected Document request(String function) {
		Document result = null;
		
		TestStats.startEvent(this, function);
		try {
			// Close the request
			xml.endElement("request");
			
			URL url = new URL(TestSettings.CONSEQUENCES_URL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
			try {
				conn.setDoInput(true);
				conn.setDoOutput(true);
				conn.setRequestMethod("POST");
				
				// Write the request to the connection
				OutputStream out = conn.getOutputStream();
				try {
					xml.flush();
					bytes.writeTo(out);
				}
				finally {
					out.close();
				}
				
				// Read in the resulting XML
				InputStream in = conn.getInputStream();
				try {
					result = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
				}
				finally {
					in.close();
				}
			}
			finally {
				conn.disconnect();
			}
		}
		catch (MalformedURLException me) {
			TestStats.failEvent(this, function, me);
		}
		catch (IOException ie) {
			TestStats.failEvent(this, function, ie);
		}
		catch (ParserConfigurationException pce) {
			TestStats.failEvent(this, function, pce);
		}
		catch (SAXException se) {
			TestStats.failEvent(this, function, se);
		}
		
		// End this event (if not already failed)
		TestStats.endEvent(this, function);
		
		return result;
	}
}
