/*
 * Created on 15-Feb-2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.totalchange.consequences;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.totalchange.consequences.imageparsers.ConsequencesImageParser;
import com.totalchange.consequences.imageparsers.ConsequencesImageParserException;

/**
 * @author Ralph Jones
 *
 * Provides the necessary code to parse an XML stream using the
 * consequences protocol to a BufferdImage instance
 */
public class ImageParser extends DefaultHandler{
	
	private static final int TYPE_DRAWING = 1;
	private static final int TYPE_SIGNATURE = 2;
	
	private int scale;
	private ConsequencesImageParser parser;
	private SAXParser saxParser;
	
	private int currType = -1;
	private boolean lineStart;
	private int offsetX, offsetY, nextOffsetX, nextOffsetY = 0;
	private boolean sigOdd = true;
	
	private int scaleIt(int num) {
		return (num * scale) / 100;
	}
	
	private double scaleIt(double num) {
		return (num * scale) / 100;
	}
	
	/**
	 * Constructor makes the base rendered image
	 */
	public ImageParser(int version, int width, int height, int scale, OutputStream out,
		ConsequencesImageParser parser) throws ConsequencesImageParserException {
		
		// Set the scaling factor
		this.scale = scale;
		
		// Set this parser in
		this.parser = parser;
		
		// Forward on the start of the image to the parser
		parser.startImage(scaleIt(width), scaleIt(height), out);
		
		// Setup SAX XML Parser
		try { 
			saxParser = SAXParserFactory.newInstance().newSAXParser();
		}
		catch (SAXException se) {
			throw new ConsequencesImageParserException(se);
		}
		catch (ParserConfigurationException pe) {
			throw new ConsequencesImageParserException(pe);
		}
	}
	
	/**
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(
		String uri,
		String localName,
		String qName,
		Attributes attributes)
		throws SAXException {
		
		if (qName.equals(XMLConsts.EL_DRAWING_POINT)) {
			// Get the x and y coords of the point
			double x = Double.parseDouble(attributes.getValue(XMLConsts.AT_DRAWING_POINT_X));
			double y = Double.parseDouble(attributes.getValue(XMLConsts.AT_DRAWING_POINT_Y));
			
			// If drawing a point, decide what to do depending on if this is following
			// new line
			if (lineStart) {
				try {
					// Start of a line, move the pen for first point
					parser.moveTo(scaleIt(x), scaleIt(y));
				}
				catch (ConsequencesImageParserException cipe) {
					throw new SAXException(cipe);
				}
				
				lineStart = false;
			}
			else {
				try {
					// Mid-line, draw to the point
					parser.lineTo(scaleIt(x), scaleIt(y));
				}
				catch (ConsequencesImageParserException cipe) {
					throw new SAXException(cipe);
				}
			}
		}
		else if (qName.equals(XMLConsts.EL_DRAWING_LINE)) {
			// If get a new line, just mark that at the start of a line
			lineStart = true;
		}
		else if (qName.equals(XMLConsts.EL_DRAWING_CANVAS)) {
			// Will need to figure out the positions based on whether this is a drawing
			// or a signature.
			if (currType == TYPE_DRAWING) {
				// Figure out the current offset based on this canvas
				nextOffsetX = 0;
				nextOffsetY = offsetY + 
					Integer.parseInt(attributes.getValue(XMLConsts.AT_DRAWING_CANVAS_HEIGHT)) -
					Integer.parseInt(attributes.getValue(XMLConsts.AT_DRAWING_CANVAS_OFFSET_Y));
			}
			else if (currType == TYPE_SIGNATURE) {
				// Signatures are tiled side by side.  So if on an odd numbered
				// signature it is on the left.
				if (sigOdd) {
					// Is on the left so next will be on the same height but on the right
					nextOffsetX = Integer.parseInt(attributes.getValue(XMLConsts.AT_DRAWING_CANVAS_WIDTH));
					nextOffsetY = offsetY;					
				}
				else {
					// Is on the right, so next offset will be below on the left
					nextOffsetX = 0;
					nextOffsetY = offsetY + 
						Integer.parseInt(attributes.getValue(XMLConsts.AT_DRAWING_CANVAS_HEIGHT));
				}
				
				// Invert the signature so the next one is in the next position. 
				sigOdd = !sigOdd;
			}
			
			try {
				parser.startCanvas(
					scaleIt(offsetX),
					scaleIt(offsetY),
					scaleIt(Integer.parseInt(attributes.getValue(XMLConsts.AT_DRAWING_CANVAS_WIDTH))),
					scaleIt(Integer.parseInt(attributes.getValue(XMLConsts.AT_DRAWING_CANVAS_HEIGHT)))
				);
			}
			catch (ConsequencesImageParserException cipe) {
				throw new SAXException(cipe);
			}
		}
	}
	
	/**
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void endElement(String uri, String localName, String qName)
		throws SAXException {
		
		// If at the end of a canvas, need to set the offsets
		if (qName.equals(XMLConsts.EL_DRAWING_CANVAS)) {
			try {
				// Set current offset to next offset
				offsetX = nextOffsetX;
				offsetY = nextOffsetY;
				
				parser.endCanvas();
			}
			catch (ConsequencesImageParserException cipe) {
				throw new SAXException(cipe);
			}
		}
	}
	
	public void addStage(InputStream xmlData) throws ConsequencesImageParserException {
		currType = TYPE_DRAWING;
		
		try {
			saxParser.parse(xmlData, this);
		}
		catch (Exception e) {
			throw new ConsequencesImageParserException(e);
		}
	}
	
	public void addStage(Reader xmlData) throws ConsequencesImageParserException {
		currType = TYPE_DRAWING;

		try {
			saxParser.parse(new InputSource(xmlData), this);
		}
		catch (Exception e) {
			throw new ConsequencesImageParserException(e);
		}
	}
	
	public void addSignature(Reader xmlData) throws ConsequencesImageParserException {
		currType = TYPE_SIGNATURE;

		try {
			saxParser.parse(new InputSource(xmlData), this);
		}
		catch (Exception e) {
			throw new ConsequencesImageParserException(e);
		}
	}
	
	public void close() throws ConsequencesImageParserException {
		parser.endImage();
	}
	
	public static void main(String[] args) throws Exception {
		if (args.length < 4) {
			System.out.println("Usage: [width] [height] [scale] [infile.xml] [outfile.swf]");
			return;
		}
		
		OutputStream out = new FileOutputStream(args[4]); 
		ImageParser parse = new ImageParser(ConsequencesSettings.PRESENT_DRAWING_VERSION,
				Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), out,				
				new com.totalchange.consequences.imageparsers.SwfAnimatedImageParser()
		);
		
		parse.addStage(new FileReader(args[3]));
		parse.close();
		out.close();
	}
}
