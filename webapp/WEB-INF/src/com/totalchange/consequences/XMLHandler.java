/*
 * Created on 12-May-2004
 */
package com.totalchange.consequences;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Ralph Jones
 *
 * This is the request handler that parses the xml formatted request from the
 * client and sets up the appropriate response.
 */
public class XMLHandler extends DefaultHandler {
	private XMLWriter out;
	
	public XMLHandler(XMLWriter out) {
		this.out = out;
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(
		String uri,
		String localName,
		String qName,
		Attributes attributes)
		throws SAXException {
			
	}
}
