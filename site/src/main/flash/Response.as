class Response {
	
	public var doneWithErrors: Boolean = false;
	public var errs: Array = new Array();
	public var responses: Array = null;
	
	/**
	 * Creates an array of errors based on an errors node
	 */
	private function parseErrors(errorsNode: XMLNode): Void {
		for (var num: Number = 0; num < errorsNode.childNodes.length; num++) {
			if (errorsNode.childNodes[num].nodeName = "error") {
				// Mark that an error has occured
				doneWithErrors = true;
				
				// Make a new error instance
				var err: ResponseError = new ResponseError();
				err.parseXML(errorsNode.childNodes[num]);
				errs.push(err);
			}
		}
	}
	
	/**
	 * Creates an array of ResponseOperators based on the response
	 */
	private function parseXML(xmlDoc: XML): Void {
		// Reset the responses
		responses = new Array();
		
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
				case "next_drawing":
					responseOp = new NextDrawingResponse();
					break;
				case "gallery_drawings":
					responseOp = new GalleryDrawingsResponse();
					break;
				case "errors":
					parseErrors(rootNode.childNodes[num]);
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