/*
 * Created on 02-May-2004
 *
 */
package com.totalchange.consequences;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Ralph Jones
 *
 * <p>This servlet is the only point of call for the Flash based consequences
 * application.  The job of this servlet is to parse the xml request and
 * initiate the appropriate response.</p> 
 */
public class ConsequencesServlet extends HttpServlet {

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		
		
	}

}
