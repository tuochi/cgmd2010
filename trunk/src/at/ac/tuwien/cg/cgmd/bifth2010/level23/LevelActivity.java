package at.ac.tuwien.cg.cgmd.bifth2010.level23;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import at.ac.tuwien.cg.cgmd.bifth2010.R;
import at.ac.tuwien.cg.cgmd.bifth2010.level23.entities.MainChar;
import at.ac.tuwien.cg.cgmd.bifth2010.level23.render.RenderView;
import at.ac.tuwien.cg.cgmd.bifth2010.level23.util.ObstacleManager;
import at.ac.tuwien.cg.cgmd.bifth2010.level23.util.OrientationListener;
import at.ac.tuwien.cg.cgmd.bifth2010.level23.util.OrientationManager;
import at.ac.tuwien.cg.cgmd.bifth2010.level23.util.Settings;

/**
 * The Class LevelActivity handles the Android Lifecycle for the level and takes care of the interaction with the use
 * @author Markus Ernst
 * @Author Florian Felberbauer
 */

public class LevelActivity extends Activity implements OrientationListener {

	/** The renderer. */
	private RenderView renderer; 
	
	private TextView fpsText;
	
	public static final Handler handler = new Handler();
	
	/** The CONTEXT. */
	private static Context CONTEXT; 
	
	/** The instance of the LevelActivity to pass around. */
	private static LevelActivity instance;
	
	/** The Constant SENSOR_MENU_ITEM if a user wants to use the orientation sensor. */
	private static final int SENSOR_MENU_ITEM = 1;
	
	//private Vector2 mainCharPos; 
	/**
	 * called when the activity is created. Here, the window is created and the the UI is placed by RenderView renderer
	 * @param savedInstanceState the Bundle received from Android 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v("dasdf", "onCreate in LevelActivity.java");
		// thx @ lvl 11
		/* Fullscreen window without title */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	 	Window window = getWindow();
	 	window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
	 	
	 	setContentView(R.layout.l23_level);
		renderer = (RenderView)findViewById(R.id.l23_RenderView);
		renderer.setOrientationListener(this);
		fpsText = (TextView)findViewById(R.id.l23_TextViewFps);
        
        CONTEXT = this; 
        OrientationManager.registerListener(this);
 
        instance = this;
	}
	
	/**
	 * Gets the singleton of LevelActivity.
	 *
	 * @return singleton of LevelActivity
	 */
	public static LevelActivity getInstance()
	{
		return instance;
	}
	
	/**
	 * Called when the activity becomes visible <p>
	 * Followed by onResume() or onStop()
	 */
	@Override
	public void onStart() {
		super.onStart();
		
	}

	/**
	 * Called when the activity is being stopped
	 * Followed by onStart() 
	 */
	@Override
	public void onRestart() {
		super.onRestart();
		
	}
	
	/**
	 * Called when the activity is paused. Should be slim, because it can be called often <p>
	 * Here, animation and other cpu-consuming tasks should be ended <p>
	 * Additionally, persistent storage should be done here, which is implemented using serialization <p>
	 * Followed by onResume() or onStop()
	 */
	@Override
	public void onPause() {
		super.onPause(); 
		renderer.onPause();
		//renderer.persistSceneEntities();
		Settings.MAINCHARPOS = renderer.getMainCharPos(); 
	}
	
	/**
	 * Called when the activity is resumed 
	 * Followed by onPause()
	 */
	@Override
	public void onResume() {
		super.onResume(); 
		renderer.onResume();
		//renderer.restoreSceneEntities();
	}
	
	/**
	 * called when the activity is stopped. 
	 * Followed by onRestart() or onDestroy() 
	 */
	@Override
	public void onStop() {
		super.onStop();
		
	}
	
	/**
	 * Called before the activity is going to be destroyed
	 */
	@Override
	public void onDestroy() {
		
		super.onDestroy();
		if (OrientationManager.isListening()) 
			OrientationManager.unregisterListener(this);
	}
	
	/**
	 * Handles the notification that the orienation has changed (for roll only for us!)
	 */
	@Override
	public void onOrientationChanged(float azimuth, float pitch, float roll) {
		
	}
	
	/**
	 * Called before the activity is moved to the background but will not be called in every case. So save persistent data in onPause()
	 * @param toSave Bundle to store the data to 
	 */
	@Override
	public void onSaveInstanceState(Bundle toSave) {
		
		try {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(38); 
		DataOutputStream dos = new DataOutputStream(bos); 
		renderer.writeToStream(dos);
		dos.close(); 
		bos.close(); 
		byte[] byteArray = bos.toByteArray(); 
		
		ObstacleManager.getInstance().writeToBundle(toSave);
		toSave.putByteArray("l42_persist", byteArray);

		} catch (Exception e) {
			System.out.println("Error writing to stream in LevelActivity.java: "+e.getMessage()); 
		}
		//renderer.persistSceneEntities(toSave); 
	}

	/**
	 * Called to restore the saved instance state
	 * @param toRestore the bundle where the data was stored to 
	 */
	@Override
	public void onRestoreInstanceState(Bundle toRestore) {
		//renderer.restoreSceneEntities(toRestore);
		//Log.v("onRestoreInstanceState", "called!!!");

		try {
		byte[] byteArray = toRestore.getByteArray("l42_persist"); 
		ByteArrayInputStream bis = new ByteArrayInputStream(byteArray); 
		DataInputStream dis = new DataInputStream(bis); 
		renderer.readFromStream(dis); 
		dis.close(); 
		bis.close(); 
		
		ObstacleManager.getInstance().readFromBundle(toRestore); 
		} catch (Exception e) {
			System.out.println("Error restoring from bundle in LevelAcitvity.java: "+e.getMessage()); 
		}
	}
	
	/**
	 * Implements onRollLeft from OrientationListener
	 * Is called when orientation sensor checks that the mobile is rolled to the left 
	 */
	
	public void onRollLeft() {
		renderer.handleRollMovement(MainChar.MOVE_LEFT); 		
	}

	/**
	 * Implements onRollRight from OrientationListener
	 * Is called when orientation sensor checks that the mobile is rolled to the right 
	 */
	public void onRollRight() {
		renderer.handleRollMovement(MainChar.MOVE_RIGHT);		
	}
	
	/**
	 * Implements isInDeadZone from OrientationListener
	 * Is called when the main char is in the dead zone to set the movement to NO_MOVEMENT 
	 */
	public void isInDeadZone() {
		renderer.handleRollMovement(MainChar.NO_MOVEMENT);		
	}
	
	/**
	 * Gets the Android context
	 *
	 * @return the context
	 */
	public static Context getContext() {
		return CONTEXT;
	}
	
	/**
	 * Creates the Options Menu for enabling or disabling the orientation sensor 
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		menu.add(0, SENSOR_MENU_ITEM, 0, "Turn on Sensor");
	    return true;
	}
	
	/**
	 * called when an option from menu is selected
	 * enables or disables the orientation sensor 
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) 
	    {
	    case SENSOR_MENU_ITEM:
	    	if(renderer.isUseSensor())
	    		item.setTitle("Turn on Sensor");
	    	else
	    		item.setTitle("Turn off Sensor");
	        renderer.switchSensor();
	        
	        return true;

	    }
	    return false;
	}
	
	public void fpsChanged(float fps)
	{
		fpsText.setText(Math.round(fps) + " fps");
	}
}