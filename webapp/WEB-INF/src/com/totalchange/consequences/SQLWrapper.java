/*
 * Created on 05-May-2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
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
			"  head, " +
			"  body, " + 
			"  legs " +
			"FROM " +
			"  drawings " +
			"WHERE " +
			"  completed = 'N' AND " +
			"  locked < ? AND " +
			"  head_author_id <> ? AND " +
			"  body_author_id <> ? AND " +
			"  legs_author_id <> ? " +
			"ORDER BY " +
			"  body, " +
			"  legs " +
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
			"  head, " +
			"  body, " + 
			"  legs " +
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
			"  drawing_id = ?"
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
	
	public static final PreparedStatement insertDrawing() {
		return null;
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
}