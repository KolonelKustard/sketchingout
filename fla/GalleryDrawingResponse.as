class GalleryDrawingResponse implements ResponseOperator {
	public var drawingID: String;
	public var stages: Array = new Array();
	
	public function parseXML(responseNode: XMLNode): Void {
		// Go through the nodes that are defined and fill in the blanks on
		// the user object linked to this response.
		for (var num: Number = 0; num < responseNode.childNodes.length; num++) {
			var node: XMLNode = responseNode.childNodes[num];
			trace(node.nodeName);
			
			switch (node.nodeName) {
				case "drawing_id":
					drawingID = node.firstChild.nodeValue;
					break;
				case "drawing_stage":
					var drawing: Drawing = new Drawing();
					trace(node.firstChild.nodeValue);
					//drawing.fromString(node.firstChild.nodeValue);
					//stages.push(drawing);
					break;
			}
		}
	}
}