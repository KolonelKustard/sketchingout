/*
 * Created on 24-Jun-2004
 */
package com.totalchange.consequences;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.xml.sax.Attributes;

/**
 * @author RalphJones
 *
 * <p>This RequestHandler is used to return an entire drawing based on the requested
 * drawing id.</p>
 */
public class GalleryDrawingRequest implements RequestHandler {
	
	/**
	 * <p>Outputs a stage from the resultset as a single drawing stage node</p>
	 * 
	 * @param out
	 * @param res
	 * @param stage
	 * @throws SQLException
	 * @throws IOException
	 */
	private void outputStage(XMLWriter out, ResultSet res, String stage) throws
		SQLException, IOException {
		
		// Start new drawing stage node
		out.startElement(XMLConsts.EL_GALLERY_DRAWING_STAGE);
		
		// Say which stage this is
		out.writeElement(XMLConsts.EL_GALLERY_DRAWING_STAGE_NUM, stage);
		
		// Say who is responsible for this stage
		out.writeElement(XMLConsts.EL_GALLERY_DRAWING_NAME, 
			res.getString("stage_" + stage + "_author_name"), true);
			
		// Output the drawing
		out.startElement(XMLConsts.EL_GALLERY_DRAWING_DRAWING);
		out.startCData();
		SQLWrapper.outputClob(out, res.getClob("stage_" + stage));
		out.endCData();
		out.endElement(XMLConsts.EL_GALLERY_DRAWING_DRAWING);
		
		// Output the signature
		out.startElement(XMLConsts.EL_GALLERY_DRAWING_SIGNATURE);
		out.startCData();
		SQLWrapper.outputClob(out, res.getClob("stage_" + stage + "_signature"));
		out.endCData();
		out.endElement(XMLConsts.EL_GALLERY_DRAWING_SIGNATURE);
		
		// End this stage
		out.endElement(XMLConsts.EL_GALLERY_DRAWING_STAGE);
	}

	/**
	 * <p>Drawing ID is in an attribute and is used to query the database.  Then drawing
	 * is sent back</p>
	 * 
	 * @see com.totalchange.consequences.RequestHandler#start(com.totalchange.consequences.XMLWriter, java.sql.Connection, com.totalchange.consequences.ConsequencesErrors, org.xml.sax.Attributes)
	 */
	public void start(
		XMLWriter out,
		Connection conn,
		ConsequencesErrors errs,
		Attributes attributes)
		throws HandlerException {
		
		// Get the drawing ID parameter
		String drawingID = attributes.getValue(XMLConsts.AT_GALLERY_DRAWING_ID);
		
		// Check got a drawing id
		if (drawingID == null) {
			throw new HandlerException("Must provide a drawing id in a request for a " +
				"gallery drawing.");
		}
		
		try {
			// Make request for drawing
			PreparedStatement pstmt = SQLWrapper.getDrawing(conn, drawingID);
			
			// Execute request
			ResultSet res = pstmt.executeQuery();
			
			// Check got a result
			if (!res.first()) {
				// Close queries
				res.close();
				pstmt.close();
				
				// Throw exception
				throw new HandlerException("No drawing with id: " + drawingID + "found.");
			}
			
			// Process result
			out.startElement(XMLConsts.EL_GALLERY_DRAWING);
			
			// Output drawing id
			out.writeElement(XMLConsts.EL_GALLERY_DRAWING_ID, drawingID);
			
			// Get the number of stages
			int numStages = res.getInt("stage");
			
			// Output the number of stages
			out.writeElement(XMLConsts.EL_GALLERY_DRAWING_NUM_STAGES,
				String.valueOf(numStages));
				
			// For each stage, output a drawing stage node
			for (int num = 1; num <= numStages; num++) {
				outputStage(out, res, String.valueOf(num));
			}
			
			// End gallery drawing
			out.endElement(XMLConsts.EL_GALLERY_DRAWING);
			
			// Close query
			res.close();
			pstmt.close();
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
