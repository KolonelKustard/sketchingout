function FlashMediator(form, flashProxy, flashMethod) {
	this.form = form;
	this.flashProxy = flashProxy;
	this.flashMethod = flashMethod;
	
	// Add a hidden form field to the form
	
	
	// Link the form submission to our handler
	this.form.onsubmit = this.submitHandler;
}

FlashMediator.prototype.submitHandler = function() {
	alert("Test");
	
	// Alert the flash movie to the submission
	this.flashProxy.call(this.flashMethod, "bum");
	
	return true;
}