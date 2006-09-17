/*
 * Created on 03-Jun-2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.totalchange.sketchingout;

/**
 * @author RalphJones
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class HandlerException extends Exception {
	public static final long serialVersionUID = 1;
	
	private int errorCode = -1;
	
	public HandlerException(int errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}
	
	public HandlerException(String message) {
		super(message);
	}
	
	public int getErrorCode() {
		return errorCode;
	}
}
