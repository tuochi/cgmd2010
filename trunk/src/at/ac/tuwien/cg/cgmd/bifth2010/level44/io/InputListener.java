package at.ac.tuwien.cg.cgmd.bifth2010.level44.io;

import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import at.ac.tuwien.cg.cgmd.bifth2010.level44.GameScene;

/**
 * InputListener to detect various Gestures (Fling/Swipe, Tap)
 * 
 * @author Matthias
 *
 */

public class InputListener extends SimpleOnGestureListener {
	/** maximum horizontal distance to be travelled during swipe to be detected as a Swipe Gesture */
	private static final int SWIPE_MAX_OFF_PATH = 70;
	private static final int SWIPE_THRESHOLD_VELOCITY = 100;
	/** width of the Screen */
	private int width;
	/** height of the Screen */
	private int height;
	private GameScene scene;

	
	public InputListener(GameScene scene, int width, int height) {
		this.scene = scene;
		this.width = width;
		this.height = height;
	}
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		InputGesture.Position position;
		
		// check that swipe is straight
		if (Math.abs(e1.getX() - e2.getX()) > SWIPE_MAX_OFF_PATH)
            return false;
		
		// check if swipe is long enough to be a gesture
		if (e2.getY() - e1.getY() > Swipe.MIN_LENGTH && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
			// left half of screen
			if (e1.getX() <= width/3) {
				position = InputGesture.Position.LEFT;
			} 
			// right half of screen
			else if (e1.getX() >= width*2/3) {
				position = InputGesture.Position.RIGHT;
			}
			// Middle of the screen
			else {
				position = InputGesture.Position.MIDDLE;
			}
			
			// add Gesture to InputQueue
			scene.addInputGesture(new Swipe(e1.getX(), e1.getY(), e2.getX(), e2.getY(), position));
		}
		
		return true;
	}
	
	@Override
	public boolean onDoubleTap(MotionEvent e) {
		scene.addInputGesture(new DoubleTap(e.getX(), e.getY()));
		
		return true;
	}
}