class CanvasMovieClip extends MovieClip {
	private var theDrawing: Drawing;
	private var currLine: Line;
	
	private var minX, minY, maxX, maxY: Number;
	
	private function clearCanvas(): Void {
		clear();
	}
	
	private function inBounds(theX, theY: Number): Boolean {
		return ((theX >= minX) && (theX <= maxX) && (theY >= minY) && (theY <= maxY));
	}
	
	public function onMouseDown(): Void {
		// Get the mouse coords
		var currX: Number = _xmouse;
		var currY: Number = _ymouse;
		
		// Check in the bounds
		if (inBounds(currX, currY)) {
			// Make a new line in the drawing
			currLine = theDrawing.addLine();
			currLine.addPoint(_xmouse, _ymouse);
		
			// Start line actually drawn on the canvas
			lineStyle(1, 0x000000, 100);
			moveTo(_xmouse, _ymouse);
		}
	}
	
	public function onMouseUp(): Void {
		// Stop drawing
		currLine = null;
	}
	
	public function onMouseMove(): Void {
		// If have a current line, add point
		if (currLine != null) {
			var currX: Number = _xmouse;
			var currY: Number = _ymouse;
			
			// Check in bounds
			if (inBounds(currX, currY)) {
				currLine.addPoint(currX, currY);
				lineTo(currX, currY);
			}
		}
	}
	
	public function get drawing(): Drawing {
		// Make sure the width and height of the canvas match the width and height
		// of the movie clip
		theDrawing.width = _width;
		theDrawing.height = _height;
		
		return theDrawing;
	}
	
	public function set drawing(aDrawing: Drawing) {
		// Parse the drawing, putting it onto this canvas.
		if (aDrawing == null) {
			// If is a null drawing then just clear this canvas and make a
			// new blank drawing.
			clearCanvas();
			theDrawing = new Drawing();
		}
		else {
			// Clear the canvas and set this drawing instance to the
			// passed in drawing
			clearCanvas();
			theDrawing = aDrawing;
			
			// Go through all the lines
			var lines: Array = theDrawing.getLines();
			for (var num: Number = 0; num < lines.length; num++) {
				var line: Line = lines[num];
				
				// No current way of defining line style.  Default to simple!
				lineStyle(1, 0x000000, 100);
				
				// Get the points of this line
				var points: Array = line.getPoints();
				
				// Go through all the points, plotting them back
				var firstPoint: Boolean = true;
				for (var pointNum: Number = 0; pointNum < points.length; pointNum++) {
					var point: Point = points[pointNum];
					
					if (firstPoint) {
						moveTo(point.x, point.y);
						firstPoint = false;
					}
					else {
						lineTo(point.x, point.y);
					}
				}
			}
		}
	}
	
	/**
	 * Just creates a new blank drawing to represent this (presumably) new
	 * blank canvas.
	 */
	public function CanvasMovieClip() {
		theDrawing = new Drawing();
		
		// Define the bounds of this drawing tool
		minX = 0;
		minY = 0;
		maxX = _width;
		maxY = _height;
	}
}