class GalleryDrawing {
	private static var URL_BASE: String = "http://localhost:8080/consequences/drawing";
	
	public var id: String;
	public var width: Number;
	public var height: Number;
	public var numStages: Number;
	public var authors: Array = new Array();
	
	private function getDrawingURL(): String {
		return URL_BASE + "?id=" + id + "&type=";
	}
	
	public function get urlPNG(): String {
		return getDrawingURL() + "png";
	}
	
	public function get urlSWF(): String {
		return getDrawingURL() + "swf";
	}
	
	public function get urlAnimatedSWF(): String {
		return getDrawingURL() + "animswf";
	}
}