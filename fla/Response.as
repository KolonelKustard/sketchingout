class Response {
	
	public var doneWithErrors: Boolean = false;
	public var errs: Array = new Array();
	public var responses: Array = new Array();
	
	/**
	 * Creates a response object.  This will parse the xml document that is returned into
	 * a series of response objects.
	 */
	public function Response(xmlDoc: XML) {
		trace(xmlDoc.toString());
	}
	
	
}