/*
 * Created on 10-Jun-2004
 *
 */
package com.totalchange.consequences;

import java.io.PipedReader;
import java.io.PipedWriter;
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
	
	private PipedReader pipeIn;
	private PipedWriter pipeOut;
	private int totalLength;
	
	public ClobRequest(PreparedStatement pstmt, int paramNum) throws IOException {
			
		this.pstmt = pstmt;
		this.paramNum = paramNum;
		
		// Setup piped reader/writer
		pipeOut = new PipedWriter();
		pipeIn = new PipedReader(pipeOut);
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
			
		totalLength += length;
		try {
			pipeOut.write(ch, start, length);
		}
		catch (Exception e) {
			errs.addException(this.getClass(), e);
		}
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
			// Flush output
			pipeOut.flush();
			
			// Setup output on the prepared statement
			pstmt.setCharacterStream(paramNum, pipeIn, totalLength);
			
			// Close streams
			pipeOut.close();
			pipeIn.close();
		}
		catch (Exception e) {
			errs.addException(this.getClass(), e);
		}
	}

}
