package at.ac.tuwien.cg.cgmd.bifth2010.level13.gameobjects;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;
import at.ac.tuwien.cg.cgmd.bifth2010.level13.FPSCounter;
import at.ac.tuwien.cg.cgmd.bifth2010.level13.GameControl;
import at.ac.tuwien.cg.cgmd.bifth2010.level13.MyRenderer;
import at.ac.tuwien.cg.cgmd.bifth2010.level13.Texture;
import at.ac.tuwien.cg.cgmd.bifth2010.level13.Vector2;

/**
 * 
 * @author arthur/sebastian (group 13)
 *
 */
public abstract class GameObject {
	//smallest size of an object (= player size, = tile size)
	public static final int BLOCKSIZE = 32;
	
	//offset of background and beer due to movement
	public static Vector2 offset = new Vector2(0, 0);
	
	//position of object ((0,0) is bottom left)
	public Vector2 position;
	public static float drunkenRotation = 0;
	//vertex coordinates
	protected FloatBuffer vertexBuffer;
	protected float[] vertices;
	protected ShortBuffer indexBuffer;
	
	public Vector2 moveVec;
	//texture of object (=singleton)
	protected Texture texture;
	
	protected GameControl gameControl;
	
	
	public boolean isActive = true;
	
	public static void reset() {
		GameObject.offset = new Vector2(0, 0);
	}
	
	public static void setStartTile(Vector2 tile) {
		//set offset
		float centerX = ((MyRenderer.screenWidth / GameObject.BLOCKSIZE) / 2) * GameObject.BLOCKSIZE;
		float centerY = ((MyRenderer.screenHeight / GameObject.BLOCKSIZE) / 2) * GameObject.BLOCKSIZE;
		//move starting tile to center
		float startingTileX = tile.x * GameObject.BLOCKSIZE;
		float startingTileY = tile.y * GameObject.BLOCKSIZE;
		float offsetX = centerX - startingTileX;
		float offsetY = centerY - startingTileY;
		GameObject.offset = new Vector2(-offsetX, -offsetY);
	}
	/**
	 * constructor sets up object (used by subtypes)
	 * @param objectWidth
	 * @param objectHeight
	 */
	public GameObject(float objectWidth, float objectHeight) {
		this.gameControl = GameControl.getInstance();
		//set position
		this.position = new Vector2(0, 0);
		
		//define vertices coordinates
		vertices = new float[12];
		//bottem left
		vertices[0] = 0.0f;
		vertices[1] = 0.0f;
		vertices[2] = 0f;
		//bottom right
		vertices[3] = objectWidth;
		vertices[4] = 0.0f;
		vertices[5] = 0f;
		//top right
		vertices[6] = objectWidth;
		vertices[7] = objectHeight;
		vertices[8] = 0f;
		//top left
		vertices[9] = 0.0f;
		vertices[10] = objectHeight;
		vertices[11] = 0f;
		
		//set up vertex-buffer
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertices.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		vertexBuffer = byteBuf.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);
		
		//set up index buffer
		short[] indices = { 0, 1, 2, 0, 2, 3 };
		ByteBuffer indexBBuffer = ByteBuffer.allocateDirect(indices.length * 2);
		indexBBuffer.order(ByteOrder.nativeOrder());
		indexBuffer = indexBBuffer.asShortBuffer();
		indexBuffer.put(indices);
		indexBuffer.position(0);
		
		//set texture
		this.texture = TextureSingletons.getTexture(this.getClass().getSimpleName());
	}
	
	/**
	 * draws the object onto the screen
	 * @param gl
	 */
	public void draw(GL10 gl){
		
		//Reset modelview matrix
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		//enable client state
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		
		//bind texture
		gl.glBindTexture(GL10.GL_TEXTURE_2D, this.texture.textures[0]);
		
		//define texture coordinates
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, this.texture.textureBuffer);
		
		//point to vertex buffer
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		
		//translate to correct position
		
		gl.glTranslatef(this.position.x, this.position.y, 0.0f);

	
		//draw
		gl.glDrawElements(GL10.GL_TRIANGLES, 6, GL10.GL_UNSIGNED_SHORT, indexBuffer);
		
		//disable client state
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	};

	/**
	 * updates offset with movement
	 * @param movement
	 */
	public static void updateOffset(Vector2 movement) {
		Vector2 temp = movement.clone();
		temp.x *= (FPSCounter.getInstance().getDt() / 1000f);
		temp.y *= (FPSCounter.getInstance().getDt() / 1000f);
		
		//-offset = center - tile -> tile = center + offset
		float tileXBefore = (((MyRenderer.screenWidth / GameObject.BLOCKSIZE) / 2) * GameObject.BLOCKSIZE + offset.x) / GameObject.BLOCKSIZE;
		float tileYBefore = (((MyRenderer.screenHeight / GameObject.BLOCKSIZE) / 2) * GameObject.BLOCKSIZE + offset.y) / GameObject.BLOCKSIZE;
		
		offset.add(temp);
		
		float tileXAfter = (((MyRenderer.screenWidth / GameObject.BLOCKSIZE) / 2) * GameObject.BLOCKSIZE + offset.x) / GameObject.BLOCKSIZE;
		float tileYAfter = (((MyRenderer.screenHeight / GameObject.BLOCKSIZE) / 2) * GameObject.BLOCKSIZE + offset.y) / GameObject.BLOCKSIZE;
		
		Log.d("df", "beforex: " + tileXBefore + " afterx:" + tileXAfter);
		Log.d("df", "beforey: " + tileYBefore + " aftery:" + tileYAfter);
		
		
		if(((int)tileXBefore) != ((int)tileXAfter) && (int)tileXBefore != tileXBefore && (int)tileXAfter != tileXAfter) {
			offset.x = (-1)* (((MyRenderer.screenWidth / GameObject.BLOCKSIZE / 2)) * GameObject.BLOCKSIZE - ((int)Math.max((int)tileXBefore, (int)tileXAfter))*GameObject.BLOCKSIZE);
		}
		if(((int)tileYBefore) != ((int)tileYAfter) && (int)tileYBefore != tileYBefore && (int)tileYAfter != tileYAfter) {
			offset.y = (-1)* (((MyRenderer.screenHeight / GameObject.BLOCKSIZE / 2)) * GameObject.BLOCKSIZE - ((int)Math.max((int)tileYBefore, (int)tileYAfter))*GameObject.BLOCKSIZE);
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
	}
	
	/**
	 * Execute whatever the sub class should do here
	 */
	
	
	public void update(){
	}
	
	
}