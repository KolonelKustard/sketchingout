/*
 * Created on 05-Jun-2004
 *
 */
package com.totalchange.consequences;

import java.io.IOException;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

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
		
		// Output the locked time
		out.writeElement(XMLConsts.EL_NEXT_DRAWING_LOCKED_SECS, Integer.toString(lockSecs));
		
		// Find out which body part needs to be drawn next, and therefore
		// which body part to return in the response.
		Clob draw = null;
		String drawStr = null;
		switch (next.getInt("stage")) {
			case 1: draw = next.getClob("head"); drawStr = XMLConsts.AV_HEAD; 
			case 2: draw = next.getClob("body"); drawStr = XMLConsts.AV_BODY;
			case 3: draw = next.getClob("legs"); drawStr = XMLConsts.AV_LEGS;
		}
		
		// Check have a previous drawing to give
		if ((draw == null) || (draw.length() <= 0)) {
			throw new HandlerException("Asked for a previous drawing to work from " +
				"but none found.");
		}
		
		// Make attribute which says which 
		AttributesImpl attr = new AttributesImpl();
		attr.addAttribute("", "", XMLConsts.AT_NEXT_DRAWING_BODY_PART, "", drawStr);
		
		// Output drawing
		out.startElement(XMLConsts.EL_NEXT_DRAWING_DRAWING, attr);
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
		// Output a new id and a timeout value, but no drawing
		out.startElement(XMLConsts.EL_NEXT_DRAWING);
		out.writeElement(XMLConsts.EL_NEXT_DRAWING_ID, new RandomGUID().toString());
		out.writeElement(XMLConsts.EL_NEXT_DRAWING_LOCKED_SECS, Integer.toString(XMLConsts.DEFAULT_LOCK_SECS));
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
		
		// The result set that will contain the next drawing
		ResultSet next = null;
		
		try {
			if (nextDrawingID == null) {
				// Not specified a drawing to do.  Look up from public location.
				next = SQLWrapper.getNextDrawingPublic(conn, userID);
				
				// See if got a drawing
				if (next.first()) {
					// Got a drawing.  Now need to lock it.
					SQLWrapper.lockDrawing(conn, next.getString("id"), XMLConsts.DEFAULT_LOCK_SECS);
					
					// Now drawing is locked, output it.
					outputDrawing(next, out, XMLConsts.DEFAULT_LOCK_SECS);
				}
				else {
					// Not got a drawing.  Start a new one!
					outputNewDrawing(out);
				}
			}
			else {
				// Specified a drawing.  Look up using distinguished id.
				next = SQLWrapper.getNextDrawingPrivate(conn, nextDrawingID);
				
				// When specifying next drawing, need to make sure get a result.
				// If don't get a result it could mean this person is too late
				// and the lock time has expired.
				if (next.first()) {
					// Got a result.  Figure out how much time is left before
					// the private picture is unlocked.
					int lock = 3600;
					outputDrawing(next, out, lock);
				}
				else {
					throw new HandlerException("Could not find drawing specified.  " +
						"Could be because the locked timeout has expired and the " +
						"drawing has become public.");
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