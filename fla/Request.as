class Request {
	private static var DEFAULT_URL: String = "http://localhost:8080/consequences/consequences";
	
	private var requestXML: XML;
	private var responseXML: XML;
	private var rootNode: XMLNode;
	
	private var requestURL: String = DEFAULT_URL;
	
	public function Request() {
		// Reset the request
		clear();
	}
	
	private function loadXML(success: Boolean) {
		trace("Loaded: " + success);
		trace(responseXML.toString());
	}
	
	public function clear() {
		// Create the root of the request
		requestXML = new XML();
		requestXML.ignoreWhite = true;
		requestXML.onLoad = loadXML;
		
		rootNode = requestXML.createElement("request");
		requestXML.appendChild(rootNode);
	}
	
	public function addRequest(operator: RequestOperator) {
		// Add the request
		rootNode.appendChild(operator.addXMLRequest(requestXML));
	}
	
	public function send() {
		// Construct the response
		responseXML = new XML();
		responseXML.ignoreWhite = true;
		responseXML.onLoad = loadXML;
		
		// Send the request
		requestXML.sendAndLoad(requestURL, responseXML);
		
		// Clear the request
		//clear();
	}
	
	public function toString(): String {
		return requestXML.toString();
	}
}