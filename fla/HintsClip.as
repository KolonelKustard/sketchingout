class HintsClip extends MovieClip {
	private static var FADE_OUT_TIME = 1000;
	private static var FADE_BACK_IN_TIME = 200;
	
	private var inField: Boolean = false;
	private var currFocus: Object = null;
	private var newClipNum: Number = 0;
	private var currentClip: MovieClip = null;
	private var theDefaultClip: MovieClip;
	
	private var focusObjs: Array = new Array();
	private var assocClips: Array = new Array();
	private var assocTimes: Array = new Array();
	private var numVisible: Number = 0;
	
	private function showHint(hintNum: Number): Void {
		trace("Showing: " + hintNum);
		
		var foundClip: MovieClip;
		if (hintNum >= 0) {
			foundClip = assocClips[hintNum];
		} else {
			foundClip = theDefaultClip;
		}
		
		// Check got a valid clip
		if (foundClip != null) {
			// See if the clip is already visible or not
			if (foundClip._alpha > 0) {
				// Already visible, so don't draw it, just show it
				foundClip._alpha = 100;
			} else {
				// Not visible, so start playing it in
				foundClip.gotoAndPlay(1);
				foundClip._alpha = 100;
				
				// Increment the number that are visible
				numVisible++;
			}
		}
		
		// Set in the current clip
		currentClip = foundClip;
	}
	
	private function hideHint(hintNum: Number): Void {
		trace("Hiding: " + hintNum);
		
		var foundClip: MovieClip;
		if (hintNum >= 0) {
			foundClip = assocClips[hintNum];
		} else {
			foundClip = theDefaultClip;
		}
		
		if (foundClip != null) {
			foundClip._alpha = 0;
			numVisible--;
		}
	}
	
	private function setFocusedObj(focused: Object): Void {
		currFocus = focused;
		
		if (focused == null) {
			// Load default
			if (theDefaultClip != null) showHint(-1);
		}
		else {
			for (var num: Number = 0; num < focusObjs.length; num++) {
				if (focusObjs[num] == focused) {
					// Show found clip
					showHint(num);
					break;
				}
			}
		}
	}
	
	private function onSetFocus(oldFocus: Object, newFocus: Object): Void {
		inField = (newFocus != null);
		if (currFocus != newFocus) setFocusedObj(newFocus);
	}
	
	private function onMouseMove(): Void {
		// Only do anything if not presently in a focusable field.
		if (!inField) {
			// Find what the mouse is over
			var currMouseX: Number = _root._xmouse;
			var currMouseY: Number = _root._ymouse;
			var found: Boolean = false;
			
			for (var num: Number = 0; num < focusObjs.length; num++) {
				var x, y, width, height: Number = 0;
				
				if ((focusObjs[num] instanceof MovieClip) || (focusObjs[num] instanceof TextField)) {
					x = focusObjs[num]._x;
					y = focusObjs[num]._y;
					width = focusObjs[num]._width;
					height = focusObjs[num]._height;
				}
				
				// Check to see if in bounds and not already of focus
				if (
					(currMouseX >= x) && (currMouseX <= (x + width)) &&
					(currMouseY >= y) && (currMouseY <= (y + height))
				) {
					// Set focused and stop looping
					found = true;
					if (currFocus != focusObjs[num]) setFocusedObj(focusObjs[num]);
					break;
				}
			}
			
			// If didn't find anything, check to see if should turn back to default
			if ((!found) && (currFocus != null)) {
				setFocusedObj(null);
			}
		}
	}
	
	private function onEnterFrame(): Void {
		// Only do anything if something is visible
		if (numVisible > 1) {
			// Go through all the clips and hide as appropriate
			for (var num: Number = 0; num < assocClips.length; num++) {
				// If it's not in focus and it's visible, fade it out
				if ((assocClips[num] != currentClip) && (assocClips[num]._alpha > 0)) hideHint(num);
			}
			
			// Do the same with the default clip
			if ((theDefaultClip != currentClip) && (theDefaultClip._alpha > 0)) hideHint(-1);
		}
	}
	
	public function set defaultHint(defaultClip: String): Void {
		theDefaultClip.loadMovie(defaultClip);
		if (currFocus == null) setFocusedObj(null);
	}
	
	public function addAssociation(srcObj: Object, assocClip: String): Void {
		focusObjs.push(srcObj);
		
		// Make a new clip for the association and load the url into it
		newClipNum++;
		var newClip: MovieClip = createEmptyMovieClip("hintsclip_" + newClipNum, newClipNum);
		
		// Make sure not visible so it loads but doesn't show
		newClip._alpha = 0;
		
		// Load it
		newClip.loadMovie(assocClip);
		
		// Put into the array
		assocClips.push(newClip);
	}
	
	public function HintsClip() {
		Selection.addListener(this);
		Mouse.addListener(this);
		
		newClipNum++;
		theDefaultClip = createEmptyMovieClip("hintsclip_" + newClipNum, newClipNum);
		theDefaultClip._alpha = 0;
	}
}