//SET UP GALLERY

//load UI frame border
_root.attachMovie("CoverUp","CoverUp",999);

//init sound clip
var galleryMusic = new Sound();
galleryMusic.attachSound("gallerymusic");
galleryMusic.start("gallerymusic", 999);

//init gallery object
gallery = new Gallery();

// Incrementing ID for the next drawings
newID = 1;

// The event that will be fired when a gallery drawing is finished
function drawingOnMovieClipPlayed() {
	// Just ask for another picture
	gallery.next();
}

// The event that is fired by the gallery when the next drawing is ready
function galleryOnNext(drawing: GalleryDrawing) {
	if (drawing != null) {
		// Create a new GalleryInterface on the stage
		var newDrawing: GalleryInterface = attachMovie("UILoader", "UILoader" + newID, newID++);
		
		// Set the position on the Y coord to sit
		newDrawing._y = 70;
		
		// Set the start and end points
		newDrawing.rightX = 670;
		newDrawing.leftX = -75;
		
		// Make sure when it finishes we catch its finished event
		newDrawing.onMidPoint = drawingOnMovieClipPlayed;
		
		// Now load the drawing into it
		newDrawing.loadDrawing(drawing);
	}
};

// Link gallery onNext event and get first pic
gallery.onNext = galleryOnNext;
gallery.next();

// Now go autonomous
//stop();