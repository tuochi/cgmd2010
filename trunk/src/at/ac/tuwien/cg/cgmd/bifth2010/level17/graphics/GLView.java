package at.ac.tuwien.cg.cgmd.bifth2010.level17.graphics;


import javax.microedition.khronos.opengles.GL;

import android.content.Context;
import android.opengl.GLSurfaceView;
import at.ac.tuwien.cg.cgmd.bifth2010.level17.GameThread;
import at.ac.tuwien.cg.cgmd.bifth2010.level17.World;
import at.ac.tuwien.cg.cgmd.bifth2010.level17.math.MatrixTrackingGL;
import at.ac.tuwien.cg.cgmd.bifth2010.level17.math.Vector2;

public class GLView extends GLSurfaceView {
	
	private JMRenderer mRenderer;//Game's Renderer 
	//private Vector2 mWindowSize;
	private GameThread mGame;
	private World mWorld;
	

    public GLView(Context context, Vector2 size, World world) 
    {
         super(context);                
                         
         mWorld = world;
          
         mRenderer = new JMRenderer(false, world);  
         this.setGLWrapper(new GLSurfaceView.GLWrapper() {
            public GL wrap(GL gl) {
                return new MatrixTrackingGL(gl);
            }});
         
         setRenderer(mRenderer);
         

         
         //mWindowSize = size;
         
         mGame = new GameThread(this, world);
         mGame.start();
   
    } 
    
    public void fingerMove(float x, float y)
    {
    	Vector2 newTouchPos = new Vector2(x, y);
    	fingerMove(newTouchPos);
    }
    
    public void fingerMove(Vector2 pos)
    {
    	mWorld.fingerMove(pos);
    }
    
    public void fingerDown(float x, float y)
    {
    	Vector2 newTouchPos = new Vector2(x, y);
    	fingerDown(newTouchPos);
    }
    
    public void fingerDown(Vector2 pos)
    {
    	mWorld.fingerDown(pos);
    }   
    
    public void fingerUp(float x, float y)
    {
    	Vector2 newTouchPos = new Vector2(x, y);
    	fingerUp(newTouchPos);
    }

    
    public void fingerUp(Vector2 pos)
    
    {
    	mWorld.fingerUp(pos);
    }

	@Override
	public void onPause() {
		if(mWorld != null)
			mWorld.setPause(true);
		super.onPause();
	}

	@Override
	public void onResume() {
		//if(mWorld != null)
			//mWorld.setPause(false);
		super.onResume();
	}
	
	public void onStart() {
		if(mGame != null && !mGame.isAlive() && mWorld != null)
			mGame.start();
	}


	public void onStop() {
		if(mGame != null && mGame.isAlive())
			mGame.stop();
	}
    
}