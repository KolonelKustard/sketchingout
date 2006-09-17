/*
 * Created on 15-Jan-2005
 */
package com.totalchange.sketchingout;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.activation.DataSource;

import com.totalchange.sketchingout.imageparsers.BufferedImageParser;
import com.totalchange.sketchingout.imageparsers.PdfImageParser;
import com.totalchange.sketchingout.imageparsers.SwfAnimatedImageParser;

class CompleteDrawingTransferException extends Exception {
	public static final long serialVersionUID = 1;
	
	public CompleteDrawingTransferException(String msg) {
		super(msg);
	}
	
	public CompleteDrawingTransferException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public CompleteDrawingTransferException(Throwable cause) {
		super(cause);
	}
}

class CompleteDrawingTransferDataSource implements DataSource {
	private ResultSet drawing;
	private Vector streams;
	
	private void addString(String str) {
		streams.add(new ByteArrayInputStream(str.getBytes()));
	}
	
	private void addInputStream(InputStream in) {
		streams.add(in);
	}
	
	public CompleteDrawingTransferDataSource(ResultSet drawing) {
		this.drawing = drawing;
		streams = new Vector();
	}
	
	/**
	 * @see javax.activation.DataSource#getContentType()
	 */
	public String getContentType() {
		return "text/xml";
	}
	
	/**
	 * @see javax.activation.DataSource#getInputStream()
	 */
	public InputStream getInputStream() throws IOException {
		try {
			addString(
				"<complete_drawing " +
					"id=\"" + drawing.getString("id") + "\" " +
					"friendly_id=\"" + drawing.getInt("friendly_id") + "\" " +
					"version=\"" + drawing.getInt("version") + "\" " +
					"width=\"" + drawing.getInt("width") + "\" " +
					"height=\"" + drawing.getInt("height") + "\" " +
				">"
			);
		
			// Run through all the stages
			for (int stage = 1; stage <= drawing.getInt("stage"); stage++) {
				addString(
					"<stage " +
						"author_id=\"" + drawing.getString("stage_" + stage + "_author_id") + "\" " +
						"author_name=\"" + drawing.getString("stage_" + stage + "_author_name") + "\" " +
						"author_email=\"" + drawing.getString("stage_" + stage + "_author_email") + "\" " +
					">" +
					"<drawing>"
				);
				
				addInputStream(drawing.getAsciiStream("stage_" + stage));
				addString("</drawing><signature>");
				addInputStream(drawing.getAsciiStream("stage_" + stage + "_signature"));
				addString("</signature></stage>");
			}
		}
		catch(SQLException se) {
			throw new IOException(se.getMessage());
		}
		
		addString("</complete_drawing>");
		
		return new SequenceInputStream(streams.elements());
	}
	
	/**
	 * @see javax.activation.DataSource#getName()
	 */
	public String getName() {
		return "drawing.xml";
	}
	
	/**
	 * @see javax.activation.DataSource#getOutputStream()
	 */
	public OutputStream getOutputStream() throws IOException {
		throw new IOException("Output stream not supported");
	}
}

/**
 * @author RalphJones
 */
public class CompleteDrawingTransfer {	
	public static void transfer(Connection conn, String drawingID) throws 
			CompleteDrawingTransferException {
		
		try {
			// Get the requested drawing
			PreparedStatement pstmt = SQLWrapper.getDrawingForTransfer(conn, drawingID);
			ResultSet res = pstmt.executeQuery();
			
			try {
				// Check got a drawing
				if (!res.first()) {
					throw new SQLException("Could not find drawing with ID: " + drawingID);
				}
				
				// Make up a data source to handle the drawing
				CompleteDrawingTransferDataSource ds = new CompleteDrawingTransferDataSource(res);
				
				// Now transfer the drawing using email for the time being
				SketchingoutEmail msg = new SketchingoutEmail(SMTPSessionFactory.getSMTPSession());
				msg.setFromName(SketchingoutSettings.EMAIL_FROM_NAME);
				msg.setFromEmail(SketchingoutSettings.EMAIL_FROM_EMAIL);
				msg.setToName(SketchingoutSettings.COMPLETE_DRAWING_TO_EMAIL);
				msg.setToEmail(SketchingoutSettings.COMPLETE_DRAWING_TO_EMAIL);
				
				msg.setSubject("Complete Drawing no. " + res.getInt("friendly_id"));
				msg.setBody("Complete Drawing no. " + res.getInt("friendly_id"));
				
				msg.addAttachment(ds);
				
				msg.send();
			}
			finally {
				res.close();
				pstmt.close();
			}
		}
		catch(Exception e) {
			throw new CompleteDrawingTransferException(e);
		}
	}
	
	/**
	 * Saves a drawing to the gallery.  The gallery is also a queue - so the first
	 * added gallery item is removed prior to the last (current) item being added.
	 * This is an attempt at working within a restrictive file space environment.
	 * 
	 * @param conn
	 * @param drawingID
	 * @throws CompleteDrawingTransferException
	 */
	public static final void save(Connection conn, String drawingID) throws 
			CompleteDrawingTransferException {
		try {
			// Remove old gallery item(s)
			GalleryThinner.deleteOldDrawings(conn);
			
			// Define filenames for pdf and swf for saving
			String thumb, pdf, swf = "";
			
			PreparedStatement ps = SQLWrapper.getCompleteDrawing(conn, drawingID);
			ResultSet res = ps.executeQuery();
			
			try {
				// Check got a drawing
				if (!res.first()) {
					throw new SQLException("Could not find drawing with ID: " + drawingID);
				}
				
				// Make filenames for pdf and animated swf versions
				thumb = res.getInt("friendly_id") + ".png";
				pdf = res.getInt("friendly_id") + ".pdf";
				swf = res.getInt("friendly_id") + ".swf";
				 
				OutputStream fo;
				
				// Make file for thumbnail
				fo = new FileOutputStream(SketchingoutSettings.FS_DRAWING_STORE + thumb);
				ImageParser.parseResultSet(res, 50, 0, fo, new BufferedImageParser("png"));
				fo.close();
				
				// Make file for pdf
				fo = new FileOutputStream(SketchingoutSettings.FS_DRAWING_STORE + pdf);
				ImageParser.parseResultSet(res, 100, 0, fo, new PdfImageParser());
				fo.close();
				
				// Make file for swf 
				fo = new FileOutputStream(SketchingoutSettings.FS_DRAWING_STORE + swf);
				ImageParser.parseResultSet(res, 100, 0, fo, new SwfAnimatedImageParser());
				fo.close();
			}
			finally {
				res.close();
				ps.close();
			}
			
			// Now transfer the drawing from the active drawings table to the gallery
			SQLWrapper.saveToGallery(conn, drawingID, thumb, pdf, swf);
		}
		catch(Exception e) {
			throw new CompleteDrawingTransferException(e);
		}
	}
	
	/**
	 * <p>Converts all completed drawings in the database to gallery drawings</p>
	 * 
	 * @param args No params
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Connection conn = SQLWrapper.makeConnection();
		PreparedStatement pstmt = SQLWrapper.getCompleteDrawings(conn);
		ResultSet res = pstmt.executeQuery();
		try {
			while (res.next()) {
				System.out.println("Processing drawing: " + res.getString("id"));
				
				// Save the drawing
				save(conn, res.getString("id"));
				
				// Delete this drawing
				SQLWrapper.deleteDrawing(conn, res.getString("id"));
			}
		}
		finally {
			res.close();
			pstmt.close();
			conn.close();
		}
	}
}