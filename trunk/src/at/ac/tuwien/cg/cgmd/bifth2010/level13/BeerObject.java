package at.ac.tuwien.cg.cgmd.bifth2010.level13;

import javax.microedition.khronos.opengles.GL10;

/**
 * 
 * @author arthur (group 13)
 *
 */
public class BeerObject extends GameObject {

	
	//if object is visible
	private boolean visible;

	/**
	 * constructor calls super() with object's dimensions
	 * @param x x-position (*GameObject.BLOCKSIZE)
	 * @param y y-position (*GameObject.BLOCKSIZE)
	 */
	public BeerObject(int x, int y) {
		super(GameObject.BLOCKSIZE, GameObject.BLOCKSIZE);
		
		//set position
		this.position.x = x * GameObject.BLOCKSIZE;
		this.position.y = y * GameObject.BLOCKSIZE;
		
		//beer is visible until player drinks it
		this.visible = true;
	}

	/**
	 * @see GameObject#draw(GL10)
	 */
	@Override
	public void draw(GL10 gl) {
		//check for player-collision
		if(CollisionHandler.checkBeerCollision((int)this.position.x, (int)this.position.y)) {
			this.visible = false;
		}
		
		//don't draw invisible beer
		if(!visible) {
			return;
		}
		
		//update position with offset
		this.position.sub(GameObject.offset);
		
		super.draw(gl);
		
		//reset position
		this.position.add(GameObject.offset);		
	}
}