class CountdownMovieClip extends MovieClip {
	private var txtFld: TextField;
	
	private var startTime: Number;
	private var endTime: Number;
	private var running: Boolean = false;
	private var sentReminder: Boolean = false;
	private var minsRemaining: Number;
	private var secsRemaining: Number;
	
	public var prefix: String = "";
	public var suffix: String = "";
	
	public var onNearlyDone: Function = null;
	public var onDone: Function = null;
	
	private function changeTimer(): Void {
		// Determine how much time is remaining.
		var timeRemaining: Number = endTime - getTimer();
		
		// Don't want negative timeRemaining values
		if (timeRemaining < 0) timeRemaining = 0;
		
		// Determine time remaining in minutes and seconds
		var secs: Number = Math.floor((timeRemaining / 1000) % 60);
		
		// See if the text in the field should be changed
		if (secs != secsRemaining) {
			var hrs: Number = Math.floor((timeRemaining / 1000) / 3600);
			var mins: Number = Math.floor((timeRemaining / 1000) / 60);
			
			minsRemaining = mins;
			secsRemaining = secs;
			
			var hrsTxt: String;
			var minsTxt: String;
			var secsTxt: String;
			
			if (hrs == 1) hrsTxt = "hr"; else hrsTxt = "hrs";
			if (mins == 1) minsTxt = "min"; else minsTxt = "mins";
			if (secs == 1) secsTxt = "sec"; else secsTxt = "secs";
			
			if (hrs > 0) {
				txtFld.text =
					prefix +
					String(hrs) + " " + hrsTxt + " " +
					String(mins) + " " + minsTxt +
					suffix;
			}
			else {
				txtFld.text =
					prefix +
					String(mins) + " " + minsTxt + " " +
					String(secs) + " " + secsTxt +
					suffix;
			}
		}
		
		// If gone over the reminder period, send reminder
		if ((!sentReminder) && (timeRemaining <= SketchingoutSettings.COUNTDOWN_TIMER_REMINDER)) {
			sentReminder = true;
			if (onNearlyDone != null) onNearlyDone(timeRemaining);
		}
		
		// If gone over the all done period, say it's out of time
		if (timeRemaining <= 0) {
			running = false;
			if (onDone != null) onDone();
		}
	}
	
	/**
	 * Starts this timer to countdown the number of seconds as given
	 * by interval parameter.
	 */
	public function countdown(interval: Number): Void {
		if (interval <= 0) throw new Error("Interval must be greater than 0");
		
		startTime = getTimer();
		endTime = startTime + (1000 * interval);
		
		running = true;
		sentReminder = false;
		minsRemaining = 0;
		secsRemaining = 0;
		changeTimer();
	}
	
	public function onEnterFrame(): Void {
		if (running) changeTimer();
	}
}