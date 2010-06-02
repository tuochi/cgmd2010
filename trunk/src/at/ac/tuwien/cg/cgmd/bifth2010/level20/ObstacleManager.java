/**
 * 
 */
package at.ac.tuwien.cg.cgmd.bifth2010.level20;

import javax.microedition.khronos.opengles.GL10;

import at.ac.tuwien.cg.cgmd.bifth2010.R;

/**
 * Manages spawning and handling of obstacles in the game.
 * 
 * @author Ferdinand Pilz
 * @author Reinhard Sprung
 *
 */
public class ObstacleManager {
	/** The obstacle entity to spawn. */
	private RenderEntity obstacle;
	/** The x-Position the obstacles are spawned. */
	private float spawnPos;
	/** The number of spawned obstacles up to now. */
	private int nSpawnedObstacles;
	/** The number of successfully avoided obstacles. */
	private int nAvoidedObstacles;
	/** The minimum time span in milliseconds between obstacle spawning. */
	private float timeInterval;
	/** The next time an obstacle is spawned. */
	private float nextSpawnTime;
	/** Accumulates the time since the last obstacle vanished. */
	private float updateTime;
	/** The position where the player crashes into the obstacle. */
	protected float crashPosition;

	/** Constructor of ObstacleManager. */
	public ObstacleManager() {
		// TODO Adjust scaling and positioning.
		obstacle = new RenderEntity(500, 40, 1, 110, 110);		
		nSpawnedObstacles = 0;
		nAvoidedObstacles = 0;
		timeInterval = 10000;
		nextSpawnTime = timeInterval;
		updateTime = 0;		
		crashPosition = 100;
	}
	
	/** Initializes the manager. 
	 * @param gl */
	public void init(GL10 gl) {
		spawnPos = LevelActivity.renderView.getWidth() + 100;
		obstacle.x = spawnPos;
		createObstacle(gl);
	}
	
	/** 
	 * Does the necessary updating of the ObstacleManager.  
	 * @param dt 	 The passed time since the last update in milliseconds.
	 * @param scroll The distance to move the obstacle along the x axis.
	 * */
	public void update(float dt, float scroll) {
		if (!obstacle.visible) {
			updateTime += dt;
		}			
		
		if (updateTime >= nextSpawnTime) {
			spawnObstacle();	
			nSpawnedObstacles++;
		}			
		
		// Obstacle spawned.
		if (obstacle.visible) {
			// Scroll.
			obstacle.x -= scroll;
			// Crashing with player.
			if (obstacle.x < crashPosition) {
				EventManager.getInstance().dispatchEvent(EventManager.OBSTACLE_CRASH, obstacle);
			}
		}
		
		// Obstacle vanished.
		if (obstacle.x < 0) {
			removeObstacle();
		}
	}

	
	public void touchEvent(float x, float y) {
		if (obstacle.hitTest(x, y)) {
			nAvoidedObstacles++;
			removeObstacle();
		}
	
	}
	
	public void removeObstacle() {
		obstacle.visible = false;
		obstacle.x = spawnPos;
		updateTime = 0;
	}

	private void spawnObstacle() {
		obstacle.visible = true;		
	}

	public void createObstacle(GL10 gl) {
		obstacle.visible = false;
		obstacle.texture = LevelActivity.renderView.getTexture(R.drawable.l20_obstacle, gl);		
	}
	
	public void render(GL10 gl) {
		obstacle.render(gl);	
	}
}
