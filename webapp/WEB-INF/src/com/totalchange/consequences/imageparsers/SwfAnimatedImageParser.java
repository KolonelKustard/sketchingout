/*
 * Created on 01-Jul-2004
 */
package com.totalchange.consequences.imageparsers;

import java.io.IOException;
import java.io.OutputStream;

import com.anotherbigidea.flash.SWFConstants;
import com.anotherbigidea.flash.interfaces.SWFActions;
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
public class SwfAnimatedImageParser implements ConsequencesImageParser {
	
	private static final int SWF_FPS = 120;
	
	private SWFTagTypes swf;
	private int drawID;
	
	private int offsetX, offsetY = 0;
	private int lastPosX, lastPosY = 0;

	/**
	 * @see com.totalchange.consequences.imageparsers.ConsequencesImageParser#startImage(int, int, java.io.OutputStream)
	 */
	public void startImage(int width, int height, OutputStream out)
		throws ConsequencesImageParserException {
		
		// Init
		drawID = 0;
		
		// Make SWF Movie
		SWFWriter writer = new SWFWriter(out);
		swf = new TagWriter(writer);
		
		try {
			// Set header info
			swf.header(5, -1, width * SWFConstants.TWIPS, height * SWFConstants.TWIPS,
				SWF_FPS, -1);
			
			// Set background colour
			swf.tagSetBackgroundColor(new Color(255,255,255));
		}
		catch (IOException ie) {
			throw new ConsequencesImageParserException(ie);
		}
	}

	/**
	 * @see com.totalchange.consequences.imageparsers.ConsequencesImageParser#endImage()
	 */
	public void endImage() throws ConsequencesImageParserException {
		try{
			// Send a stop action to the current frame
			SWFActions actions = swf.tagDoAction();
			actions.start(0);
			actions.stop();
			actions.end();
			actions.done();
			
			// Show this action in a new frame
			swf.tagShowFrame();
			
			// Send the end of the movie
			swf.tagEnd();
		}
		catch (IOException ie) {
			throw new ConsequencesImageParserException(ie);
		}
	}

	/**
	 * @see com.totalchange.consequences.imageparsers.ConsequencesImageParser#startCanvas(int, int, int, int)
	 */
	public void startCanvas(int x, int y, int width, int height)
		throws ConsequencesImageParserException {
		
		// Set the offsets
		offsetX = (int)(x * SWFConstants.TWIPS);
		offsetY = (int)(y * SWFConstants.TWIPS);
	}

	/**
	 * @see com.totalchange.consequences.imageparsers.ConsequencesImageParser#endCanvas()
	 */
	public void endCanvas() throws ConsequencesImageParserException {
	}

	/**
	 * @see com.totalchange.consequences.imageparsers.ConsequencesImageParser#moveTo(double, double)
	 */
	public void moveTo(double x, double y)
		throws ConsequencesImageParserException {
		
		// Set current position
		lastPosX = (int)(x * SWFConstants.TWIPS);
		lastPosY = (int)(y * SWFConstants.TWIPS);
	}

	/**
	 * <p>This procedure does all the work of drawing a line as a new shape in a new frame of
	 * the swf movie</p>
	 * 
	 * @see com.totalchange.consequences.imageparsers.ConsequencesImageParser#lineTo(double, double)
	 */
	public void lineTo(double x, double y)
		throws ConsequencesImageParserException {
			
		try {
			// Get new absolute position in twips
			int newPosX = (int)(x * SWFConstants.TWIPS);
			int newPosY = (int)(y * SWFConstants.TWIPS);
			
			// The size of the rect for this shape is the rect made by the origin position
			// and the destination position
			Rect drawRect = new Rect(0, 0, newPosX - lastPosX, newPosY - lastPosY);
			
			// Increment drawing id
			drawID++;
			
			// Define a new shape
			SWFShape draw = swf.tagDefineShape(drawID, drawRect);
			
			// Setup the shape (just a single line)
			draw.defineLineStyle(1 * SWFConstants.TWIPS, new Color(0, 0, 0));
			draw.setLineStyle(1);
			
			// Draw line to new relative point
			draw.line(newPosX - lastPosX, newPosY - lastPosY);
			
			// Finished line
			draw.done();
			
			// Place shape at end of last line and offset according to current canvas
			Matrix matrix = new Matrix(lastPosX + offsetX, lastPosY + offsetY);
			swf.tagPlaceObject2(false, -1, drawID, drawID, matrix, null, -1, null, 0);
			
			// Show this frame
			swf.tagShowFrame();
			
			// Set last known absolute position to where we are now
			lastPosX = newPosX;
			lastPosY = newPosY;
		}
		catch (IOException ie) {
			throw new ConsequencesImageParserException(ie);
		}
	}

}
