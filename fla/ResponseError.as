class ResponseError implements ResponseOperator {
	public static var ERR_INVALID_DRAWING_ID: Number = 1;
	
	public var source: String;
	public var type: String;
	public var code: Number = -1;
	public var message: String;
	public var fullStackTrace: String;
	public var stackTrace: Array = new Array();
	
	private function parseStackTrace(stackTraceNode: XMLNode): Void {
		fullStackTrace = "";
		for (var num: Number = 0; num < stackTraceNode.childNodes.length; num++) {
			var stackNode: XMLNode = stackTraceNode.childNodes[num];
			if (stackNode.nodeName = "trace") {
				// Add to the array of strings
				stackTrace.push(stackNode.firstChild.nodeValue);
				
				// Add onto the end of the stack trace string
				if (fullStackTrace != "") fullStackTrace += newline;
				fullStackTrace += stackNode.firstChild.nodeValue;
			}
		}
	}
	
	public function parseXML(responseNode: XMLNode): Void {
		// Get the properties
		for (var num: Number = 0; num < responseNode.childNodes.length; num++) {
			var node: XMLNode = responseNode.childNodes[num];
			
			switch (node.nodeName) {
				case "src":
					source = node.firstChild.nodeValue;
					break;
				case "type":
					type = node.firstChild.nodeValue;
					break;
				case "code":
					code = Number(node.firstChild.nodeValue);
					break;
				case "message":
					message = node.firstChild.nodeValue;
					break;
				case "stacktrace":
					parseStackTrace(node);
					break;
			}
		}
	}
	
	public function toString(): String {
		return message + " (" + type + ": " + source + ")" + newline + fullStackTrace;
	}
}