/*
 * Created on 21-Oct-2004
 */
package com.totalchange.sketchingout.test;

import java.io.IOException;

import org.w3c.dom.Document;

import com.totalchange.sketchingout.XMLWriter;

/**
 * @author RalphJones
 */
public class ClientDetails extends Request {
	private String id;
	private String name;
	private String email;
	private String signature;
	
	public ClientDetails(String id) {
		XMLWriter out = getXMLWriter();
		try {
			out.startElement("user_details");
			out.writeElement("id", id);
			out.endElement("user_details");
		}
		catch (IOException ie) {}
		
		Document doc = request("user_details");
		System.out.println(doc);
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getSignature() {
		return signature;
	}
}
