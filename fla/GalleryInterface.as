/**
 * This class extends the DrawInterface class.  It adds the ability to move
 * itself across the stage and destroy itself once it reaches the far edge
 * of the screen.
 */
class GalleryInterface extends DrawInterface {
	public function onEnterFrame(): Void {
		// Make sure the parent class' (DrawInterface) onEnterFrame is called
		super.onEnterFrame();
		
		// Move this instance left
		_x = _x - 1;
	}
}