/*
 * Created on 12-May-2004
 */
package com.totalchange.consequences;

import java.io.OutputStream;

import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Ralph Jones
 *
 * This is the request handler that parses the xml formatted request from the
 * client and sets up the appropriate response.
 */
public class XMLHandler extends DefaultHandler {
	private OutputStream response;
	
	public XMLHandler(OutputStream response) {
		this.response = response;
	}
}
