class DrawingPage {
	private var nextDrawingResponse: NextDrawingResponse = null;
	
	// Objects public properties
	public var nextDrawing: CanvasMovieClip;
	public var prevDrawing: CanvasMovieClip;
	public var userDetails: UserDetails;
	public var dragClip: DragMovieClip;
	public var friendsEmailEdit: TextField;
	public var countdownClip: CountdownMovieClip;
	
	// Event handlers
	public var onNewDrawing: Function = null;
	
	// Error event handlers
	public var onErrorNoDrawing: Function = null;
	public var onErrorTooMuchCovered: Function = null;
	public var onErrorNotEnoughCovered: Function = null;
	public var onErrorInvalidFriendsEmail: Function = null;
	
	// Generic error event
	public var onError: Function = null;
	
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
		
		// Set the drag clip into the canvas clip
		nextDrawing.dragClip = dragClip;
	}
	
	/**
	 * Clears the main canvas
	 */
	public function clearDrawing(): Void {
		nextDrawing.drawing.clearDrawing();
	}
	
	/**
	 * Clears the signature canvas
	 */
	public function clearSignature(): Void {
		userDetails.sigCanvas.clearDrawing();
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
	 * Makes sure the drawing and whatnot are all valid.  Also fires off error
	 * events if things go wrong.
	 */
	private function validateDrawing(): Boolean {
		if (!nextDrawing.modified) {
			if (onErrorNoDrawing <> null) onErrorNoDrawing();
			return false;
		}
		
		var currOffsetY: Number = dragClip.getOffsetY();
		if (currOffsetY < ConsequencesSettings.MIN_OFFSET_Y) {
			if (onErrorTooMuchCovered <> null) onErrorTooMuchCovered(currOffsetY, ConsequencesSettings.MIN_OFFSET_Y);
			return false;
		}
		
		if (currOffsetY > ConsequencesSettings.MAX_OFFSET_Y) {
			if (onErrorNotEnoughCovered <> null) onErrorNotEnoughCovered(currOffsetY, ConsequencesSettings.MAX_OFFSET_Y);
			return false;
		}
		
		return true;
	}
	
	private function makeSubmitDrawingRequest(): SubmitDrawingRequest {
		var subDraw: SubmitDrawingRequest = new SubmitDrawingRequest();
		
		subDraw.userID = userDetails.userID;
		subDraw.drawingID = nextDrawingResponse.drawingID;
		subDraw.stage = nextDrawingResponse.stage + 1;
		
		if (friendsEmailEdit.text == "")
		  subDraw.nextUserEmail = null
		else
		  subDraw.nextUserEmail = friendsEmailEdit.text;
		
		// The drawing has to be shrunk and the offset calculated before submission
		subDraw.drawing = nextDrawing.drawing;
		subDraw.drawing.shrink();
		subDraw.drawing.offsetY = dragClip.getOffsetY();
		
		return subDraw;
	}
	
	/**
	 * Constructs a request to send a drawing and any user details that have changed.
	 * This function will return null if a validation error occurs.
	 */
	public function getSubmitRequest(): XML {
		// Validate
		if (!validateDrawing()) return null;
		if (!userDetails.validateUserDetails()) return null;
		
		// Construct request
		var request: Request = new Request();
		
		// Check to see if any user details have changed.
		if (userDetails.modified()) {
			request.addRequest(userDetails.getSubmitUserRequest());
		}
		
		// Always want the submit drawing request
		request.addRequest(makeSubmitDrawingRequest());
		
		// Lastly, get a new drawing to work with
		var nextDrawingRequest: PublicDrawingRequest = new PublicDrawingRequest();
		nextDrawingRequest.userID = userDetails.userID;
		request.addRequest(nextDrawingRequest);
		
		return request.getXML();
	}
	
	/**
	 * Sets up the next drawing
	 */
	private function setNextDrawing(next: NextDrawingResponse): Void {
		// Keep reference of drawing currently being used
		this.nextDrawingResponse = next;
		
		// Use the event handler to announce the next drawing
		if (onNewDrawing != null) onNewDrawing(next.stage + 1);
		
		// Set the timer ticking
		if (countdownClip != null) countdownClip.countdown(next.lockedSecs);
		
		// Reset next drawing
		nextDrawing.drawing = null;
		
		// Reset the height of the drag clip
		dragClip.resetPos();
		
		// Get the previous drawing
		var prevDraw: Drawing = next.drawing;
		
		// Move the previous drawing to overlap the current drawing according to the
		// offset value.
		prevDrawing._x = nextDrawing._x;
		prevDrawing._y = nextDrawing._y - (prevDraw.height - prevDraw.offsetY);
		
		// Set in the previous drawing
		prevDrawing.drawing = prevDraw;
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
			// Use the event handler to handle errors
			for (var num = 0; num < response.errs.length; num++) {
				if (onError != null) onError(response.errs[num]);
			}
		}
	}
}