class Drawing {
	public var width: Number;
	public var height: Number;
	public var offsetX: Number;
	public var offsetY: Number;
	
	private var lines: Array;
	
	public function Drawing() {
		// Initialise lines array
		lines = new Array();
	}
	
	public function addLine(): Line {
		var line: Line = new Line();
		
		lines.push(line);
		return line;
	}
	
	public function getLines(): Array {
		return lines;
	}
	
	/**
	 * Clears the array of lines effectively wiping the drawing
	 */
	public function clear() {
		lines = new Array();
	}
	
	/**
	 * Returns this drawing as a 
	 */
	public function toString(): String {
		// First up create the xml instance to use
		var xml: XML = new XML();
		
		// Make the root canvas node
		var canvasNode: XMLNode = xml.createElement("canvas");
		canvasNode.attributes.width = width;
		canvasNode.attributes.height = height;
		canvasNode.attributes.offsetx = offsetX;
		canvasNode.attributes.offsety = offsetY;
		
		// Add as many line nodes as there are in the array
		for (var lineNum: Number = 0; lineNum < lines.length; lineNum++) {
			// Start line node
			var lineNode: XMLNode = xml.createElement("line");
			
			// Get line
			var line: Line = lines[lineNum];
			
			// Go through the points of this line
			var points: Array = line.getPoints();
			for (var pointNum: Number = 0; pointNum < points.length; pointNum++) {
				var point: Point = points[pointNum];
				
				// Create a point node
				var pointNode: XMLNode = xml.createElement("point");
				pointNode.attributes.x = point.x;
				pointNode.attributes.y = point.y;
				
				// Add point to the line
				lineNode.appendChild(pointNode);
			}
			
			// Add line
			canvasNode.appendChild(lineNode);
		}
		
		// Add the canvas node to the root
		xml.appendChild(canvasNode);
		
		// Return the xml as raw XML
		return xml.toString();
	}
}