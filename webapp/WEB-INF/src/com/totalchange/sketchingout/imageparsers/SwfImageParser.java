/*
 * Created on 01-Jul-2004
 */
package com.totalchange.sketchingout.imageparsers;

import java.io.IOException;
import java.io.OutputStream;

import com.anotherbigidea.flash.SWFConstants;
import com.anotherbigidea.flash.interfaces.SWFShape;
import com.anotherbigidea.flash.interfaces.SWFTagTypes;
import com.anotherbigidea.flash.structs.Color;
import com.anotherbigidea.flash.structs.Matrix;
import com.anotherbigidea.flash.structs.Rect;
import com.anotherbigidea.flash.writers.SWFWriter;
import com.anotherbigidea.flash.writers.TagWriter;

/**
 * @author RalphJones
 * 
 * <p>This implementation of the image parser interface outputs a drawing to an
 * .swf format file (Macromedia's Flash format).</p>
 */
public class SwfImageParser implements ConsequencesImageParser {
	
	private SWFTagTypes swf;
	private SWFShape canvas;
	private Matrix canvasPos;
	private int canvasID;
	
	private int lastPosX, lastPosY = 0;

	/**
	 * @see com.totalchange.sketchingout.imageparsers.ConsequencesImageParser#startImage(int, int, java.io.OutputStream)
	 */
	public void startImage(int width, int height, OutputStream out)
		throws ConsequencesImageParserException {
		
		// Init
		canvasID = 0;
		
		// Make SWF Movie
		SWFWriter writer = new SWFWriter(out);
		swf = new TagWriter(writer);
		
		try {
			// Set header info
			swf.header(5, -1, width * SWFConstants.TWIPS, height * SWFConstants.TWIPS,
				12,	-1);
			
			// Set background colour
			swf.tagSetBackgroundColor(new Color(255,255,255));
		}
		catch (IOException ie) {
			throw new ConsequencesImageParserException(ie);
		}
	}

	/**
	 * @see com.totalchange.sketchingout.imageparsers.ConsequencesImageParser#endImage()
	 */
	public void endImage() throws ConsequencesImageParserException {
		try{
			// Show the first frame
			swf.tagShowFrame();
			
			// Send the end of the movie
			swf.tagEnd();
		}
		catch (IOException ie) {
			throw new ConsequencesImageParserException(ie);
		}
	}

	/**
	 * @see com.totalchange.sketchingout.imageparsers.ConsequencesImageParser#startCanvas(int, int, int, int)
	 */
	public void startCanvas(int x, int y, int width, int height)
		throws ConsequencesImageParserException {
		
		try {
			// Start a new canvas
			Rect canvasRect = new Rect(0, 0, width * SWFConstants.TWIPS, height * SWFConstants.TWIPS);
			canvas = swf.tagDefineShape(++canvasID, canvasRect);
			
			// Define line style
			canvas.defineLineStyle(1 * SWFConstants.TWIPS, new Color(0, 0, 0));
			canvas.setLineStyle(1);
			
			// Define matrix to say where this canvas will go
			canvasPos = new Matrix(x * SWFConstants.TWIPS, y * SWFConstants.TWIPS);
		}
		catch (IOException ie) {
			throw new ConsequencesImageParserException(ie);
		}
	}

	/**
	 * @see com.totalchange.sketchingout.imageparsers.ConsequencesImageParser#endCanvas()
	 */
	public void endCanvas() throws ConsequencesImageParserException {
		try {
			// Draw the canvas to the movie
			canvas.done();
			swf.tagPlaceObject2(false, -1, 1, canvasID, canvasPos, null, -1, null, 0);
		}
		catch (IOException ie) {
			throw new ConsequencesImageParserException(ie);
		}
	}

	/**
	 * @see com.totalchange.sketchingout.imageparsers.ConsequencesImageParser#moveTo(double, double)
	 */
	public void moveTo(double x, double y)
		throws ConsequencesImageParserException {
		
		try {
			// Set current position
			lastPosX = (int)(x * SWFConstants.TWIPS);
			lastPosY = (int)(y * SWFConstants.TWIPS);
			
			// Move to absolute position in this canvas
			canvas.move(lastPosX, lastPosY);
		}
		catch (IOException ie) {
			throw new ConsequencesImageParserException(ie);
		}
	}

	/**
	 * @see com.totalchange.sketchingout.imageparsers.ConsequencesImageParser#lineTo(double, double)
	 */
	public void lineTo(double x, double y)
		throws ConsequencesImageParserException {
			
		try {
			// Get new absolute position in twips
			int newPosX = (int)(x * SWFConstants.TWIPS);
			int newPosY = (int)(y * SWFConstants.TWIPS);
			
			// Move to new relative position
			canvas.line(newPosX - lastPosX, newPosY - lastPosY);
			
			// Set last known absolute position to where we are now
			lastPosX = newPosX;
			lastPosY = newPosY;
		}
		catch (IOException ie) {
			throw new ConsequencesImageParserException(ie);
		}
	}

}
