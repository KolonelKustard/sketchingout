/*
 * Created on 31-May-2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.totalchange.consequences;

import java.util.ArrayList;
import java.io.IOException;

/**
 * @author RalphJones
 *
 * <p>A store for errors.  Errors may not be fatal and as such they are
 * stored as the request is processed in the Consequences servlet.  Any
 * errors are then returned as a part of the response and it's up to the
 * client as to how to respond to them.</p>
 */
public class ConsequencesErrors {
	/**
	 * @author RalphJones
	 *
	 * <p>Inner class construct for holding errors generated as we go along</p>
	 */
	class ConsequencesError {
		private String source;
		private String type;
		private String message;
		private StackTraceElement[] trace;
		
		public ConsequencesError(Class caller, Exception e) {
			source = caller.getName();
			type = e.getClass().getName();
			message = e.getMessage();
			trace = e.getStackTrace();
		}
		
		public String getSource() {
			return source;
		}
		
		public String getType() {
			return type;
		}
		
		public String getMessage() {
			return message;
		}
		
		public StackTraceElement[] getTrace() {
			return trace;
		}
	}
	
	private ArrayList errors;
	
	public ConsequencesErrors() {
		errors = new ArrayList();
	}
	
	public void addException(Class caller, Exception e) {
		errors.add(new ConsequencesError(caller, e));
	}
	
	public void clear() {
		errors.clear();
	}
	
	public int size() {
		return errors.size();
	}
	
	public ConsequencesError getError(int index) {
		return (ConsequencesError) errors.get(index);
	}
	
	/**
	 * Sends the errors to 
	 * 
	 * @param out XMLWriter instance to send errors to
	 */
	public void outputErrors(XMLWriter out) throws IOException {
		out.startElement(XMLConsts.EL_ERRORS);
		
		for (int num = 0; num < size(); num++) {
			// Get error
			ConsequencesError err = getError(num);
			
			// Output to stream
			out.startElement(XMLConsts.EL_ERROR);
			out.writeElement(XMLConsts.EL_ERROR_SRC, err.getSource());
			out.writeElement(XMLConsts.EL_ERROR_TYPE, err.getType());
			out.writeElement(XMLConsts.EL_ERROR_MESSAGE, err.getMessage());
			
			// See if there's a stack trace
			StackTraceElement[] trace = err.getTrace();
			if ((trace != null) && (trace.length > 0)) {
				// Add stack trace elements
				out.startElement(XMLConsts.EL_ERROR_STACK_TRACE);
				
				for (int numSt = 0; numSt < trace.length; numSt++) {
					out.writeElement(XMLConsts.EL_ERROR_TRACE, trace[numSt].toString());
				}
				
				out.endElement(XMLConsts.EL_ERROR_STACK_TRACE);
			}
			
			out.endElement(XMLConsts.EL_ERROR);
		}
		
		out.endElement(XMLConsts.EL_ERRORS);
	}
}
