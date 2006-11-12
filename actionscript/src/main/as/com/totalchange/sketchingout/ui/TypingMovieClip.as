class TypingMovieClip extends MovieClip {
	private static var LETTERS_PER_SECOND: Number = 60;
	
	private var txtFld: TextField;
	
	private var theText: String;
	private var textLen, textWritten: Number;
	private var startTime: Number;
	
	/**
	 * Writes the appropriate amount out each frame
	 */
	private function onEnterFrame(): Void {
		if (textWritten < textLen) {
			// Work out how much should be written
			textWritten = Math.ceil(((getTimer() - startTime) / 1000) * LETTERS_PER_SECOND);
			
			// Put it in the text field
			if (textWritten != txtFld.text.length) {
				txtFld.text = theText.substr(0, textWritten);
			}
		}
	}
	
	/**
	 * The constructor first must get the text out of the text field
	 * so it appears to be blank...
	 */
	public function TypingMovieClip() {
		// Get the text
		theText = txtFld.text;
		
		// Clear the text box
		txtFld.text = "";
		
		// Get the appropriate lengths
		textLen = theText.length;
		textWritten = 0;
		
		// Set the start time for measurement against later on
		startTime = getTimer();
	}
}