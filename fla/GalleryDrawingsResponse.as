class GalleryDrawingsResponse implements ResponseOperator {
	
	public var galleryDrawings: Array = new Array();
	
	private function parseAuthorXML(authorNode: XMLNode): GalleryDrawingAuthor {
		// Make a new author
		var author: GalleryDrawingAuthor = new GalleryDrawingAuthor();
		
		// Run through the child nodes, adding as we go
		for (var num: Number = 0; num < authorNode.childNodes.length; num++) {
			var node: XMLNode = authorNode.childNodes[num];
			
			switch (node.nodeName) {
				case "stage":
					author.stage = Number(node.firstChild.nodeValue);
					break;
				case "name":
					author.name = node.firstChild.nodeValue;
					break;
			}
		}
		
		// Return this author
		return author;
	}
	
	private function parseDrawingXML(drawingNode: XMLNode): Void {
		// Make a new drawing
		var galleryDrawing: GalleryDrawing = new GalleryDrawing();
		
		// Run through the child nodes, adding as we go
		for (var num: Number = 0; num < drawingNode.childNodes.length; num++) {
			var node: XMLNode = drawingNode.childNodes[num];
			
			switch (node.nodeName) {
				case "drawing_id":
					galleryDrawing.id = node.firstChild.nodeValue;
					break;
				case "width":
					galleryDrawing.width = Number(node.firstChild.nodeValue);
					break;
				case "height":
					galleryDrawing.height = Number(node.firstChild.nodeValue);
					break;
				case "num_stages":
					galleryDrawing.numStages = Number(node.firstChild.nodeValue);
					break;
				case "stage_author":
					galleryDrawing.authors.push(parseAuthorXML(node));
					break;
			}
		}
		
		// Add this drawing
		galleryDrawings.push(galleryDrawing);
	}
	
	public function parseXML(responseNode: XMLNode): Void {
		// Look for gallery drawing child nodes
		for (var num: Number = 0; num < responseNode.childNodes.length; num++) {
			var node: XMLNode = responseNode.childNodes[num];
			
			switch (node.nodeName) {
				case "gallery_drawing":
					parseDrawingXML(node);
					break;
			}
		}
	}
}