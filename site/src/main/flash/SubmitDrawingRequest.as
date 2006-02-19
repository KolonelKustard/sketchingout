class SubmitDrawingRequest implements RequestOperator {
	
	private static var MIN_STAGE: Number = 1;
	private static var MAX_STAGE: Number = 4;
	
	public var userID: String;
	public var userName: String;
	public var userEmail: String;
	public var drawingID: String;
	public var stage: Number;
	public var drawing: Drawing;
	public var signature: Drawing;
	public var nextUserEmail: String;
	
	private function validate(): Void {
		if (userID == null) {
			throw new Error("No user id specified");
		}
		
		if (userName == null) {
			throw new Error("No user name specified");
		}
		
		if (userEmail == null) {
			throw new Error("No user email specified");
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
		
		if (signature == null) {
			throw new Error("No signature specified to send");
		}
	}
	
	private function constructXML(xmlDoc: XML): XMLNode {
		// Create the root element of this request
		var requestNode: XMLNode = xmlDoc.createElement("submit_drawing");
		
		// Have to do a little check here.  If on the last stage, then the drawing
		// cannot have an offset (no drawing will follow so it's not appropriate).
		if (stage >= MAX_STAGE) {
			drawing.offsetY = 0;
		}
		
		// Set the attributes
		requestNode.attributes.user_id = userID;
		requestNode.attributes.user_name = userName;
		requestNode.attributes.user_email = userEmail;
		requestNode.attributes.drawing_id = drawingID;
		requestNode.attributes.width = Math.ceil(drawing.width);
		requestNode.attributes.height = Math.ceil(drawing.height);
		requestNode.attributes.offsety = Math.round(drawing.offsetY);
		requestNode.attributes.signature_width = Math.ceil(signature.width);
		requestNode.attributes.signature_height = Math.ceil(signature.height);
		requestNode.attributes.stage = stage;
		if (nextUserEmail != null) {
			requestNode.attributes.next_user_email = nextUserEmail;
		}
		
		// Add the drawing node
		var drawingNode: XMLNode = xmlDoc.createElement("drawing");
		drawingNode.appendChild(xmlDoc.createTextNode(drawing.toString()));
		requestNode.appendChild(drawingNode);
		
		// Add the signature node
		var signatureNode: XMLNode = xmlDoc.createElement("signature");
		signatureNode.appendChild(xmlDoc.createTextNode(signature.toString()));
		requestNode.appendChild(signatureNode);
		
		return requestNode;
	}
	
	public function addXMLRequest(xmlDoc: XML): XMLNode {
		// Perform validation
		validate();
		
		// Now need to construct XML
		return constructXML(xmlDoc);
	}
}