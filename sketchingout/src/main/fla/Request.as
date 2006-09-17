class Request {
	private var requestXML: XML;
	private var rootNode: XMLNode;
	
	public function Request() {
		// Reset the request
		clear();
	}
	
	public function clear(): Void {
		// Create the root of the request
		requestXML = new XML();
		requestXML.ignoreWhite = true;
		
		rootNode = requestXML.createElement("request");
		requestXML.appendChild(rootNode);
	}
	
	public function addRequest(operator: RequestOperator): Void {
		// Add the request
		rootNode.appendChild(operator.addXMLRequest(requestXML));
	}
	
	public function getXML(): XML {
		return new XML(requestXML.toString());
	}
	
	public function toString(): String {
		return requestXML.toString();
	}
}