/*
 * Created on 07-Jun-2004
 *
 */
package com.totalchange.consequences;

import java.sql.Connection;
import java.sql.ResultSet;

import org.xml.sax.Attributes;

/**
 * @author RalphJones
 *
 */
public class UserSubmitRequest implements RequestHandler {
	
	private boolean inSignature;

	/**
	 * Checks for whether the current users details exist yet.  If they do then
	 * an sql UPDATE is performed.  If not then an INSERT is performed.
	 * 
	 * @see com.totalchange.consequences.RequestHandler#start(com.totalchange.consequences.XMLWriter, java.sql.Connection, com.totalchange.consequences.ConsequencesErrors, org.xml.sax.Attributes)
	 */
	public void start(
		XMLWriter out,
		Connection conn,
		ConsequencesErrors errs,
		Attributes attributes)
		throws HandlerException {
			
		// Set default properties
		inSignature = false;	
		
		// Find the user id
		String userID = attributes.getValue(XMLConsts.AT_USER_ID);
		if (userID == null) {
			throw new HandlerException("User ID attribute not defined.");
		}
		
		// Find the other attributes
		String name = attributes.getValue(XMLConsts.AT_USER_NAME);
		String email = attributes.getValue(XMLConsts.AT_USER_EMAIL);
		String password = attributes.getValue(XMLConsts.AT_USER_PASSWORD);
		
		// Try and find the user
		try {
			ResultSet res = SQLWrapper.getUser(conn, userID);
			
			// Decide whether to insert or update depending on if a result
			// is found.
			if (res.first()) {
				// UPDATE
			}
			else {
				// INSERT
			}
		}
		catch (Exception e) {
			errs.addException(this.getClass(), e);
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
		// Return this if a signature node is found.
		if (name.equals(XMLConsts.EL_USER_SIGNATURE)) {
			inSignature = true;
			return this;
		}
		else {
			return null;
		}
	}

	/**
	 * @see com.totalchange.consequences.RequestHandler#end()
	 */
	public void end() throws HandlerException {
		
	}

}
