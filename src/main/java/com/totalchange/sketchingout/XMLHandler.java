/*
 * Created on 12-May-2004
 */
package com.totalchange.sketchingout;

import java.util.ArrayList;
import java.sql.Connection;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Ralph Jones
 *
 * This is the request handler that parses the xml formatted request from the
 * client and sets up the appropriate response.
 */
public class XMLHandler extends DefaultHandler {
	private XMLWriter out;
	private SketchingoutErrors errs;
	private Connection conn;
	private ArrayList handlers;
	private RequestHandler curr;
	
	public XMLHandler(XMLWriter out, SketchingoutErrors errs, Connection conn) {
		this.out = out;
		this.errs = errs;
		this.conn = conn;
		
		// Make array list which will be used as a stack for the handlers
		handlers = new ArrayList();
		
		// Make sure current handler is null
		curr = null;
	}
	
	/**
	 * <p>Start of an element asks the current handler for a sub-handler and
	 * that gets added to the stack.  The only special case is that if this
	 * start element is the root element of the request, the default request
	 * handler is instantiated.</p>
	 * 
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(
		String uri,
		String localName,
		String qName,
		Attributes attributes)
		throws SAXException {
		
		// Check to see if this is default request element
		if (qName.equals(XMLConsts.EL_REQUEST)) {
			// Make default handler
			curr = new DefaultRequest();
			
			// Make sure is the first and only element in the stack
			handlers.clear();
			handlers.add(curr);
		}
		else {
			// If have no current then it means there has been no request element
			// and therefore must stop here
			if (curr == null) {
				throw new SAXException("Poorly formatted request.  Make sure have a root " +
				  "element of type " + XMLConsts.EL_REQUEST + ".");
			}
			
			// Not the default element, so ask the current element for sub-handler
			try {
				RequestHandler newHandler = curr.getChild(qName);
				
				// Check got one
				 if (newHandler != null) {
					 // Add to the top of the stack and set to current
					 handlers.add(newHandler);
				
					 // Set to current
					 curr = newHandler;
				 }
			}
			catch (HandlerException e) {
				errs.addException(this.getClass(), e.getErrorCode(), e);
			}
		}
		
		// Check got a current handler and send request on
		if (curr != null) {
			try {
				curr.start(out, conn, errs, attributes);
			}
			catch (HandlerException e) {
				errs.addException(this.getClass(), e.getErrorCode(), e);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	public void characters(char[] ch, int start, int length)
		throws SAXException {
		
		// If got a current handler, send character data onto it
		if (curr != null) {
			try {
				curr.data(ch, start, length);
			}
			catch (HandlerException e) {
				errs.addException(this.getClass(), e.getErrorCode(), e);
			}
		}
	}

	/**
	 * <p>Removes the current element from the top of the stack and sets the
	 * current handler to the new top of the stack<p>
	 * 
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void endElement(String uri, String localName, String qName)
		throws SAXException {
			
		// If have a current, tell it it's ended
		if (curr != null) {
			try {
				curr.end();
			}
			catch (HandlerException e) {
				errs.addException(this.getClass(), e.getErrorCode(), e);
			}
		
			// No longer need current	
			curr = null;
		}
		
		// Now get next
		if (handlers.size() > 0) {
			// Remove top handler
			handlers.remove(handlers.size() - 1);
			if (handlers.size() > 0) {
				// Set current to new top of the stack.
				curr = (RequestHandler) handlers.get(handlers.size() - 1);
			}
		}
	}

	/**
	 * Gets added to error cache
	 * 
	 * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
	 */
	public void error(SAXParseException e) throws SAXException {
		errs.addException(this.getClass(), e);
	}

}
