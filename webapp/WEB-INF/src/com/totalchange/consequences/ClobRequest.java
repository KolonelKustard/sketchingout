/*
 * Created on 10-Jun-2004
 *
 */
package com.totalchange.consequences;

import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import org.xml.sax.Attributes;

/**
 * @author RalphJones
 *
 * <p>A utility class to aid with streaming data into a CLOB object using JDBC</p> 
 * 
 */
public class ClobRequest implements RequestHandler {
	
	private PreparedStatement pstmt;
	private int paramNum;
	
	private ConsequencesErrors errs;
	private CharArrayWriter clobChrs = new CharArrayWriter();
	
	public ClobRequest(PreparedStatement pstmt, int paramNum) throws IOException {
			
		this.pstmt = pstmt;
		this.paramNum = paramNum;
	}

	/**
	 * @see com.totalchange.consequences.RequestHandler#start(com.totalchange.consequences.XMLWriter, java.sql.Connection, com.totalchange.consequences.ConsequencesErrors, org.xml.sax.Attributes)
	 */
	public void start(
		XMLWriter out,
		Connection conn,
		ConsequencesErrors errs,
		Attributes attributes)
		throws HandlerException {
			
		this.errs = errs;
	}

	/**
	 * @see com.totalchange.consequences.RequestHandler#data(char[], int, int)
	 */
	public void data(char[] ch, int start, int length)
		throws HandlerException {
			
		clobChrs.write(ch, start, length);
	}

	/**
	 * @see com.totalchange.consequences.RequestHandler#getChild(java.lang.String)
	 */
	public RequestHandler getChild(String name) throws HandlerException {
		return null;
	}

	/**
	 * @see com.totalchange.consequences.RequestHandler#end()
	 */
	public void end() throws HandlerException {
		try {
			char[] clob = clobChrs.toCharArray();
			CharArrayReader reader = new CharArrayReader(clob);
			pstmt.setCharacterStream(paramNum, reader, clob.length);
			
		}
		catch (Exception e) {
			errs.addException(this.getClass(), e);
		}
	}

}
