class ResponseError implements ResponseOperator {
	public var source: String;
	public var type: String;
	public var message: String;
	public var stackTrace: Array = new Array();
	
	public function parseXML(responseNode: XMLNode): Void {
	}
}