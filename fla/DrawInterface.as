class DrawInterface extends MovieClip {
	private var innerMC: MovieClip = null;
	private var eventGone: Boolean = true;
	private var mcNum: Number = 1;
	
	public var onMovieClipPlayed: Function = null;

	public function onEnterFrame(): Void {
		if (
			(innerMC != null) && // Got a movie clip?
			(!eventGone) && // Has event already fired?
			(innerMC._totalframes != 0) && // Makes sure there are some frames loaded
			(innerMC._currentframe == innerMC._totalframes) // Sees if at the end
		) {
			eventGone = true;
			if (onMovieClipPlayed != null) onMovieClipPlayed(this);
		}
	}
	
	public function loadMovie(url: String): Void {
		// If already loaded, make sure gets removed
		if (innerMC != null) innerMC.unloadMovie();
		
		// Say not fired yet and create a new blank movie clip
		eventGone = false;
		innerMC = createEmptyMovieClip("innerMC", 1);
		innerMC.loadMovie(url);
	}
}