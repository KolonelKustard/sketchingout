class Drawing {
	public var readOnly: Boolean = false;
	
	public var width: Number = 0;
	public var height: Number = 0;
	public var offsetX: Number = 0;
	public var offsetY: Number = 0;
	
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
	public function clear(): Void {
		lines = new Array();
	}
	
	/**
	 * Returns this drawing as a string.  Basically serialises it.
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
	
	public function fromString(drawing: String): Void {
		// Clear this drawing
		clear();
		
		// Make an xml object to work with this drawing string
		var drawingXML: XML = new XML();
		drawingXML.ignoreWhite = true;
		drawingXML.parseXML(drawing);
		
		// Get the main canvas node
		var canvasNode: XMLNode = drawingXML.firstChild;
		if (canvasNode.nodeName != "canvas") throw new Error("Not a valid drawing");
		
		// Canvas properties
		width = canvasNode.attributes.width;
		height = canvasNode.attributes.height;
		offsetX = canvasNode.attributes.offsetx;
		offsetY = canvasNode.attributes.offsety;
		
		// Now look for lines
		for (var numLine: Number = 0; numLine < canvasNode.childNodes.length; numLine++) {
			var lineNode: XMLNode = canvasNode.childNodes[numLine];
			if (lineNode.nodeName == "line") {
				// Make a new line
				var line: Line = this.addLine();
				
				// Now run through all the points on the line, adding them in
				for (var numPoint: Number = 0; numPoint < lineNode.childNodes.length; numPoint++) {
					var pointNode: XMLNode = lineNode.childNodes[numPoint];
					if (pointNode.nodeName == "point") {
						line.addPoint(pointNode.attributes.x, pointNode.attributes.y);
					}
				}
			}
		}
	}
}