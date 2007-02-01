import com.totalchange.sketchingout.drawer.DrawerCanvasMovieClip;
import com.totalchange.sketchingout.drawing.Drawing;

class com.totalchange.sketchingout.drawer.JavaScriptClient{
	public var canvas: DrawerCanvasMovieClip;
	public var background: MovieClip;
	
	public function clear(): Void {
		canvas.clearDrawing();
	}
	
	public function setWidth(width: Number): Void {
		canvas._width = width;
	}
	
	public function setHeight(height: Number): Void {
		canvas._height = height;
	}
	
	public function undoStart(): Void {
		canvas.undoStart();
	}
	
	public function undoStop(): Void {
		canvas.undoStop();
	}
	
	public function save(): Void {
		var sendPic: LoadVars = new LoadVars();
		sendPic.content = canvas.drawing.toString();
		sendPic.filename = "drawing.xml";
		sendPic.send("flashback", "_self", "POST");
	}
	
	public function loadDrawing(id: String): Void {
		var drawXml: XML = new XML();
		drawXml.ignoreWhite = true;
		Object(drawXml).canvas = canvas;
		
		drawXml.onLoad = function(success: Boolean) {
			if (success) {
				var newDrawing: Drawing = new Drawing();
				newDrawing.fromString(this.toString());
				this.canvas.drawing = newDrawing;
			}
		}
		drawXml.load("middleman?MNNNnnnnggggghhhhhh=" + id);
	}
	
	public function loadBackground(id: String): Void {
		background.loadMovie("middleman?MNNNnnnnggggghhhhhh=" + id, "GET");
		background._x = canvas._x;
		background._y = canvas._y;
	}
	
	public function setBackgroundAlpha(alpha: Number): Void {
		background._alpha = Number(alpha);
	}
}