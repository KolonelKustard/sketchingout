class UserDetailsResponse implements ResponseOperator {
	public var user: User;
	
	public function parseXML(responseNode: XMLNode): Void {
		// Go through the nodes that are defined and fill in the blanks on
		// the user object linked to this response.
		for (var num: Number = 0; num < responseNode.childNodes.length; num++) {
			var node: XMLNode = responseNode.childNodes[num];
			
			switch (node.nodeName) {
				case "id":
					user.id = node.firstChild.nodeValue;
					break;
				case "name":
					user.name = node.firstChild.nodeValue;
					break;
				case "email":
					user.email = node.firstChild.nodeValue;
					break;
				case "signature":
					user.signature = new Drawing();
					user.signature.fromString(node.firstChild.nodeValue);
					break;
			}
		}
	}
	
	public function UserDetailsResponse() {
		user = new User();
	}
}