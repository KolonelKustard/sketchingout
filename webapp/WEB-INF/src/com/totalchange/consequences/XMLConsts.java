/*
 * Created on 31-May-2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.totalchange.consequences;

/**
 * @author RalphJones
 *
 * <p>Pointless class that provides String constants for the various XML
 * elements and attributes or whatever.</p>
 */
public class XMLConsts {
	public static final String EL_REQUEST = "request";
	public static final String EL_RESPONSE = "response";
	
	public static final String EL_ERRORS = "errors";
	public static final String EL_ERROR = "error";
	public static final String EL_ERROR_SRC = "src";
	public static final String EL_ERROR_TYPE = "type";
	public static final String EL_ERROR_MESSAGE = "message";
	public static final String EL_ERROR_STACK_TRACE = "stacktrace";
	public static final String EL_ERROR_TRACE = "trace";
	
	public static final String EL_USER_DETAILS = "user_details";
	public static final String EL_USER_SUBMIT = "user_submit";
	public static final String AT_USER_ID = "id";
	public static final String EL_USER_ID = "id";
	public static final String AT_USER_NAME = "name";
	public static final String EL_USER_NAME = "name";
	public static final String AT_USER_EMAIL = "email";
	public static final String EL_USER_EMAIL = "email";
	public static final String EL_USER_SIGNATURE = "signature";
	
	public static final String EL_NEXT_DRAWING = "next_drawing";
	public static final String AT_NEXT_DRAWING_ID = "drawing_id";
	public static final String AT_NEXT_DRAWING_USER_ID = "user_id";
	public static final String EL_NEXT_DRAWING_ID = "drawing_id";
	public static final String EL_NEXT_DRAWING_STAGE = "stage";
	public static final String EL_NEXT_DRAWING_LOCKED_SECS = "locked_secs";
	public static final String EL_NEXT_DRAWING_DRAWING = "drawing";
	
	public static final String EL_SUBMIT_DRAWING = "submit_drawing";
	public static final String AT_SUBMIT_DRAWING_USER_ID = "user_id";
	public static final String AT_SUBMIT_DRAWING_DRAWING_ID = "drawing_id";
	public static final String AT_SUBMIT_DRAWING_STAGE = "stage";
	public static final String AT_SUBMIT_DRAWING_NEXT_USER_EMAIL = "next_user_email";
	public static final String EL_SUBMIT_DRAWING_DRAWING = "drawing";
	
	public static final String EL_GALLERY_DRAWINGS = "gallery_drawings";
	public static final String AT_GALLERY_DRAWINGS_TYPE = "type";
	public static final String AT_GALLERY_DRAWINGS_START = "start";
	public static final String AT_GALLERY_DRAWINGS_QUANTITY = "quantity";
	
	public static final String EL_GALLERY_DRAWING = "gallery_drawing";
	public static final String AT_GALLERY_DRAWING_ID = "drawing_id";
	public static final String EL_GALLERY_DRAWING_ID = "drawing_id";
	public static final String EL_GALLERY_DRAWING_WIDTH = "width";
	public static final String EL_GALLERY_DRAWING_HEIGHT = "height";
	public static final String EL_GALLERY_DRAWING_NUM_STAGES = "num_stages";
	
	public static final String EL_GALLERY_DRAWING_STAGE_AUTHOR = "stage_author";
	public static final String EL_GALLERY_DRAWING_STAGE_AUTHOR_STAGE = "stage";
	public static final String EL_GALLERY_DRAWING_STAGE_AUTHOR_NAME = "name";
	
	public static final String EL_DRAWING_CANVAS = "canvas";
	public static final String AT_DRAWING_CANVAS_WIDTH = "width";
	public static final String AT_DRAWING_CANVAS_HEIGHT = "height";
	public static final String AT_DRAWING_CANVAS_OFFSET_X = "offsetx";
	public static final String AT_DRAWING_CANVAS_OFFSET_Y = "offsety";
	public static final String EL_DRAWING_LINE = "line";
	public static final String EL_DRAWING_POINT = "point";
	public static final String AT_DRAWING_POINT_X = "x";
	public static final String AT_DRAWING_POINT_Y = "y";
}
