function FlashMediator(form, flashProxy, flashMethod) {
	this.form = form;
	this.flashProxy = flashProxy;
	this.flashMethod = flashMethod;
	
	// Make up an id
	this.id = Math.round(Math.random() * new Date().getTime());
	
	// Set the hidden field of the form to the id
	this.form.MNNNnnnnggggghhhhhh.value = this.id;
	
	// Link the form submission to our handler
	this.form.onsubmit = this.submitHandler;
}

FlashMediator.prototype.submitHandler = function() {
	// Alert the flash movie to the submission
	this.flashProxy.call(this.flashMethod, this.id);
	
	return true;
}