class DrawingPage {
	public var nextDrawing: CanvasMovieClip;
	public var prevDrawing: CanvasMovieClip;
	public var userDetails: UserDetails;
	public var dragClip: DragMovieClip;
	public var friendsEmailEdit: TextField;
	
	/**
	 * The constructor just creates a new instance of the user details
	 * class.
	 */
	public function DrawingPage() {
		userDetails = new UserDetails();
	}
	
	/**
	 * This function is called once all references are in place and sets
	 * references everywhere
	 */
	public function init(): Void {		
		// Set the previous and next into the drag clip so it knows where to
		// cover up
		dragClip.nextDrawing = nextDrawing;
		dragClip.resetPos();
	}
	
	/**
	 * This function is the event handler for clicking the submit button
	 */
	public function submitUserAndDrawing(): Void {
	}
	
	/**
	 * Clears the main canvas
	 */
	public function clearDrawing(): Void {
		nextDrawing.drawing = null;
	}
	
	/**
	 * Clears the signature canvas
	 */
	public function clearSignature(): Void {
		userDetails.sigCanvas.drawing = null;
	}
	
	public function getInitRequest(): XML {
		// Construct a request for the initial user details and for the next
		// drawing to do.
		var userRequest: UserDetailsRequest = new UserDetailsRequest();
		
		// Set the user ID
		userRequest.userID = userDetails.userID;
		
		// Make a next public drawing request
		var nextDrawingRequest: PublicDrawingRequest = new PublicDrawingRequest();
		
		// Set your user id so server can avoid giving back a drawing you've
		// already been involved in
		nextDrawingRequest.userID = userDetails.userID;
		
		// Construct request
		var request = new Request();
		request.addRequest(userRequest);
		request.addRequest(nextDrawingRequest);
		
		return request.getXML();
	}
	
	/**
	 * Sets up the next drawing
	 */
	private function setNextDrawing(nextDrawing: NextDrawingResponse): Void {
		// TODO: Implement!
	}
	
	/**
	 * Parses an XML object that represents a response from the server
	 */
	public function parseResponse(xmlResponse: XML): Void {
		var response: Response = new Response(xmlResponse);
		
		// See what responses there are
		var responses: Array = response.responses;
		for (var num = 0; num < responses.length; num++) {
			// Do something based on the instance type
			if (responses[num] instanceof UserDetailsResponse) {
				// This is a user details response, get a user object from it
				var user: User = UserDetailsResponse(responses[num]).user;
				
				// Set the user details into the UserDetails instance
				userDetails.setUserDetails(user);
			}
			else if (responses[num] instanceof NextDrawingResponse) {
				var nextDrawing: NextDrawingResponse = NextDrawingResponse(responses[num]);
				setNextDrawing(nextDrawing);
			}
		}
		// See if had any errors
		if (response.doneWithErrors) {
			// Print out all the errors to the trace output window thingy
			for (var num = 0; num<response.errs.length; num++) {
				trace("");
				trace(response.errs[num]);
				trace("");
			}
		}
	}
}