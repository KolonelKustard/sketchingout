class DrawInterface extends MovieClip {
	
	function DrawInterface() {
	}

	public function onEnterFrame(): Void {
		if (this._currentframe != 0 && this._currentframe == this._totalframes) {
			//trace("end of clip")
			//
		}
	}
	
	public function onLoad(): Void {
		trace("clip loaded")
	}
}