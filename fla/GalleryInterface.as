/**
 * This class extends the DrawInterface class.  It adds the ability to move
 * itself across the stage and destroy itself once it reaches the far edge
 * of the screen.
 */
class GalleryInterface extends DrawInterface {
	// The number of milliseconds a drawing takes to cross the screen
	private static var TIME_TO_CROSS_TO_MIDDLE: Number = 10 * 1000;
	private static var TIME_TO_WAIT_IN_MIDDLE: Number = 5 * 1000;
	private static var TIME_TO_CROSS_FROM_MIDDLE: Number = 10 * 1000;
	
	private var startTime, endTime, startX, endX, totalStartX, totalEndX: Number = -1;
	private var inMiddle, beenInMiddle: Boolean = false;
	
	private var firedMidPoint, firedReachedEnd: Boolean = false;
	public var onMidPoint: Function = null;
	public var onReachedEnd: Function = null;
	
	public function onEnterFrame(): Void {
		// Make sure the parent class' (DrawInterface) onEnterFrame is called
		super.onEnterFrame();
		
		// See if in the middle
		if (inMiddle) {
			// Don't move, but check to see if should move
			if (getTimer() >= endTime) {
				inMiddle = false;
				beenInMiddle = true;
				
				startTime = getTimer();
				endTime = startTime + TIME_TO_CROSS_FROM_MIDDLE;
				startX = _x;
				endX = totalEndX;
			}
			
			// When reach mid point send event
			if ((!firedMidPoint) && (getTimer() >= (((endTime - startTime) / 2) + startTime))) {
				firedMidPoint = true;
				if (onMidPoint != null) onMidPoint();
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
		super.loadMovie(drawing.urlAnimatedSWF);
		
		startTime = getTimer();
		endTime = startTime + TIME_TO_CROSS_TO_MIDDLE;
		totalStartX = 800;
		totalEndX = -100;
		
		// Move to the middle
		startX = totalStartX;
		endX = (totalStartX / 2) + (totalEndX / 2);
	}
}