/**
 * This class extends the DrawInterface class.  It adds the ability to move
 * itself across the stage and destroy itself once it reaches the far edge
 * of the screen.
 */
class GalleryInterface extends DrawInterface {
	// The number of milliseconds a drawing takes to cross the screen
	private static var TIME_TO_CROSS_TO_MIDDLE: Number = 10 * 1000;
	private static var TIME_TO_WAIT_IN_MIDDLE: Number = 5 * 1000;
	private static var TIME_TO_CROSS_FROM_MIDDLE: Number = 3 * 1000;
	
	private var startTime, endTime, startX, endX, totalStartX, totalEndX: Number = -1;
	private var inMiddle, beenInMiddle: Boolean = false;
	
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
		}
		else {
			// Move this instance by the correct amount
			var percent: Number = (getTimer() - startTime) / endTime;
			var offset: Number = percent * (0 - (startX - endX));
			var newX: Number = Math.round(startX + offset);
			
			// Only move if not gone beyond end
			if ((_x != newX) && (percent <= 1)) {
				// Move this
				_x = newX;
				trace(Math.round(percent) + " " + Math.round(offset) + " " + Math.round(getTimer() / 1000) + " " + Math.round(startTime / 1000) + " " + Math.round(endTime / 1000) + " " + newX + " " + startX + " " + endX);
			}
			
			// See whether been in middle before and if in middle
			if (!beenInMiddle && (percent >= 1)) {
				inMiddle = true;
				
				startTime = getTimer();
				endTime = startTime + TIME_TO_WAIT_IN_MIDDLE;
				startX = _x;
				endX = _x;
			}
		}
	}
	
	public function loadDrawing(drawing: GalleryDrawing): Void {
		super.loadMovie(drawing.urlAnimatedSWF);
		
		startTime = getTimer();
		endTime = startTime + TIME_TO_CROSS_TO_MIDDLE;
		totalStartX = 687;
		totalEndX = -50;
		
		// Move to the middle
		startX = totalStartX;
		endX = (totalStartX / 2) + (totalEndX / 2);
	}
}