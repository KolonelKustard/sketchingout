class DragMovieClip extends MovieClip {
	public var nextDrawing: CanvasMovieClip;
	
	private function mouseOverCanvas(): Boolean {
		if (nextDrawing == null) return false;
		
		var actualX: Number = _xmouse + _x;
		var actualY: Number = _ymouse + _y;
		
		if ((actualX >= nextDrawing._x) &&
			(actualX <= (nextDrawing._x + nextDrawing._width)) &&
			(actualY >= nextDrawing._y) &&
			(actualY <= (nextDrawing._x + nextDrawing._height))) return true;
		else return false;
	}
	
	public function onPress(): Void {
		// Determine if mouse is over the main canvas.  If it is, do nothing.
		if (mouseOverCanvas()) return;
		
		var theTop: Number = ((nextDrawing._y + nextDrawing.drawing.getBottom()) - SketchingoutSettings.MAX_OFFSET_Y) - _height;
		var theBottom: Number = ((nextDrawing._y + nextDrawing.drawing.getBottom()) - SketchingoutSettings.MIN_OFFSET_Y) - _height;
		
		//constrain (locked, left, top, right, bottom)
		this.startDrag(false, _x, theTop, _x, theBottom);
		
		// Need to set the next drawing to read only to make sure don't draw on it!
		nextDrawing.drawing.readOnly = true;
	};
	
	public function onRelease(): Void {
		// Stop dragging
		this.stopDrag();
		
		// Set the next drawing to allow drawing again
		nextDrawing.drawing.readOnly = false;
	};
	
	/**
	 * Gets the offset Y as a value of covering the drawing.
	 */
	public function getOffsetY(): Number {
		// The offset value is the bottom of the drawing minus the bottom of this
		// movie clip
		var bottomOfDrawing: Number = nextDrawing._y + nextDrawing.drawing.getBottom();
		var bottomOfDragBar: Number = this._y + this._height;
		
		//trace(Math.ceil(bottomOfDrawing - bottomOfDragBar) + " [" + bottomOfDrawing + ", " + bottomOfDragBar + "]");
		return Math.ceil(bottomOfDrawing - bottomOfDragBar);
	}
	
	/**
	 * Resets the position of the drag movie clip to the top of the next drawing
	 * canvas.
	 */
	public function resetPos(): Void {
		_y = nextDrawing._y - _height;
	}
}