
back_btn.onPress = function() {
	//
}

next_btn.onPress = function() {
	//
	theGallery.start = 4;
	getGalleryDrawings();
}


// Set the starting X and Y positions for the gallery images.
_global.thisX = 0;
_global.thisY = 0;

/* Set static values for the Stage's width and height. 
   Using Stage.width and Stage.height within the code results in 
   strangely positioned full images when testing in the Flash environment 
   (but the problem doesn't exist when published to a SWF file). */
_global.stageWidth = 550;
_global.stageHeight = 400;

var theGallery: GalleryDrawingsRequest = new GalleryDrawingsRequest();
theGallery.start = 0;
theGallery.quantity = 1;

//initial call to get first set of drawings
getGalleryDrawings()



	
function getGalleryDrawings(){
var request:Request = new Request();
	request.addRequest(theGallery);
	
	var xmlRequest:XML = request.getXML();
	//trace(xmlRequest);
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
					var galleryDetails:GalleryDrawingsResponse = GalleryDrawingsResponse(responses[num]);
					//array of drawing objects
					var drawings: Array = galleryDetails.galleryDrawings
					//and an array of locations
					var drawingsUrl: Array = new Array();
					//loop through drawings 
					for (var drw=0; drw<drawings.length;drw++){
					//add urls to array	
					drawingsUrl.push(drawings[drw].urlAnimatedSWF);
			}
				//pass array to function
				displayGallery(drawingsUrl);
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

//finally send it all off and wait for response
xmlRequest.sendAndLoad("http://localhost:8080/consequences/consequences", xmlResponse);
}
	
/* create a function which loops through the images in an array,
   and creates new movie clips on the Stage. */
function displayGallery(gallery_array:Array) {
	var galleryLength:Number = gallery_array.length;
	// loop through each of the images in the gallery_array.
	for (var i = 0; i<galleryLength; i++) {
		/* create a movie clip instance which holds the drawing. We'll also set a variable, 
		   thisMC, which is an alias to the movie clip instance. */
		var thisMC:MovieClip = this.createEmptyMovieClip("image"+i+"_mc", i);
		
		/* load the current image source into the new movie clip instance, 
		   using the MovieClipLoader class. */
		mcLoader_mcl.loadClip(gallery_array[i], thisMC);
				
		// set the _x and _y coordinates of the new movie clip.
		//thisMC._x += thisMC._width;
		//thisMC._y = _global.thisY;
		
		
		image0_mc._x = 0
		image1_mc._x = 125
		image2_mc._x = 250
		image3_mc._x = 375
		
		
		
		// if you've displayed 5 columns of images, start a new row.
		if ((i+1)%5 == 0) {
			// reset the X and Y positions
			_global.thisX = 20;
			_global.thisY += 180;
		} else {
			_global.thisX += 150//80+20;
		}
		
	}
}

// define the MovieClipLoader instance and MovieClipLoader listener Object.
var mcLoader_mcl:MovieClipLoader = new MovieClipLoader();
var mclListener:Object = new Object();
mclListener.onLoadStart = function() {
};

// when the onLoadInit event is thrown, you're free to position the instances 
mclListener.onLoadInit = function(evt:MovieClip) {
	//evt._parent["preloader"+evt.getDepth()+"_mc"].removeMovieClip();
	/* set local variables for the target movie clip's width and height,
	   and the desired settings for the image stroke and border. */
	var thisWidth:Number = evt._width;
	var thisHeight:Number = evt._height;
	var thisX:Number = evt._x;
	var thisY:Number = evt._y;
	var borderWidth:Number = 2;
	var marginWidth:Number = 8;
	evt.scale = 50;
	
	thisX += thisWidth;
	
	/* scale the target movie clip so it appears as a thumbnail. 
	   This allows users to quickly view a full image without downloading it every time, 
	   but unfortunaltey also causes a large initial download. */
	evt._xscale = evt.scale;
	evt._yscale = evt.scale;
	
	/* when the target_mc movie clip instance is pressed, begin to drag the current movie clip 
	   and set some temporary variables so once you are finished rescaling and positioning 
	   the full image, you can return the instance to its original position. */
	/*
	evt.onMouseDown = function() {
		// start dragging the current clip.
		trace("drg")
		this.startDrag();
		// set the _xscale and _yscale properties back to 100% so the image appears full sized. 
		//   You're also storing the original X and Y coordinates so you can return the image where you found it. 
		this._xscale = 100;
		this._yscale = 100;
		this.origX = this._x;
		this.origY = this._y;
		// find the depth of the current movie clip, and store it within the movie clip.
		this.origDepth = this.getDepth();
		// :TRICKY: swap the depth of the current movie clip, with the next highest movie clip of the _parent. 
		//   Effectively this makes the current movie clip the top of the "stack". 
		this.swapDepths(this._parent.getNextHighestDepth());
		// try and center the current movie clip on the Stage.
		this._x = (_global.stageWidth-evt._width+30)/2;
		this._y = (_global.stageHeight-evt._height+30)/2;
		// apply a transition to the movie clip which makes the movie clip flicker for a split second.
		//mx.transitions.TransitionManager.start(this, {type:mx.transitions.Photo, direction:0, duration:1, easing:mx.transitions.easing.Strong.easeOut, param1:empty, param2:empty});
	};
	*/
	
	// when the movie clip instance is released, stop dragging the movie clip. 
	 //  Reset the _xscale and _yscale properties as well as the _x and _y coordinates.
	/*
	evt.onRelease = function() {
		this.stopDrag();
		this._xscale = this.scale;
		this._yscale = this.scale;
		this._x = this.origX;
		this._y = this.origY;
	};
	*/
	// if the mouse cursor was released outside of the movie clip, call the onRelease handler.
	//evt.onReleaseOutside = evt.onRelease;
};
mcLoader_mcl.addListener(mclListener);
stop();