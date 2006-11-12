class User {
	private static var USER_DETAILS_SHARED_OBJECT: String = "UserDetails";
	
	private var so: SharedObject;
	
	public var id: String;
	public var name: String;
	public var email: String;
	public var signature: Drawing;
	
	private function load(id: String): Void {
		trace("Loading person: " + id);
		
		// Check the id matches
		if ((so.data.id != undefined) && (so.data.id != id)) {
			trace("Clearing: " + so.data.id);
			so.clear();
		}
		this.id = id;
		
		// If everything is defined, load it in
		if (so.data.name != undefined) this.name = so.data.name;
		else this.name = "";
		
		if (so.data.email != undefined) this.email = so.data.email;
		else this.email = "";
		
		if (so.data.signature != undefined) this.signature.fromString(so.data.signature);
		else signature.clear();
		
		trace("Got person: " + so.data.name + " [" + so.data.email + "]");
	}
	
	public function save(): Void {
		so.data.id = this.id;
		so.data.name = this.name;
		so.data.email = this.email;
		so.data.signature = this.signature.toString();
		
		trace(so.flush());
	}
	
	public function User(id: String) {
		so = SharedObject.getLocal(USER_DETAILS_SHARED_OBJECT);
		signature = new Drawing();
		
		load(id);
	}
}