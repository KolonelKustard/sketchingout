/*
 * Created on 15-Feb-2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.totalchange.consequences;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Ralph Jones
 *
 * Provides the necessary code to parse an XML stream using the
 * consequences protocol to a BufferdImage instance
 */
public class ImageParser extends DefaultHandler{
	
	private BufferedImage bufferedImage;
	private Graphics2D graphics2d;
	
	private double offsetX, offsetY, nextOffsetX, nextOffsetY = 0.0d;
	private double posX, posY = 0.0d;
	private Line2D line = new Line2D.Double();
	
	private boolean lineStart;

	/**
	 * Constructor makes the base rendered image
	 */
	public ImageParser(int width, int height) {
		// Create image
		bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		// Get drawing canvas for image
		graphics2d = bufferedImage.createGraphics();
		graphics2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON));
			
		// Set the background to white
		graphics2d.setBackground(Color.WHITE);
		
		// Set the line colours to black
		graphics2d.setColor(Color.BLACK);
		
		// Clear the background of the current image
		graphics2d.clearRect(0, 0, width, height);
	}
	
	private void moveTo(double x, double y) {
		// Set the current position
		posX = x;
		posY = y;
	}
	
	private void lineTo(double x, double y) {
		// Draw a line from courrent pos to new pos
		line.setLine(posX, posY, x, y);
		graphics2d.draw(line);
		
		// Set current pos to where we've drawn to
		posX = x;
		posY = y;
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
			double x = Double.valueOf(attributes.getValue(XMLConsts.AT_DRAWING_POINT_X)).doubleValue();
			double y = Double.valueOf(attributes.getValue(XMLConsts.AT_DRAWING_POINT_Y)).doubleValue();
			
			// If drawing a point, decide what to do depending on if this is following
			// new line
			if (lineStart) {
				// Start of a line, move the pen for first point
				moveTo(offsetX + x, offsetY + y);
				lineStart = false;
			}
			else {
				// Mid-line, draw to the point
				lineTo(offsetX + x, offsetY + y);
			}
		}
		else if (qName.equals(XMLConsts.EL_DRAWING_LINE)) {
			// If get a new line, just mark that at the start of a line
			lineStart = true;
		}
		else if (qName.equals(XMLConsts.EL_DRAWING_CANVAS)) {
			// If at the start of a canvas, set the offsets to be set at the end of this
			// canvas...
			nextOffsetX = 0.0d; // (not yet used - not sure if will ever need to)
			
			// Set the next offset to the current offset + the height of this image - the
			// offsety value (thats the bit at the bottom that overlaps with the next
			// stage)
			nextOffsetY = offsetY +
				Double.valueOf(attributes.getValue(XMLConsts.AT_DRAWING_CANVAS_HEIGHT)).doubleValue() -
				Double.valueOf(attributes.getValue(XMLConsts.AT_DRAWING_CANVAS_OFFSET_Y)).doubleValue();
		}
	}
	
	/**
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void endElement(String uri, String localName, String qName)
		throws SAXException {
		
		// If at the end of a canvas, need to set the offsets
		if (qName.equals(XMLConsts.EL_DRAWING_CANVAS)) {
			offsetX = nextOffsetX;
			offsetY = nextOffsetY;
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
	
	public RenderedImage getRenderedImage() {
		return bufferedImage;
	}
	
	public static void main(String[] args) throws Exception {
		ImageParser parser = new ImageParser(200, 400); 
		parser.addStage(new java.io.FileInputStream("C:\\tdev\\consequences\\xml\\drawing.xml"));
		parser.addStage(new java.io.FileInputStream("C:\\tdev\\consequences\\xml\\drawing.xml"));
		parser.addStage(new java.io.FileInputStream("C:\\tdev\\consequences\\xml\\drawing.xml"));
		parser.addStage(new java.io.FileInputStream("C:\\tdev\\consequences\\xml\\drawing.xml"));
		
		RenderedImage image = parser.getRenderedImage();
		javax.imageio.ImageIO.write(image, "png", new java.io.File("C:\\test.png"));
	}
}
