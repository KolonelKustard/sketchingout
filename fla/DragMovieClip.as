class DragMovieClip extends MovieClip {
	public var nextDrawing: CanvasMovieClip;
	
	public function onPress(): Void {
		var maxHeight: Number = nextDrawing._y - nextDrawing._height;
		
		//constrain (locked, left, top, right, bottom)
		this.startDrag(false, _x, maxHeight, _x, 0);
		
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
		return 0;
	}
	
	/**
	 * Resets the position of the drag movie clip to the top of the next drawing
	 * canvas.
	 */
	public function resetPos(): Void {
		_x = nextDrawing._x;
		_y = nextDrawing._y - nextDrawing._height;
	}
}