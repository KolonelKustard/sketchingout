﻿class GuiValidationClip extends MovieClip {

  // Declare props to reflect expected contents of this movie clip
  public var mcBubbleMsg:MovieClip;
  
  // Executes when this clip first loads
  public function onLoad() {
    // Start off invisible
    this._visible = false;
  }
  
  // Public function to show a message
  public function showMessage(message:String, pointTo:mx.core.UIComponent, focus:Boolean, offsetX:Number,  offsetY:Number) {
    // Place the message in the text field within this clip
    mcBubbleMsg.txtMessage.text = message;

    // Position this clip on the Stage
    this._y = pointTo.y;
    this._x = pointTo._x + pointTo.width;
    
    // Set focus to the item we're pointing at, if called for 
    if (focus) {
      pointTo.setFocus();
    }
	
	// Set Offset X, if called for 
    if (offsetX) {
      this._x = pointTo._x + offsetX;
    }
	
	// Set Offset Y, if called for 
    if (offsetY) {
      this._y = pointTo._y + offsetY;
    }
  
    // Make this clip visible
    this._visible = true;
    
    // Play fade-in animation
    this.gotoAndPlay("fade in");
  }
  
  // Public function to hide the message
  public function hideMessage() {
    this._visible = false;
  }
}