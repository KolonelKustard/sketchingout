/*
 * Created on 01-Jul-2004
 */
package com.totalchange.sketchingout.imageparsers;

import java.io.OutputStream;

/**
 * @author RalphJones
 */
public interface ConsequencesImageParser {
	public void startImage(int width, int height, OutputStream out)
		throws ConsequencesImageParserException;
	public void endImage() throws ConsequencesImageParserException;
	
	public void startCanvas(int x, int y, int width, int height)
		throws ConsequencesImageParserException;
	public void endCanvas() throws ConsequencesImageParserException;
	
	public void moveTo(double x, double y) throws ConsequencesImageParserException;
	public void lineTo(double x, double y) throws ConsequencesImageParserException;
}