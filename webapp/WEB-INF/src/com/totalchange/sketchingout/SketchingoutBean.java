/*
 * Created on 05-May-2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.totalchange.sketchingout;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;

import com.totalchange.sketchingout.RandomGUID;

/**
 * @author RalphJones
 *
 * <p>This bean is a simple user id generator.  At present it does nothing other 
 * than generate a new user id if one isn't passed in a cookie.  There is no need 
 * at present to do any database mangling in this call as will automatically create
 * a new user record in the database on the first submission from the client.</p>
 * <p>The main point of the bean is to provide an id (existing or new) to the
 * Flash movie so it has a point of reference.  May decide to secure this a little
 * more later on by doing a database check.  But it seems pretty pointless at
 * present to try and tackle spoofing.  In fact it may prove quite amusing if
 * some little punk does decide to spoof others.</p>
 * <p>Now this class also provides a few thumbnails from the gallery</p>
 */
public class SketchingoutBean {
	private static final String COOKIE_NAME = "consequences";
	private static final int SECONDS_PER_YEAR = 60*60*24*365;
	
	private static final int NUM_GALLERY_THUMBS = 5;
	
	private String userID;
	private String drawingID;
	
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet res;
	
	public String getFlashParams() {
		// See if got to pass a next drawing parameter or not
		if (drawingID == null) {
			return SketchingoutSettings.URL_PARAM_USER_ID + "=" + userID;
		}
		else {
			return SketchingoutSettings.URL_PARAM_USER_ID + "=" + userID + "&" +
				SketchingoutSettings.URL_PARAM_DRAWING_ID + "=" + drawingID;
		}
	}
	
	/**
	 * <p>Checks to see if a cookie already resides.  If it does, that is used to
	 * get the user id.  If it doesn't then a new id is created.<p>
	 * <p>When an id is retrieved/created, a cookie is then sent back with this
	 * id.</p>
	 * 
	 * @param request The servlet request
	 * @param response The servlet response
	 */
	public void initiate(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();
		
		// Look for userID from cookie
		userID = null;
		if (cookies != null) {
			for (int num = 0; num < cookies.length; num++) {
				if (cookies[num].getName().equals(COOKIE_NAME)) {
					userID = cookies[num].getValue();
					break;
				}
			}
		}
		
		// If no user id, make one
		if (userID == null) {
			userID = new RandomGUID().toString();
		}
		
		// Set user id back into a cookie
		Cookie cookie = new Cookie(COOKIE_NAME, userID);
		cookie.setMaxAge(SECONDS_PER_YEAR);
		response.addCookie(cookie);
		
		// Look for drawing id
		drawingID = request.getParameter(SketchingoutSettings.URL_PARAM_DRAWING_ID);
	}
	
	/**
	 * <p>For the client to use to decide how many thumbnails to get from the
	 * gallery</p>
	 * 
	 * @return Number of gallery thumbnails to display
	 */
	public int getNumThumbnails() {
		return NUM_GALLERY_THUMBS;
	}
	
	/**
	 * <p>Must be called by the client before attempting to get any thumbnails</p>
	 */
	public void connect() throws SQLException, ClassNotFoundException {
		conn = SQLWrapper.makeConnection();
		pstmt = SQLWrapper.getHomepageThumbnails(conn, NUM_GALLERY_THUMBS);
		res = pstmt.executeQuery();
	}
	
	/**
	 * <p>The client calls this method to get the next thumbnail.  Returns null
	 * if no more drawings left</p>
	 * 
	 * @return The url to the next thumbnail
	 */
	public String getNextThumbnail() throws SQLException {
		if (res.next()) {
			return SketchingoutSettings.URL_DRAWING_STORE + res.getString("thumbnail_filename");
		}
		else {
			return null;
		}
	}
	
	/**
	 * <p>Must be called by the client once finished with thumbnails</p>
	 */
	public void disconnect() throws SQLException {
		if (res != null) {
			res.close();
			res = null;
		}
		
		if (pstmt != null) {
			pstmt.close();
			pstmt = null;
		}
		
		if (conn != null) {
			conn.close();
			conn = null;
		}
	}
}
