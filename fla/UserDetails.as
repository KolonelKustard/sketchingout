class UserDetails {
	private var user: User;
	
	public var userID: String = "1";
	public var nameEdit: TextField;
	public var emailEdit: TextField;
	public var sigCanvas: CanvasMovieClip;
	
	public function UserDetails() {
	}
	
	public function setUserDetails(user: User): Void {
		this.user = user;
		userID = user.id;
		
		if (user.name != null) {
			nameEdit.text = user.name;
		}
		
		if (user.email != null) {
			emailEdit.text = user.email;
		}
	}
}