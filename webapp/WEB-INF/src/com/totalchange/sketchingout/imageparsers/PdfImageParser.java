/*
 * Created on 21-Nov-2004
 */
package com.totalchange.sketchingout.imageparsers;

import java.io.OutputStream;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

/**
 * @author RalphJones
 */
public class PdfImageParser implements SketchingoutImageParser {
	
	private static final float LINE_WIDTH = 1;
	
	private Document document;
	private PdfWriter writer;
	private PdfContentByte cb;
	
	private int offsetX, offsetY = 0;

	/**
	 * @see com.totalchange.sketchingout.imageparsers.SketchingoutImageParser#startImage(int, int, java.io.OutputStream)
	 */
	public void startImage(int width, int height, OutputStream out)
			throws SketchingoutImageParserException {
		document = new Document();
		
		try {
			writer = PdfWriter.getInstance(document, out);
			
			document.open();
			cb = writer.getDirectContent();
			
			cb.setLineWidth(LINE_WIDTH);
		}
		catch (DocumentException de) {
			throw new SketchingoutImageParserException(de);
		}
	}

	/**
	 * @see com.totalchange.sketchingout.imageparsers.SketchingoutImageParser#endImage()
	 */
	public void endImage() throws SketchingoutImageParserException {
		cb.stroke();
		document.close();
	}

	/**
	 * @see com.totalchange.sketchingout.imageparsers.SketchingoutImageParser#startCanvas(int, int, int, int)
	 */
	public void startCanvas(int x, int y, int width, int height)
			throws SketchingoutImageParserException {
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
		cb.moveTo((float) x + offsetX, (float) y + offsetY);
	}

	/**
	 * @see com.totalchange.sketchingout.imageparsers.SketchingoutImageParser#lineTo(double, double)
	 */
	public void lineTo(double x, double y)
			throws SketchingoutImageParserException {
		cb.lineTo((float) x + offsetX, (float) y + offsetY);
	}

}
