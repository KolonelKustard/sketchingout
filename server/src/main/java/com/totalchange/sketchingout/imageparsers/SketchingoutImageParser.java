/*
 * Created on 01-Jul-2004
 */
package com.totalchange.sketchingout.imageparsers;

import java.awt.Color;
import java.io.OutputStream;

/**
 * @author RalphJones
 */
public interface SketchingoutImageParser {
	public void startImage(int width, int height, OutputStream out)
		throws SketchingoutImageParserException;
	public void endImage() throws SketchingoutImageParserException;
	
	public void startCanvas(int x, int y, int width, int height)
		throws SketchingoutImageParserException;
	public void endCanvas() throws SketchingoutImageParserException;
	
	public void moveTo(double x, double y) throws SketchingoutImageParserException;
	public void lineTo(double x, double y, int penWidth, Color color) throws SketchingoutImageParserException;
}