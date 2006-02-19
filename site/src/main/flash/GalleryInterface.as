/**
 * This class extends the DrawInterface class.  It adds the ability to move
 * itself across the stage and destroy itself once it reaches the far edge
 * of the screen.
 */
class GalleryInterface extends MovieClip {
	// The number of milliseconds a drawing takes to cross the screen
	private static var TIME_TO_CROSS_TO_MIDDLE: Number = 10 * 1000;
	private static var TIME_TO_WAIT_IN_MIDDLE: Number = 5 * 1000;
	private static var TIME_TO_CROSS_FROM_MIDDLE: Number = 10 * 1000;
	
	private static var MARGIN_TOP: Number = 0;
	private static var MARGIN_BOTTOM: Number = 20;
	private static var MARGIN_LEFT: Number = 0;
	private static var MARGIN_RIGHT: Number = 0;
	
	private var showpic: MovieClip;
	private var drawing: GalleryDrawing;
	private var innerMC: MovieClip = null;
	private var startTime, endTime, startX, endX, totalStartX, totalEndX: Number = -1;
	private var inMiddle, beenInMiddle: Boolean = false;
	
	private var firedMidPoint, firedReachedEnd: Boolean = false;
	
	// Public positions
	public var rightX, leftX: Number;
	
	// Public events
	public var onMidPoint: Function = null;
	public var onReachedEnd: Function = null;
	
	/**
	 * This event is fired by the show picture button.  It's a horrible hack to work
	 * around the funky focus issues of Flash.  In the constructor a reference to this
	 * is added as the 'parent' property of the showpic button.
	 */
	private function onShowPicPressed(): Void {
		// Horrible hack to get PDF url
		var pdfUrl = MovieClip(this).parent.drawing.urlPDF;
		
		// Just open pdf in a new window
		getURL(pdfUrl, "_blank");
	}
	
	public function onEnterFrame(): Void {
		// See if in the middle
		if (inMiddle) {
			// If not finished drawing just do nothing
			if (innerMC._currentframe < innerMC._totalframes) {
				return;
			}
			
			// When reach mid point send event
			if (
				(!firedMidPoint) && 
				(getTimer() >= (((endTime - startTime) / 2) + startTime))
			) {
				firedMidPoint = true;
				if (onMidPoint != null) onMidPoint();
			}
			
			// Don't move, but check to see if should move
			if (getTimer() >= endTime) {
				inMiddle = false;
				beenInMiddle = true;
				
				startTime = getTimer();
				endTime = startTime + TIME_TO_CROSS_FROM_MIDDLE;
				startX = _x;
				endX = totalEndX;
			}
		}
		else {
			// Move this instance by the correct amount
			var percent: Number = (getTimer() - startTime) / (endTime - startTime);
			var offset: Number = percent * (0 - (startX - endX));
			var newX: Number = Math.round(startX + offset);
			
			// Only move if not gone beyond end
			if ((_x != newX) && (percent <= 1)) {
				// Move this
				_x = newX;
			}
			
			// See whether reached the end
			if (percent >= 1) {
				// See whether been in the middle before
				if (!beenInMiddle) {
					inMiddle = true;
					
					startTime = getTimer();
					endTime = startTime + TIME_TO_WAIT_IN_MIDDLE;
					startX = _x;
					endX = _x;
				}
				else {
					// Been in middle already so fire event to say reached the end then destroy thyself
					firedReachedEnd = true;
					if (onReachedEnd != null) onReachedEnd();
					
					removeMovieClip(this);
				}
			}
		}
	}
	
	public function loadDrawing(drawing: GalleryDrawing): Void {
		this.drawing = drawing;
		
		// If already loaded, make sure gets removed
		if (innerMC != null) innerMC.unloadMovie();
		
		// Create a new blank movie clip to show the drawing in
		innerMC = createEmptyMovieClip("innerMC", 1);
		
		// Calculate the best size and position of the embedded drawing
		var xScale: Number = Math.floor(((_width - MARGIN_LEFT - MARGIN_RIGHT) / drawing.width) * 100);
		var yScale: Number = Math.floor(((_height - MARGIN_TOP - MARGIN_BOTTOM) / drawing.height) * 100);
		
		// Use the lowest scale
		var theScale: Number;
		if (xScale < yScale) theScale = xScale;
		else theScale = yScale;
		
		innerMC._xscale = theScale;
		innerMC._yscale = theScale;
		
		// Now make sure it appears in the middle
		var newWidth: Number = drawing.width * (theScale / 100);
		var newHeight: Number = drawing.height * (theScale / 100);
		
		innerMC._x = MARGIN_LEFT + Math.round(((_width - MARGIN_RIGHT) / 2) - (newWidth / 2));
		innerMC._y = MARGIN_TOP + Math.round(((_height - MARGIN_BOTTOM) / 2) - (newHeight / 2));
		
		// Load the drawing
		innerMC.loadMovie(drawing.urlAnimatedSWF);
		
		// Set the start and end times for initial cross to middle
		startTime = getTimer();
		endTime = startTime + TIME_TO_CROSS_TO_MIDDLE;
		
		// Set the start and end to be off the stage to the right and off the
		// stage to the left with a bit of extra room for manouevre
		totalStartX = Math.ceil(rightX + (_width / 2) + 10);
		totalEndX = Math.floor(leftX - (_width / 2) - 10);
		
		// Move to the middle
		startX = totalStartX;
		endX = (totalStartX / 2) + (totalEndX / 2);
		
		// Set location to the start and make visible
		this._x = totalStartX;
		this._alpha = 100;
	}
	
	public function GalleryInterface() {
		// Link up button
		showpic.onPress = onShowPicPressed;
		showpic.parent = this;
		
		// Make sure invisible
		this._alpha = 0;
	}
}