/*
 * Created on 07-Jun-2004
 *
 */
package com.totalchange.sketchingout;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

import org.xml.sax.Attributes;

/**
 * @author RalphJones
 *
 */
public class UserSubmitRequest implements RequestHandler {
	
	private SketchingoutErrors errs;
	
	private PreparedStatement pstmt;
	private int paramID = 0;
	private int paramName = 0;
	private int paramEmail = 0;
	private int paramSignatureWidth = 0;
	private int paramSignatureHeight = 0;
	private int paramSignature = 0;

	/**
	 * Checks for whether the current users details exist yet.  If they do then
	 * an sql UPDATE is performed.  If not then an INSERT is performed.
	 * 
	 * @see com.totalchange.sketchingout.RequestHandler#start(com.totalchange.consequences.XMLWriter, java.sql.Connection, com.totalchange.consequences.ConsequencesErrors, org.xml.sax.Attributes)
	 */
	public void start(
		XMLWriter out,
		Connection conn,
		SketchingoutErrors errs,
		Attributes attributes)
		throws HandlerException {

		// Set default properties
		this.errs = errs;
		
		// Find the user id
		String userID = attributes.getValue(XMLConsts.AT_USER_ID);
		if (userID == null) {
			throw new HandlerException("User ID attribute not defined.");
		}
		
		// Try and find the user
		try {
			PreparedStatement userPS = SQLWrapper.getUser(conn, userID);
			ResultSet res = userPS.executeQuery();
			
			// Decide whether to insert or update depending on if a result
			// is found.
			if (res.first()) {
				pstmt = SQLWrapper.updateUser(conn);

				paramID = SQLWrapper.UPD_USER_ID;
				paramName = SQLWrapper.UPD_USER_NAME;
				paramEmail = SQLWrapper.UPD_USER_EMAIL;
				paramSignatureWidth = SQLWrapper.UPD_USER_SIGNATURE_WIDTH;
				paramSignatureHeight = SQLWrapper.UPD_USER_SIGNATURE_HEIGHT;
				paramSignature = SQLWrapper.UPD_USER_SIGNATURE;
			}
			else {
				pstmt = SQLWrapper.insertUser(conn);
				
				paramID = SQLWrapper.INS_USER_ID;
				paramName = SQLWrapper.INS_USER_NAME;
				paramEmail = SQLWrapper.INS_USER_EMAIL;
				paramSignatureWidth = SQLWrapper.INS_USER_SIGNATURE_WIDTH;
				paramSignatureHeight = SQLWrapper.INS_USER_SIGNATURE_HEIGHT;
				paramSignature = SQLWrapper.INS_USER_SIGNATURE;
			}
			
			res.close();
			userPS.close();
			
			// Set basic parameters
			pstmt.setString(paramID, userID);
			pstmt.setString(paramName, attributes.getValue(XMLConsts.AT_USER_NAME));
			pstmt.setString(paramEmail, attributes.getValue(XMLConsts.AT_USER_EMAIL));
			
			// Check for signature width and height parameters
			String widthStr = attributes.getValue(XMLConsts.AT_USER_SIGNATURE_WIDTH);
			String heightStr = attributes.getValue(XMLConsts.AT_USER_SIGNATURE_HEIGHT);
			
			if (widthStr == null) pstmt.setNull(paramSignatureWidth, Types.INTEGER);
			else pstmt.setInt(paramSignatureWidth, Integer.parseInt(widthStr));
			
			if (heightStr == null) pstmt.setNull(paramSignatureHeight, Types.INTEGER);
			else pstmt.setInt(paramSignatureHeight, Integer.parseInt(heightStr));
			
			// Default to null for signature
			pstmt.setString(paramSignature, null);
		}
		catch (Exception e) {
			errs.addException(this.getClass(), e);
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
		// Check for submission of signature
		if (name.equals(XMLConsts.EL_USER_SIGNATURE)) {
			try {
				return new ClobRequest(pstmt, paramSignature);
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
		}
		catch (Exception e) {
			errs.addException(this.getClass(), e);
		}
	}

}
