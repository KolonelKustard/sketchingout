/*
 * Created on 01-Jul-2004
 */
package com.totalchange.sketchingout.imageparsers;

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
public class SwfAnimatedImageParser implements SketchingoutImageParser {
	
	private static final int SWF_FPS = 30;
	private static final int POINTS_PER_FRAME = 10;
	
	private SWFTagTypes swf;
	private SWFShape currLine;
	private Rect currRect;
	private Matrix currMatrix;
	private int lineID;
	private int pointNum;
	
	private int lastPosX, lastPosY = 0;

	/**
	 * @see com.totalchange.sketchingout.imageparsers.SketchingoutImageParser#startImage(int, int, java.io.OutputStream)
	 */
	public void startImage(int width, int height, OutputStream out)
		throws SketchingoutImageParserException {
		
		// Init
		lineID = 0;
		pointNum = 0;
		
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
			throw new SketchingoutImageParserException(ie);
		}
	}

	/**
	 * @see com.totalchange.sketchingout.imageparsers.SketchingoutImageParser#endImage()
	 */
	public void endImage() throws SketchingoutImageParserException {
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
			throw new SketchingoutImageParserException(ie);
		}
	}

	/**
	 * @see com.totalchange.sketchingout.imageparsers.SketchingoutImageParser#startCanvas(int, int, int, int)
	 */
	public void startCanvas(int x, int y, int width, int height)
		throws SketchingoutImageParserException {
		
		// Define the bounds of this canvas
		currRect = new Rect(0, 0, width * SWFConstants.TWIPS, height * SWFConstants.TWIPS);
		currMatrix= new Matrix(x * SWFConstants.TWIPS, y * SWFConstants.TWIPS);
		/*
		try {
			
			
			//canvas = swf.tagDefineShape(++canvasID, canvasRect);
			
			// Define line style
			//canvas.defineLineStyle(1 * SWFConstants.TWIPS, new Color(0, 0, 0));
			//canvas.setLineStyle(1);
			
			// Define matrix to say where this canvas will go
			
		}
		catch (IOException ie) {
			throw new ConsequencesImageParserException(ie);
		}
		*/
	}

	/**
	 * @see com.totalchange.sketchingout.imageparsers.SketchingoutImageParser#endCanvas()
	 */
	public void endCanvas() throws SketchingoutImageParserException {
		/*try {
			// Draw the canvas to the movie
			canvas.done();
			swf.tagPlaceObject2(false, -1, 1, canvasID, canvasPos, null, -1, null, 0);
		}
		catch (IOException ie) {
			throw new ConsequencesImageParserException(ie);
		}*/
	}

	/**
	 * @see com.totalchange.sketchingout.imageparsers.SketchingoutImageParser#moveTo(double, double)
	 */
	public void moveTo(double x, double y)
		throws SketchingoutImageParserException {
		
		try {
			// Draw previous line
			if (currLine != null) {
				currLine.done();
				swf.tagPlaceObject2(false, -1, 1, lineID, currMatrix, null, -1, null, 0);
			}
			
			// Make a new line
			currLine = swf.tagDefineShape(++lineID, currRect);
			
			currLine.defineLineStyle(1 * SWFConstants.TWIPS, new Color(0, 0, 0));
			currLine.setLineStyle(1);
			
			// Set current position
			lastPosX = (int)(x * SWFConstants.TWIPS);
			lastPosY = (int)(y * SWFConstants.TWIPS);
			
			// Move to absolute position in this canvas
			currLine.move(lastPosX, lastPosY);
		}
		catch (IOException ie) {
			throw new SketchingoutImageParserException(ie);
		}
	}

	/**
	 * @see com.totalchange.sketchingout.imageparsers.SketchingoutImageParser#lineTo(double, double)
	 */
	public void lineTo(double x, double y)
		throws SketchingoutImageParserException {
			
		pointNum++;
		
		try {
			// Get new absolute position in twips
			int newPosX = (int)(x * SWFConstants.TWIPS);
			int newPosY = (int)(y * SWFConstants.TWIPS);
			
			// Move to new relative position
			currLine.line(newPosX - lastPosX, newPosY - lastPosY);
			
			// Set last known absolute position to where we are now
			lastPosX = newPosX;
			lastPosY = newPosY;
			
			// See if need to move frames
			if (pointNum >= POINTS_PER_FRAME) {
				// Draw line
				currLine.done();
				swf.tagPlaceObject2(false, -1, 1, lineID, currMatrix, null, -1, null, 0);
				
				// Step frame on
				swf.tagShowFrame();
				
				// Make a new line
				currLine = swf.tagDefineShape(++lineID, currRect);
				
				currLine.defineLineStyle(1 * SWFConstants.TWIPS, new Color(0, 0, 0));
				currLine.setLineStyle(1);
				
				// Move to absolute position in this canvas
				currLine.move(lastPosX, lastPosY);
				
				pointNum = 0;
			}
		}
		catch (IOException ie) {
			throw new SketchingoutImageParserException(ie);
		}
	}

}
