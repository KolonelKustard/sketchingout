/*
 * Created on 03-Jul-2004
 */
package com.totalchange.consequences;

import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author RalphJones
 *
 * <p>This class has a drawing passed through it and makes sure that only the offset
 * part of the drawing is returned.  This prevents a client having to deal with
 * covering over or drawing back a whole image.  Instead it just has to draw back the
 * bit this sends back.</p>
 */
public class DrawingReducer extends DefaultHandler {
	
	class CurrentLine {
		// Not used yet but will hold brush size and colour and stuff.
	}
	
	class DrawingPoint {
		int x;
		int y;
	}
	
	private XMLWriter out;
	private int offsetX, offsetY;
	private CurrentLine currLine = new CurrentLine();
	private DrawingPoint lastPoint = new DrawingPoint();
	
	public DrawingReducer(Writer out) {
		this.out = new XMLWriter(out);
	}
	
	/**
	 * <p>Decides if a point ona line is relevant.  If it is then it gets drawn.</p>
	 * 
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(
		String uri,
		String localName,
		String qName,
		Attributes attributes)
		throws SAXException {
			
		// See what type of element this is
		if (qName.equals(XMLConsts.EL_DRAWING_CANVAS)) {
			// Take the width, height, offsetx and offsety to use to decide how much of
			// the drawing to allow through
			int canvasW = Integer.parseInt(attributes.getValue(XMLConsts.AT_DRAWING_CANVAS_WIDTH));
			int canvasH = Integer.parseInt(attributes.getValue(XMLConsts.AT_DRAWING_CANVAS_HEIGHT));
			int canvasX = Integer.parseInt(attributes.getValue(XMLConsts.AT_DRAWING_CANVAS_OFFSET_X));
			int canvasY = Integer.parseInt(attributes.getValue(XMLConsts.AT_DRAWING_CANVAS_OFFSET_Y));
			
			// The offsets are the height/width minus the offsetx/offsety
			offsetX = canvasW - canvasX;
			offsetY = canvasH - canvasY;
			
			// Regenerate the canvas to the client to say it's only the height of the
			// offset y value but the same width, and with no offset at all.
			AttributesImpl attr = new AttributesImpl();
			attr.addAttribute(null, null, XMLConsts.AT_DRAWING_CANVAS_WIDTH, null, String.valueOf(canvasW));
			attr.addAttribute(null, null, XMLConsts.AT_DRAWING_CANVAS_HEIGHT, null, String.valueOf(canvasY));
			attr.addAttribute(null, null, XMLConsts.AT_DRAWING_CANVAS_OFFSET_X, null, "0");
			attr.addAttribute(null, null, XMLConsts.AT_DRAWING_CANVAS_OFFSET_Y, null, "0");
			
			try {
				out.startElement(qName, attr);
			}
			catch (IOException ie) {
				throw new SAXException(ie);
			}
		}
	}
	
	/**
	 * <p>Passes through the closing of an element if it should be drawn</p>
	 * 
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void endElement(String uri, String localName, String qName)
		throws SAXException {
	
		// Depends on type
		if (qName.equals(XMLConsts.EL_DRAWING_CANVAS)) {
			// Just output the end canvas for now.
			try {
				out.endElement(qName);
			}
			catch (IOException ie) {
				throw new SAXException(ie);
			}
		}
	}

	/**
	 * <p>This method is just convenience for passing an input stream of the drawing
	 * straight through to an XMLWriter instance.</p>
	 * 
	 * @param in
	 * @param out
	 */
	public static void passThrough(Reader in, Writer out) throws 
		ParserConfigurationException, IOException, SAXException {
			
		// Make a new instance of the pass through handler
		DrawingReducer reducer = new DrawingReducer(out);
		
		// Setup SAX XML Parser
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		
		// Pass through
		parser.parse(new InputSource(in), reducer);
	}
	
	/**
	 * <p>Tests with an xml text file passed through the first parameter</p>
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		OutputStreamWriter out = new OutputStreamWriter(System.out);
		passThrough(new FileReader(args[0]), out);
		out.close();
	}
}
