/*
 * Created on 01-Jul-2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.totalchange.sketchingout.imageparsers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author RalphJones
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class BufferedImageParser implements SketchingoutImageParser {
	
	private String basicType;
	private OutputStream out;
	
	private BufferedImage bufferedImage;
	private Graphics2D graphics2d;
	
	private double offsetX, offsetY = 0.0d;
	private double posX, posY = 0.0d;
	private Line2D line = new Line2D.Double();
	
	public BufferedImageParser(String basicType) {
		this.basicType = basicType;
	}

	/**
	 * @see com.totalchange.sketchingout.imageparsers.SketchingoutImageParser#startImage(int, int, java.io.OutputStream)
	 */
	public void startImage(int width, int height, OutputStream out)
		throws SketchingoutImageParserException {
			
		this.out = out;
			
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

	/**
	 * @see com.totalchange.sketchingout.imageparsers.SketchingoutImageParser#endImage()
	 */
	public void endImage() throws SketchingoutImageParserException {
		// Write the image according to the basic type asked for
		try {
			javax.imageio.ImageIO.write(bufferedImage, basicType, out);
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
		
		// Set the offsets
		offsetX = x;
		offsetY = y;
	}

	/**
	 * @see com.totalchange.sketchingout.imageparsers.SketchingoutImageParser#endCanvas()
	 */
	public void endCanvas() throws SketchingoutImageParserException {
	}

	/**
	 * @see com.totalchange.sketchingout.imageparsers.SketchingoutImageParser#moveTo(double, double)
	 */
	public void moveTo(double x, double y)
		throws SketchingoutImageParserException {
		
		// Set the current position
		posX = x;
		posY = y;
	}

	/**
	 * @see com.totalchange.sketchingout.imageparsers.SketchingoutImageParser#lineTo(double, double)
	 */
	public void lineTo(double x, double y, int penWidth, Color color)
		throws SketchingoutImageParserException {
			
		// Draw a line from current pos to new pos
		line.setLine(posX + offsetX, posY + offsetY, x + offsetX, y + offsetY);
		
		graphics2d.setColor(color);
		graphics2d.draw(line);
		
		// Set current pos to where we've drawn to
		posX = x;
		posY = y;
	}

}
