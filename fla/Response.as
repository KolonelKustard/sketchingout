class Response {
	
	public var doneWithErrors: Boolean = false;
	public var errs: Array = new Array();
	public var responses: Array = new Array();
	
	/**
	 * Creates an array of errors.
	 */
	private function parseErrors(errorsNode: XMLNode): Void {
	}
	
	/**
	 * Creates an array of ResponseOperators based on the response
	 */
	private function parseXML(xmlDoc: XML): Void {
		// Get the root node
		var rootNode: XMLNode = xmlDoc.firstChild;
		
		// Throw an error if not a response node
		if (rootNode.nodeName != "response") throw new Error("Not a valid response");
		
		// Run through the child nodes of the main response and act on them depending on
		// its type
		for (var num: Number = 0; num < rootNode.childNodes.length; num++) {
			// Make a blank response operator
			var responseOp: ResponseOperator = null;
			
			switch (rootNode.childNodes[num].nodeName) {
				case "user_details":
					responseOp = new UserDetailsResponse();
					break;
				case "next_drawing":
					responseOp = new NextDrawingResponse();
					break;
				case "gallery_drawing":
					responseOp = new GalleryDrawingResponse();
					break;
				case "errors":
					trace("Errors");
					break;
			}
			
			// If got a response operator, set this node into it and add it to the
			// array of responses
			if (responseOp != null) {
				responseOp.parseXML(rootNode.childNodes[num]);
				responses.push(responseOp);
			}
		}
	}
	
	/**
	 * Creates a response object.  This will parse the xml document that is returned into
	 * a series of response objects.
	 */
	public function Response(xmlDoc: XML) {
		parseXML(xmlDoc);
	}
	
	
}