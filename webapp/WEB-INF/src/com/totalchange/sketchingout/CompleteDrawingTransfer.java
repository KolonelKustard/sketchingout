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

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.totalchange.sketchingout.imageparsers.SwfAnimatedImageParser;

class CompleteDrawingTransferException extends Exception {
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
				Message msg = new MimeMessage(SMTPSessionFactory.getSMTPSession());
				msg.setFrom(new InternetAddress(SketchingoutSettings.EMAIL_FROM_EMAIL, 
						SketchingoutSettings.EMAIL_FROM_NAME));
				
				msg.addRecipient(Message.RecipientType.TO,
						new InternetAddress(SketchingoutSettings.COMPLETE_DRAWING_TO_EMAIL));
				
				msg.setSubject("Complete Drawing no. " + res.getInt("friendly_id"));
				
				// Create the MIME multi part
				MimeMultipart multiPart = new MimeMultipart();
				
				// Create and add the body part
				BodyPart body = new MimeBodyPart();
				body.setText("Complete drawing no. " + res.getInt("friendly_id"));
				multiPart.addBodyPart(body);
				
				// Create and add the data
				BodyPart img = new MimeBodyPart();
				img.setDataHandler(new DataHandler(ds));
				img.setFileName(ds.getName());
				multiPart.addBodyPart(img);
				
				// Put the parts to the message
				msg.setContent(multiPart);
				
				// Send the message
				Transport.send(msg);
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
	
	public static final void save(Connection conn, String drawingID) throws 
			CompleteDrawingTransferException {
		// Output the drawing to a file
		try {
			PreparedStatement ps = SQLWrapper.getCompleteDrawing(conn, drawingID);
			ResultSet res = ps.executeQuery();
			
			try {
				// Check got a drawing
				if (!res.first()) {
					throw new SQLException("Could not find drawing with ID: " + drawingID);
				}
				
				// Make file output stream
				OutputStream fo = new FileOutputStream("./test.swf");
				
				// Send drawing to file output stream
				ImageParser.parseResultSet(res, 100, 0, fo, new SwfAnimatedImageParser());
			}
			finally {
				res.close();
				ps.close();
			}
		}
		catch(Exception e) {
			throw new CompleteDrawingTransferException(e);
		}
	}
}