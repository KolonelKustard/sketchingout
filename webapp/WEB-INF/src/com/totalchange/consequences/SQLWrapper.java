/*
 * Created on 05-May-2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.totalchange.consequences;

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
	
	public static final ResultSet getUser(Connection conn, String userID) throws
	  SQLException {
		PreparedStatement pstmt = conn.prepareStatement(
		  "SELECT * FROM users WHERE id = ?"
		);
		
		pstmt.setString(1, userID);
		
		return pstmt.executeQuery();
	}
}