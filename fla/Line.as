class Line {
	private var points: Array;
	
	public function Line() {
		// Make points array
		points = new Array();
	}
	
	public function addPoint(x: Number, y: Number): Point {
		var point: Point = new Point();
		point.x = x;
		point.y = y;
		
		points.push(point);
		return point;
	}
	
	public function getPoints(): Array {
		return points;
	}
}