/*
 * Created on 21-Nov-2004
 */
package com.totalchange.sketchingout.imageparsers;

import java.io.IOException;
import java.io.OutputStream;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import com.totalchange.sketchingout.SketchingoutSettings;

/**
 * @author RalphJones
 */
public class PdfImageParser implements SketchingoutImageParser {
	
	private static final String SKETCHING_OUT_FONT = "/DeannasHand.ttf";
	
	private static final float MARGIN_LEFT = 50;
	private static final float MARGIN_RIGHT = 50;
	private static final float MARGIN_TOP = 50;
	private static final float MARGIN_BOTTOM = 50;
	
	private static final float LINE_WIDTH = 1;
	
	private Document document;
	private PdfWriter writer;
	private PdfContentByte cb;
	
	private float pageHeight = 0;
	private float insetX, insetY, offsetX, offsetY = 0;
	private float scale = 0;
	
	private void calculateDimensions(float pageWidth, float pageHeight, 
			float drawingWidth, float drawingHeight) {
		
		// Need to keep page height as coords is bottom to top for Y axis
		// instead of top to bottom
		this.pageHeight = pageHeight;
		
		// Get page dimensions taking into account margins
		float pWidth = pageWidth - MARGIN_LEFT - MARGIN_RIGHT;
		float pHeight = pageHeight - MARGIN_TOP - MARGIN_BOTTOM;
		
		// Need to scale the drawing to fill the available page
		scale = pHeight / drawingHeight;
		
		// Check the width doesn't overrun.  If so, need to base scale on
		// the height.
		if ((drawingWidth * scale) > pWidth) scale = pWidth / drawingWidth; 
		
		// Now need to center the drawing by calculating the insets
		insetX = (pWidth / 2) - ((drawingWidth * scale) / 2);
		insetY = (pHeight / 2) - ((drawingHeight * scale) / 2);
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
			
			BaseFont baseFont = BaseFont.createFont(SKETCHING_OUT_FONT, BaseFont.WINANSI, BaseFont.EMBEDDED);
			
			Font headerFont = new Font(baseFont, 24, Font.BOLD);
			HeaderFooter header = new HeaderFooter(new Phrase("SKETCHING OUT", headerFont), false);
			header.setAlignment(HeaderFooter.ALIGN_CENTER);
			document.setHeader(header);
			
			Font footerFont = new Font(baseFont, 12);
			//Anchor anchor = new Anchor("http://www.sketchingout.co.uk", footerFont);
			//anchor.setReference("http://www.sketchingout.co.uk");
			//anchor.setName("Sketching Out Website");
			Phrase footerPhrase = new Phrase(SketchingoutSettings.URL_ROOT, footerFont);
			
			HeaderFooter footer = new HeaderFooter(footerPhrase, false);
			footer.setAlignment(HeaderFooter.ALIGN_CENTER);
			document.setFooter(footer);
			
			document.open();
			cb = writer.getDirectContent();
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
		offsetX = insetX + x;
		offsetY = insetY + y;
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
		cb.moveTo(
			((float) x + offsetX) * scale, 
			pageHeight - (((float) y + offsetY) * scale)
		);
	}

	/**
	 * @see com.totalchange.sketchingout.imageparsers.SketchingoutImageParser#lineTo(double, double)
	 */
	public void lineTo(double x, double y)
			throws SketchingoutImageParserException {
		cb.lineTo(
			((float) x + offsetX) * scale, 
			pageHeight - (((float) y + offsetY) * scale)
		);
	}

}
