/*
 * Created on 05-Jun-2004
 *
 */
package com.totalchange.consequences;

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
 */
public class NextDrawingRequest implements RequestHandler {
	
	/**
	 * <p>Outputs an existing drawing.</p>
	 * 
	 * @param conn
	 * @param next
	 * @param out
	 * @throws SQLException
	 * @throws IOException
	 */
	private void outputDrawing(ResultSet next, XMLWriter out, int lockSecs)
		throws SQLException , IOException, HandlerException {
			
		// Start output
		out.startElement(XMLConsts.EL_NEXT_DRAWING);
		
		// Output the drawing id
		out.writeElement(XMLConsts.EL_NEXT_DRAWING_ID, next.getString("id"));
		
		// Find out the stage currently at
		int stage = next.getInt("stage");
		out.writeElement(XMLConsts.EL_NEXT_DRAWING_STAGE, String.valueOf(stage));
		
		// Output the locked time
		out.writeElement(XMLConsts.EL_NEXT_DRAWING_LOCKED_SECS, Integer.toString(lockSecs));
		
		// Use the stage value to find out which drawing to return
		Clob draw = null;
		switch (stage) {
			case 1: draw = next.getClob("head");
			case 2: draw = next.getClob("body");
			case 3: draw = next.getClob("legs");
		}
		
		// Check have a previous drawing to give
		if ((draw == null) || (draw.length() <= 0)) {
			throw new HandlerException("Asked for a previous drawing to work from " +
				"but none found.");
		}
		
		// Output drawing
		out.startElement(XMLConsts.EL_NEXT_DRAWING_DRAWING);
		SQLWrapper.outputClob(out, draw);
		out.endElement(XMLConsts.EL_NEXT_DRAWING_DRAWING);
		
		// End next
		out.endElement(XMLConsts.EL_NEXT_DRAWING);
	}
	
	/**
	 * <p>Outputs a new blank drawing.</p>
	 * 
	 * @param out
	 * @throws IOException
	 */
	private void outputNewDrawing(XMLWriter out) throws IOException {
		// Output a new id, stage 0 and a timeout value, but no drawing
		out.startElement(XMLConsts.EL_NEXT_DRAWING);
		out.writeElement(XMLConsts.EL_NEXT_DRAWING_ID, new RandomGUID().toString());
		out.writeElement(XMLConsts.EL_NEXT_DRAWING_STAGE, "0");
		out.writeElement(XMLConsts.EL_NEXT_DRAWING_LOCKED_SECS, Integer.toString(ConsequencesSettings.DEFAULT_LOCK_SECS));
		out.endElement(XMLConsts.EL_NEXT_DRAWING);
	}

	/**
	 * @see com.totalchange.consequences.RequestHandler#start(com.totalchange.consequences.XMLWriter, java.sql.Connection, com.totalchange.consequences.ConsequencesErrors, org.xml.sax.Attributes)
	 */
	public void start(
		XMLWriter out,
		Connection conn,
		ConsequencesErrors errs,
		Attributes attributes)
		throws HandlerException {
			
		// Get user id
		String userID = attributes.getValue(XMLConsts.AT_NEXT_DRAWING_USER_ID);
		if (userID == null) {
			throw new HandlerException("Must provide a user id for a request for " +
				"a drawing.");
		}
		
		// Look for next drawing id.
		String nextDrawingID = attributes.getValue(XMLConsts.AT_NEXT_DRAWING_ID);
		
		try {
			if (nextDrawingID == null) {
				// Not specified a drawing to do.  Look up from public location.
				PreparedStatement pstmt = SQLWrapper.getNextDrawingPublic(conn, userID);
				ResultSet next = pstmt.executeQuery();
				
				// See if got a drawing
				if (next.first()) {
					// Got a drawing.  Now need to lock it.
					SQLWrapper.lockDrawing(conn, next.getString("id"), ConsequencesSettings.DEFAULT_LOCK_SECS);
					
					// Now drawing is locked, output it.
					outputDrawing(next, out, ConsequencesSettings.DEFAULT_LOCK_SECS);
				}
				else {
					// Not got a drawing.  Start a new one!
					outputNewDrawing(out);
				}
				
				// Close query
				next.close();
				pstmt.close();
			}
			else {
				// Specified a drawing.  Look up using distinguished id.
				PreparedStatement pstmt = SQLWrapper.getNextDrawingPrivate(conn, nextDrawingID);
				ResultSet next = pstmt.executeQuery();
				
				try {
					// When specifying next drawing, need to make sure get a result.
					// If don't get a result it could mean this person is too late
					// and the lock time has expired.
					if (next.first()) {
						// Got a result.  Figure out how much time is left before
						// the private picture is unlocked.
						long locked = next.getTimestamp(1).getTime();
						int lock = (int) (locked - System.currentTimeMillis()) / 1000;
						
						outputDrawing(next, out, lock);
					}
					else {
						throw new HandlerException("Could not find drawing specified.  " +
							"Could be because the locked timeout has expired and the " +
							"drawing has become public.");
					}
				}
				finally {
					// Close query
					next.close();
					pstmt.close();
				}
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
		return null;
	}

	/**
	 * @see com.totalchange.consequences.RequestHandler#end()
	 */
	public void end() throws HandlerException {
	}

}
