/*
 * Created on 05-Jun-2004
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
		Clob draw = next.getClob("stage_" + stage);
		
		// Check have a previous drawing to give
		if ((draw == null) || (draw.length() <= 0)) {
			throw new HandlerException("Asked for a previous drawing to work from " +
				"but none found.");
		}
		
		// Output drawing
		out.startElement(XMLConsts.EL_NEXT_DRAWING_DRAWING);
		out.startCData();
		SQLWrapper.outputClob(out, draw);
		out.endCData();
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
	private void outputNewDrawing(Connection conn, XMLWriter out) throws
		IOException, SQLException, HandlerException {
		
		// See if a maximum number of active drawings has been specified
		if (SketchingoutSettings.MAX_ACTIVE_DRAWINGS != -1) {
			// Check to see if the max is reached
			if (SQLWrapper.getDrawingPoolSize(conn) >=
				SketchingoutSettings.MAX_ACTIVE_DRAWINGS)
				
				throw new HandlerException(
						SketchingoutErrors.ERR_ACTIVE_DRAWING_DATABASE_FULL,
						"Sorry but the active drawing database is full.  No " +
						"new drawings presently permitted.  Please try again " +
						"later.");
		}
		
		// Output a new id, stage 0 and a timeout value, but no drawing
		out.startElement(XMLConsts.EL_NEXT_DRAWING);
		out.writeElement(XMLConsts.EL_NEXT_DRAWING_ID, new RandomGUID().toString());
		out.writeElement(XMLConsts.EL_NEXT_DRAWING_STAGE, "0");
		out.writeElement(XMLConsts.EL_NEXT_DRAWING_LOCKED_SECS, Integer.toString(SketchingoutSettings.DEFAULT_LOCK_SECS));
		out.endElement(XMLConsts.EL_NEXT_DRAWING);
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
					SQLWrapper.lockDrawing(conn, next.getString("id"), SketchingoutSettings.DEFAULT_LOCK_SECS);
					
					// Now drawing is locked, output it.
					outputDrawing(next, out, SketchingoutSettings.DEFAULT_LOCK_SECS);
				}
				else {
					// Not got a drawing.  Start a new one!
					outputNewDrawing(conn, out);
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
					// and the lock time has expired.  This is because every time a
					// drawing is submitted the distinguished id for the picture is
					// changed.
					if (next.first()) {
						// Got a result.  Figure out how much time is left before
						// the private picture is unlocked.
						long locked = next.getTimestamp("locked").getTime();
						int lock = (int) (locked - System.currentTimeMillis()) / 1000;
						
						// If the amount of time left is less than the normal amount of
						// time, reset the locked time.  This makes it a real last ditch
						// as the distinguished ID will also be reset.
						if (lock < SketchingoutSettings.DEFAULT_LOCK_SECS) {
							lock = SketchingoutSettings.DEFAULT_LOCK_SECS;
							SQLWrapper.lockDrawing(conn, next.getString("id"), lock);
						}
						
						// Send out the drawing
						outputDrawing(next, out, lock);
					}
					else {
						throw new HandlerException(SketchingoutErrors.ERR_INVALID_DRAWING_ID,
							"Could not find drawing specified.  Could be because the locked " +
							"timeout has expired and the drawing has become public.");
					}
				}
				finally {
					// Close query
					next.close();
					pstmt.close();
				}
			}
		}
		catch (HandlerException he) {
			errs.addException(this.getClass(), he.getErrorCode(), he);
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
