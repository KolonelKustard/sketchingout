class GalleryDrawing {
	public var id: String;
	public var width: Number;
	public var height: Number;
	public var scale: Number;
	public var numStages: Number;
	public var authors: Array = new Array();
	
	private function getDrawingURL(): String {
		return ConsequencesSettings.GALLERY_URL + "?id=" + id + "&scale=" + Math.round(scale) + "&type=";
	}
	
	public function get urlPNG(): String {
		return getDrawingURL() + "png";
	}
	
	public function get urlJPG(): String {
		return getDrawingURL() + "jpg";
	}
	
	public function get urlSWF(): String {
		return getDrawingURL() + "swf";
	}
	
	public function get urlAnimatedSWF(): String {
		return getDrawingURL() + "animswf";
	}
}