/*
 * Created on 31-May-2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.totalchange.consequences;

import java.io.Writer;
import java.io.IOException;

import org.xml.sax.Attributes;

/**
 * @author RalphJones
 *
 * <p>A very basic xml output writer.  Extends a standard Java writer and adds
 * simple methods for the efficient output of XML to a writer stream.</p>
 * <p>This class is pretty much a cut down version of a class by the same name
 * from <a href="http://www.megginson.com">megginson.com</a>.  However don't
 * need the added complexity of that class, so have a very simple butchered
 * version here.</p>
 */
public class XMLWriter extends Writer {
	private Writer out;
	private boolean inCData;
	
	/**
	 * <p>Create an XMLWriter instance that outputs to a Writer.</p>
	 * 
	 * @param out A Writer
	 */
	public XMLWriter(Writer out) {
		this.out = out;
		
		inCData = false;
	}
	
	/**
	 * <p>Starts an xml element.  Just wraps the local name with brackets.</p>
	 * 
	 * @param localName The name of the element
	 * @throws IOException From the writer
	 */
	public void startElement(String localName) throws IOException {
		out.write('<' + localName + '>');
	}
	
	/**
	 * <p>Starts an xml element.  Same as the simpler startElement(String)
	 * method but allows attributes to be added too.</p>
	 * 
	 * @param localName The name of the element
	 * @param attributes
	 * @throws IOException From the writer
	 */
	public void startElement(String localName, Attributes attributes)
		throws IOException {
		
		// Start the element
		out.write('<' + localName);
		
		// Go through all the attributes adding them
		int length = attributes.getLength();
		for (int num = 0; num < length; num++) {
			// Pass the name straight through
			out.write(' ' + attributes.getQName(num) + "=\"");
			
			// Encode the value
			this.write(attributes.getValue(num));
			
			// Pass the closing quote straight through
			out.write('\"');
		}
		
		// Close the element tag
		out.write('>');
	}
	
	/**
	 * <p>Ends an element.  Wraps localName with closing brackets.</p>
	 * 
	 * @param localName The name of the element
	 * @throws IOException From the writer
	 */
	public void endElement(String localName) throws IOException {
		out.write("</" + localName + '>');
	}
	
	/**
	 * Starts an XML CDATA section.  Also stops the processing of char data that
	 * is written.  MUST start and end the CDATA section after starting an element
	 * and before ending an element.
	 * 
	 * @throws IOException
	 */
	public void startCData() throws IOException {
		out.write("<![CDATA[");
		inCData = true;
	}
	
	/**
	 * <p>Ends a CDATA section.</p>
	 * 
	 * @throws IOException
	 */
	public void endCData() throws IOException {
		out.write("]]>");
		inCData = false;
	}
	
	/**
	 * <p>Utility procedure that outputs an entire element in one.  If the
	 * writeIfNullValue boolean is true then the element is written as blank.  Otherwise
	 * the element may be omitted if the value is null.
	 * 
	 * @param localName
	 * @param data
	 * @param writeIfNullValue Set to true to make sure an element is written
	 * @throws IOException
	 */
	public void writeElement(String localName, String data, boolean writeIfNullValue) 
		throws IOException {
			
		if (data != null) {
			startElement(localName);
			write(data);
			endElement(localName);
		}
		else if (writeIfNullValue) {
			out.write('<' + localName + " />");
		}
	}
	
	/**
	 * <p>Utility procedure that outputs an entire element in one.  Leaves the
	 * element out entirely if the String passed in is null.</p>
	 * 
	 * @param localName The name of the element
	 * @param data The String data to put in the element
	 * @throws IOException From the writer
	 */
	public void writeElement(String localName, String data) throws IOException {
		if (data != null) {
			startElement(localName);
			write(data);
			endElement(localName);
		}
	}
	
	/**
	 * Closes the underlying Writer
	 * 
	 * @see java.io.Writer#close()
	 */
	public void close() throws IOException {
		out.close();
	}

	/**
	 * Flushes the underlying Writer
	 * 
	 * @see java.io.Writer#flush()
	 */
	public void flush() throws IOException {
		out.flush();
	}

	/**
	 * <p>Writes characters to the underlying Writer encoding them according to
	 * the rules defined by XML.</p>
	 * 
	 * @see java.io.Writer#write(char[], int, int)
	 */
	public void write(char[] cbuf, int off, int len) throws IOException {
		if (!inCData) {
			for (int i = off; i < off + len; i++) {
				switch (cbuf[i]) {
					case '&':
						out.write("&amp;");
						break;
					case '<':
						out.write("&lt;");
						break;
					case '>':
						out.write("&gt;");
						break;
					case '\"':
						out.write("&quot;");
						break;
					default:
						if (cbuf[i] > '\u007f') {
							out.write("&#");
							out.write(Integer.toString(cbuf[i]));
							out.write(';');
						} else {
							out.write(cbuf[i]);
						}
				}
			}
		}
		else {
			out.write(cbuf, off, len);
		}
	}

}
