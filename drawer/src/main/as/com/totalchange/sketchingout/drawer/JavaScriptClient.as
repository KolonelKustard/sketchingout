import com.totalchange.sketchingout.drawer.DrawerCanvasMovieClip;

class com.totalchange.sketchingout.drawer.JavaScriptClient{
	public var canvas: DrawerCanvasMovieClip;
	
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
	
	public function loadMonkey(monkey: String): Void {
		canvas.loadMovie("http://localhost:8080/drawer/middleman?MNNNnnnnggggghhhhhh=" + monkey, "GET");
	}
}