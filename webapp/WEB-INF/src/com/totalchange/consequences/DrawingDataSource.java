/*
 * Created on 19-Aug-2004
 */
package com.totalchange.consequences;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

import javax.activation.DataSource;

import com.totalchange.consequences.imageparsers.BufferedImageParser;
import com.totalchange.consequences.imageparsers.ConsequencesImageParserException;

/**
 * @author RalphJones
 * 
 * <p>This class is used for attaching a finished drawing to a number of emails
 * for sending off to the individuals involved</p>
 */
public class DrawingDataSource implements DataSource {
	
	private ImageParser parser;
	private ByteArrayOutputStream out;
	private byte[] drawing = null;
	
	public DrawingDataSource(int version, int width, int height, int scale) throws
		ConsequencesImageParserException {
		
		// Make somewhere to store the image data
		out = new ByteArrayOutputStream();
		
		// Make the parser
		parser = new ImageParser(version, width, height, scale, out, 
			new BufferedImageParser("png"));
	}

	/**
	 * @see javax.activation.DataSource#getContentType()
	 */
	public String getContentType() {
		return "image/png";
	}

	/**
	 * @see javax.activation.DataSource#getInputStream()
	 */
	public InputStream getInputStream() throws IOException {
		if (drawing == null) {
			// If no drawing, get it out the byte array input stream
			drawing = out.toByteArray();
			
			// Have now finished with the input stream so can signal to
			// garbage collector that memory is free
			out = null;
			parser = null;
		}
		
		return new ByteArrayInputStream(drawing);
	}

	/**
	 * @see javax.activation.DataSource#getName()
	 */
	public String getName() {
		return "Consequences.png";
	}

	/**
	 * @see javax.activation.DataSource#getOutputStream()
	 */
	public OutputStream getOutputStream() throws IOException {
		throw new IOException("Output stream not supported");
	}
	
	public void addStage(Reader xmlData) throws ConsequencesImageParserException {
		if (parser == null) throw new RuntimeException("Cannot add to drawing once " +
			"a call to getInputSource has been made.");
		
		parser.addStage(xmlData);
	}
	
	public void addSignature(Reader xmlData) throws ConsequencesImageParserException {
		if (parser == null) throw new RuntimeException("Cannot add to drawing once " +
			"a call to getInputSource has been made.");
		
		parser.addSignature(xmlData);
	}

}
