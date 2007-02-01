import com.totalchange.sketchingout.SketchingoutSettings;

import com.totalchange.sketchingout.drawing.Drawing;
import com.totalchange.sketchingout.drawing.Line;
import com.totalchange.sketchingout.drawing.Point;

import com.totalchange.sketchingout.ui.DragMovieClip;

class com.totalchange.sketchingout.ui.CanvasMovieClip extends MovieClip {
	private var theDrawing: Drawing;
	private var currLine: Line;
	private var minX, minY, maxX, maxY: Number;
	private var hasMouse: Boolean = false;
	
	private var linkedDragClip: DragMovieClip = null;
	private var bottomOfDrawing: Number;
	
	private var undo: Boolean = false;
	private var undoStartMillis: Number;
	private var undoLastMillis: Number;
	private var undoCurrVelocity: Number;
	
	public var color: Number = 0x000000;
	public var thickness: Number = 1;
	public var modified: Boolean = false;
	public var penMovieClip: MovieClip = null;
	
	public var undoAcceleration: Number = 0.00005;
	public var undoMaxVelocity: Number = 0.1;
	
	private function clearCanvas(): Void {
		clear();
		bottomOfDrawing = 0;
		moveDragClip();
	}
	
	private function inBounds(theX, theY: Number): Boolean {
		return ((theX >= minX) && (theX <= maxX) && (theY >= minY) && (theY <= maxY) && (!theDrawing.readOnly));
	}
	
	private function moveDragClip(): Void {
		// If no drag clip do nothing
		if (linkedDragClip == null) return;
		
		// Get the current actual bottom of the drawing
		var bottom: Number = bottomOfDrawing + _y;
		
		// Get the bottom of the drag clip
		var dragBottom: Number = linkedDragClip._y + linkedDragClip._height;
		
		// See if the offset is ok
		if ((dragBottom - SketchingoutSettings.DEFAULT_OFFSET_Y) != bottom) {
			// Set the location of the drag clip accordingly
			linkedDragClip._y = (bottom - SketchingoutSettings.DEFAULT_OFFSET_Y) - linkedDragClip._height;
		}
	}
	
	private function doUndo(): Void {
		var theDrawing: Drawing = this.drawing;
		var timeNow: Number = getTimer();
		
		// Work out our current velocity (use the acceleration stuff)
		if (undoCurrVelocity < undoMaxVelocity) {
			// Work out what velocity should be based on elapsed time
			// and acceleration
			undoCurrVelocity = undoAcceleration * (timeNow - undoStartMillis);
			
			// Make sure don't go over the max
			if (undoCurrVelocity > undoMaxVelocity) {
				undoCurrVelocity = undoMaxVelocity;
			}
		} else if (undoCurrVelocity > undoMaxVelocity) {
			// Make sure don't go over the max
			undoCurrVelocity = undoMaxVelocity;
		}
		
		// Work out based on time and velocity how many points to remove
		var pointsToObliterate: Number = 0;
		if (undoCurrVelocity != 0) {
			 pointsToObliterate = (timeNow - undoLastMillis) * undoCurrVelocity;
		}
		undoLastMillis = timeNow;
		
		// Get rid of them and set the revised drawing back in
		theDrawing.removeLastXPoints(pointsToObliterate);
		this.drawing = theDrawing;
	}
	
	public function onMouseDown(): Void {
		// Get the mouse coords
		var currX: Number = _xmouse;
		var currY: Number = _ymouse;
		
		// Check in the bounds
		if (inBounds(currX, currY)) {
			// Make a new line in the drawing
			currLine = theDrawing.addLine();
			currLine.color = color;
			currLine.thickness = thickness;
			currLine.addPoint(currX, currY);
		
			// Start line actually drawn on the canvas
			lineStyle(thickness, color, 100);
			moveTo(currX, currY);
			
			// Set the bottom of the drawing to here
			if (bottomOfDrawing < currY) bottomOfDrawing = currY;
		}
	}
	
	public function onMouseUp(): Void {
		// Add point and stop drawing
		onMouseMove();
		currLine = null;
	}
	
	public function onMouseMove(): Void {
		// If not got mouse, see if should have mouse
		if ((!hasMouse) && (inBounds(_xmouse, _ymouse)) && (penMovieClip != null)) {
			hasMouse = true;
			Mouse.hide();
			penMovieClip._visible = true;
		}
		
		// If has mouse, move pencil clip
		if (hasMouse) {
			penMovieClip._x = _x + _xmouse;
			penMovieClip._y = _y + _ymouse;
			updateAfterEvent();
			
			// Check to see if moved outside bounds
			if (!inBounds(_xmouse, _ymouse)) {
				hasMouse = false;
				Mouse.show();
				penMovieClip._visible = false;
			}
		}
		
		// If have a current line, add point
		if (currLine != null) {
			var currX: Number = _xmouse;
			var currY: Number = _ymouse;
			
			// Check in bounds
			if (inBounds(currX, currY)) {
				currLine.addPoint(currX, currY);
				lineTo(currX, currY);
				
				modified = true;
				
				// Set the bottom of the drawing to here and call the move drag clip
				// function
				if (bottomOfDrawing < currY) bottomOfDrawing = currY;
				moveDragClip();
			}
		}
	}
	
	public function onEnterFrame(): Void {
		if (undo) {
			doUndo();
		}
	}
	
	public function get drawing(): Drawing {
		// Make sure the width and height of the canvas match the width and height
		// of the movie clip
		theDrawing.width = _width;
		theDrawing.height = _height;
		
		return theDrawing;
	}
	
	public function set drawing(aDrawing: Drawing): Void {
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
				
				// Define the line style
				lineStyle(line.thickness, line.color, 100);
				
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
		
		// Move the drag clip
		bottomOfDrawing = theDrawing.getBottom();
		moveDragClip();
		
		modified = false;
	}
	
	public function clearDrawing(): Void {
		// Set the drawing to null to clear it
		drawing = null;
		
		// But make sure records that it's modified
		modified = true;
	}
	
	/**
	 * This function strips whitespace from the top of the drawing
	 */
	public function trim(): Void {
		// Get the drawing, offset it, then set it back in
		var moveDrawing: Drawing = drawing;
		
		var fromTop: Number = moveDrawing.getTop();
		moveDrawing.offsetYBy(0 - fromTop);
		
		drawing = moveDrawing;
	}
	
	public function undoStart(): Void {
		undoStartMillis = getTimer();
		undoLastMillis = undoStartMillis;
		undoCurrVelocity = 0;
		undo = true;
	}
	
	public function undoStop(): Void {
		undo = false;
	}
	
	public function set dragClip(dragClip: DragMovieClip): Void {
		linkedDragClip = dragClip;
		
		if (linkedDragClip != null) {
			
		}
		else {
		}
	}
	
	public function set _width(width: Number): Void {
		super._width = width;
		maxX = width;
	}
	
	public function get _width(): Number {
		return maxX;
	}
	
	public function set _height(height: Number): Void {
		super._height = height;
		maxY = height;
	}
	
	public function get _height(): Number {
		return maxY;
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
		maxX = super._width;
		maxY = super._height;
		
		// Unload any piccies
//		this.unloadMovie();
	}
}