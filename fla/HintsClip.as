class HintsClip extends MovieClip {
	private var displayClip: MovieClip;
	private var inField: Boolean = false;
	private var currFocus: Object = null;
	private var currentClip: String = null;
	private var theDefaultClip: String = null;
	
	private var focusObjs: Array = new Array();
	private var assocClips: Array = new Array();
	
	
	private function setFocusedObj(focused: Object): Void {
		currFocus = focused;
		
		if (focused == null) {
			// Load default
			if (theDefaultClip != null) displayClip.loadMovie(theDefaultClip);
		}
		else {
			for (var num: Number = 0; num < focusObjs.length; num++) {
				if (focusObjs[num] == focused) {
					// Load found clip
					displayClip.loadMovie(assocClips[num]);
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
	
	public function set defaultHint(defaultClip: String): Void {
		theDefaultClip = defaultClip;
		if (currFocus == null) setFocusedObj(null);
	}
	
	public function get defaultHint(): String {
		return theDefaultClip;
	}
	
	public function addAssociation(srcObj: Object, assocClip: String): Void {
		focusObjs.push(srcObj);
		assocClips.push(assocClip);
	}
	
	public function HintsClip() {
		Selection.addListener(this);
		Mouse.addListener(this);
		displayClip = createEmptyMovieClip("displayClip", 1);
	}
}