function flashMediator(form, flashProxy, flashMethod) {
	// Make up an id
	var id = Math.round(Math.random() * new Date().getTime());
	
	// Set the hidden field of the form to the id
	form.MNNNnnnnggggghhhhhh.value = id;
	
	// Link the form submission to our handler
	form.onsubmit = function() {
		alert("TART " + flashMethod + " [" + id + "]");
	
		// Alert the flash movie to the submission
		flashProxy.call(flashMethod, id);
		
		return true;
	}
}