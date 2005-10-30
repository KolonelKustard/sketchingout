/*
 * Created on 05-May-2004
 */
package com.totalchange.sketchingout;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.sql.*;

/**
 * @author Ralph Jones
 *
 * <p>This class encapsulates all the SQL queries used by the consequences
 * application.  The aim is to allow easier transport from one RDBMS to
 * another.</p>
 * <p>The reason an object relational mapping tool like Hibernate hasn't been
 * used is purely because of portability.  We cannot presently afford to pay
 * for decent hosting, so must work towards the lowest spec.  Thus explaining
 * why this current version is using MySQL.</p>
 */
public class SQLWrapper {
	public static final Connection makeConnection() throws ClassNotFoundException, 
		SQLException {
			
		Class.forName(SketchingoutSettings.DB_CLASSNAME);
		Connection conn = DriverManager.getConnection(
			SketchingoutSettings.DB_URL,
			SketchingoutSettings.DB_USERNAME,
			SketchingoutSettings.DB_PASSWORD
		);
		
		return conn;
	}
	
	public static final PreparedStatement getNextDrawingPublic(Connection conn, String userID)
		throws SQLException {
		
		PreparedStatement pstmt = conn.prepareStatement(
			"SELECT " +
			"  id, " +
			"  stage, " +
			"  stage_1, " +
			"  stage_2, " + 
			"  stage_3 " +
			"FROM " +
			"  drawings " +
			"WHERE " +
			"  completed = 'N' AND " +
			"  locked < ? AND " +
			"  stage_1_author_id <> ? AND " +
			"  (stage_2_author_id IS NULL OR stage_2_author_id <> ?) AND " +
			"  (stage_3_author_id IS NULL OR stage_3_author_id <> ?) " +
			"ORDER BY " +
			"  stage DESC " +
			"LIMIT 1"
		);
		
		pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
		pstmt.setString(2, userID);
		pstmt.setString(3, userID);
		pstmt.setString(4, userID);
		
		return pstmt;
	}
	
	public static final PreparedStatement getNextDrawingPrivate(Connection conn, 
		String distinguishedID) throws SQLException {
		
		PreparedStatement pstmt = conn.prepareStatement(
			"SELECT " +
			"  id, " +
			"  locked, " +
			"  stage, " +
			"  stage_1, " +
			"  stage_2, " + 
			"  stage_3 " +
			"FROM " +
			"  drawings " +
			"WHERE " +
			"  completed = 'N' AND " +
			"  distinguished_id = ?"
		);
		
		pstmt.setString(1, distinguishedID);
		
		return pstmt;
	}
	
	public static final void lockDrawing(Connection conn, String drawingID,
		int lockSecs) throws SQLException {
		
		PreparedStatement pstmt = conn.prepareStatement(
			"UPDATE " +
			"  drawings " +
			"SET " +
			"  locked = ?, " +
			"  distinguished_id = null " +
			"WHERE " +
			"  id = ?"
		);
		
		// Convert seconds into milliseconds and add to the current time
		long lock = System.currentTimeMillis() + (lockSecs * 1000);
		
		// Set parameters
		pstmt.setTimestamp(1, new Timestamp(lock));
		pstmt.setString(2, drawingID);
		
		// Execute
		pstmt.execute();
		
		// Close
		pstmt.close();
	}
	
	public static final int INS_DRAW_DRAWING = 9;
	public static final int INS_DRAW_SIGNATURE = 10;
	public static final PreparedStatement insertDrawing(Connection conn, 
		String drawingID, String distinguishedID, int width, int height,
		String userID, String userName, String userEmail, int lockSecs)
		throws SQLException{
			
		PreparedStatement pstmt = conn.prepareStatement(
			"INSERT INTO drawings(" +
			"  id, completed, locked, distinguished_id, width, height, version," +
			"  stage, stage_1_author_id, stage_1_author_name, stage_1_author_email, " +
			"  stage_1, stage_1_signature" +
			") VALUES( " +
			"  ?, 'N', ?, ?, ?, ?, " + SketchingoutSettings.PRESENT_DRAWING_VERSION + 
			"  , 1, ?, ?, ?, ?, ?" +
			")"
		);
		
		// Set parameters
		pstmt.setString(1, drawingID);
		pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis() + (lockSecs * 1000)));
		pstmt.setString(3, distinguishedID);
		pstmt.setInt(4, width);
		pstmt.setInt(5, height);
		pstmt.setString(6, userID);
		pstmt.setString(7, userName);
		pstmt.setString(8, userEmail);
		
		// Default drawing and signature to null
		pstmt.setString(INS_DRAW_DRAWING, null);
		pstmt.setString(INS_DRAW_SIGNATURE, null);
		
		return pstmt;
	}
	
	public static final int UPD_DRAW_DRAWING = 9;
	public static final int UPD_DRAW_SIGNATURE = 10;
	public static final PreparedStatement updateDrawing(Connection conn, 
		String drawingID, boolean complete, String distinguishedID, int expandHeight,
		int stage, String userID, String userName, String userEmail, int lockSecs)
		throws SQLException {
		
		// Convert the stage number into a string to use to identify the field names
		// in the statement
		String stageStr = String.valueOf(stage);
		
		PreparedStatement pstmt = conn.prepareStatement(
			"UPDATE " +
			"  drawings " +
			"SET " +
			"  completed = ?, " +
			"  locked = ?, " +
			"  distinguished_id = ?, " +
			"  height = height + ?, " +
			"  stage = ?, " +
			"  stage_" + stageStr + "_author_id = ?, " +
			"  stage_" + stageStr + "_author_name = ?, " +
			"  stage_" + stageStr + "_author_email = ?, " +
			"  stage_" + stageStr + " = ?, " +
			"  stage_" + stageStr + "_signature = ? " +
			"WHERE " +
			"  id = ?"
		);
		
		// Set parameters
		if (complete) {
			pstmt.setString(1, "Y");
		} else {
			pstmt.setString(1, "N");
		}
		
		pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis() + (lockSecs * 1000)));
		pstmt.setString(3, distinguishedID);
		pstmt.setInt(4, expandHeight);
		pstmt.setInt(5, stage);
		pstmt.setString(6, userID);
		pstmt.setString(7, userName);
		pstmt.setString(8, userEmail);
		
		// Initialise drawing and signature to null
		pstmt.setString(UPD_DRAW_DRAWING, null);
		pstmt.setString(UPD_DRAW_SIGNATURE, null);
		
		// Set drawing id into place
		pstmt.setString(11, drawingID);
		
		return pstmt;
	}
	
	public static final PreparedStatement getDrawing(Connection conn, String friendlyID)
		throws SQLException {
		
		PreparedStatement pstmt = conn.prepareStatement(
			"SELECT " +
			"  version, " +
			"  width, " +
			"  height, " +
			"  stage, " +
			"  stage_1_author_name, " +
			"  stage_1, " +
			"  stage_1_signature, " +
			"  stage_2_author_name, " +
			"  stage_2, " +
			"  stage_2_signature, " +
			"  stage_3_author_name, " +
			"  stage_3, " +
			"  stage_3_signature, " +
			"  stage_4_author_name, " +
			"  stage_4, " +
			"  stage_4_signature " +
			"FROM " +
			"  drawings " +
			"WHERE " +
			"  friendly_id = ?"
		);
		
		pstmt.setInt(1, Integer.valueOf(friendlyID).intValue());
		
		return pstmt;
	}

	public static final PreparedStatement getCompleteDrawing(Connection conn, 
		String drawingID) throws SQLException {
		
		PreparedStatement pstmt = conn.prepareStatement(
			"SELECT " +
			"  friendly_id, " +
			"  version, " +
			"  width, " +
			"  height, " +
			"  stage, " +
			"  stage_1_author_name, " +
			"  stage_1_author_email, " +
			"  stage_2_author_name, " +
			"  stage_2_author_email, " +
			"  stage_3_author_name, " +
			"  stage_3_author_email, " +
			"  stage_4_author_name, " +
			"  stage_4_author_email, " +
			"  stage_1, " +
			"  stage_1_signature, " +
			"  stage_2, " +
			"  stage_2_signature, " +
			"  stage_3, " +
			"  stage_3_signature, " +
			"  stage_4, " +
			"  stage_4_signature " +
			"FROM " +
			"  drawings " +
			"WHERE " +
			"  id = ?"
		);
		
		pstmt.setString(1, drawingID);
		
		return pstmt;
	}
	
	public static final PreparedStatement getDrawingForTransfer(Connection conn, 
		String drawingID) throws SQLException {
		
		PreparedStatement pstmt = conn.prepareStatement(
			"SELECT " +
			"  * " +
			"FROM " +
			"  drawings " +
			"WHERE " +
			"  id = ?"
		);
		
		pstmt.setString(1, drawingID);
		
		return pstmt;
	}
	
	public static final PreparedStatement getCompleteDrawings(Connection conn) throws 
		SQLException {
		
		return conn.prepareStatement(
			"SELECT " +
			"  id " +
			"FROM " +
			"  drawings " +
			"WHERE " +
			"  completed = 'Y'"
		);
	}
	
	public static final PreparedStatement getHomepageThumbnails(Connection conn,
		int numToGet) throws SQLException {
		
		PreparedStatement pstmt = conn.prepareStatement(
			"SELECT " +
			"  thumbnail_filename " +
			"FROM " +
			"  gallery " +
			"WHERE " +
			"  thumbnail_filename IS NOT NULL " +
			"ORDER BY " +
			"  friendly_id DESC " +
			"LIMIT " + numToGet
		);
			
		return pstmt;
	}
	
	public static final PreparedStatement getLatestGalleryDrawings(Connection conn, 
		int offset, int rowCount) 
		throws SQLException {
		
		PreparedStatement pstmt = conn.prepareStatement(
			"SELECT " +
			"  friendly_id, " +
			"  width, " +
			"  height, " +
			"  stage, " +
			"  stage_1_author_name, " +
			"  stage_2_author_name, " +
			"  stage_3_author_name, " +
			"  stage_4_author_name, " +
			"  anim_swf_filename, " +
			"  pdf_filename " +
			"FROM " +
			"  gallery " +
			"ORDER BY " +
			"  friendly_id DESC " +
			"LIMIT " + offset + ", " + rowCount
		);
			
		return pstmt;
	}
	
	/**
	 * <p>Copies a drawing from the active drawings table to the gallery
	 * table</p>
	 * 
	 * @param conn
	 * @param drawingID
	 * @param pdfFilename
	 * @param swfFilename
	 * @throws SQLException
	 */
	public static final void saveToGallery(Connection conn, String drawingID,
		String thumbnailFilename, String pdfFilename, String swfFilename) throws
		SQLException {
		
		// Get fields to be copied
		PreparedStatement pstmt = conn.prepareStatement(
			"INSERT INTO gallery( " +
			"  id, " +
			"  width, " +
			"  height, " +
			"  stage, " +
			"  stage_1_author_id, " +
			"  stage_1_author_name, " +
			"  stage_2_author_id, " +
			"  stage_2_author_name, " +
			"  stage_3_author_id, " +
			"  stage_3_author_name, " +
			"  stage_4_author_id, " +
			"  stage_4_author_name, " +
			"  thumbnail_filename, " +
			"  anim_swf_filename, " +
			"  pdf_filename) " +
			
			"SELECT " +
			"  id, " +
			"  width, " +
			"  height, " +
			"  stage, " +
			"  stage_1_author_id, " +
			"  stage_1_author_name, " +
			"  stage_2_author_id, " +
			"  stage_2_author_name, " +
			"  stage_3_author_id, " +
			"  stage_3_author_name, " +
			"  stage_4_author_id, " +
			"  stage_4_author_name, " +
			"  ?, " +
			"  ?, " +
			"  ? " +
			"FROM " +
			"  drawings " +
			"WHERE " +
			"  id = ?"
		);
		pstmt.setString(1, thumbnailFilename);
		pstmt.setString(2, swfFilename);
		pstmt.setString(3, pdfFilename);
		pstmt.setString(4, drawingID);
		
		pstmt.execute();
		pstmt.close();
	}
	
	/**
	 * <p>Gets the oldest drawing ready for deletion</p>
	 * 
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	public static final PreparedStatement getOldestGalleryDrawing(
		Connection conn) throws SQLException {
		
		PreparedStatement pstmt = conn.prepareStatement(
			"SELECT " +
			"  id, " +
			"  thumbnail_filename, " +
			"  anim_swf_filename, " +
			"  pdf_filename " +
			"FROM " +
			"  gallery " +
			"ORDER BY " +
			"  friendly_id ASC " +
			"LIMIT 1"
		);
		
		return pstmt;
	}
	
	/**
	 * <p>Gets the number of items currently in storage in the active drawing
	 * pool</p>
	 * @return
	 */
	public static final int getDrawingPoolSize(Connection conn) throws
		SQLException {
		
		PreparedStatement pstmt = conn.prepareStatement(
			"SELECT COUNT(*) FROM drawings"
		);
		ResultSet res = pstmt.executeQuery();
		
		try {
			return res.getInt(0);
		}
		finally {
			res.close();
			pstmt.close();
		}
	}
	
	/**
	 * <p>Gets the number of items presently held in the gallery</p>
	 * 
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	public static final int getGallerySize(Connection conn) throws
		SQLException {
		
		PreparedStatement pstmt = conn.prepareStatement(
			"SELECT COUNT(*) FROM gallery"
		);
		ResultSet res = pstmt.executeQuery();
		
		try {
			return res.getInt(0);
		}
		finally {
			res.close();
			pstmt.close();
		}
	}
	
	/**
	 * <p>Deletes a single gallery drawing identified by its unique ID</p>
	 * 
	 * @param conn
	 * @param drawingID
	 * @throws SQLException
	 */
	public static final void deleteGalleryDrawing(Connection conn,
		String drawingID) throws SQLException {
		
		PreparedStatement pstmt = conn.prepareStatement(
			"DELETE FROM gallery WHERE id = ?"
		);
		
		pstmt.setString(1, drawingID);
		pstmt.execute();
		pstmt.close();
	}
	
	/**
	 * <p>Deletes a single drawing from the main drawing table identified by
	 * unique ID</p>
	 * 
	 * @param conn
	 * @param drawingID
	 * @throws SQLException
	 */
	public static final void deleteDrawing(Connection conn, String drawingID) 
		throws SQLException {
		
		PreparedStatement pstmt = conn.prepareStatement(
			"DELETE FROM drawings WHERE id = ?"
		);
		
		pstmt.setString(1, drawingID);
		pstmt.execute();
		pstmt.close();
	}
	
	/**
	 * <p>Utility procedure to output a blob to the xml writer as the data in an
	 * element</p> 
	 * 
	 * @param out
	 * @param in
	 * @param element
	 */
	public static final void outputClob(Writer out, Clob clob) 
		throws SQLException, IOException {
		
		if (clob != null) {
			// Get input stream as a reader
			Reader in = clob.getCharacterStream();
		
			if (in != null) {			
				// Pass blob to writer
				int inChar = in.read();
				while (inChar > -1) {
					out.write(inChar);
					inChar = in.read();
				}
			}
		}
	}
}