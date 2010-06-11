package at.ac.tuwien.cg.cgmd.bifth2010.level44;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import at.ac.tuwien.cg.cgmd.bifth2010.framework.SessionState;
import at.ac.tuwien.cg.cgmd.bifth2010.level44.io.InputGesture;
import at.ac.tuwien.cg.cgmd.bifth2010.level44.io.InputListener;
import at.ac.tuwien.cg.cgmd.bifth2010.level44.sound.SoundPlayer;
import at.ac.tuwien.cg.cgmd.bifth2010.level44.io.Swipe;
import at.ac.tuwien.cg.cgmd.bifth2010.level44.io.DoubleTap;


/**
 * Entry-Point for Level 44
 */

public class LevelActivity extends Activity {
	/** The Scene for displaying */
	private GameScene scene;
	/** detect input gestures */
	private GestureDetector gestureDetector;

	/**
	 * @see Activity.onCreate(Bundle b)
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// always change Media Volume
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		/* Fullscreen window without title */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Window window = getWindow();
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		scene = new GameScene(this);
		
		if (savedInstanceState != null) {
			scene.restoreInstanceState(savedInstanceState);
		}
		
		setContentView(scene);
		
		Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		gestureDetector = new GestureDetector(new InputListener(scene, display.getWidth(), display.getHeight()));
		new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (gestureDetector.onTouchEvent(event)) {
					return true;
				}
				return false;
			}
		};
		
		/* Default result when user exits the activity */
		SessionState s = new SessionState();
		s.setProgress(0);
		setResult(Activity.RESULT_OK, s.asIntent());
	}

	public void finishLevel(int score) {
		//the SessionState is a convenience class to set a result
		SessionState s = new SessionState();
		//we set the progress the user has made (must be between 0-100)
		s.setProgress(score);
		//we call the activity's setResult method 
		setResult(Activity.RESULT_OK, s.asIntent());
		//we finish this activity
		finish();
	}

	/**
	 * Resume the scene
	 */
	@Override
	protected void onResume() {
		super.onResume();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		// check if sounds effects are set and create soundPlayer
		SessionState state = new SessionState(getIntent().getExtras());
		if(state!=null) {
			SoundPlayer.createInstance(getApplicationContext(), state.isMusicAndSoundOn());
		} else {
			SoundPlayer.createInstance(getApplicationContext(), false);
		}
		
		System.out.println("##### Music: " + SoundPlayer.getInstance().isMusicOn());
		
		if (scene != null) {
			scene.onResume();
		}
	}

	/**
	 * Pause the scene
	 */
	@Override
	protected void onPause() {
		super.onPause();
		
		SoundPlayer.getInstance().release();
		
		if (scene != null) {
			scene.onPause();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}
	
	@Override
	public boolean onTrackballEvent(MotionEvent event) {
		if (scene != null) {
			InputGesture gesture = null;

			if (event.getY() < -.1f) {
				/* Up movement - flap both wings */
				gesture = new DoubleTap(0, 0);
			} else if (event.getX() < -.1f) {
				/* Left movement - flap right wing (go left) */
				gesture = new Swipe(0, Swipe.MAX_LENGTH, 0, 0, Swipe.MAX_VELOCITY*.7f, InputGesture.Position.RIGHT);
			} else if (event.getX() > .1f) {
				/* Right movement - flap left wing (go right) */
				gesture = new Swipe(0, Swipe.MAX_LENGTH, 0, 0, Swipe.MAX_VELOCITY*.7f, InputGesture.Position.LEFT);
			}

			if (gesture != null) {
				scene.addInputGesture(gesture);
			}
		}
		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if (scene != null) {
			scene.saveInstanceState(outState);
		}
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		
		if (scene != null) {
			scene.restoreInstanceState(savedInstanceState);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	        finishLevel(scene.getScore());
	    }
	    
	    return super.onKeyDown(keyCode, event);
	}
}
