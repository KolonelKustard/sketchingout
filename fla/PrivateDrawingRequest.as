class PrivateDrawingRequest implements RequestOperator {
	
	public var userID: String;
	public var drawingID: String;
	
	private function validate(): Void {
		if (userID == null) {
			throw new Error("No user id specified");
		}
		
		if (drawingID == null) {
			throw new Error("No drawing id specified");
		}
	}
	
	private function constructXML(xmlDoc: XML): XMLNode {
		// Create the root element of this request
		var requestNode: XMLNode = xmlDoc.createElement("submit_drawing");
		
		// Set the attributes
		requestNode.attributes.user_id = userID;
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