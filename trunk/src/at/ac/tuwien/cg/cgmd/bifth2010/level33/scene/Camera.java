package at.ac.tuwien.cg.cgmd.bifth2010.level33.scene;

import static android.opengl.GLES10.GL_MODELVIEW;
import static android.opengl.GLES10.glLoadIdentity;
import static android.opengl.GLES10.glMatrixMode;
import static android.opengl.GLU.gluLookAt;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLU;
import android.util.Log;
import at.ac.tuwien.cg.cgmd.bifth2010.level33.GameView;
import at.ac.tuwien.cg.cgmd.bifth2010.level33.math.Vector3f;
/**
 * The Class Camera
 * @author roman hochstoger & christoph fuchs
 */
public class Camera {
	
	public final static float standardZoom= 10.f;// standart Game zoom
	public final static float outZoom = 50.f;// overview Zoom
	public static float zoom = standardZoom;
	private boolean somethingChanged = true;
	
	Vector3f eye = new Vector3f(0f,zoom, 0.0000000001f);
	Vector3f view = new Vector3f(0, 0, 0);
	Vector3f up = new Vector3f(0, 1, 0);
	

    public void init(GL10 gl, int width, int height){
    	
		 float aspectRatio = (float)width / height;
	        GLU.gluPerspective( gl, 45, aspectRatio, 0.001f, 100 );
    	
    }
    
    /**
     * look at specific point
     * @param gl
     */
	public void lookAt(GL10 gl) {
		 Log.d("lookAt","0");
		// if nothing has changed -> do nothing
		if(!somethingChanged)
			return;
		
		 Log.d("lookAt","1");
		
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();

        Log.d("lookAt","2");

        eye.y=zoom;
        
//        if(zoom==standardZoom)
//        	view.set(0, 0, 0);
//        else
        	view.set(0,0,0);
        	
      //  DebugView
      //  	eye.set(((GameView.lastTouch.x*2)-1)*10,((GameView.lastTouch.y*2)-1)*10,((GameView.lastTouch.x*2)-1)*10);
        
        	 Log.d("lookAt","3");
      	gluLookAt(gl,eye.x, eye.y, eye.z, view.x, view.y, view.z , up.x, up.y, up.z  );// momentan nur zum probieren, danach von oben

      	// set to standard -> hasNotChanged == true
      	somethingChanged=false;
	}

	/**
	 * switch between play zoom and overview zoom
	 */
	public void switchZoom() {
		somethingChanged=true;
	
		if(zoom==standardZoom)
			zoom=outZoom;
		else
			zoom=standardZoom;
		
	}


}
