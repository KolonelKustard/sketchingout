class SubmitUserRequest implements RequestOperator {
	
	public var user: User;
	
	private function validate(): Void {
		if (user == null) throw new Error("Must specify a User instance");
		if (user.id == null) throw new Error("No user id specified");
	}
	
	private function constructXML(xmlDoc: XML): XMLNode {
		// Create the root element of this request
		var requestNode: XMLNode = xmlDoc.createElement("user_submit");
		
		// Set the attributes
		requestNode.attributes.id = user.id;
		if (user.name != null) requestNode.attributes.name = user.name;
		if (user.email != null) requestNode.attributes.email = user.email;
		
		// If have a signature, add it
		if (user.signature != null) {
			// Set its width and height into the submission
			requestNode.attributes.signature_width = user.signature.width;
			requestNode.attributes.signature_height = user.signature.height;
			
			var signatureNode: XMLNode = xmlDoc.createElement("signature");
			signatureNode.appendChild(xmlDoc.createTextNode(user.signature.toString()));
			requestNode.appendChild(signatureNode);
		}
		
		return requestNode;
	}
	
	public function addXMLRequest(xmlDoc: XML): XMLNode {
		// Perform validation
		validate();
		
		// Now need to construct XML
		return constructXML(xmlDoc);
	}
}