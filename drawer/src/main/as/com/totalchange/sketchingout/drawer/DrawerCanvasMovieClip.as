import com.totalchange.sketchingout.ui.CanvasMovieClip;
import com.senocular.drawing.DashedLine;

class com.totalchange.sketchingout.drawer.DrawerCanvasMovieClip extends CanvasMovieClip {
	private static var DOTTY_LENGTH: Number = 7;
	private static var DOTTY_GAP: Number = DOTTY_LENGTH;
	private static var DOTTY_OFFSET: Number = 2 * DOTTY_LENGTH;
	
	private var boundingBox: MovieClip;
	
	public function DrawerCanvasMovieClip() {
		super();
		
		// Make the bounding box movie clip and then draw the bounding box
		boundingBox = createEmptyMovieClip("boundingBox", getNextHighestDepth());
		drawBounds();
	}
	
	private function drawDottyLine(startX, startY, endX, endY: Number): Void {
		var dashLine: DashedLine = new DashedLine(boundingBox, DOTTY_LENGTH, DOTTY_GAP);
		
		boundingBox.lineStyle(1, 0x000000, 100);
		dashLine.moveTo(startX, startY);
		dashLine.lineTo(endX, endY);
	}
	
	/**
	 * Draws a groovy bounding box for the canvas
	 */
	private function drawBounds(): Void {
		boundingBox.clear();
		drawDottyLine(0 - DOTTY_OFFSET, -1, Number(_width) + DOTTY_OFFSET, -1);
		drawDottyLine(Number(_width) + 1, 0 - DOTTY_OFFSET, Number(_width) + 1, Number(_height) + DOTTY_OFFSET);
		drawDottyLine(Number(_width) + DOTTY_OFFSET, Number(_height) + 1, 0 - DOTTY_OFFSET, Number(_height) + 1);
		drawDottyLine(-1, Number(_height) + DOTTY_OFFSET, -1, 0 - DOTTY_OFFSET);
	}
	
	public function set _width(width: Number): Void {
		super._width = width;
		drawBounds();
	}
	
	public function get _width(): Number {
		return super._width;
	}
	
	public function set _height(height: Number): Void {
		super._height = height;
		drawBounds();
	}
	
	public function get _height(): Number {
		return super._height;
	}
}