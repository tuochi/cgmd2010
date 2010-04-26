package at.ac.tuwien.cg.cgmd.bifth2010.level50;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import at.ac.tuwien.cg.cgmd.bifth2010.R;

public class LevelObject {
	

	int tex[]=new int[1];
	private IntBuffer texBuf=IntBuffer.wrap(tex);
	private static HashMap<Integer,Integer> textures = new HashMap<Integer, Integer>();
	Context context;
	private LevelCollision level;
	boolean collision, gravity;
	
	float x, y;
	int width, height;
	int id;
	float movement[] = new float[4];
	int direction = 1;
	int textureID = -1;
	GL10 gl;
	
	
	// Our vertices.
	private float vertices[] = {
		       0.0f, 0.0f, 0.0f,  // 0, Top Left
		       0.0f, 1.0f, 0.0f,  // 1, Bottom Left
		       1.0f, 1.0f, 0.0f,  // 2, Bottom Right
		       1.0f, 0.0f, 0.0f   // 3, Top Right
		};
	
	private float texcoord[] = {
		       0.0f,  1.0f,
		       0.0f,  0.0f,
		       1.0f,  0.0f,
		       1.0f,  1.0f
		};

	// The order we like to connect them.
	private short[] indices = { 0, 1, 2, 0, 2, 3 };

	// Our vertex buffer.
	private FloatBuffer vertexBuffer, texcoordBuffer;

	// Our index buffer.
	private ShortBuffer indexBuffer;

	public LevelObject(GL10 gl, Context context, LevelCollision level, float x, float y, int width, int height, int id) {
		this.context = context;
		this.level = level;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.id = id;
		this.gl = gl;
		
		movement[0]=0.0f;
		movement[1]=0.0f;
		movement[2]=0.0f;
		movement[3]=0.0f;
		
		// a float is 4 bytes, therefore we multiply the number if
		// vertices with 4.
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		vertexBuffer = vbb.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);

		// short is 2 bytes, therefore we multiply the number if
		// vertices with 2.
		ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		indexBuffer = ibb.asShortBuffer();
		indexBuffer.put(indices);
		indexBuffer.position(0);
		
		ByteBuffer tbb = ByteBuffer.allocateDirect(texcoord.length * 4);
		tbb.order(ByteOrder.nativeOrder());
		texcoordBuffer = tbb.asFloatBuffer();
		texcoordBuffer.put(texcoord);
		texcoordBuffer.position(0);
		
		if (textures.containsKey(id)) {
			texBuf.put(textures.get(id));
		} else {
			gl.glGenTextures(1, texBuf);
			textures.put(id, texBuf.get(0));
			LoadTexture(0,id,gl,context);
		}
	}

	/**
	 * This function draws our square on screen.
	 * @param gl
	 */
	public void draw(GL10 gl) {
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glEnable(GL10.GL_TEXTURE_2D);

		gl.glBindTexture(GL10.GL_TEXTURE_2D, texBuf.get(0));      
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT,0, texcoordBuffer);
        
        gl.glPushMatrix();
		gl.glLoadIdentity();
		float xn = x-movement[2]+movement[3];
		float yn = y-movement[0]+movement[1];
		if (collision)
			if (!testCollision(xn, yn)){
				x = xn; y = yn;
			} else if (!testCollision(xn, y)){
				x = xn;
			} else if (!testCollision(x, yn)){
				y = yn;
			}
		
		if (gravity && movement[0]>0.0f) movement[0]-=1.0f;
		
		if (movement[3]-movement[2] < 0) direction = -1;
		else if (movement[3]-movement[2] > 0) direction = 1;
		
		gl.glTranslatef(x + (1-direction)/2*width, y, 0);
		gl.glScalef((float)width*direction, (float)height, 1.0f);

		gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_SHORT, indexBuffer);
		
		gl.glPopMatrix();

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisable(GL10.GL_TEXTURE_2D);

		gl.glDisable(GL10.GL_CULL_FACE);
	}
	
	public void move(float x, float y) {
		if (!testCollision(this.x+x,this.y+y)) {
			this.x += x;
			this.y += y;
		}
	}
	
	public void move(int direction, float amount) {
		if (direction == 0) amount*=3;
		movement[direction] = amount;
	}
	
	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public float getPositionX() {return x;}
	public float getPositionY() {return y;}
	
	boolean testCollision(float x, float y) {
		if ((level.TestCollision((int) Math.floor((x)/20), (int) Math.floor((y)/20))&0x00ffffff) != 0 &&
				(level.TestCollision((int) Math.floor((x+width)/20), (int) Math.floor((y)/20))&0x00ffffff) != 0 &&
				(level.TestCollision((int) Math.floor((x+width)/20), (int) Math.floor((y+height)/20))&0x00ffffff) != 0 &&
				(level.TestCollision((int) Math.floor((x)/20), (int) Math.floor((y+height)/20))&0x00ffffff) != 0) {
			return false;
		} else return true;
	}
	
	public void changeTexture(int id) {
		texBuf.clear();
		if (textures.containsKey(id)) {
			texBuf.put(textures.get(id));
		} else {
			gl.glGenTextures(1, texBuf);
			textures.put(id, texBuf.get(0));
			LoadTexture(0,id,gl,context);
		}		
	}
	
	public static void clearTextures(GL10 gl) {
		IntBuffer texInts = IntBuffer.allocate(0);
		for (Integer tex : textures.values()) {
			texInts.put(tex);
		}
		texInts.position(0);
		gl.glDeleteTextures(texInts.capacity(),texInts);
	}
	
	private void LoadTexture(int num, int id, GL10 gl, Context context)
	{
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texBuf.get(num));				
		Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), id);
		
		if (width == 0) width = bmp.getWidth();
		if (height == 0) height = bmp.getHeight();
		int w = bmp.getWidth();
		int h = bmp.getHeight();

		int pixels[]=new int[w*h];
		bmp.getPixels(pixels, 0, w, 0, 0, w, h);
		int pix1[]=new int[w*h];
		for(int i=0; i<h; i++)
        {
             for(int j=0; j<w; j++)
             {
                  //correction of R and B
                  int pix=pixels[i*w+j];
                  int pb=(pix>>16)&0xff;
                  int pr=(pix<<16)&0x00ff0000;
                  int px1=(pix&0xff00ff00) | pr | pb;
                  //correction of rows
                  pix1[(h-i-1)*w+j]=px1;
             }
        }     
		
		IntBuffer tbuf=IntBuffer.wrap(pix1);
//		ib.position(0);		
		gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, w, h, 0, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, tbuf);		
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);//LINEAR );
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);//LINEAR );											
	}
	
	public void enableGravity(boolean b) {
		gravity = b;
		if (b) movement[1] = 5.0f;
		else movement[1] = 0.0f;
	}
	
	public void enableCollision(boolean b) {
		collision = b;
	}
}