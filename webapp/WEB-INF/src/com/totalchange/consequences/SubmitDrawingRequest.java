/*
 * Created on 07-Jun-2004
 *
 */
package com.totalchange.consequences;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.xml.sax.Attributes;

/**
 * @author RalphJones
 *
 */
public class SubmitDrawingRequest implements RequestHandler {
	
	private PreparedStatement pstmt;

	/**
	 * @see com.totalchange.consequences.RequestHandler#start(com.totalchange.consequences.XMLWriter, java.sql.Connection, com.totalchange.consequences.ConsequencesErrors, org.xml.sax.Attributes)
	 */
	public void start(
		XMLWriter out,
		Connection conn,
		ConsequencesErrors errs,
		Attributes attributes)
		throws HandlerException {
		
		// Make sure statement is null
		pstmt = null;
			
		// Get required attributes
		String drawingID = attributes.getValue(XMLConsts.AT_SUBMIT_DRAWING_DRAWING_ID);
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
		
		String nextUserEmail = attributes.getValue(XMLConsts.AT_SUBMIT_DRAWING_NEXT_USER_EMAIL);
		
		// Convert stage to an int.
		int stage = Integer.valueOf(stageStr).intValue();
		
		// Make sure within the bounds
		if ((stage < 1) || (stage > ConsequencesSettings.MAX_NUM_STAGES)) {
			throw new HandlerException("Stage value must be between 1 and " + 
				ConsequencesSettings.MAX_NUM_STAGES);
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
			
			// Decide on whether to insert or update.  If on stage 1 then insert.
			// Otherwise update.
			if (stage == 1) {
			}
			else {
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
	 * @see com.totalchange.consequences.RequestHandler#data(char[], int, int)
	 */
	public void data(char[] ch, int start, int length)
		throws HandlerException {
	}

	/**
	 * @see com.totalchange.consequences.RequestHandler#getChild(java.lang.String)
	 */
	public RequestHandler getChild(String name) throws HandlerException {
		return null;
	}

	/**
	 * @see com.totalchange.consequences.RequestHandler#end()
	 */
	public void end() throws HandlerException {
	}

}
