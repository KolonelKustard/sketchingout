/*
 * Created on 02-May-2004
 *
 */
package com.totalchange.sketchingout;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * @author Ralph Jones
 *
 * <p>This servlet is the only point of call for the Flash based consequences
 * application.  The job of this servlet is to parse the xml request and
 * initiate the appropriate response.</p> 
 */
public class SketchingoutServlet extends HttpServlet {

	/* 
	 * <p>Handles raw post data as xml and passes it to the SAX parser to decide
	 * on the appropriate response</p>
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
			
		// Make sure response is formatted to XML
		response.setContentType("text/xml");
			
		// Create an instance of the XML writer used for the response
		XMLWriter writer = new XMLWriter(response.getWriter());
		writer.startElement(XMLConsts.EL_RESPONSE);
		
		// Create an instance of the error cache
		SketchingoutErrors errs = new SketchingoutErrors();
		
		Connection conn = null;
		
		try {
			// Create a JDBC connection to use for the duration of this request
			conn = SQLWrapper.makeConnection();
						
			// Get a SAX parser to use to parse the incoming request
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			parser.parse(request.getInputStream(), new XMLHandler(writer, errs, conn));
		}
		catch(Exception e) {
			// Add error to the error cache
			errs.addException(this.getClass(), e);
		}
		finally {
			// Close the JDBC connection
			try {
				if (conn != null) {
					conn.close();
				}
			}
			catch (SQLException e) {
			}
		}
		
		// If there are errors, add them to the output
		if (errs.size() > 0) {
			errs.outputErrors(writer);
		}
		
		// Finish the output
		writer.endElement(XMLConsts.EL_RESPONSE);
	}

}
