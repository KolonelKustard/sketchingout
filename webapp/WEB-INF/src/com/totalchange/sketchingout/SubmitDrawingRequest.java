/*
 * Created on 07-Jun-2004
 */
package com.totalchange.sketchingout;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.mail.MessagingException;

import org.xml.sax.Attributes;

/**
 * @author RalphJones
 *
 */
public class SubmitDrawingRequest implements RequestHandler {
	
	private Connection conn;
	private SketchingoutErrors errs;
	private PreparedStatement pstmt;
	
	private String drawingID = null;
	private boolean complete = false;
	
	private int paramDrawing = 0;

	/**
	 * @see com.totalchange.sketchingout.RequestHandler#start(com.totalchange.consequences.XMLWriter, java.sql.Connection, com.totalchange.consequences.ConsequencesErrors, org.xml.sax.Attributes)
	 */
	public void start(
		XMLWriter out,
		Connection conn,
		SketchingoutErrors errs,
		Attributes attributes)
		throws HandlerException {
		
		// Get reference to connection and errors store
		this.conn = conn;
		this.errs = errs;
		
		// Make sure statement is null
		pstmt = null;
			
		// Get required attributes
		drawingID = attributes.getValue(XMLConsts.AT_SUBMIT_DRAWING_DRAWING_ID);
		if (drawingID == null) {
			throw new HandlerException("Must pass a drawing id to submit a drawing");
		}
		
		String userID = attributes.getValue(XMLConsts.AT_SUBMIT_DRAWING_USER_ID);
		if (userID == null) {
			throw new HandlerException("Must pass a user id to submit a drawing");
		}
		
		String stageStr = attributes.getValue(XMLConsts.AT_SUBMIT_DRAWING_STAGE);
		if (stageStr == null) {
			throw new HandlerException("Must pass the stage your submitted drawing represents");
		}
		
		String widthStr = attributes.getValue(XMLConsts.AT_SUBMIT_DRAWING_WIDTH);
		if (widthStr == null) {
			throw new HandlerException("Must pass the width of the submitted drawing");
		}
		
		String heightStr = attributes.getValue(XMLConsts.AT_SUBMIT_DRAWING_HEIGHT);
		if (heightStr == null) {
			throw new HandlerException("Must pass the height of the submitted drawing");
		}
		
		String offsetYStr = attributes.getValue(XMLConsts.AT_SUBMIT_DRAWING_OFFSET_Y);
		if (heightStr == null) {
			throw new HandlerException("Must pass the offset Y of the submitted drawing");
		}
		
		// Convert the dimensions of this drawing.
		int width = Integer.parseInt(widthStr);
		int height = Integer.parseInt(heightStr) - Integer.parseInt(offsetYStr);
		
		// Get the sending on stuff
		String nextUserEmail = attributes.getValue(XMLConsts.AT_SUBMIT_DRAWING_NEXT_USER_EMAIL);
		String distinguishedID = null;
		int lockSecs = 0;
		
		// If got a next user, will need to define a distiguished id and set locked
		// time for drawing.
		if (nextUserEmail != null) {
			distinguishedID = new RandomGUID().toString();
			lockSecs = SketchingoutSettings.PRIVATE_LOCK_SECS;
		}
		
		// Convert stage to an int.
		int stage = Integer.parseInt(stageStr);
		
		// Make sure within the bounds
		if ((stage < 1) || (stage > SketchingoutSettings.MAX_NUM_STAGES)) {
			throw new HandlerException("Stage value must be between 1 and " + 
				SketchingoutSettings.MAX_NUM_STAGES);
		}
		
		// See if completed or not 
		complete = (stage >= SketchingoutSettings.MAX_NUM_STAGES);
		
		// If completed AND trying to send on to someone, let client know that the
		// next person won't be notified but the drawing will be processed anyway.
		if (complete && (distinguishedID != null)) {
			errs.addException(this.getClass(), new HandlerException("This drawing has " +
				"been completed but a recipient was specified to continue the " +
				"drawing.  The drawing will complete as normal but the recipient " +
				"will not be notified."));
		}
		
		try {
			// Get users details to fill in the statement parameters
			PreparedStatement usrPstmt = SQLWrapper.getUser(conn, userID);
			ResultSet usrRes = usrPstmt.executeQuery();
			
			// Look for first result
			if (!usrRes.first()) {
				// No result?  Throw an exception...
				throw new HandlerException("Could not find a user with ID: " + userID);
			}
			
			// Get this users details
			String usrName = usrRes.getString("name");
			String usrEmail = usrRes.getString("email");
			
			// Decide on whether to insert or update.  If on stage 1 then insert.
			// Otherwise update.
			if (stage == 1) {
				// If inserting, need to leave space for signatures at the bottom of the
				// image.  So need to get the signature width/height and multiply it
				// by half the number of stages to get a 2x? square.
				int sigWidth = 2 * usrRes.getInt("signature_width");
				int sigHeight = (SketchingoutSettings.MAX_NUM_STAGES / 2) *
					usrRes.getInt("signature_height");
				
				// Use the greatest of the two widths for the width of the image
				width = Math.max(width, sigWidth);
				
				// Add the extra height to the inserted drawing
				height += sigHeight;
				
				// Insert a drawing
				pstmt = SQLWrapper.insertDrawing(conn, drawingID, distinguishedID,
					width, height, userID, usrName, usrEmail, lockSecs);
					
				// Copy across signature
				SQLWrapper.copyClob(usrRes.getClob("signature"), pstmt, SQLWrapper.INS_DRAW_SIGNATURE);
				
				// Get parameter for inserting drawing
				paramDrawing = SQLWrapper.INS_DRAW_DRAWING;
			}
			else {
				// Update a drawing
				pstmt = SQLWrapper.updateDrawing(conn, drawingID, complete, distinguishedID,
					height, stage, userID, usrName, usrEmail, lockSecs);
					
				// Copy across signature
				SQLWrapper.copyClob(usrRes.getClob("signature"), pstmt, SQLWrapper.UPD_DRAW_SIGNATURE);
				
				// Get parameter for updating drawing
				paramDrawing = SQLWrapper.UPD_DRAW_DRAWING;
			}
			
			// If not complete and have requested this to be sent on to an email address,
			// send it on!
			if ((distinguishedID != null) && (!complete)) {
				try {
					PrivateDrawingProcessor.sendDrawingOn(usrName, usrEmail,
						nextUserEmail, distinguishedID);
				}
				catch (MessagingException me) {
					errs.addException(this.getClass(), me);
				}
				catch (UnsupportedEncodingException ee) {
					errs.addException(this.getClass(), ee);
				}
			}
			
			// Close statements
			usrRes.close();
			usrPstmt.close(); 
		}
		catch(SQLException se) {
			// Only catch SQL exceptions
			errs.addException(this.getClass(), se);
		}
	}

	/**
	 * @see com.totalchange.sketchingout.RequestHandler#data(char[], int, int)
	 */
	public void data(char[] ch, int start, int length)
		throws HandlerException {
	}

	/**
	 * @see com.totalchange.sketchingout.RequestHandler#getChild(java.lang.String)
	 */
	public RequestHandler getChild(String name) throws HandlerException {
		// Check for submission of drawing
		if (name.equals(XMLConsts.EL_NEXT_DRAWING_DRAWING)) {
			try {
				return new ClobRequest(pstmt, paramDrawing);
			}
			catch (Exception e) {
				throw new HandlerException(e.getMessage());
			}
		}
		else {
			return null;
		}
	}

	/**
	 * @see com.totalchange.sketchingout.RequestHandler#end()
	 */
	public void end() throws HandlerException {
		try {
			// Execute statement
			pstmt.execute();
		
			// Close statement
			pstmt.close();
			
			// See if this drawing is complete
			if (complete) {
				// Run the procedure to deal with a completed drawing...
				CompleteDrawingProcessor.process(drawingID);
			}
		}
		catch (Exception e) {
			errs.addException(this.getClass(), e);
		}
	}

}
