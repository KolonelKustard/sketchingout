//validation
// Create an instance of the GuiValidationClip, named mcBubble
    _root.attachMovie("GuiValidationClip", "mcBubble", 1000);


//find OffsetY (area chosen by user as visible) and store/display(for debug)
var positionOfBottomOfCanvas:Number = mainCanvas._y+mainCanvas._height;
var positionOfBottomOfDragClip:Number = hideClip._y+mainCanvas._height;
_global.distanceBetween = positionOfBottomOfCanvas-positionOfBottomOfDragClip;
offsetYPos_txt.text = Math.round(distanceBetween); 



//Define draggable movieclip and set bounds
hideClip.onPress = function() {
	//constrain (locked, left, top, right, bottom)
	this.startDrag(false, 12, mainCanvas._y-mainCanvas._height, 12,0 );
	
	mainCanvas.drawing.readOnly = true;
};
//when stopped dragging, find the offsetY
hideClip.onRelease = function() {
	this.stopDrag();
	mainCanvas.drawing.readOnly = false;
};

clearDrawing_btn.onPress = function() {
	mainCanvas.drawing = null;
}

//this is temporary for testing - simply runs the initial call to server
getData_btn.onPress = function() {
	getFirstRequest();
};

gallery_btn.onPress = function() {
	gotoAndPlay("gallery");
}

clearSig_btn.onPress = function() {
	sigCanvas.drawing = null;
}



function getFirstRequest() {
	var userRequest:UserDetailsRequest = new UserDetailsRequest();
	// this is who you are. a generated id. collected from querystring
	userRequest.userID = "1";
	// Make a next public drawing request
	var nextDraw:PublicDrawingRequest = new PublicDrawingRequest();
	//this is which drawing you want back
	nextDraw.userID = nextDrawingID_stp.value;
	//  The aim of the request operator is to
	// allow multiple requests to be sent with one network transaction.
	var request:Request = new Request();
	request.addRequest(userRequest);
	request.addRequest(nextDraw);
	// Now get the xml document that results from the request
	var xmlRequest:XML = request.getXML();
	trace(xmlRequest);
	var xmlResponse:XML = new XML();
	xmlResponse.ignoreWhite = true;
	// Setup the onLoad handler
	xmlResponse.onLoad = function(ok:Boolean) {
		if (ok) {
			// Make a new Response instance.  The Response class wraps the XML document
			// that is returned by the server.  The code below then extracts the various
			// parts of the response.
			var response:Response = new Response(this);
			// See what responses there are
			var responses:Array = response.responses;
			for (var num = 0; num<responses.length; num++) {
				// Do something based on the instance type
				if (responses[num] instanceof UserDetailsResponse) {
					// This is a user details response, get a user object from it
					var userDetails:User = UserDetailsResponse(responses[num]).user;
					//trace("User: ["+userDetails.id+"] "+userDetails.name+" <"+userDetails.email+"> Signature: "+userDetails.signature.toString());
					//set text fields with userDetails
					if (userDetails.name != null && userDetails.email != null) {
						yourName_txt.text = userDetails.name;
						yourEmail_txt.text = userDetails.email;
						sigCanvas.drawing = userDetails.signature;
					}
				} else if (responses[num] instanceof NextDrawingResponse) {
					var nextDrawing:NextDrawingResponse = NextDrawingResponse(responses[num]);
					nextStage = nextDrawing.stage;
					//actions based on stage number
					
					//hide drag clip on feet - stage 2? seems a bit odd..
					if (nextStage > 2) {
						hideClip._visible = false;
					}
						
					//nextDrawing.drawing.shrink();
					//trace("next draw height: " +nextDrawing.drawing.height)
					
					//reset the hideClip to the top of the canvas
					hideClip._y = mainCanvas._y - mainCanvas._height;

					//get the area left visible by the last drawing
					offsetYLastDrawing = Math.round(Number(nextDrawing.drawing.offsetY))
					trace(offsetYLastDrawing)
					//position the last draw canvas over the main canvas 
					lastDrawCanvas._y = mainCanvas._y
					//position with offsetY
					lastDrawCanvas._y = ( lastDrawCanvas._y - nextDrawing.drawing.height + offsetYLastDrawing )
					//make read only
					lastDrawCanvas.drawing.readOnly = true;
					
					nextDrawingID = nextDrawing.drawingID;
					//trace(nextDrawingID)
					//trace("Next Drawing: ["+nextDrawing.drawingID+"], Stage: "+nextDrawing.stage+", Locked Secs: "+nextDrawing.lockedSecs+", Drawing: "+nextDrawing.drawing.toString());
					// Set the drawing into the canvas movie clip!  The Flash linkage stuff is quite clever
					// really!!!
					lastDrawCanvas.drawing = nextDrawing.drawing;
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
	};
	// Send and load the request
	
	xmlRequest.sendAndLoad("http://localhost:8080/consequences/consequences", xmlResponse);
}

//do validation before sending data to server
send_btn.onPress = function() {
	
	//create new drawing request
	var sub:SubmitDrawingRequest = new SubmitDrawingRequest();
	sub.userID = "1";
	sub.drawingID = nextDrawingID;
	sub.stage = nextStage + 1;
	sub.drawing = mainCanvas.drawing;
	sub.drawing.shrink();
	
	
	//call validate function and pass drawing height
	//validate(sub.drawing.height)
	
	 
	//find offsetY
	var positionOfBottomOfCanvas:Number = mainCanvas._y + sub.drawing.height;
	var positionOfBottomOfDragClip:Number = hideClip._y + hideClip._height;
	
	//check for position of drag clip.
	//needs to be more than +n (100? half height of canvas) 
	//pixels from top of canvas
	//and -n less than bottom of drawing
	
	//*FLAWED* - catches people covering up all drawing ok
	//but dosent leave much scope for revealing a bit more drawing..
	//maybe need a rule that says "if you havent covered up at least
	//half of your drawing set it to cover half?
	
	//*TODO* dont calculate if feet section

		if (positionOfBottomOfDragClip > sub.drawing.height){
			hideClip._y = mainCanvas._y + sub.drawing.height - mainCanvas._height - 30;
			positionOfBottomOfDragClip = hideClip._y + hideClip._height;
			_root.mcBubble.showMessage("You didn't want to cover up your drawing. sorted!", hideClip, false, 150, 200);
			
		}
		//cover up half the drawing if revealed more than half..(!?)
		if (positionOfBottomOfDragClip < sub.drawing.height/2){
			hideClip._y = mainCanvas._y + (sub.drawing.height/2 - mainCanvas._height) + 30;
			positionOfBottomOfDragClip = hideClip._y + hideClip._height;
			_root.mcBubble.showMessage("You needed to cover up more than that. sorted!", hideClip, false, 150, 200);
			
		}
	
	
	//if (validate(sub.drawing.height)) {
	
	var distanceBetween:Number = positionOfBottomOfCanvas - positionOfBottomOfDragClip;
	offsetYPos_txt.text = Math.round(distanceBetween);
	trace("drawing height: " + sub.drawing.height)
	trace("bottom of canvas: " +positionOfBottomOfCanvas)
	trace("bottom of drag clip: " +positionOfBottomOfDragClip)
	//set the final offset!
	sub.drawing.offsetY = Math.round(Number(distanceBetween));

	//create user
	//To do: check whether need to create new user
	var newUser:User = new User();
	newUser.id = "1";
	newUser.name = yourName_txt.text;
	newUser.email = yourEmail_txt.text;
	newUser.signature = sigCanvas.drawing;
	var newUserSubmit:SubmitUserRequest = new SubmitUserRequest();
	newUserSubmit.user = newUser;
	var req:Request = new Request();
	req.addRequest(newUserSubmit);
	req.addRequest(sub);
	var xmlCrap:XML = req.getXML();
	trace("sending");
	var tmpResponse:XML = new XML();
	xmlCrap.sendAndLoad("http://localhost:8080/consequences/consequences", tmpResponse);
	trace(xmlCrap);
	
	//}//end validate
}

function validate(drawingHeight:Number){
	
	//check for no drawing - testing for height 
	if (drawingHeight < 1){
		_root.mcBubble.showMessage("You've not drawn anything!", mainCanvas, false, 150, 100);
	return false;
	}
		
	if (yourName_txt.text == '') {
     	 _root.mcBubble.showMessage("Who are you? Please enter a name, thanks!", yourName_txt, true);
    return false;
    };
	
	if (yourEmail_txt.text == '') {
      	_root.mcBubble.showMessage("Please enter your email address!", yourEmail_txt, true);
      return false;
	}
	if (yourEmail_txt.text.indexOf("@") < 1) {
      	_root.mcBubble.showMessage("Your email address must contain an '@'", yourEmail_txt, true);
    return false;
	}
    if (yourEmail_txt.text.lastIndexOf("@") > yourEmail_txt.text.lastIndexOf(".")  ) {
      	_root.mcBubble.showMessage("There must be a dot after the @ sign.", yourEmail_txt, true);
     return false;
	}
	return true
}

stop();
