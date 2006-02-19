class GalleryDrawingsRequest implements RequestOperator {
	
	public static var GALLERY_REQUEST_TYPE_LATEST = 0;
	
	public var type: Number = GALLERY_REQUEST_TYPE_LATEST;
	public var start: Number = 0;
	public var quantity: Number = 25;
	
	private function validate(): Void {
		if (type <> 0) throw new Error("Must specify a valid type of gallery request");
		if (quantity <= 0) throw new Error("Quantity must be more than 0");
	}
	
	private function constructXML(xmlDoc: XML): XMLNode {
		// Create the root element of this request
		var requestNode: XMLNode = xmlDoc.createElement("gallery_drawings");
		
		// Set the attributes
		requestNode.attributes.type = type;
		requestNode.attributes.start = start;
		requestNode.attributes.quantity = quantity;
		
		return requestNode;
	}
	
	public function addXMLRequest(xmlDoc: XML): XMLNode {
		// Perform validation
		validate();
		
		// Now need to construct XML
		return constructXML(xmlDoc);
	}
}