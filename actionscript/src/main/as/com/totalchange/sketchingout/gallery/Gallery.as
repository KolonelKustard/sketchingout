class Gallery {
	private static var NUM_TO_GET: Number = 20;
	
	private var currNum: Number = 0;
	private var theDrawings: Array = new Array();
	private var eof: Boolean = false;
	
	// This event is called when a GalleryDrawing response is ready to
	// be passed back to the client
	public var onNext: Function;
	
	/**
	 * Calls the callback for the next requested drawing
	 */
	function returnDrawing(index: Number): Void {
		if (index > -1) {
			onNext(theDrawings[index]);
		}
		else {
			onNext(null);
		}
	}
	
	function addGalleryDrawings(drawings: Array): Void {
		// We know the curr num will be the top of the cache
		currNum = theDrawings.length;
		
		for (var num = 0; num < drawings.length; num++) {
			// Add these drawings to the end of the array
			theDrawings.push(drawings[num]);
		}
		
		// See if resulted in more
		if (theDrawings.length > currNum) {
			// Return the first of the new lot
			returnDrawing(currNum);
		}
		else {
			// No new ones.  EOF time.
			eof = true;
			returnDrawing(-1);
		}
	}
	
	private function parseXML(theXML: XML): Void {
		var response: Response = new Response(theXML);
		
		// Run through the responses and pick out any gallery stuff
		var responses: Array = response.responses;
		for (var num = 0; num < responses.length; num++) {
			if (responses[num] instanceof GalleryDrawingsResponse) {
				addGalleryDrawings(GalleryDrawingsResponse(responses[num]).galleryDrawings);
			}
		}
	}
	
	private function onXMLLoaded(loadedOK: Boolean): Void {
		if (loadedOK) {
			// Ugly workaround for the XML classes event model.  Gets reference to this and
			// still passes the compile time checking!
			Gallery(Object(this).callBack).parseXML(XML(this));
		}
	}
	
	private function requestMore(): Void {
		// Construct response (callBack adds this intance to the response for future referencing)
		var responseXML: XML = new XML();
		responseXML.ignoreWhite = true;
		responseXML.onLoad = onXMLLoaded;
		
		// This is a massive kludge to make sure a reference to this instance of Gallery
		// can be retrieved as a property of the response XML object.  It's because the
		// nice addListener event model doesn't extend to the XML class.
		Object(responseXML).callBack = this;
		
		// Make gallery request
		var gall: GalleryDrawingsRequest = new GalleryDrawingsRequest();
		
		// Always start from the end of the cache
		gall.start = theDrawings.length;
		gall.quantity = NUM_TO_GET;
		
		// Make and submit request
		var request: Request = new Request();
		request.addRequest(gall);
		var submitRequest: XML = request.getXML();
		submitRequest.sendAndLoad(SketchingoutSettings.SKETCHINGOUT_URL, responseXML);
	}
	
	public function prev(): Void {
		if (currNum > 0) {
			currNum--;
			returnDrawing(currNum);
		}
		else {
			returnDrawing(-1);
		}
	}
	
	public function next(): Void {
		// If reached the end of file, return nothing
		if (eof) {
			returnDrawing(-1);
		}
		else {
			// See if the next call results in reaching the end of the cache
			if (theDrawings.length <= currNum) {
				// Ask for more!
				requestMore();
			}
			else {
				// Just return the next from the cache
				currNum++;
				returnDrawing(currNum);
			}
		}
	}
}