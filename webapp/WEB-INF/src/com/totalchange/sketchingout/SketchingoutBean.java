/*
 * Created on 05-May-2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.totalchange.sketchingout;

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
 */
public class SketchingoutBean {
	private static final String COOKIE_NAME = "consequences";
	private static final int SECONDS_PER_YEAR = 60*60*24*365;
	
	private String userID;
	private String drawingID;
	
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
}
