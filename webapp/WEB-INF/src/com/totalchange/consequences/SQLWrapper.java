/*
 * Created on 05-May-2004
 */
package com.totalchange.consequences;

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
 * why this current version is using HSQLDB</p>
 */
public class SQLWrapper {
	private static final String DB_CLASSNAME = "com.mysql.jdbc.Driver";
	private static final String DB_URL = "jdbc:mysql://localhost/consequences";
	private static final String DB_USERNAME = "";
	private static final String DB_PASSWORD = "";
	
	public static final Connection makeConnection() throws ClassNotFoundException, 
		SQLException {
			
		Class.forName(DB_CLASSNAME);
		Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
		return conn;
	}
	
	public static final PreparedStatement getUser(Connection conn, String userID) throws
		SQLException {
			
		PreparedStatement pstmt = conn.prepareStatement(
			"SELECT id, name, email, signature FROM users WHERE id = ?"
		);
		
		pstmt.setString(1, userID);
		
		return pstmt;
	}
	
	public static final int INS_USER_ID = 1;
	public static final int INS_USER_NAME = 2;
	public static final int INS_USER_EMAIL = 3;
	public static final int INS_USER_SIGNATURE = 4; 
	public static final PreparedStatement insertUser(Connection conn) throws
		SQLException {
			
		PreparedStatement pstmt = conn.prepareStatement(
			"INSERT INTO users(id, name, email, signature) VALUES(?, ?, ?, ?)"
		);
		
		return pstmt;
	}
	
	public static final int UPD_USER_ID = 4;
	public static final int UPD_USER_NAME = 1;
	public static final int UPD_USER_EMAIL = 2;
	public static final int UPD_USER_SIGNATURE = 3;
	public static final PreparedStatement updateUser(Connection conn) throws
		SQLException {
			
		PreparedStatement pstmt = conn.prepareStatement(
			"UPDATE users SET name = ?, email = ?, signature = ? WHERE id = ?"
		);
		
		return pstmt;
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
			"  locked = ? " +
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
	
	public static final int INS_DRAW_DRAWING = 7;
	public static final int INS_DRAW_SIGNATURE = 8;
	public static final PreparedStatement insertDrawing(Connection conn, 
		String drawingID, String distinguishedID, String userID, String userName, 
		String userEmail, int lockSecs) throws SQLException{
			
		PreparedStatement pstmt = conn.prepareStatement(
			"INSERT INTO drawings(" +
			"  id, completed, locked, distinguished_id, stage, " +
			"  stage_1_author_id, stage_1_author_name, stage_1_author_email, " +
			"  stage_1, stage_1_signature" +
			") VALUES( " +
			"  ?, 'N', ?, ?, 1, ?, ?, ?, ?, ?" +
			")"
		);
		
		// Set parameters
		pstmt.setString(1, drawingID);
		pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis() + (lockSecs * 1000)));
		pstmt.setString(3, distinguishedID);
		pstmt.setString(4, userID);
		pstmt.setString(5, userName);
		pstmt.setString(6, userEmail);
		
		// Default drawing and signature to null
		pstmt.setString(INS_DRAW_DRAWING, null);
		pstmt.setString(INS_DRAW_SIGNATURE, null);
		
		return pstmt;
	}
	
	public static final int UPD_DRAW_DRAWING = 8;
	public static final int UPD_DRAW_SIGNATURE = 9;
	public static final PreparedStatement updateDrawing(Connection conn, 
		String drawingID, boolean complete, String distinguishedID, int stage,
		String userID, String userName,	String userEmail, int lockSecs)
		throws SQLException {
		
		// Convert the stage number into a string to use to identify the field names
		// in the statement
		String stageID = String.valueOf(stage);
		
		PreparedStatement pstmt = conn.prepareStatement(
			"UPDATE " +
			"  drawings " +
			"SET " +
			"  completed = ?, " +
			"  locked = ?, " +
			"  distinguished_id = ?, " +
			"  stage = ?, " +
			"  stage_" + stageID + "_author_id = ?, " +
			"  stage_" + stageID + "_author_name = ?, " +
			"  stage_" + stageID + "_author_email = ?, " +
			"  stage_" + stageID + " = ?, " +
			"  stage_" + stageID + "_signature = ? " +
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
		pstmt.setInt(4, stage);
		pstmt.setString(5, userID);
		pstmt.setString(6, userName);
		pstmt.setString(7, userEmail);
		
		// Initialise drawing and signature to null
		pstmt.setString(UPD_DRAW_DRAWING, null);
		pstmt.setString(UPD_DRAW_SIGNATURE, null);
		
		// Set drawing id into place
		pstmt.setString(10, drawingID);
		
		return pstmt;
	}
	
	public static final PreparedStatement getDrawing(Connection conn, String friendlyID)
		throws SQLException {
		
		PreparedStatement pstmt = conn.prepareStatement(
			"SELECT " +
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
	
	/**
	 * Utility procedure to output a blob to the xml writer as the data in an element 
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
	
	/**
	 * Utility procedure to copy a clob from one result set to a clob in another
	 * prepared statement.
	 * 
	 * @param in
	 * @param out
	 * @param param
	 * @throws SQLException
	 * @throws IOException
	 */
	public static final void copyClob(Clob in, PreparedStatement out, int param)
		throws SQLException {
			
		if (in == null) {
			// Set to null
			out.setString(param, null);
		}
		else {
			// Try and get a reader
			Reader reader = in.getCharacterStream();
			
			if ((reader == null) || (in.length() <= 0)) {
				// No reader or no chars, set to null
				out.setString(param, null);
			}
			else {
				// Send the reader to the prepared statement as a char stream
				out.setCharacterStream(param, reader, (int)in.length());
			} 
		}
	}
}