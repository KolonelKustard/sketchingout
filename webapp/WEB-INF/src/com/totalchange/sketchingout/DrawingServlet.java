/*
 * Created on 27-Jun-2004
 */
package com.totalchange.sketchingout;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.totalchange.sketchingout.imageparsers.BufferedImageParser;
import com.totalchange.sketchingout.imageparsers.PdfImageParser;
import com.totalchange.sketchingout.imageparsers.SketchingoutImageParser;
import com.totalchange.sketchingout.imageparsers.SketchingoutImageParserException;
import com.totalchange.sketchingout.imageparsers.SwfAnimatedImageParser;
import com.totalchange.sketchingout.imageparsers.SwfImageParser;

/**
 * @author RalphJones
 */
public class DrawingServlet extends HttpServlet {
	
	private void parseRequest(String type, String drawingID, int scale, int loss,
		OutputStream out) throws SQLException, ClassNotFoundException,
		SketchingoutImageParserException {
		
		// Make connection
		Connection conn = SQLWrapper.makeConnection();
		
		// Get drawing
		PreparedStatement pstmt = SQLWrapper.getDrawing(conn, drawingID);
		ResultSet res = pstmt.executeQuery();
		
		try {
			// Check got a drawing
			if (!res.first()) throw new SQLException("Could not find drawing with id: " +
				drawingID);
				
			// Make a parser depending on the type
			SketchingoutImageParser parser;
			if (type.equals("png")) parser = new BufferedImageParser(type);
			else if (type.equals("jpg")) parser = new BufferedImageParser(type);
			else if (type.equals("swf")) parser = new SwfImageParser();
			else if (type.equals("animswf")) parser = new SwfAnimatedImageParser();
			else if (type.equals("pdf")) parser = new PdfImageParser();
			else throw new SketchingoutImageParserException(type + " not a supported type.");
			
			// Make an image parser
			ImageParser imageParser = new ImageParser(
				res.getInt("version"),
				res.getInt("width"),
				res.getInt("height"),
				scale,
				loss,
				out, 
				parser
			);
				
			// Find the number of stages
			int numStages = res.getInt("stage");
			
			// Run through the stages adding them to the parser
			for (int num = 0; num < numStages; num++) {
				try {
					imageParser.addStage(res.getCharacterStream("stage_" + (num + 1)));
				}
				catch (Exception e) {
					// If have an exception just print it to the console
					e.printStackTrace();
				}
			}
			
			// Run through the signatures adding them to the parser
			for (int num = 0; num < numStages; num++) {
				try {
					imageParser.addSignature(res.getCharacterStream("stage_" + 
						(num + 1) + "_signature"));
				}
				catch (Exception e) {
					// Again just print exception to the stage
					e.printStackTrace();
				}
			}
			
			// Close parser
			imageParser.close();
		}
		finally {
			res.close();
			pstmt.close();
			conn.close(); 
		}
	}
	
	/**
	 * Converts a basic type (e.g. "png") to a MIME type (e.g. "image/png")
	 * 
	 * @param type The basic type (e.g. "png")
	 * @return null if not an allowed type, or the allowed type (e.g. "image/png")
	 */
	private String getAllowedMimeType(String type) {
		if (type.equals("png")) return "image/png";
		else if (type.equals("jpg")) return "image/jpeg";
		else if (type.equals("swf")) return "application/x-shockwave-flash";
		else if (type.equals("animswf")) return "application/x-shockwave-flash";
		else if (type.equals("pdf")) return "application/pdf";
		else return null;
	}
	
	/**
	 * Actually does the work of a request.
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void doRequest(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		
		// Get type and id parameters
		String type = request.getParameter("type");
		String id = request.getParameter("id");
		String scaleStr = request.getParameter("scale");
		String lossStr = request.getParameter("loss");
		
		// Throw errors if blank
		if (type == null) throw new ServletException("No image type specified");
		if (id == null) throw new ServletException("No drawing id specified");
		
		int scale;
		if (scaleStr == null) scale = 100;
	    else scale = Integer.parseInt(scaleStr);
	    
		int loss;
		if (lossStr == null) loss = 0;
		else loss = Integer.parseInt(lossStr);
		
		// Find a mime type from basic type
		String mimeType = getAllowedMimeType(type);
		if (mimeType == null) throw new ServletException(type + " not a supported type");
		
		// Set the content type
		response.setContentType(mimeType);
		
		try {
			// Parse the request and output it
			parseRequest(type, id, scale, loss, response.getOutputStream());
		}
		catch (Exception e) {
			// Wrap the error in a ServletException
			throw new ServletException(e);  
		}
	}

	/**
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
			
		doRequest(request, response);
	}

	/**
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		
		doRequest(request, response);
	}

}
