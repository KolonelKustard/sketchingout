/*
 * Created on 14-Jun-2004
 *
 */
package com.totalchange.consequences;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author RalphJones
 *
 */
public class CompleteDrawingProcessor {
	public static final void process(Connection conn, 
		String drawingID) throws SQLException {
			
		System.out.println("Drawing Complete!  ID: " + drawingID);
	}
}
