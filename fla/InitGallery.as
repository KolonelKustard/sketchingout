//SET UP GALLERY

//init sound clip
var galleryMusic = new Sound();
galleryMusic.attachSound("gallerymusic");
galleryMusic.start("gallerymusic", 999)

//init gallery object
var gallery:Gallery = new Gallery();
//create and setup loader clip
attachMovie("UIloader", "UIloader", 100)
UIloader._x = 687;
UIloader._xscale = 40;
UIloader._yscale = 40;

//only called when first drawing finished and past halfway
UIloader.onMovieClipPlayed = function() {
	//should I create a new UIloader clip here??
	//
	
	//Attempt at getting next pic and loading into new clip
	/*
	gallery.next();
	attachMovie("UIloader", "UIloader1", 200)
	UIloader1._x = 687;
	UIloader1._xscale = 40;
	UIloader1._yscale = 40;
	UIloader1.loadMovie(currentDrawing.urlAnimatedSWF);
	*/
};

gallery.onNext = function(drawing:GalleryDrawing) {
	currentDrawing = drawing;
	//load the next pic into the loader clip
	UIloader.loadMovie(currentDrawing.urlAnimatedSWF);

};
//get first pic
gallery.next();

stop();
