class Line {
	private var points: Array;
	private var lastPointX: Number;
	private var lastPointY: Number;
	
	public function Line() {
		// Make points array
		points = new Array();
		
		// Set last points to negative values
		lastPointX = -1;
		lastPointY = -1;
	}
	
	public function addPoint(x: Number, y: Number): Point {
		var point: Point = new Point();
		point.x = x;
		point.y = y;
		
		// Make sure not duplicating last added point before adding this one
		if ((x != lastPointX) || (y != lastPointY)) {
			points.push(point);
		}
		
		// Set last x and y
		lastPointX = x;
		lastPointY = y;
		
		return point;
	}
	
	public function getPoints(): Array {
		return points;
	}
}