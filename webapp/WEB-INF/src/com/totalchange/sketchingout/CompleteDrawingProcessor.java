/*
 * Created on 14-Jun-2004
 *
 */
package com.totalchange.sketchingout;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.Session;

import com.totalchange.sketchingout.imageparsers.BufferedImageParser;
import com.totalchange.sketchingout.imageparsers.PdfImageParser;
import com.totalchange.sketchingout.imageparsers.SketchingoutImageParserException;

class CompleteDrawingProcessorException extends Exception {
	public CompleteDrawingProcessorException(String msg) {
		super(msg);
	}
	
	public CompleteDrawingProcessorException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public CompleteDrawingProcessorException(Throwable cause) {
		super(cause);
	}
}

/**
 * @author RalphJones
 */
public class CompleteDrawingProcessor {
	private static final void sendMessage(Session session, String name, String email,
		int stage, DataSource imageDS, DataSource pdfDS) throws MessagingException, 
		UnsupportedEncodingException {
		
		// Make new email
		SketchingoutEmail msg = new SketchingoutEmail(session, SketchingoutEmails.EMAILS);
		msg.setToName(name);
		msg.setToEmail(email);
		msg.setStage(stage);
		
		// Add attachments
		msg.addAttachment(imageDS);
		msg.addAttachment(pdfDS);
		
		// Send message
		msg.send();
	}
	
	private static final void processMessages(Connection conn, 
		String drawingID) throws CompleteDrawingProcessorException, SQLException {
		
		// Get the drawing from the database with all the contact details too.
		PreparedStatement pstmt = SQLWrapper.getCompleteDrawing(conn, drawingID);
		
		// Open the query
		ResultSet res = pstmt.executeQuery();
		
		// Open try block to make sure both are closed
		try {
			// Check got a result
			if (!res.first()) {
				throw new CompleteDrawingProcessorException("Could not find a drawing " +
					"with ID: " + drawingID);
			}
			
			// Find out how many stages are involved in this drawing
			int numStages = res.getInt("stage");
			
			// Create the drawing datasources that will serve as the attachment to the
			// outgoing email
			DrawingDataSource imageDS, pdfDS;
			try {
				imageDS = new DrawingDataSource(
					res.getInt("version"),
					res.getInt("width"),
					res.getInt("height"),
					100,
					new BufferedImageParser("png"),
					"sketchingout.png",
					"image/png"
				);
				
				pdfDS = new DrawingDataSource(
					res.getInt("version"),
					res.getInt("width"),
					res.getInt("height"),
					100,
					new PdfImageParser(),
					"sketchingout.pdf",
					"application/pdf"
				);
				
				// Now go through each stage and construct the drawing
				for (int num = 0; num < numStages; num++) {
					imageDS.addStage(res.getCharacterStream("stage_" + (num + 1)));
					pdfDS.addStage(res.getCharacterStream("stage_" + (num + 1)));
				}
				
				// And add the signatures
				for (int num = 0; num < numStages; num++) {
					imageDS.addSignature(res.getCharacterStream("stage_" + 
						(num + 1) + "_signature"));
					
					pdfDS.addSignature(res.getCharacterStream("stage_" + 
						(num + 1) + "_signature"));
				}
			}
			catch (SketchingoutImageParserException ce) {
				throw new CompleteDrawingProcessorException(ce);
			}
			
			// Open a session to the mail server
			Session session = SMTPSessionFactory.getSMTPSession();
			
			// Have a drawing, so go through each stage again and send an email with the
			// drawing attached.
			for (int num = 0; num < numStages; num++) {
				String stageStr = String.valueOf(num + 1);
				
				try {
					sendMessage(
						session,
						res.getString("stage_" + stageStr + "_author_name"),
						res.getString("stage_" + stageStr + "_author_email"),
						num + 1,
						imageDS,
						pdfDS
					);
				}
				catch (MessagingException me) {
					// If an address that was supplied was invalid then a messaging
					// exception may be thrown.  Don't want to return this to the
					// client, but want to log it somewhere so just send it to standard
					// err output.
					me.printStackTrace();
				}
				catch (UnsupportedEncodingException ee) {
					throw new CompleteDrawingProcessorException(ee);
				}
			}
		}
		finally {
			res.close();
			pstmt.close();
		}
	}
	
	static final void processDrawing(String drawingID) {
		try {
			// Get database connection
			Connection conn = SQLWrapper.makeConnection();
			try {
				// Process the emails to the participants
				processMessages(conn, drawingID);
				
				// Initiate a transfer of this drawing
				CompleteDrawingTransfer.transfer(conn, drawingID);
				
				// Save this drawing to the gallery
				CompleteDrawingTransfer.save(conn, drawingID);
				
				// Delete the drawing from the active drawings table
				SQLWrapper.deleteDrawing(conn, drawingID);
			}
			finally {
				conn.close();
			}
		}
		catch (Exception e) {
			// Print the error
			e.printStackTrace();
			
			// Try and send the error to someone who cares
			try {
				SketchingoutEmail errMail = new SketchingoutEmail(SMTPSessionFactory.getSMTPSession());
				errMail.setFromName("SketchingOut Errors");
				errMail.setFromEmail(SketchingoutSettings.EMAIL_FROM_EMAIL);
				errMail.setToName("SketchingOut Errors");
				errMail.setToEmail(SketchingoutSettings.COMPLETE_DRAWING_TO_EMAIL);
				errMail.setSubject("Error: " + e.getMessage());
				errMail.setBody("Error occurred:\n\n" + e.getMessage());
				errMail.send();
			}
			catch (Exception me) {
				me.printStackTrace();
			}
		}
	}
	
	public static final void process(String drawingID) {
		// If not threaded pass straight through, otherwise wake up the
		// completion thread.
		if (!SketchingoutSettings.COMPLETE_THREADED) {
			processDrawing(drawingID);
		}
		else {
			processDrawing(drawingID);
		}
	}
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Please provide drawing ID as argument.");
		}
		else {
			CompleteDrawingProcessor.process(args[0]);
		}
	}
}
