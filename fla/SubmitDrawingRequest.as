﻿class SubmitDrawingRequest implements RequestOperator {
	
	private static var MIN_STAGE: Number = 1;
	private static var MAX_STAGE: Number = 4;
	
	public var userID: String;
	public var drawingID: String;
	public var stage: Number;
	public var drawing: Drawing;
	public var nextUserEmail: String;
	
	private function validate(): Void {
		if (userID == null) {
			throw new Error("No user id specified");
		}
		
		if (drawingID == null) {
			throw new Error("No drawing id specified");
		}
		
		if ((stage < MIN_STAGE) || (stage > MAX_STAGE)) {
			throw new Error("Stage outside of allowed bounds");
		}
		
		if (drawing == null) {
			throw new Error("No drawing specified to send");
		}
	}
	
	private function constructXML(xmlDoc: XML): XMLNode {
		// Create the root element of this request
		var requestNode: XMLNode = xmlDoc.createElement("submit_drawing");
		
		// Set the attributes
		requestNode.attributes.user_id = userID;
		requestNode.attributes.drawing_id = drawingID;
		requestNode.attributes.width = drawing.width;
		requestNode.attributes.height = drawing.height;
		requestNode.attributes.offsety = drawing.offsetY;
		requestNode.attributes.stage = stage;
		if (nextUserEmail != null) {
			requestNode.attributes.next_user_email = nextUserEmail;
		}
		
		// Add the drawing node
		var drawingNode: XMLNode = xmlDoc.createElement("drawing");
		drawingNode.appendChild(xmlDoc.createTextNode(drawing.toString()));
		requestNode.appendChild(drawingNode);
		
		return requestNode;
	}
	
	public function addXMLRequest(xmlDoc: XML): XMLNode {
		// Perform validation
		validate();
		
		// Now need to construct XML
		return constructXML(xmlDoc);
	}
}