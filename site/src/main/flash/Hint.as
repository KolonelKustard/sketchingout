class Hint {
	private static var FADE_OUT_TIME = 200;
	private static var FADE_BACK_IN_TIME = 200;
	
	private var startedHiding: Number;
	
	public var hintVisible: Boolean;
	public var hintFocused: Boolean;
	
	public var control: Object;
	public var clipUrl: String;
	public var clip: MovieClip;
	
	public function showHint(): Void {
		startedHiding = -1;
		
		if (!hintVisible) {
			hintVisible = true;
			clip._alpha = 100;
			clip.gotoAndPlay(1);
		} else {
			clip._alpha = 100;
		}
	}
	
	public function hideHint(): Void {
		if (startedHiding < 0) startedHiding = getTimer();
		
		// Fade by the appropriate amount
		var newAlpha: Number = 100 - (((getTimer() - startedHiding) / FADE_OUT_TIME) * 100);
		
		if (newAlpha < 0) clip._alpha = 0;
		else clip._alpha = newAlpha;
		
		if (clip._alpha == 0) {
			hintVisible = false;
			startedHiding = -1;
		}
	}
	
	public function setHintUrl(url: String): Void {
		if (url != null) clip.loadMovie(url);
	}
	
	public function Hint(parent: HintsClip, control: Object, clipUrl: String) {
		this.control = control;
		this.clipUrl = clipUrl;
		
		var depthNum: Number = parent.getNextDepth();
		clip = parent.createEmptyMovieClip("hint_clip_" + depthNum, depthNum);
		clip._alpha = 0;
		
		startedHiding = -1;
		
		setHintUrl(clipUrl);
	}
}