class Gallery {
	private var currNum: Number = 0;
	
	private function makeTempDrawing(): GalleryDrawing {
		var drawing: GalleryDrawing = new GalleryDrawing();
		drawing.id = "1";
		drawing.width = 400;
		drawing.height = 900;
		drawing.numStages = 4;
		
		for (var num: Number = 0; num < 4; num++) {
			var auth: GalleryDrawingAuthor = new GalleryDrawingAuthor();
			auth.stage = num + 1;
			auth.name = "Test Gallery User";
			
			drawing.authors.push(auth);
		}
		
		return drawing;
	}
	
	public function prev(): GalleryDrawing {
		if (currNum > 0) {
			currNum--;
			return makeTempDrawing();
		}
		else {
			return null;
		}
	}
	
	public function next(): GalleryDrawing {
		if (currNum < 50) {
			currNum++;
			return makeTempDrawing();
		}
		else {
			return null;
		}
	}
}