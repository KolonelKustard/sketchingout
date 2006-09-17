package com.totalchange.sketchingout;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GalleryThinner {
	private static void DeleteDrawing(File file) {
		try {
			if (file.exists()) file.delete();
		}
		catch (Exception e) {}
	}
	
	/**
	 * Deletes the files associated with a drawing
	 * 
	 * @param res
	 * @throws SQLException
	 */
	private static void deleteDrawingFiles(ResultSet res) throws SQLException {
		String thumb = res.getString("thumbnail_filename");
		if (thumb != null)
			DeleteDrawing(new File(SketchingoutSettings.FS_DRAWING_STORE + thumb));
		
		String pdf = res.getString("pdf_filename");
		if (pdf != null)
			DeleteDrawing(new File(SketchingoutSettings.FS_DRAWING_STORE + pdf));
		
		String swf = res.getString("anim_swf_filename");
		if (swf != null)
			DeleteDrawing(new File(SketchingoutSettings.FS_DRAWING_STORE + swf));
	}
	
	/**
	 * Deletes the last gallery drawing from the gallery
	 * 
	 * @param conn
	 */
	private static void deleteLastDrawing(Connection conn) throws SQLException {
		// Find the oldest drawing in the gallery
		PreparedStatement pstmt = SQLWrapper.getOldestGalleryDrawing(conn);
		ResultSet res = pstmt.executeQuery();
		try {
			if (res.first()) {
				// Delete all the files associated with this drawing
				deleteDrawingFiles(res);
				
				// Now delete the gallery entry
				SQLWrapper.deleteDrawing(conn, res.getString("id"));
			}
		}
		finally {
			res.close();
			pstmt.close();
		}
	}
	
	/**
	 * Deletes old drawings from the gallery based upon the defined maximum
	 * number of gallery items.
	 * 
	 * @param conn
	 * @throws SQLException
	 */
	public static void deleteOldDrawings(Connection conn) throws SQLException {
		// Only bother if the gallery is restricted
		if (SketchingoutSettings.MAX_STORED_DRAWINGS == -1) return;
		
		// Now need to find out how many gallery items there are...
		int numItems = SQLWrapper.getGallerySize(conn);
		
		// Find the difference between the max size and the current size
		int diff = numItems - SketchingoutSettings.MAX_STORED_DRAWINGS;
		
		// Delete the difference
		for (int num = 0; num < diff; num++)
			deleteLastDrawing(conn);
	}
}
