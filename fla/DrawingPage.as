class DrawingPage extends MovieClip {
	public var nextDrawing: CanvasMovieClip;
	public var prevDrawing: CanvasMovieClip;
	public var userDetails: UserDetails;
	public var dragClip: DragMovieClip;
	public var submitButton: MovieClip;
	public var friendsEmailEdit: TextField;
	public var clearDrawingButton: Button;
	
	function DrawingPage() {
		userDetails = new UserDetails();
	}
}