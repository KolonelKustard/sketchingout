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
	
	private static void DeleteDrawing(ResultSet res) throws SQLException {
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
	
	public static void DeleteDrawing(PreparedStatement pstmt) {
		
	}
}
