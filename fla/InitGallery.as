﻿//SET UP GALLERY

//init sound clip
var galleryMusic = new Sound();
galleryMusic.attachSound("gallerymusic");
galleryMusic.start("gallerymusic", 999)

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
	// Create a new GalleryInterface on the stage
	var newDrawing: GalleryInterface = attachMovie("UILoader", "UILoader" + newID, newID++);
	newDrawing._x = 687;
	newDrawing._xscale = 40;
	newDrawing._yscale = 40;
	
	// Make sure when it finishes we catch its finished event
	newDrawing.onMovieClipPlayed = drawingOnMovieClipPlayed;
	
	// Now load the drawing into it
	newDrawing.loadMovie(drawing.urlAnimatedSWF);
};

// Link gallery onNext event and get first pic
gallery.onNext = galleryOnNext;
gallery.next();

// Now go autonomous
stop();