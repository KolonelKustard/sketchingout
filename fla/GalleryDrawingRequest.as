class GalleryDrawingRequest implements RequestOperator {
	
	public var drawingID: String;
	
	private function validate(): Void {
		if (drawingID == null) throw new Error("Must specify a drawing id");
	}
	
	private function constructXML(xmlDoc: XML): XMLNode {
		// Create the root element of this request
		var requestNode: XMLNode = xmlDoc.createElement("gallery_drawing");
		
		// Set the attributes
		requestNode.attributes.drawing_id = drawingID;
		
		return requestNode;
	}
	
	public function addXMLRequest(xmlDoc: XML): XMLNode {
		// Perform validation
		validate();
		
		// Now need to construct XML
		return constructXML(xmlDoc);
	}
}