/*
 * Created on 14-Jun-2004
 *
 */
package com.totalchange.consequences;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

class CompleteDrawingProcessorException extends Exception {
	public CompleteDrawingProcessorException(String msg) {
		super(msg);
	}
}

/**
 * @author RalphJones
 *
 */
public class CompleteDrawingProcessor {
	private static final void sendMessage(Session session, String name, String email) 
		throws MessagingException, UnsupportedEncodingException {
		
		MimeMessage msg = new MimeMessage(session);
		
		// Who from
		msg.setFrom(new InternetAddress(ConsequencesSettings.EMAIL_FROM_EMAIL,
			ConsequencesSettings.EMAIL_FROM_NAME));
		
		// Who to	
		msg.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
		
		// Set subject and text
		msg.setSubject("A drawing you were involved in has been completed.");
		msg.setText("Not yet implemented.  Need to write the code to make a PNG out " +
			"of a drawing.");
		
		// Send the message
		Transport.send(msg);
	}
	
	public static final void process(Connection conn, 
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
			
			// Now go through each stage and construct the drawing
			for (int num = 0; num < numStages; num++) {
				// Nothing to do yet...
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
						res.getString("stage_" + stageStr + "_author_email")
					);
				}
				catch (Exception e) {
					// If an address that was supplied was invalid then a messaging
					// exception may be thrown.  Don't want to return this to the
					// client, but want to log it somewhere so just send it to standard
					// err output.
					e.printStackTrace();
				}
			}
		}
		finally {
			res.close();
			pstmt.close();
		}
	}
}
