class SketchingoutSettings {
	/*
	 * The BASE_URL constant needs to be blank for in place use as this
	 * indicates a relative path.  However for running within the Flash
	 * development environment it can be useful to change this to an
	 * absolute path.
	 */
	public static var BASE_URL: String = "http://localhost:8080/sketchingout/";
	
	public static var SKETCHINGOUT_URL: String = BASE_URL + "sketchingout";
	
	public static var MIN_OFFSET_Y: Number = 20;
	public static var MAX_OFFSET_Y: Number = 40;
	public static var DEFAULT_OFFSET_Y: Number = 20;
	
	// A reminder gets sent out after this period in milliseconds.
	public static var COUNTDOWN_TIMER_REMINDER: Number = 1000 * 60;
	
	public static var LAST_STAGE_NUM: Number = 4;
	
	// The following constants define the default drawing style
	public static var DEFAULT_LINE_COLOR: Number = 0x0000CC;
	public static var DEFAULT_LINE_THICKNESS: Number = 1;
}