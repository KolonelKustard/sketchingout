/*
 * Created on 03-Jun-2004
 *
 */
package com.totalchange.consequences;

import java.sql.Connection;

import org.xml.sax.Attributes;

/**
 * @author RalphJones
 *
 * <p>This class does nothing other than provide a factory service for child
 * handlers.</p>
 */
public class DefaultRequest implements RequestHandler {

	/**
	 * @see com.totalchange.consequences.RequestHandler#start(java.sql.Connection, com.totalchange.consequences.ConsequencesErrors, org.xml.sax.Attributes)
	 */
	public void start(
		XMLWriter out,
		Connection conn,
		ConsequencesErrors errs,
		Attributes attributes) throws HandlerException {
			
	}

	/**
	 * @see com.totalchange.consequences.RequestHandler#data(char[], int, int)
	 */
	public void data(char[] ch, int start, int length) throws HandlerException {
	}

	/**
	 * Provides a factory service for request handlers
	 * 
	 * @see com.totalchange.consequences.RequestHandler#getChild(java.lang.String)
	 */
	public RequestHandler getChild(String name) throws HandlerException {
		if (name.equals(XMLConsts.EL_USER_DETAILS)) {
			return new UserRequest();
		}
		else if (name.equals(XMLConsts.EL_USER_SUBMIT)) {
			return new UserSubmitRequest();
		}
		else if (name.equals(XMLConsts.EL_NEXT_DRAWING)) {
			return new NextDrawingRequest();
		}
		else if (name.equals(XMLConsts.EL_SUBMIT_DRAWING)) {
			return new SubmitDrawingRequest();
		}
		else if (name.equals(XMLConsts.EL_GALLERY_DRAWINGS)) {
			return new GalleryDrawingsRequest();
		}
		else {
			return null;
		}
	}

	/**
	 * @see com.totalchange.consequences.RequestHandler#end()
	 */
	public void end() throws HandlerException {
	}

}
