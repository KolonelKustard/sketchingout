/**
 * This class extends the DrawInterface class.  It adds the ability to move
 * itself across the stage and destroy itself once it reaches the far edge
 * of the screen.
 */
class GalleryInterface extends DrawInterface {
	// The number of milliseconds a drawing takes to cross the screen
	private static var TIME_TO_CROSS_SCREEN: Number = 25 * 1000;
	
	// The number of milliseconds a drawing waits in the middle of the
	// screen before it moves off again
	private static var TIME_TO_WAIT_IN_MIDDLE: Number = 5 * 1000;
	
	private var startTime, waitTime, startX, endX: Number = -1;
	private var inMiddle, beenInMiddle: Boolean = false;
	
	public function onEnterFrame(): Void {
		// Make sure the parent class' (DrawInterface) onEnterFrame is called
		super.onEnterFrame();
		
		// See if in the middle
		if (inMiddle) {
			// Don't move, but check to see if should move
			if ((getTimer() - waitTime) >= TIME_TO_WAIT_IN_MIDDLE) {
				inMiddle = false;
				beenInMiddle = true;
			}
		}
		else {
			// Move this instance left by the correct amount
			var percent: Number = (getTimer() - startTime) / (TIME_TO_CROSS_SCREEN - TIME_TO_WAIT_IN_MIDDLE);
			var offset: Number = percent * (0 - (startX - endX));
			var newX: Number = Math.round(startX + offset);
		
			if ((_x != newX) && (percent <= 1)) {
				// Move this
				_x = newX;
				
				// See if in middle and whether been in middle before
				if (!beenInMiddle && (percent >= 0.5)) {
					inMiddle = true;
					waitTime = getTimer();
				}
			}
		}
	}
	
	public function loadDrawing(drawing: GalleryDrawing): Void {
		super.loadMovie(drawing.urlAnimatedSWF);
		
		startTime = getTimer();
		startX = 687;
		endX = -50;
	}
}