/*
 * Created on 03-Jun-2004
 *
 */
package com.totalchange.sketchingout;

import java.sql.Connection;
import org.xml.sax.Attributes;

/**
 * @author RalphJones
 *
 * <p>Interface to allow easier addition of 
 */
public interface RequestHandler {
	public void start(XMLWriter out, Connection conn, SketchingoutErrors errs, 
		Attributes attributes) throws HandlerException;
	public void data(char[] ch, int start, int length) throws HandlerException;
	public RequestHandler getChild(String name) throws HandlerException;
	public void end() throws HandlerException;
}
