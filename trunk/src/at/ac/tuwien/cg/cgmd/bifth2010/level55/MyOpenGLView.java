package at.ac.tuwien.cg.cgmd.bifth2010.level55;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;


public class MyOpenGLView extends GLSurfaceView {
	
    public MyOpenGLView(Context context) {
        super(context);
        this.setDebugFlags(DEBUG_CHECK_GL_ERROR);
        Texture.setContext(context);
        Sound.setContext(context);
        MyRenderer.setContext(context);
        

		this.requestFocus();
		this.setFocusableInTouchMode(true);
    }

    public MyOpenGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    static boolean isExtensionSupported(GL10 gl, String extension) {
    	String extensions;
    	if (gl instanceof GL11) {
    		GL11 gl11=(GL11)gl;
    		extensions=gl11.glGetString(GL11.GL_EXTENSIONS);
    	} else {
    		extensions=gl.glGetString(GL10.GL_EXTENSIONS);
    	}

    	if (extensions.contains(extensions)) return true;
    	
    	
		return false;
    }
}
