class UserDetailsRequest implements RequestOperator {
	
	public var userID: String;
	
	private function validate(): Void {
		if (userID == null) {
			throw new Error("No user id specified");
		}
	}
	
	private function constructXML(xmlDoc: XML): XMLNode {
		// Create the root element of this request
		var requestNode: XMLNode = xmlDoc.createElement("user_details");
		
		// Set the attributes
		requestNode.attributes.id = userID;
		
		return requestNode;
	}
	
	public function addXMLRequest(xmlDoc: XML): XMLNode {
		// Perform validation
		validate();
		
		// Now need to construct XML
		return constructXML(xmlDoc);
	}
}