package at.ac.tuwien.cg.cgmd.bifth2010.level44;

import java.util.LinkedList;
import java.util.Queue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Vibrator;
import at.ac.tuwien.cg.cgmd.bifth2010.R;
import at.ac.tuwien.cg.cgmd.bifth2010.level44.io.InputGesture;
import at.ac.tuwien.cg.cgmd.bifth2010.level44.physics.Crosshairs;
import at.ac.tuwien.cg.cgmd.bifth2010.level44.physics.PhysicalRabbit;
import at.ac.tuwien.cg.cgmd.bifth2010.level44.sound.SoundPlayer;
import at.ac.tuwien.cg.cgmd.bifth2010.level44.twodee.CoinBucketSprite;
import at.ac.tuwien.cg.cgmd.bifth2010.level44.twodee.Landscape;
import at.ac.tuwien.cg.cgmd.bifth2010.level44.twodee.RabbitSprite;
import at.ac.tuwien.cg.cgmd.bifth2010.level44.twodee.Sprite;
import at.ac.tuwien.cg.cgmd.bifth2010.level44.twodee.Texture;
import at.ac.tuwien.cg.cgmd.bifth2010.level44.twodee.TextureParts;
import at.ac.tuwien.cg.cgmd.bifth2010.level44.twodee.TimeDisplay;


public class GameScene extends GLSurfaceView implements Renderer {
	/** the context of the scene */
	private LevelActivity activity = null;
	/** the flying rabbit */
	private PhysicalRabbit rabbit;
	/** the crosshairs that shoot on the rabbit */
	private Crosshairs crosshairs;
	/** the landscape moving with parallax-effect */
	private Landscape landscape;
	/** a coin sprite for drawing the OSD */
	private Sprite coin;
	/** the time display sprite */
	private Sprite timeDisplay;

	/** thread for game logic */
	private GameThread gameThread;
	
	private TimeManager timeManager;

	/** queue of all inputGestures to process */
	private Queue<InputGesture> inputQueue = new LinkedList<InputGesture>();
	/** soundPlayer */
	private SoundPlayer player = null;
	/** the system's vibrator */
	private Vibrator vibrator = null;

	
	public GameScene(LevelActivity context) {
		super(context);
		
		this.activity = context;
		
		player = SoundPlayer.getInstance(context);
		// get the system's vibrator
		vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
		
		setRenderer(this);
		setRenderMode(RENDERMODE_CONTINUOUSLY);
		System.err.println("GameScene created");
		rabbit = null;
		landscape = null;
		coin = null;
		timeDisplay = null;
		gameThread = null;
		timeManager = new TimeManager();
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		if (landscape != null) {
			landscape.draw(gl);
		}
		
		if (crosshairs != null) {
			crosshairs.draw(gl);
		}
		
		if (rabbit != null && coin != null) {
			int coins = rabbit.getCoinCount();
			
			for (int i=0; i<coins; i++) {
				coin.setPosition(20+i*coin.getWidth()/2, 20);
				coin.draw(gl);				
			}
		}
		
		if (timeDisplay != null) {
			timeDisplay.draw(gl);
		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(0, width, height, 0, 0, 1);
		gl.glClearColorx((int)(255*.5), (int)(255*.7), 255, 255);
	}
	
	private void configureTexture(GL10 gl, Texture texture) {
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture.getTextureName());
		gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		Texture mainTexture = new Texture(gl, getContext(), R.drawable.l44_texture);
		configureTexture(gl, mainTexture);
		
		rabbit = new PhysicalRabbit(new RabbitSprite(mainTexture), new Sprite(TextureParts.makeCoin(mainTexture)), this.getWidth(), this.getHeight());
		rabbit.setPosition(getWidth()/2, getHeight()/2);
		
		crosshairs = new Crosshairs(this, mainTexture, this.getWidth(), this.getHeight());
		crosshairs.setPosition(30, getHeight()/2);
		crosshairs.setRabbit(rabbit);
		
		landscape = new Landscape(mainTexture, getWidth(), getHeight());
		landscape.setRabbit((PhysicalRabbit)rabbit);
		
		coin = new Sprite(TextureParts.makeCoin(mainTexture));
		
		timeDisplay = new TimeDisplay(mainTexture, timeManager);
		timeDisplay.setPosition(getWidth()-timeDisplay.getWidth()-10, 10);
		
		restartGameThread();
	}
	
	private void restartGameThread() {
		stopGameThread();
		
		if (rabbit != null && landscape != null && crosshairs != null && timeManager != null) {
			gameThread = new GameThread(this, rabbit, landscape, crosshairs, timeManager);
			gameThread.start();
		}
	}
	
	public void stopGameThread() {
		if (gameThread != null) {
			gameThread.doQuit();
			gameThread = null;
		}	
		
		if (timeManager != null) {
			timeManager.onPause();
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		stopGameThread();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		restartGameThread();
	}

	public void clearInputQueue() {
		inputQueue.clear();
	}
	
	public void addInputGesture(InputGesture gesture) {
		//if (inputQueue.size() < 1)
		inputQueue.clear();
		inputQueue.add(gesture);
	}
	
	public InputGesture getNextInputGesture() {
		return inputQueue.poll();
	}
	
	public SoundPlayer getSoundPlayer() {
		return player;
	}
	
	public Vibrator getVibrator() {
		return vibrator;
	}
	
	public PhysicalRabbit getRabbit() {
		return rabbit;
	}

	public int getScore() {
		return (CoinBucketSprite.FULL_COIN_COUNT - rabbit.getCoinCount())*10;
	}
	
	public void finishLevel() {
		activity.finishLevel(getScore());
	}
}
