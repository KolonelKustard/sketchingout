/*
 * Created on 01-Aug-2004
 */
package com.totalchange.consequences;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.xml.sax.Attributes;

/**
 * @author ralphjones
 */
public class GalleryDrawingsRequest implements RequestHandler {
	
	private static final int GALLERY_REQUEST_TYPE_LATEST = 0;
	
	/**
	 * Outputs a single drawing
	 * 
	 * @param out
	 * @param res
	 * @throws SQLException
	 * @throws IOException
	 */
	private void outputOneDrawing(XMLWriter out, ResultSet res) throws SQLException,
		IOException {
		
		// Packages up the gallery drawing
		out.startElement(XMLConsts.EL_GALLERY_DRAWING);
		
		try {
			// Send the values from this row in the database table
			out.writeElement(XMLConsts.EL_GALLERY_DRAWING_ID, String.valueOf(res.getInt("friendly_id")));
			out.writeElement(XMLConsts.EL_GALLERY_DRAWING_WIDTH, String.valueOf(res.getInt("width")));
			out.writeElement(XMLConsts.EL_GALLERY_DRAWING_HEIGHT, String.valueOf(res.getInt("height")));
			
			// Get the number of stages and output it
			int numStages = res.getInt("stage");
			out.writeElement(XMLConsts.EL_GALLERY_DRAWING_NUM_STAGES, String.valueOf(numStages));
			
			// Run through the stages, adding the users
			for (int num = 0; num < numStages; num++) {
				out.startElement(XMLConsts.EL_GALLERY_DRAWING_STAGE_AUTHOR);
				try {
					int stageNum = num + 1;
					out.writeElement(XMLConsts.EL_GALLERY_DRAWING_STAGE_AUTHOR_STAGE, String.valueOf(stageNum));
					out.writeElement(XMLConsts.EL_GALLERY_DRAWING_STAGE_AUTHOR_NAME, res.getString("stage_" + stageNum + "_author_name"), true);
				}
				finally {
					// Make sure close this stage author node
					out.endElement(XMLConsts.EL_GALLERY_DRAWING_STAGE_AUTHOR);
				}
			}
		}
		finally {
			// Make sure close the drawing element
			out.endElement(XMLConsts.EL_GALLERY_DRAWING);
		}
	}

	/**
	 * Outputs a series of drawings resulting from a query for drawings
	 * 
	 * @param out
	 * @param pstmt
	 * @param start
	 * @param quantity
	 * @throws SQLException
	 * @throws IOException
	 */
	private void outputDrawings(XMLWriter out, PreparedStatement pstmt, int start, 
		int quantity) throws SQLException, IOException {
		
		// Start off by outputting the start of the gallery responses
		out.startElement(XMLConsts.EL_GALLERY_DRAWINGS);
		
		ResultSet res = pstmt.executeQuery();
		
		try {
			// Now run through the specified quantity
			int sentSoFar = 0;
			while (res.next()) {
				// If at the end of the quantity to send, stop.
				if (sentSoFar >= quantity) break;
				
				// Send this gallery drawing
				outputOneDrawing(out, res);
				
				// Increment the number sent
				sentSoFar++;
			}
		}
		finally {
			// Close the result set
			res.close();
			
			// Close the gallery response 
			out.endElement(XMLConsts.EL_GALLERY_DRAWINGS);
		}
	}
	
	/**
	 * @see com.totalchange.consequences.RequestHandler#start(com.totalchange.consequences.XMLWriter, java.sql.Connection, com.totalchange.consequences.ConsequencesErrors, org.xml.sax.Attributes)
	 */
	public void start(XMLWriter out, Connection conn, ConsequencesErrors errs,
		Attributes attributes) throws HandlerException {
		
		// Find out what type of gallery to use
		String typeStr = attributes.getValue(XMLConsts.AT_GALLERY_DRAWINGS_TYPE);
		if (typeStr == null) {
			throw new HandlerException("Must specify what type of gallery you want to view.");
		}
		
		String startStr = attributes.getValue(XMLConsts.AT_GALLERY_DRAWINGS_START);
		if (startStr == null) {
			throw new HandlerException("Must specify a starting point for a gallery request.");
		}
		
		String quantityStr = attributes.getValue(XMLConsts.AT_GALLERY_DRAWINGS_QUANTITY);
		if (quantityStr == null) {
			throw new HandlerException("Must specify a quantity to return for a gallery request.");
		}
		
		// Convert all the Strings to ints
		int type = Integer.parseInt(typeStr);
		int start = Integer.parseInt(startStr);
		int quantity = Integer.parseInt(quantityStr);
		
		try {
			// Get a query for some drawings based on the type specified
			PreparedStatement pstmt = null;
			switch(type) {
				case GALLERY_REQUEST_TYPE_LATEST:
					pstmt = SQLWrapper.getLatestGalleryDrawings(conn, start, quantity);
					break;
				default:
					throw new HandlerException("Type: " + type + " is not a valid gallery request type");
			}
			
			try {
				// Send the statement through to be outputted
				outputDrawings(out, pstmt, start, quantity);
			}
			finally {
				// Make sure statement gets closed
				pstmt.close();
			}
		}
		catch(Exception e) {
			errs.addException(this.getClass(), e);
		}
	}

	/**
	 * @see com.totalchange.consequences.RequestHandler#data(char[], int, int)
	 */
	public void data(char[] ch, int start, int length) throws HandlerException {
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
