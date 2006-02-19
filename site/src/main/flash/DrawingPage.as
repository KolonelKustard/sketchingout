class DrawingPage {
	private var nextDrawingResponse: NextDrawingResponse = null;
	private var sentDrawing = false;
	
	// Objects public properties
	public var nextDrawingID: String = null;
	public var nextDrawing: CanvasMovieClip;
	public var prevDrawing: CanvasMovieClip;
	public var userDetails: UserDetails;
	public var dragClip: DragMovieClip;
	public var friendsEmailEdit: TextField;
	public var countdownClip: CountdownMovieClip;
	
	// Event handlers
	public var onDrawingFinished: Function = null;
	public var onNewDrawing: Function = null;
	
	// Error event handlers
	public var onErrorNoDrawing: Function = null;
	public var onErrorTooMuchCovered: Function = null;
	public var onErrorNotEnoughCovered: Function = null;
	public var onErrorInvalidFriendsEmail: Function = null;
	public var onErrorInvalidDrawingID: Function = null;
	
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
		
		// Set the default drawing style
		nextDrawing.color = SketchingoutSettings.DEFAULT_LINE_COLOR;
		nextDrawing.thickness = SketchingoutSettings.DEFAULT_LINE_THICKNESS;
		
		userDetails.sigCanvas.color = SketchingoutSettings.DEFAULT_LINE_COLOR;
		userDetails.sigCanvas.thickness = SketchingoutSettings.DEFAULT_LINE_THICKNESS;
		
		// Load the user from the Flash shared object
		userDetails.setUserDetails(new User(userDetails.userID));
	}
	
	/**
	 * Clears the main canvas
	 */
	public function clearDrawing(): Void {
		nextDrawing.clearDrawing();
	}
	
	/**
	 * Clears the signature canvas
	 */
	public function clearSignature(): Void {
		userDetails.sigCanvas.clearDrawing();
	}
	
	public function getInitRequest(): XML {
		// Construct a request for the next drawing
		var request: Request = new Request();
		
		// See if public or private
		if (nextDrawingID == null) {
			// Make a next public drawing request
			var nextDrawingRequest: PublicDrawingRequest = new PublicDrawingRequest();
		
			// Set your user id so server can avoid giving back a drawing you've
			// already been involved in
			nextDrawingRequest.userID = userDetails.userID;
			request.addRequest(nextDrawingRequest);
		}
		else {
			// Use the drawing id to make a next private drawing request
			var nextPrivateRequest: PrivateDrawingRequest = new PrivateDrawingRequest();
			nextPrivateRequest.userID = userDetails.userID;
			nextPrivateRequest.drawingID = nextDrawingID;
			request.addRequest(nextPrivateRequest);
			
			// Make sure can't use next drawing id by accident again
			nextDrawingID = null;
		}
		
		return request.getXML();
	}
	
	/**
	 * Makes sure the drawing and whatnot are all valid.  Also fires off error
	 * events if things go wrong.
	 */
	private function validateDrawing(): Boolean {
		if (nextDrawing.drawing.lines.length <= 0) {
			if (onErrorNoDrawing <> null) onErrorNoDrawing();
			return false;
		}
		
		var currOffsetY: Number = dragClip.getOffsetY();
		if (currOffsetY < SketchingoutSettings.MIN_OFFSET_Y) {
			if (onErrorTooMuchCovered <> null) onErrorTooMuchCovered(currOffsetY, SketchingoutSettings.MIN_OFFSET_Y);
			return false;
		}
		
		if (currOffsetY > SketchingoutSettings.MAX_OFFSET_Y) {
			if (onErrorNotEnoughCovered <> null) onErrorNotEnoughCovered(currOffsetY, SketchingoutSettings.MAX_OFFSET_Y);
			return false;
		}
		
		if ((friendsEmailEdit.text != "") && (!userDetails.validateEmailAddress(friendsEmailEdit.text))) {
			if (onErrorInvalidFriendsEmail <> null) onErrorInvalidFriendsEmail();
			return false;
		}
		
		return true;
	}
	
	private function makeSubmitDrawingRequest(): SubmitDrawingRequest {
		var subDraw: SubmitDrawingRequest = new SubmitDrawingRequest();
		
		subDraw.userID = userDetails.userID;
		subDraw.userName = userDetails.userName;
		subDraw.userEmail = userDetails.userEmail;
		subDraw.drawingID = nextDrawingResponse.drawingID;
		subDraw.stage = nextDrawingResponse.stage + 1;
		
		if (friendsEmailEdit.text == "")
			subDraw.nextUserEmail = null
		else
			subDraw.nextUserEmail = friendsEmailEdit.text;
		  
		// If the stage is the head, strip any whitespace from the top
		if (subDraw.stage == 1) nextDrawing.trim();
		
		// The drawing has to be shrunk and the offset calculated before submission
		subDraw.drawing = nextDrawing.drawing;
		subDraw.drawing.offsetY = dragClip.getOffsetY();
		subDraw.drawing.shrink();
		
		// Just set in the signature
		subDraw.signature = userDetails.sigCanvas.drawing;
		
		// Remember that drawing has been submitted
		sentDrawing = true;
		
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
			// Save the user details
			userDetails.saveUserDetails();
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
		
		// Set if the drag clip is visible or not depending if on the last stage
		// or not
		dragClip._visible = ((next.stage + 1) < SketchingoutSettings.LAST_STAGE_NUM);
		
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
		
		// Reset the friends email address
		friendsEmailEdit.text = "";
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
			if (responses[num] instanceof NextDrawingResponse) {
				var nextDrawing: NextDrawingResponse = NextDrawingResponse(responses[num]);
				setNextDrawing(nextDrawing);
			}
		}
		
		// See if had any errors
		if (response.doneWithErrors) {
			// Use the event handler to handle errors
			for (var num = 0; num < response.errs.length; num++) {
				// Deal with the specific error types
				switch (response.errs[num].code) {
					case ResponseError.ERR_INVALID_DRAWING_ID:
						if (onErrorInvalidDrawingID != null) onErrorInvalidDrawingID(response.errs[num]);
						break;
					default:
						if (onError != null) onError(response.errs[num]);
						break;
				}
			}
		}
		
		// If a drawing was sent then see if was error free?!?
		if (!response.doneWithErrors && sentDrawing) {
			if (onDrawingFinished != null) onDrawingFinished();
		}
		
		sentDrawing = false;
	}
}