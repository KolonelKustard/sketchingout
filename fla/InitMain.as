﻿// **************************************************************
// * Create and set the properties of the main controller class *
// **************************************************************
holderClip = new DrawingPage();

holderClip.prevDrawing = lastDrawCanvas;
holderClip.nextDrawing = mainCanvas;
holderClip.userDetails.userID = _root.uid;
holderClip.userDetails.nameEdit = yourName_txt;
holderClip.userDetails.emailEdit = yourEmail_txt;
holderClip.userDetails.sigCanvas = sigCanvas;
holderClip.dragClip = dragClip_mc;
holderClip.friendsEmailEdit = friendsEmail_txt;
holderClip.countdownClip = countdownClip;

//Set up custom cursor
this.attachMovie("pencil_id", "pencil_mc", this.getNextHighestDepth());
Mouse.hide();
var mouseListener:Object = new Object();
mouseListener.onMouseMove = function() {
  pencil_mc._x = _xmouse;
  pencil_mc._y = _ymouse;
  updateAfterEvent();
};
Mouse.addListener(mouseListener);


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

//called when specified drawn UI part has finished
function showNonDrawUI() {
	send_btn._visible = true;
	yourName_txt._visible = true;
	yourEmail_txt._visible = true;
	friendsEmail_txt._visible = true;
	dragClip_mc._visible = true;
	mainCanvas._visible = true;
	countdownClip._visible = true;
	news_txt._visible = true;
	//bodyparts._visible = true;
}


// **************************************************************
// * Load UI parts into the loader clips                        *
// **************************************************************
logo_ldr.onMovieClipPlayed = function() {trace("Logo Done");}
logo_ldr.loadMovie("uiparts/logo.swf");

whatsthis_ldr.onMovieClipPlayed = function() {trace("What's This Done");}
whatsthis_ldr.loadMovie("uiparts/whatsthis_box.swf");

introtxt_ldr.onMovieClipPlayed = function() 
	{
		trace("Intro Done");
		showNonDrawUI();
	}
introtxt_ldr.loadMovie("uiparts/introtext.swf");

newsbox_ldr.onMovieClipPlayed = function() {trace("News Box Done");}
newsbox_ldr.loadMovie("uiparts/news_box.swf");

drawbox_ldr.onMovieClipPlayed = function() {trace("Draw Box Done");}
drawbox_ldr.loadMovie("uiparts/draw_box.swf");

sigbox_ldr.onMovieClipPlayed = function() {trace("Sig Box Done");}
sigbox_ldr.loadMovie("uiparts/sig_box.swf");

viewgallerybtn_ldr.onMovieClipPlayed = function() {trace("View Gallery Done");}
viewgallerybtn_ldr.loadMovie("uiparts/viewgallery_btn.swf");

hintsbox_ldr.onMovieClipPlayed = function() {trace("Hints Box Done");}
hintsbox_ldr.loadMovie("uiparts/hints_box.swf");

txtfldsbtn_ldr.onMovieClipPlayed = function() {trace("Txt Flds Btn?!? Done");}
txtfldsbtn_ldr.loadMovie("uiparts/txtflds-btn.swf");







// **************************************************************
// * Define event functions                                     *
// **************************************************************

/**
 * Called before any data is sent to the server.
 */
function onStartLoading(): Void {
	trace("Loading...");
	_root.hintsLoader.loadMovie("uiparts/loading_msg.swf");
	headHighlight._visible = false;
	bodyHighlight._visible = false;
	legsHighlight._visible = false;
	feetHighlight._visible = false;
}

/**
 * Called when the server returns a response.  The loadedOK parameter is
 * true if the request/response took place.  The only reason this would be
 * false is if the server was not reachable.
 */
function onEndLoading(loadedOK: Boolean): Void {
	trace("Done " + loadedOK);
	
}

/**
 * Called when a new drawing is received.  The stage number indicates the
 * current stage to be drawn by the client (e.g. 1 = head, 4 = feet).
 */
holderClip.onNewDrawing = function(stage: Number): Void {
	trace("Starting new drawing of stage: " + stage);
	switch (stage) {
		case 1 :
			_root.hintsLoader.loadMovie("uiparts/yourturnhead_msg.swf");
			headHighlight._visible = true;
			bodyparts._visible = true;
			break;
		case 2 :
			_root.hintsLoader.loadMovie("uiparts/yourturnbody_msg.swf");
			bodyHighlight._visible = true;
			bodyparts._visible = true;
			break;
		case 3 :
			_root.hintsLoader.loadMovie("uiparts/yourturnlegs_msg.swf");
			legsHighlight._visible = true;
			bodyparts._visible = true;
			break;
		case 4 :
			_root.hintsLoader.loadMovie("uiparts/yourturnfeet_msg.swf");
			feetHighlight._visible = true;
			bodyparts._visible = true;
			dragClip_mc._visible = false;
			break;
		default :
			trace("oh dear")
	}
}

/**
 * This is called by the countdown timer to say that time is running out.
 * The interval at which this function is called is defined in:
 * SketchingoutSettings.COUNTDOWN_TIMER_REMINDER
 */
countdownClip.onNearlyDone = function(timeRemaining: Number): Void {
	trace("You have only " + String(timeRemaining / 1000) + " seconds remaining to draw)");
}

/**
 * This event is called if the user runs out of time while drawing.  If this
 * happens they can not be allowed to submit their drawing otherwise it could
 * cock up the locking mechanisms on the server.
 */
countdownClip.onDone = function(): Void {
	trace("Oops took too long in drawing.  Fetching another drawing.");
	requestDrawing();
}

/**
 * Called if the person neglects to bother to do a drawing.
 */
holderClip.onErrorNoDrawing = function(): Void {
	trace("Spaz alert: Do a drawing.");
}

/**
 * Called if the person covers too much of the drawing with the
 * dragger.
 */
holderClip.onErrorTooMuchCovered = function(currOffsetY: Number, minOffsetY: Number): Void {
	trace("Spaz alert: Don't cover up so much: " + currOffsetY + "/" + minOffsetY);
}

/**
 * Called if the person doesn't cover up enough of the drawing with
 * the dragger.
 */
holderClip.onErrorNotEnoughCovered = function(currOffsetY: Number, maxOffsetY: Number): Void {
	trace("Spaz alert: Not enough covered up: " + currOffsetY + "/" + maxOffsetY);
}

/**
 * Called when the friends email address isn't blank but doesn't have an @
 * sign and a full stop.
 */
holderClip.onErrorInvalidFriendsEmail = function(): Void {
	trace("Spaz alert: Your friends email address is spacced up.");
}

/**
 * Called when no name is entered in the user details.
 */
holderClip.userDetails.onErrorNoName = function(): Void {
	trace("Spaz alert: What's your name bitch?");
	_root.hintsLoader.loadMovie("uiparts/entername_msg.swf");
}

/**
 * Called when no email address or an invalid email address is entered.
 */
holderClip.userDetails.onErrorInvalidEmail = function(): Void {
	trace("Spaz alert: That email address does not conform to the ISO 9765523789834 standards.");
	_root.hintsLoader.loadMovie("uiparts/checkyouremail_msg.swf");
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

/**
 * Clear the signature canvas
 */
clearSig_btn.onPress = function() {
	holderClip.clearSignature();
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
}

/**
 * Does the business
 */
send_btn.onPress = function() {
	var submitRequest: XML = holderClip.getSubmitRequest();
	
	// Above call returns null if an error occured
	if (submitRequest != null) {
		var responseXML: XML = new XML();
		responseXML.ignoreWhite = true;
		responseXML.onLoad = onXMLLoaded;
		
		onStartLoading();
		submitRequest.sendAndLoad(SketchingoutSettings.SKETCHINGOUT_URL, responseXML);
	}
}




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