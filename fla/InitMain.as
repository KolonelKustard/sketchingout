// **************************************************************
// * Create and set the properties of the main controller class *
// **************************************************************
holderClip = new DrawingPage();

holderClip.prevDrawing = lastDrawCanvas;
holderClip.nextDrawing = mainCanvas;
holderClip.nextDrawingID = _root.did;
holderClip.userDetails.userID = _root.uid;
holderClip.userDetails.nameEdit = yourName_txt;
holderClip.userDetails.emailEdit = yourEmail_txt;
holderClip.userDetails.sigCanvas = sigCanvas;
holderClip.dragClip = dragClip_mc;
holderClip.friendsEmailEdit = friendsEmail_txt;
holderClip.countdownClip = countdownClip;

hintsLoader.defaultHint = null;
hintsLoader.addAssociation(yourName_txt, "uiparts/pleaseentername_msg.swf");
hintsLoader.addAssociation(yourEmail_txt, "uiparts/enteryouremail_msg.swf");
hintsLoader.addAssociation(sigCanvas, "uiparts/signyourname_msg.swf");
hintsLoader.addAssociation(friendsEmail_txt, "uiparts/enterafriendsemail_msg.swf");

// Cache error messages as hints
hintsLoader.addAssociation(null, "uiparts/wheresdrawing_msg.swf");
hintsLoader.addAssociation(null, "uiparts/entername_msg.swf");
hintsLoader.addAssociation(null, "uiparts/checkyouremail_msg.swf");
hintsLoader.addAssociation(null, "uiparts/friendsemail_gonewrong_msg.swf");
hintsLoader.addAssociation(null, "uiparts/oopsgonewrong_msg.swf");


//Set up custom cursor
this.attachMovie("pencil_id", "pencil_mc", this.getNextHighestDepth());
pencil_mc._visible = false;
mainCanvas.penMovieClip = pencil_mc;
sigCanvas.penMovieClip = pencil_mc;

//hide UI parts that do not draw
send_btn._visible = false;
yourName_txt._visible = false;
yourEmail_txt._visible = false;
friendsEmail_txt._visible = false;
dragClip_mc._visible = false;
mainCanvas._visible = false;
countdownClip._visible = false;
bodyparts._visible = false;
headHighlight._visible = false;
bodyHighlight._visible = false;
legsHighlight._visible = false;
feetHighlight._visible = false;
news_txt._visible = false;
viewGallery_btn._visible = false;
clearSig_btn._visible = false;
clearDrawing_btn._visible = false;

//called when specified drawn UI part has finished
function showNonDrawUI() {
	send_btn._visible = true;
	yourName_txt._visible = true;
	yourEmail_txt._visible = true;
	friendsEmail_txt._visible = true;
	mainCanvas._visible = true;
	countdownClip._visible = true;
	news_txt._visible = true;
	viewGallery_btn._visible = true;
	clearSig_btn._visible = true;
	clearDrawing_btn._visible = true;
}


// **************************************************************
// * Load UI parts into the loader clips                        *
// **************************************************************
logo_ldr.onMovieClipPlayed = function(){}
logo_ldr.loadMovie("uiparts/logo.swf");

whatsthis_ldr.onMovieClipPlayed = function(){}
whatsthis_ldr.loadMovie("uiparts/whatsthis_box.swf");

introtxt_ldr.onMovieClipPlayed = function() {
	showNonDrawUI();
}
introtxt_ldr.loadMovie("uiparts/introtext.swf");

newsbox_ldr.onMovieClipPlayed = function() {}
newsbox_ldr.loadMovie("uiparts/news_box.swf");

drawbox_ldr.onMovieClipPlayed = function() {}
drawbox_ldr.loadMovie("uiparts/draw_box.swf");

sigbox_ldr.onMovieClipPlayed = function() {}
sigbox_ldr.loadMovie("uiparts/sig_box.swf");

viewgallerybtn_ldr.onMovieClipPlayed = function() {}
viewgallerybtn_ldr.loadMovie("uiparts/viewgallery_btn.swf");

hintsbox_ldr.onMovieClipPlayed = function(){}
hintsbox_ldr.loadMovie("uiparts/hints_box.swf");

txtfldsbtn_ldr.onMovieClipPlayed = function() {}
txtfldsbtn_ldr.loadMovie("uiparts/txtflds-btn.swf");







// **************************************************************
// * Define event functions                                     *
// **************************************************************

/**
 * Called before any data is sent to the server.
 */
function onStartLoading(): Void {
	_root.hintsLoader.defaultHint = "uiparts/loading_msg.swf";

}

/**
 * Called when the server returns a response.  The loadedOK parameter is
 * true if the request/response took place.  The only reason this would be
 * false is if the server was not reachable.
 */
function onEndLoading(loadedOK: Boolean): Void {
	trace("Done " + loadedOK);
	//focus 'Name' text field
	//Selection.setFocus("yourEmail_txt");
		
}

/**
 * Called when a new drawing is received.  The stage number indicates the
 * current stage to be drawn by the client (e.g. 1 = head, 4 = feet).
 */
holderClip.onNewDrawing = function(stage: Number): Void {
	trace("Starting new drawing of stage: " + stage);
	switch (stage) {
		case 1 :
			_root.hintsLoader.defaultHint = "uiparts/yourturnhead_msg.swf";
			headHighlight._visible = true;
			bodyparts._visible = true;
			break;
		case 2 :
			_root.hintsLoader.defaultHint = "uiparts/yourturnbody_msg.swf";
			bodyHighlight._visible = true;
			bodyparts._visible = true;
			break;
		case 3 :
			_root.hintsLoader.defaultHint = "uiparts/yourturnlegs_msg.swf";
			legsHighlight._visible = true;
			bodyparts._visible = true;
			break;
		case 4 :
			_root.hintsLoader.defaultHint = "uiparts/yourturnfeet_msg.swf";
			feetHighlight._visible = true;
			bodyparts._visible = true;
			break;
		default :
			_root.hintsLoader.defaultHint = "uiparts/oopsgonewrong_msg.swf";
	}
}

/**
 * This is called by the countdown timer to say that time is running out.
 * The interval at which this function is called is defined in:
 * SketchingoutSettings.COUNTDOWN_TIMER_REMINDER
 */
countdownClip.onNearlyDone = function(timeRemaining: Number): Void {
	hintsLoader.showCachedHint("uiparts/quick_msg.swf");
	//trace("You have only " + String(timeRemaining / 1000) + " seconds remaining to draw)");
}

/**
 * This event is called if the user runs out of time while drawing.  If this
 * happens they can not be allowed to submit their drawing otherwise it could
 * cock up the locking mechanisms on the server.
 */
countdownClip.onDone = function(): Void {
	//ADD HINTS CLIP HERE
	trace("Oops took too long in drawing.  Fetching another drawing.");
	requestDrawing();
}

/**
 * Called if the person neglects to bother to do a drawing.
 */
holderClip.onErrorNoDrawing = function(): Void {
	hintsLoader.showCachedHint("uiparts/wheresdrawing_msg.swf");
}

/**
 * Called when the friends email address isn't blank but doesn't have an @
 * sign and a full stop.
 */
holderClip.onErrorInvalidFriendsEmail = function(): Void {
	hintsLoader.showCachedHint("uiparts/friendsemail_gonewrong_msg.swf");
}

/**
 * Called if the drawing ID that is passed in is not valid.  This could be if the
 * drawing has already been done or if the private drawing locked time has expired.
 */
holderClip.onErrorInvalidDrawingID = function(): Void {
	trace("Drawing expired");
}

/**
 * Called when no name is entered in the user details.
 */
holderClip.userDetails.onErrorNoName = function(): Void {
	hintsLoader.showCachedHint("uiparts/entername_msg.swf");
}

/**
 * Called when no email address or an invalid email address is entered.
 */
holderClip.userDetails.onErrorInvalidEmail = function(): Void {
	hintsLoader.showCachedHint("uiparts/checkyouremail_msg.swf");
}

/**
 * Generic error handler.  This is only called in the event of a very
 * serious error (i.e. server returns an error) and as such should 
 * occur incredibly infrequently.  As such a dialog would be a valid
 * way to display the details of the event.
 */
holderClip.onError = function(error: ResponseError): Void {
	trace("Error: " + error.message);
	trace("Stack trace: " + error.fullStackTrace);
	hintsLoader.showCachedHint("uiparts/oopsgonewrong_msg.swf");
}




// **************************************************************
// * Stage interaction (e.g. button presses)                    *
// **************************************************************

/**
 * Clear the main canvas
 */
clearDrawing_btn.onPress = function() {
	holderClip.clearDrawing();
}
// Prevent tab stop on this button
clearDrawing_btn.tabEnabled = false;

/**
 * Clear the signature canvas
 */
clearSig_btn.onPress = function() {
	holderClip.clearSignature();
}
// Prevent tab stop on this button
clearSig_btn.tabEnabled = false;

viewGallery_btn.onPress = function() {
	getURL("gallery.jsp");
}




// **************************************************************
// * XML Handling                                               *
// **************************************************************

/**
 * Handles a response from the server
 */
function onXMLLoaded(loadedOK: Boolean): Void {
	onEndLoading(loadedOK);
	
	if (loadedOK) {
		holderClip.parseResponse(this);
	}
	
	// Re-enable the submit button
	send_btn.enabled = true;
}

/**
 * Does the business
 */
send_btn.onPress = function() {
	var submitRequest: XML = holderClip.getSubmitRequest();
	
	// Above call returns null if an error occured
	if (submitRequest != null) {
		// Stop the submit button from working
		send_btn.enabled = false;
		
		var responseXML: XML = new XML();
		responseXML.ignoreWhite = true;
		responseXML.onLoad = onXMLLoaded;
		
		onStartLoading();
		submitRequest.sendAndLoad(SketchingoutSettings.SKETCHINGOUT_URL, responseXML);
	}
}
// Prevent tab stop on this button
send_btn.tabEnabled = false;




// **************************************************************
// * Initialisation                                             *
// **************************************************************

function requestDrawing(): Void {
	var initRequest: XML = holderClip.getInitRequest();
	var responseXML: XML = new XML();
	responseXML.ignoreWhite = true;
	responseXML.onLoad = onXMLLoaded;

	onStartLoading();
	initRequest.sendAndLoad(SketchingoutSettings.SKETCHINGOUT_URL, responseXML);
}

// Initialise the controller
holderClip.init();

// Now at the end of setting everything up, perform the initial request for user
// details and next drawing
requestDrawing();

// Stop the playback on this frame
stop();