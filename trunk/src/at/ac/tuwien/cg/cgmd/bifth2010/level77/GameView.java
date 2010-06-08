package at.ac.tuwien.cg.cgmd.bifth2010.level77;

import java.io.BufferedInputStream;
import java.util.prefs.Preferences;

import at.ac.tuwien.cg.cgmd.bifth2010.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.preference.Preference;
import android.util.Log;
import android.view.MotionEvent;

/**
 * GameView is the view of the game implementation of group 77. 
 * @author Gerd Katzenbeisser
 *
 */
public class GameView extends GLSurfaceView
{

	protected static final String	TAG	= "GameView";
	L77Renderer renderer;
	
	
	Native jni;
	Audio audio;
	private int score;
	private Callback<Integer> deleteMe;
	private SharedPreferences prefs;
	
	public GameView(Context context, Callback<Integer> gameEnded)
	{
		super(context);
		audio = new Audio(context);
		deleteMe = gameEnded;
		
		Callback<Integer> updateScore = new Callback<Integer>()
		{
			
			@Override
			public void onSucces(Integer result)
			{
				Log.i(TAG, "Received new score: " + result);
				updateScore(result);
			}
			
			@Override
			public void onFailure(Throwable caught)
			{
				Log.e(TAG, "Error while updating score");
				caught.printStackTrace();
			}
		};
		
		// Play the theme song
		//audio.playSound(Audio.BUNNY_BLOCK_THEME);
		audio.playSound(Audio.BLOCK_EXPLODE_SOUND_1);

		jni = new Native(context, audio, gameEnded, updateScore);
		
		// native depends on renderer vars initialised
		setRenderer(new L77Renderer(true, context, jni));
		
	}
	/**
	 * TODO Javadoc
	 * @param sharedPreferences 
	 */
	public void onPause(SharedPreferences sharedPreferences) {
		super.onPause();
		SharedPreferences.Editor ed = sharedPreferences.edit();
		ed.putString("native", jni.nativeGetSavedState());
		ed.putInt("score", score);
	}

	/**
	 * TODO Javadoc
	 */
	public void onResume(SharedPreferences sharedPreferences) {
		super.onResume();
		score = sharedPreferences.getInt("score", 0);
		jni.nativeRestoreSavedState(sharedPreferences.getString("native", ""));
	}

	
	/**
	 * Touch Events are catched and sent to a native call.
	 */
	@Override
	public boolean onTouchEvent(final MotionEvent event)
	{
		if (event.getAction() == MotionEvent.ACTION_DOWN)
			jni.touchesBegan(event.getX(), event.getY());
		if (event.getAction() == MotionEvent.ACTION_MOVE)
			jni.touchesMoved(event.getX(), event.getY());
		if (event.getAction() == MotionEvent.ACTION_UP)
			jni.touchesEnded(event.getX(), event.getY());
			
		return true;
	}
	
	
	/**
	 * Updates the current score
	 * @param score
	 */
	private void updateScore(int score)
	{
		this.score = score;
		// TODO Display score
	}
}
