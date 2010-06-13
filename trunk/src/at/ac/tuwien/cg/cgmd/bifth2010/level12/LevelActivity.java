package at.ac.tuwien.cg.cgmd.bifth2010.level12;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.pm.ActivityInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import at.ac.tuwien.cg.cgmd.bifth2010.R;
import at.ac.tuwien.cg.cgmd.bifth2010.framework.SessionState;

public class LevelActivity extends Activity{
	private Display mDisplay = null;
	private GLRenderer mRenderer = null;
	private boolean mTutShowed = false;
	private int mKilledEnemies = 0;
	private int mSpawnedEnemies = 0;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	//Debug.startMethodTracing("Tracefile");
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);      
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        if( mDisplay == null) mDisplay = wm.getDefaultDisplay(); 
    }
    
    @Override
    public void onStart(){   
        super.onStart();
        if( mTutShowed == false ) {
        	GameMechanics.getSingleton().pause();
        	AlertDialog tutorial = new StartDialog(this);
        	tutorial.show();
        	 mTutShowed = true;	
        }     
    }
    
    @Override
    public boolean onTouchEvent (MotionEvent event)
    {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			GameWorld.getSingleton().setXYpos((int)event.getX(), (int)(mDisplay.getHeight() - event.getY()));
			//Log.d(LOG_TAG, "width: " + d.getWidth() + " height: " + d.getHeight() + " standard y: " + event.getY());
            //Log.d(LOG_TAG, "screen --> x: " + (event.getX()) + "y: " + (d.getHeight() - event.getY()));
        }
    	return super.onTouchEvent(event);
    }
    
    
    
    @Override
    protected void onResume() {
    	SoundHandler.setContext(this);
    	
		Intent callingIntent = getIntent();
		SessionState state = new SessionState(callingIntent.getExtras());
		if(state!=null){
			boolean isMusicAndSoundOn = state.isMusicAndSoundOn(); 
			SoundHandler.getSingleton().setSound(isMusicAndSoundOn);
			System.out.println("SETTING SOUND TO: "+isMusicAndSoundOn);
		}
    	
    	//SoundHandler.getSingleton().addResource(R.raw.l12_music);
    	TextureManager.getSingletonObject().initializeContext(this);
    	TextureManager.getSingletonObject().add(R.drawable.l12_icon);			
 
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE );
    	int fieldheight = (int)( mDisplay.getHeight() * 0.9 ) ;
    	int menuheight = mDisplay.getHeight() - fieldheight;
    	GameWorld.setDisplay( fieldheight, mDisplay.getWidth());
    	GameWorld.getSingleton().initVBOs();
    	SoundHandler.getSingleton().reloadSamples();
	
 	   	GLSurfaceView glview = new GLSurfaceView(this);
 	   	if( mRenderer == null ) mRenderer = new GLRenderer();
 	   	glview.setRenderer(mRenderer);
    	GameMechanics.getSingleton().setGameContext(this);
    	    
        LinearLayout l = new LinearLayout( this );
        l.setLayoutParams( new LayoutParams( LayoutParams.FILL_PARENT,   LayoutParams.FILL_PARENT));
        l.setOrientation(LinearLayout.VERTICAL);
        GameUI.createSingleton( this, menuheight, mDisplay.getWidth() );
        l.addView( GameUI.getSingleton() );
        l.addView( glview );
        
        setContentView( l );
    	//SoundHandler.getSingleton().playLoop(R.raw.l12_music);
        super.onResume();
    }
    
    
    @Override
    protected void onPause() {
    	GameMechanics.getSingleton().pause();
    	SoundHandler.getSingleton().stop();
        super.onPause();
    }
    
    @Override
	protected void onStop() {
		//we finish this activity;
		super.onStop();
    	//Debug.stopMethodTracing();
    }
    
	@Override
	public void finish() {	
		//the SessionState is a convenience class to set a result
		SessionState s = new SessionState();
		//we set the progress the user has made (must be between 0-100)
		int gainedMoney = GameMechanics.getSingleton().getMoney();
		int burnedMoney = GameMechanics.getSingleton().getBurnedMoney();
		float totalMoneyPercent = (gainedMoney + burnedMoney)*0.01f;
		if(totalMoneyPercent == 0.0f) s.setProgress(0);
		else s.setProgress((int)(burnedMoney/totalMoneyPercent));
		
		//System.out.println("burned: " + burnedMoney + " |gained: " + gainedMoney + "score: " + (int)(burnedMoney/totalMoneyPercent));
		//we call the activity's setResult method 
		setResult(Activity.RESULT_OK, s.asIntent());
		GameWorld.destroySingleton();
    	GameMechanics.destroySingleton();
    	SoundHandler.getSingleton().stop();
		super.finish();
		mDisplay = null;
	}
	
	@Override
	public void onDestroy(){
	   	super.onDestroy();
	   	mDisplay = null;
	}
	
	
	public void showFinishDialog(){
			GameMechanics.getSingleton().pause();
			this.onPrepareDialog(10, new FinishDialog(this) );
			this.onCreateDialog(10);
			//FinishDialog f = new FinishDialog(this);
			//f.show();
	}
	
	
	private class StartDialog extends AlertDialog implements android.view.View.OnClickListener{

		protected StartDialog(Context context) {
			super(context);
			this.setTitle(R.string.l12_tutorialtitle);
			this.setIcon(R.drawable.l12_icon);
			
			
			ScrollView sc = new ScrollView(context);
			LinearLayout l = new LinearLayout(context);
			l.setOrientation(LinearLayout.VERTICAL);
			
			TextView t = new TextView(context);
			t.setText(R.string.l12_intro);
			l.addView(t);
			
			LinearLayout li = new LinearLayout(context);
			li.setOrientation(LinearLayout.HORIZONTAL);
			ImageView i1 = new ImageView(context);
			i1.setImageResource(R.drawable.l12_bunny1_icon);
			li.addView(i1);
			TextView t1 = new TextView(context);
			t1.setText(R.string.l12_basic_tower);
			li.addView(t1);
			l.addView(li);
			
			LinearLayout lii = new LinearLayout(context);
			lii.setOrientation(LinearLayout.HORIZONTAL);
			ImageView i2 = new ImageView(context);
			i2.setImageResource(R.drawable.l12_bunny3_icon);
			lii.addView(i2);
			TextView t2 = new TextView(context);
			t2.setText(R.string.l12_advanced_tower);
			lii.addView(t2);
			l.addView(lii);
			
			LinearLayout liii = new LinearLayout(context);
			liii.setOrientation(LinearLayout.HORIZONTAL);
			ImageView i3 = new ImageView(context);
			i3.setImageResource(R.drawable.l12_bunny2_icon);
			liii.addView(i3);
			TextView t3 = new TextView(context);
			t3.setText(R.string.l12_hyper_tower);
			liii.addView(t3);
			l.addView(liii);
			
			LinearLayout liv = new LinearLayout(context);
			liv.setOrientation(LinearLayout.HORIZONTAL);
			ImageView i4 = new ImageView(context);
			i4.setImageResource(R.drawable.l12_bunny4_icon);
			liv.addView(i4);
			TextView t4 = new TextView(context);
			t4.setText(R.string.l12_freeze_tower);
			liv.addView(t4);
			l.addView(liv);
			
			Button btn = new Button(context);
			btn.setText("Start");
			btn.setId(1);
			btn.setOnClickListener(this);
			l.addView(btn);
				
			sc.addView(l);	
			this.setView(sc);
		}

		@Override
		public void onClick(View v) {
			GameMechanics.getSingleton().unpause();
			this.dismiss();	
		}
	}
	
	private class FinishDialog extends AlertDialog implements android.view.View.OnClickListener{

		protected FinishDialog(Context context) {
			super(context);
			this.setTitle(R.string.l12_finish);
			this.setIcon(R.drawable.l12_icon);
			
			LinearLayout l = new LinearLayout(context);
			l.setOrientation(LinearLayout.VERTICAL);
			
			TextView t = new TextView(context);
			t.setText(R.string.l12_enemies_fended);
			l.addView(t);
			TextView t1 = new TextView(context);
			t1.setText(mKilledEnemies + " / "+mSpawnedEnemies+"\n\n" );
			
			/*TextView t2 = new TextView(context);
			t.setText(R.string.l12_points);
			l.addView(t2);
			TextView t3 = new TextView(context);
			t3.setText(GameMechanics.getSingleton().getBurnedMoney() + " / 100\n\n");*/
			
			
			Button btn = new Button(context);
			btn.setText("End");
			btn.setId(1);
			btn.setOnClickListener(this);
			l.addView(btn);
					
			this.setView(l);
		}

		@Override
		public void onClick(View v) {
			this.dismiss();	
		}

		
	}
		  
}
