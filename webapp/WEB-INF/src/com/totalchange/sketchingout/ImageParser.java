/*
 * Created on 15-Feb-2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.totalchange.sketchingout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.totalchange.sketchingout.imageparsers.SketchingoutImageParser;
import com.totalchange.sketchingout.imageparsers.SketchingoutImageParserException;

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
	private int loss;
	private int currPoint;
	private SketchingoutImageParser parser;
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
	public ImageParser(int version, int width, int height, int scale, int loss, 
		OutputStream out, SketchingoutImageParser parser)
		throws SketchingoutImageParserException {
		
		// Set the scaling factor and the number of points to skip
		this.scale = scale;
		this.loss = loss;
		
		// Set this parser in
		this.parser = parser;
		
		// Forward on the start of the image to the parser
		parser.startImage(scaleIt(width), scaleIt(height), out);
		
		// Setup SAX XML Parser
		try { 
			saxParser = SAXParserFactory.newInstance().newSAXParser();
		}
		catch (SAXException se) {
			throw new SketchingoutImageParserException(se);
		}
		catch (ParserConfigurationException pe) {
			throw new SketchingoutImageParserException(pe);
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
					currPoint = 0;
				}
				catch (SketchingoutImageParserException cipe) {
					throw new SAXException(cipe);
				}
				
				lineStart = false;
			}
			else {
				try {
					// Mid-line, draw to the point (only if reached skip points value)
					if (currPoint >= loss) {
						parser.lineTo(scaleIt(x), scaleIt(y));
						currPoint = 0;
					}
					else currPoint++;
				}
				catch (SketchingoutImageParserException cipe) {
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
			catch (SketchingoutImageParserException cipe) {
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
			catch (SketchingoutImageParserException cipe) {
				throw new SAXException(cipe);
			}
		}
	}
	
	public void addStage(InputStream xmlData) throws SketchingoutImageParserException {
		currType = TYPE_DRAWING;
		
		try {
			saxParser.parse(xmlData, this);
		}
		catch (Exception e) {
			throw new SketchingoutImageParserException(e);
		}
	}
	
	public void addStage(Reader xmlData) throws SketchingoutImageParserException {
		currType = TYPE_DRAWING;

		try {
			saxParser.parse(new InputSource(xmlData), this);
		}
		catch (Exception e) {
			throw new SketchingoutImageParserException(e);
		}
	}
	
	public void addSignature(Reader xmlData) throws SketchingoutImageParserException {
		currType = TYPE_SIGNATURE;

		try {
			saxParser.parse(new InputSource(xmlData), this);
		}
		catch (Exception e) {
			throw new SketchingoutImageParserException(e);
		}
	}
	
	public void close() throws SketchingoutImageParserException {
		parser.endImage();
	}
	
	/**
	 * <p>A utility function to parse a standard resultset using an image
	 * parser to an output stream</p>
	 * 
	 * @param res
	 * @param scale
	 * @param loss
	 * @param out
	 * @param parser
	 */
	public static void parseResultSet(ResultSet res, int scale, int loss, 
			OutputStream out, SketchingoutImageParser parser) throws
			SketchingoutImageParserException, SQLException {
		
		// Make an image parser
		ImageParser imageParser = new ImageParser(
			res.getInt("version"),
			res.getInt("width"),
			res.getInt("height"),
			scale,
			loss,
			out, 
			parser
		);
			
		// Find the number of stages
		int numStages = res.getInt("stage");
		
		// Run through the stages adding them to the parser
		for (int num = 0; num < numStages; num++) {
			try {
				imageParser.addStage(res.getCharacterStream("stage_" + (num + 1)));
			}
			catch (Exception e) {
				// If have an exception just print it to the console
				e.printStackTrace();
			}
		}
		
		// Run through the signatures adding them to the parser
		for (int num = 0; num < numStages; num++) {
			try {
				imageParser.addSignature(res.getCharacterStream("stage_" + 
					(num + 1) + "_signature"));
			}
			catch (Exception e) {
				// Again just print exception to the stage
				e.printStackTrace();
			}
		}
		
		// Close parser
		imageParser.close();
	}
	
	public static void main(String[] args) throws Exception {
		final int P_WIDTH = 0;
		final int P_HEIGHT = 1;
		final int P_SCALE = 2;
		final int P_LOSS = 3;
		final int P_INFILE = 4;
		final int P_OUTFILE = 5;
		
		if (args.length != 6) {
			System.out.println("Usage: [width] [height] [scale] [loss] [infile.xml] [outfile.swf]");
			return;
		}
		
		// Also allows directories to be used.  If the infile parameter is a directory, run
		// through all .xml files in the directory.  Otherwise use a single file.
		File inFile = new File(args[P_INFILE]);
		if (inFile.isDirectory()) {
			// Make sure the outfile is a directory too
			File outFile = new File(args[P_OUTFILE]);
			if ((outFile.exists()) && (!outFile.isDirectory())) throw new IOException("outfile must specify a " +
				"directory");
			
			// If doesn't exist, make the directory
			if (!outFile.exists()) outFile.mkdir();
			
			// Get and run through all files in the directory
			File[] files = inFile.listFiles();
			for (int num = 0; num < files.length; num++) {
				// Only work on .xml files
				if (files[num].getName().endsWith(".xml")) {
					// Convert this file to one in the outFile directory
					String fileName = files[num].getName().substring(0, files[num].getName().length() - 4);
					
					// Get the default properties
					String width = args[P_WIDTH];
					String height = args[P_HEIGHT];
					String scale = args[P_SCALE];
					String loss = args[P_LOSS];
					
					// Try and find an associated properties file to override defaults
					File propsFile = new File(inFile, fileName + ".cfg");
					if (propsFile.exists()) {
						Properties props = new Properties();
						props.load(new FileInputStream(propsFile));
						
						width = props.getProperty("width", width);
						height = props.getProperty("height", height);
						scale = props.getProperty("scale", scale);
						loss = props.getProperty("loss", loss);
					}
					
					OutputStream out = new FileOutputStream(new File(outFile, fileName + ".swf")); 
					ImageParser parse = new ImageParser(SketchingoutSettings.PRESENT_DRAWING_VERSION,
							Integer.parseInt(width), Integer.parseInt(height),
							Integer.parseInt(scale), Integer.parseInt(loss), out,
							new com.totalchange.sketchingout.imageparsers.SwfAnimatedImageParser()
					);
					
					parse.addStage(new FileReader(files[num]));
					parse.close();
					out.close();
				}
			}
		}
		else {
			OutputStream out = new FileOutputStream(args[5]); 
			ImageParser parse = new ImageParser(SketchingoutSettings.PRESENT_DRAWING_VERSION,
					Integer.parseInt(args[P_WIDTH]), Integer.parseInt(args[P_HEIGHT]),
					Integer.parseInt(args[P_SCALE]), Integer.parseInt(args[P_LOSS]), out,
					new com.totalchange.sketchingout.imageparsers.SwfAnimatedImageParser()
			);
			
			parse.addStage(new FileReader(inFile));
			parse.close();
			out.close();
		}
	}
}
