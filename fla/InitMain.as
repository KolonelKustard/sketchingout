// Create the controller instance, destroying one if it already exists
holderClip = new DrawingPage();

// Set the controllers properties
holderClip.prevDrawing = lastDrawCanvas;
holderClip.nextDrawing = mainCanvas;
holderClip.userDetails.nameEdit = yourName_txt;
holderClip.userDetails.emailEdit = yourEmail_txt;
holderClip.userDetails.sigCanvas = sigCanvas;
holderClip.dragClip = dragClip_mc;
holderClip.friendsEmailEdit = friendsEmail_txt;

// Initialise the controller
holderClip.init();

// Setup the button event handlers
clearDrawing_btn.onPress = function() {
	holderClip.clearDrawing();
}

send_btn.onPress = function() {
	holderClip.submitUserAndDrawing();
}

clearSig_btn.onPress = function() {
	holderClip.clearSignature();
}

/**
 * Handles a response from the server
 */
function onXMLLoaded(loadedOK: Boolean): Void {
	if (loadedOK) {
		holderClip.parseResponse(this);
	}
}

// Now at the end of setting everything up, perform the initial request for user
// details and next drawing
var initRequest: XML = holderClip.getInitRequest();
var responseXML: XML = new XML();
responseXML.ignoreWhite = true;
responseXML.onLoad = onXMLLoaded;
initRequest.sendAndLoad("http://localhost:8080/consequences/consequences", responseXML);

// Stop the playback on this frame
stop();