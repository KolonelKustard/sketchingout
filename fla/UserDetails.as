class UserDetails {
	private var user: User;
	
	public var userID: String = "1";
	public var nameEdit: TextField;
	public var emailEdit: TextField;
	public var sigCanvas: CanvasMovieClip;
	
	// Event handlers
	public var onErrorNoName: Function = null;
	public var onErrorInvalidEmail: Function = null;
	
	private function validateEmailAddress(email: String): Boolean {
		return null;
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
		
		sigCanvas.drawing = user.signature;
	}
	
	public function modified(): Boolean {
		// If not had a user set in yet then it's false
		if (user == null) return false;
		
		// Compare values
		if (
			(nameEdit.text != user.name) or
			(emailEdit.text != user.email) or
			(sigCanvas.modified)
		)
			return true
		else
			return false;
	}
	
	public function validateUserDetails(): Boolean {
		// Check the name
		if (nameEdit.text == "") {
			if (onErrorNoName <> null) onErrorNoName();
			return false;
		}
		
		// Check the email address entered
		if (emailEdit.text == "") {
			if (onErrorInvalidEmail <> null) onErrorInvalidEmail();
			return false;
		}
		else {
			if (!validateEmailAddress(emailEdit.text)) {
				if (onErrorInvalidEmail <> null) onErrorInvalidEmail();
				return false;
			}
		}
		
		return true;
	}
	
	public function getSubmitUserRequest(): SubmitUserRequest {
		// Set in our users values
		user.name = nameEdit.text;
		user.email = emailEdit.text;
		user.signature = sigCanvas.drawing;
		
		var subUser: SubmitUserRequest = new SubmitUserRequest();
		subUser.user = this.user;
		
		return subUser;
	}
}