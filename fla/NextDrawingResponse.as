class NextDrawingResponse implements ResponseOperator {
	public var drawingID: String;
	public var stage: Number;
	public var lockedSecs: Number;
	public var drawing: Drawing = null;
	
	public function parseXML(responseNode: XMLNode): Void {
		// Go through the nodes that are defined and fill in the blanks on
		// the user object linked to this response.
		for (var num: Number = 0; num < responseNode.childNodes.length; num++) {
			var node: XMLNode = responseNode.childNodes[num];
			
			switch (node.nodeName) {
				case "drawing_id":
					drawingID = node.firstChild.nodeValue;
					break;
				case "stage":
					stage = Number(node.firstChild.nodeValue);
					break;
				case "locked_secs":
					lockedSecs = Number(node.firstChild.nodeValue);
					break;
				case "drawing":
					drawing = new Drawing();
					drawing.fromString(node.firstChild.nodeValue);
					break;
			}
		}
	}
}