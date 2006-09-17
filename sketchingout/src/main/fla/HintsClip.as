class HintsClip extends MovieClip {
	private static var TIP_TIME: Number = 1000;
	private static var MSG_TIME: Number = 10000;
	private static var TIP_REVERT_TIME: Number = 500;
	
	private var inField: Boolean = false;
	private var currFocus: Object = null;
	private var currentHint: Hint = null;
	private var theDefaultHint: Hint;
	
	private var messageHint: Hint = null;
	private var messageUntil: Number = 0;
	
	private var hoverTime: Number  = -1;
	private var hoverFocus: Object = null;
	
	private var newClipNum: Number = 0;
	private var numVisible: Number = 0;
	private var hints: Array = new Array();
	
	private function showHint(hintNum: Number): Void {
		// Skip if showing a message...
		if (messageHint != null) return;
		
		var foundHint: Hint;
		if (hintNum >= 0) {
			foundHint = hints[hintNum];
		} else {
			foundHint = theDefaultHint;
		}
		
		// Check got a valid hint
		if (foundHint != null) {
			// See if the clip is already visible or not
			if (!foundHint.hintVisible) {
				// Increment the number that are visible
				numVisible++;
			}
			
			// Show this hint
			foundHint.showHint();
		}
		
		// Set in the current clip
		currentHint = foundHint;
	}
	
	private function hideHint(hintNum: Number): Void {
		var foundHint: Hint;
		if (hintNum >= 0) {
			foundHint = hints[hintNum];
		} else {
			foundHint = theDefaultHint;
		}
		
		if (foundHint != null) {
			foundHint.hideHint();
			if (!foundHint.hintVisible) numVisible--;
		}
	}
	
	private function setFocusedObj(focused: Object): Void {
		currFocus = focused;
		
		if (focused == null) {
			// Load default
			showHint(-1);
		}
		else {
			for (var num: Number = 0; num < hints.length; num++) {
				if (hints[num].control == focused) {
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
		// Reset the hover details
		hoverTime = -1;
		hoverFocus = null;
		
		// Only do anything if not presently in a focusable field.
		if (!inField) {
			// Find what the mouse is over
			var currMouseX: Number = _root._xmouse;
			var currMouseY: Number = _root._ymouse;
			var found: Boolean = false;
			
			for (var num: Number = 0; num < hints.length; num++) {
				var x, y, width, height: Number = 0;
				var focusedObj: Object = hints[num].control;
				
				if ((focusedObj instanceof MovieClip) || (focusedObj instanceof TextField)) {
					x = focusedObj._x;
					y = focusedObj._y;
					width = focusedObj._width;
					height = focusedObj._height;
				}
				
				// Check to see if in bounds and not already of focus
				if (
					(currMouseX >= x) && (currMouseX <= (x + width)) &&
					(currMouseY >= y) && (currMouseY <= (y + height))
				) {
					// Determine if found or not
					if (currFocus == focusedObj) {
						found = true;
					}
					
					// If found but not in use yet, set the hover time to make it appear in a bit
					if (currFocus != focusedObj) {
						hoverTime = getTimer() + TIP_TIME;
						hoverFocus = focusedObj;
					}
					
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
			var hintClip: Hint;
			
			// Go through all the clips and hide as appropriate
			for (var num: Number = 0; num < hints.length; num++) {
				// If it's not in focus and it's visible, fade it out
				hintClip = hints[num];
				if (
					(hintClip != currentHint) &&
					(hintClip != messageHint) &&
					(hintClip.hintVisible)
				)
					hideHint(num);
			}
			
			// Do the same with the default clip
			if ((theDefaultHint != currentHint) && (theDefaultHint.hintVisible)) hideHint(-1);
		}
		
		// See if hover time is set
		if (hoverTime > -1) {
			// Only do anything if hover time is over the max hover time
			if (getTimer() >= hoverTime) {
				setFocusedObj(hoverFocus);
			}
		}
		
		// See if have a message hint - if yes check it should still be visible
		if ((messageHint != null) && (getTimer() > messageUntil)) {
			messageHint = null;
			
			// Return to the default
			showHint(-1);
		}
	}
	
	public function set defaultHint(defaultClip: String): Void {
		theDefaultHint.setHintUrl(defaultClip);
		if (currFocus == null) setFocusedObj(null);
	}
	
	public function addAssociation(srcObj: Object, assocClip: String): Void {
		// Make new hint
		var newHint: Hint = new Hint(this, srcObj, assocClip);
		
		// Put into the array
		hints.push(newHint);
	}
	
	/**
	 * This is the equivalent to saying "Show Message".  It brings a hint to
	 * the foreground for the amount of time specified in MSG_TIME.
	 */
	public function showCachedHint(msgSwfUrl: String): Void {
		for (var num: Number = 0; num < hints.length; num++) {
			var msg: Hint = hints[num];
			if (msg.clipUrl == msgSwfUrl) {
				// Clear any current message
				messageHint = null;
				
				// Show the hint
				showHint(num);
				
				// Remember this is the message and say don't change for a bit
				messageHint = msg;
				messageUntil = getTimer() + MSG_TIME;
			}
		}
	}
	
	public function getNextDepth(): Number {
		return newClipNum++;
	}
	
	public function HintsClip() {
		Selection.addListener(this);
		Mouse.addListener(this);
		
		// Make new blank default hint
		theDefaultHint = new Hint(this, null, null);
	}
}