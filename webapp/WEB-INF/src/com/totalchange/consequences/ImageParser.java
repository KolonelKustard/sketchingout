/*
 * Created on 15-Feb-2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.totalchange.consequences;

import java.io.IOException;
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
	
	private ConsequencesImageParser parser;
	
	private boolean lineStart;
	private int offsetX, offsetY, nextOffsetX, nextOffsetY = 0;

	/**
	 * Constructor makes the base rendered image
	 */
	public ImageParser(int width, int height, OutputStream out,
		ConsequencesImageParser parser) throws ConsequencesImageParserException {
		
		// Set this parser in
		this.parser = parser;
		
		// Forward on the start of the image to the parser
		parser.startImage(width, height, out);
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
					parser.moveTo(x, y);
				}
				catch (ConsequencesImageParserException cipe) {
					throw new SAXException(cipe);
				}
				
				lineStart = false;
			}
			else {
				try {
					// Mid-line, draw to the point
					parser.lineTo(x, y);
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
			// Figure out the current offset based on this canvas
			nextOffsetX = 0;
			nextOffsetY = offsetY + 
				Integer.parseInt(attributes.getValue(XMLConsts.AT_DRAWING_CANVAS_HEIGHT)) -
				Integer.parseInt(attributes.getValue(XMLConsts.AT_DRAWING_CANVAS_OFFSET_Y));
			
			try {
				parser.startCanvas(
					offsetX,
					offsetY,
					Integer.parseInt(attributes.getValue(XMLConsts.AT_DRAWING_CANVAS_WIDTH)),
					Integer.parseInt(attributes.getValue(XMLConsts.AT_DRAWING_CANVAS_HEIGHT))
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
	
	public void addStage(InputStream xmlData) throws ParserConfigurationException,
		SAXException, IOException {
			
		// Setup SAX XML Parser
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		
		parser.parse(xmlData, this);
	}
	
	public void addStage(Reader xmlData) throws ParserConfigurationException,
		SAXException, IOException {
			
		// Setup SAX XML Parser
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		
		parser.parse(new InputSource(xmlData), this);
	}
	
	public void close() throws ConsequencesImageParserException {
		parser.endImage();
	}
	
	public static void main(String[] args) throws Exception {
		//ImageParser parser = new ImageParser(200, 400); 
		//parser.addStage(new java.io.FileInputStream("C:\\tdev\\consequences\\xml\\drawing.xml"));
		//parser.addStage(new java.io.FileInputStream("C:\\tdev\\consequences\\xml\\drawing.xml"));
		//parser.addStage(new java.io.FileInputStream("C:\\tdev\\consequences\\xml\\drawing.xml"));
		//parser.addStage(new java.io.FileInputStream("C:\\tdev\\consequences\\xml\\drawing.xml"));
		
		//RenderedImage image = parser.getRenderedImage();
		//javax.imageio.ImageIO.write(image, "png", new java.io.File("C:\\test.png"));
	}
}
