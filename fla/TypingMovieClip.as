class TypingMovieClip extends MovieClip {
	private var txtFld: TextField;
	
	private var theText, subText: String;
	private var textLen, textWritten: Number;
	
	/**
	 * Writes the appropriate amount out each frame
	 */
	private function onEnterFrame(): Void {
		if (textWritten < textLen) {
			subText += theText.charAt(textWritten);
			txtFld.text = subText;
			
			textWritten++;
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
		
		// Make sure the sub text starts as empty
		subText = "";
		
		// Get the appropriate lengths
		textLen = theText.length;
		textWritten = 0;
	}
}