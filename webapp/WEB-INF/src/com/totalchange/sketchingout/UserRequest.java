/*
 * Created on 03-Jun-2004
 *
 */
package com.totalchange.sketchingout;

import java.io.IOException;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.xml.sax.Attributes;

/**
 * @author RalphJones
 *
 * <p>Performs basic user request logic.  However it is possible for a user
 * to be assigned a user id but have no entry in the database.  They are not
 * persisted until they submit a drawing.  As such this request returns blank
 * details for a user that cannot be found in the database.</p> 
 */
public class UserRequest implements RequestHandler {
	
	private void outputUserDetails(XMLWriter out, Connection conn, String userID)
		throws SQLException, IOException {
		
		// Get resultset
		PreparedStatement pstmt = SQLWrapper.getUser(conn, userID);
		ResultSet res = pstmt.executeQuery();
		
		// Check for first.  If get a result, send back user details.  If no
		// result just send back id.
		if (res.first()) {
			out.startElement(XMLConsts.EL_USER_DETAILS);
			out.writeElement(XMLConsts.EL_USER_ID, res.getString("id"));
			out.writeElement(XMLConsts.EL_USER_NAME, res.getString("name"));
			out.writeElement(XMLConsts.EL_USER_EMAIL, res.getString("email"));
			
			Clob clob = res.getClob("signature");
			if ((clob != null) && (clob.length() > 0)) {
				out.startElement(XMLConsts.EL_USER_SIGNATURE);
				out.startCData();
				SQLWrapper.outputClob(out, clob);
				out.endCData();
				out.endElement(XMLConsts.EL_USER_SIGNATURE);
			}
			
			out.endElement(XMLConsts.EL_USER_DETAILS);
		}
		else {
			out.startElement(XMLConsts.EL_USER_DETAILS);
			out.writeElement(XMLConsts.EL_USER_ID, userID);
			out.endElement(XMLConsts.EL_USER_DETAILS);
		}
		
		res.close();
		pstmt.close();
	}

	/**
	 * @see com.totalchange.sketchingout.RequestHandler#start(com.totalchange.consequences.XMLWriter, java.sql.Connection, com.totalchange.consequences.ConsequencesErrors, org.xml.sax.Attributes)
	 */
	public void start(
		XMLWriter out,
		Connection conn,
		SketchingoutErrors errs,
		Attributes attributes)
		throws HandlerException {
			
		// Get the user id that is being requested and use it to setup
		// a query to return user details
		String userID = attributes.getValue(XMLConsts.AT_USER_ID);
		if (userID == null) {
			throw new HandlerException("User ID attribute not defined.");
		}
		
		try {
			outputUserDetails(out, conn, userID);
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
		return null;
	}

	/**
	 * @see com.totalchange.sketchingout.RequestHandler#end()
	 */
	public void end() throws HandlerException {
	}

}
