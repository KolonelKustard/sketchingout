/*
 * Created on 21-Nov-2004
 */
package com.totalchange.sketchingout.imageparsers;

import java.io.IOException;
import java.io.OutputStream;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDestination;
import com.lowagie.text.pdf.PdfWriter;
import com.totalchange.sketchingout.SketchingoutSettings;

/**
 * @author RalphJones
 */
public class PdfImageParser implements SketchingoutImageParser {
	private static final float MARGIN_LEFT = 50;
	private static final float MARGIN_RIGHT = 50;
	private static final float MARGIN_TOP = 80;
	private static final float MARGIN_BOTTOM = 80;
	
	private static final String TITLE_TEXT = "SKETCHING OUT";
	private static final float TITLE_SPACE_FROM_TOP = 60;
	private static final float TITLE_FONT_SIZE = 32;
	
	private static final float URL_SPACE_FROM_BOTTOM = 40;
	private static final float URL_FONT_SIZE = 14;
	
	private static final float LINE_WIDTH = 1;
	
	private Document document;
	private PdfWriter writer;
	private PdfContentByte cb;
	
	private float pageHeight, pageWidth = 0;
	private float insetX, insetY, offsetX, offsetY = 0;
	private float scale = 0;
	
	private void calculateDimensions(float pageWidth, float pageHeight, 
			float drawingWidth, float drawingHeight) {
		
		// Need to keep page height as coords system is bottom to top for Y axis
		// instead of top to bottom
		this.pageHeight = pageHeight;
		this.pageWidth = pageWidth;
		
		// Get page dimensions taking into account margins
		float pWidth = pageWidth - MARGIN_LEFT - MARGIN_RIGHT;
		float pHeight = pageHeight - MARGIN_TOP - MARGIN_BOTTOM;
		
		// Need to scale the drawing to fill the available page.  More often than
		// not the height is the best bet to use.
		scale = pHeight / drawingHeight;
		
		// Check the width doesn't overrun.  If so, need to base scale on
		// the width.
		if ((drawingWidth * scale) > pWidth) scale = pWidth / drawingWidth; 
		
		// Now need to center the drawing by calculating the insets
		insetX = ((pWidth / 2) - ((drawingWidth * scale) / 2)) + MARGIN_LEFT;
		insetY = ((pHeight / 2) - ((drawingHeight * scale) / 2)) + MARGIN_TOP;
	}

	/**
	 * @see com.totalchange.sketchingout.imageparsers.SketchingoutImageParser#startImage(int, int, java.io.OutputStream)
	 */
	public void startImage(int width, int height, OutputStream out)
			throws SketchingoutImageParserException {
		
		document = new Document(PageSize.A4, MARGIN_LEFT, MARGIN_RIGHT, MARGIN_TOP, MARGIN_BOTTOM);
		calculateDimensions(PageSize.A4.width(), PageSize.A4.height(), width, height);
		
		try {
			writer = PdfWriter.getInstance(document, out);
			
			document.addAuthor("Sketching Out");
			document.addSubject("Sketching Out Drawing");
			
			// Embed the handwriting font
			BaseFont baseFont = BaseFont.createFont(SketchingoutSettings.SKETCHING_OUT_FONT, BaseFont.WINANSI, BaseFont.EMBEDDED);
			
			// Open the document and begin direct writing to it
			document.open();
			cb = writer.getDirectContent();
			
			// Define a title for the page
			cb.beginText();
			cb.setFontAndSize(baseFont, TITLE_FONT_SIZE);
			cb.showTextAligned(PdfContentByte.ALIGN_CENTER, TITLE_TEXT, 
					pageWidth / 2, pageHeight - TITLE_SPACE_FROM_TOP, 0);
			cb.endText();
			
			// Define a footer for the page
			cb.beginText();
			cb.setFontAndSize(baseFont, URL_FONT_SIZE);
			cb.showTextAligned(PdfContentByte.ALIGN_CENTER, 
					SketchingoutSettings.URL_ROOT, pageWidth / 2, URL_SPACE_FROM_BOTTOM,
					0);
			cb.endText();
			
			// Put an action over the footer to link to the website
			PdfAction action = PdfAction.gotoLocalPage(1, new
					PdfDestination(PdfDestination.FIT), writer);
			writer.setOpenAction(action);
			
			// Define the line width for the entire drawing
			cb.setLineWidth(LINE_WIDTH);
		}
		catch (DocumentException de) {
			throw new SketchingoutImageParserException(de);
		}
		catch (IOException ie) {
			throw new SketchingoutImageParserException(ie);
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
		
		// This might take some explaining.  It's the same for the line to, so
		// may as well go in depth here.
		// The X axis needs to be the canvas offset + the width, then scaled to
		// fill the available page space.  The inset value is then added after the
		// coord has been scaled.  This is because the inset uses the pages coord
		// environment, and the scaling brings the drawing into the pages coord
		// environment.
		//
		// The Y axis does the same, but it's also inverted.  This is because the
		// PDF pages Y coord runs from bottom to top, but Sketching Outs drawing
		// Y coord runs from top to bottom. 
		cb.moveTo(
			(((float) x + offsetX) * scale) + insetX, 
			pageHeight - ((((float) y + offsetY) * scale) + insetY)
		);
	}

	/**
	 * @see com.totalchange.sketchingout.imageparsers.SketchingoutImageParser#lineTo(double, double)
	 */
	public void lineTo(double x, double y)
			throws SketchingoutImageParserException {
		
		// See moveTo for explanation.
		cb.lineTo(
			(((float) x + offsetX) * scale) + insetX, 
			pageHeight - ((((float) y + offsetY) * scale) + insetY)
		);
	}

}
