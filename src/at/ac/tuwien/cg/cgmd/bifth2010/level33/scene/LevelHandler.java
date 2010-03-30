package at.ac.tuwien.cg.cgmd.bifth2010.level33.scene;

import java.util.Random;

import android.provider.UserDictionary.Words;
import at.ac.tuwien.cg.cgmd.bifth2010.level33.math.Vector2f;
import at.ac.tuwien.cg.cgmd.bifth2010.level33.math.Vector2i;

public class LevelHandler {

	int world[];
	Vector2i worldDim;
	Vector2f gameCharacterPosition; // actual Position
	Vector2f gameCharacterTargetPosition; // target Position
	boolean characterMoves = false; // if Character is moving in this moment
	float gameCharacterSpeed = 30;

	byte way;
	byte wall;
	Random rand = new Random();

	public LevelHandler() {
		generateLevel();
		gameCharacterTargetPosition = new Vector2f(gameCharacterPosition);
	}

	/**
	 * this method generate the Level
	 */
	private void generateLevel() {

		
		// HARDCODED WORLD
		
		LevelGenration levelGenration = new LevelGenration(24);
		
		worldDim = new Vector2i(levelGenration.rows,levelGenration.columns);
//		world = new int[worldDim.area()];
//		int x = 0;
//		world[x++] = 0;		world[x++] = 0;		world[x++] = 0;		world[x++] = 0;		world[x++] = 0;
//		world[x++] = 0;		world[x++] = 1;		world[x++] = 1;		world[x++] = 1;		world[x++] = 0;
//		world[x++] = 0;		world[x++] = 1;		world[x++] = 0;		world[x++] = 0;		world[x++] = 0;
//		world[x++] = 0;		world[x++] = 1;		world[x++] = 1;		world[x++] = 1;		world[x++] = 0;
//		world[x++] = 0;		world[x++] = 0;		world[x++] = 0;		world[x++] = 1;		world[x++] = 0;
//		world[x++] = 0;		world[x++] = 1;		world[x++] = 1;		world[x++] = 1;		world[x++] = 0;
//		world[x++] = 0;		world[x++] = 0;		world[x++] = 0;		world[x++] = 0;		world[x++] = 0;
//		gameCharacterPosition = new Vector2f(1, 1);
//		// HARDCODED WORLD END
		
		
		

	//LevelGenration levelGenration = new LevelGenration(10, 3, 0.4,2 ,6 , 4,3 ,3 );
	
//		worldDim = new Vector2i(5, 5);
	//int[] theworld = new int[worldDim.area()];
	world = levelGenration.startCreation();// TODO BUG
//		
//		
	gameCharacterPosition = new Vector2f(levelGenration.getStartPosition().x,levelGenration.getStartPosition().y);
		

		
		
		
		// setup surface
		way = SceneGraph.GEOMETRY_WAY;

		// setup wall
		wall = SceneGraph.GEOMETRY_WALL;
			
		
	}

	public void updateLogic() {

		
//		// START DEMO: random walk
//		if (gameCharacterPosition.equals(gameCharacterTargetPosition)){
//			
//			boolean ok=false;
//			while(!ok)
//			{
//				Vector2i to = new Vector2i(rand.nextInt(worldDim.x),rand.nextInt(worldDim.y));
//				if(isDirectWayPossilbe(to))
//				{
//
//				gameCharacterTargetPosition.set(to.x,to.y);
//				ok=true;
//				}
//			}
//			
//		}
		// END DEMO: random walk

		// update level

		// now update Character Position

		// if Target Position is the same end
		if (gameCharacterPosition.equals(gameCharacterTargetPosition)){
			characterMoves=false;
			return;
		}
		// else it is moving
		characterMoves=true;

		// now change Position in small steps

		Vector2f diff = new Vector2f(gameCharacterTargetPosition);
		diff.subtract(gameCharacterPosition);

		Vector2f step = new Vector2f(0, 0);

		if (diff.x > 0)
			step.x = 1;
		else if (diff.x < 0)
			step.x = -1;

		if (diff.y > 0)
			step.y = 1;
		else if (diff.y < 0)
			step.y = -1;

		gameCharacterPosition.add(step.divide(0.1f/SceneGraph.deltaTime));

		// if the step size is larger then to the target
		if(Math.abs(diff.x+diff.y)<Math.abs(step.x+step.y)){
			gameCharacterPosition.set(gameCharacterTargetPosition);
			characterMoves=false;
			}
		
		
			
	}

	/**
	 * control if a direct way, horizontal or vertical is possible
	 * @param to desired point
	 * @return true if possible
	 */
	public boolean isDirectWayPossilbe(Vector2i to){
		
		// test if "to" is in the world
		if(to.x<0||to.x>worldDim.x|| to.y<0||to.y>worldDim.y){
			// test if endless walk else return false
			
			return false;
			
		}
			
		
		// if to is no wall!
		if(world[to.y*worldDim.x+to.x]==this.wall)
			return false;
		
		//test if horizontal or vertical way is possible
		Vector2i diff = new Vector2i(to);
		diff.subtract(Math.round(gameCharacterPosition.x),Math.round(gameCharacterPosition.y));
				
		// if the way to "to" is only horizontal or vertical
		if(diff.x*diff.y==0){
			
			// horizontal
			if(Math.abs(diff.x)>0){
				int x =Math.round(gameCharacterPosition.x);
				int y =Math.round(gameCharacterPosition.y);
				int step = -1;
				if(diff.x>0)
					step=1;
				for(int i= x; i!=to.x;i=i+step)
					if(world[y*worldDim.x+i]==wall)
						return false;
				return true;
					
			}
			// vertical
			else{
				int x =Math.round(gameCharacterPosition.x);
				int y =Math.round(gameCharacterPosition.y);
				int step = -1;
				if(diff.y>0)
					step=1;
				for(int i= y; i!=to.y;i=i+step)
					if(world[i*worldDim.x+x]==wall)
						return false;
				return true;
			}
			
		}
		// if the way is diagonal
		else{
			// TODO: implement diagonal check method
		}
		
		return false;
	}
	/**
	 * this method steer the Caracter to the desired position
	 * @param horizontal, if true go horizontal (x-axis), else vertical (y-axis)
	 * @param length how long (length of the path) should he go
	 * @return if the way is possible
	 */
	public boolean steerCharacterTo(boolean horizontal, int length) {
		Vector2i desiredPoint = new Vector2i(Math
				.round(gameCharacterPosition.x), Math
				.round(gameCharacterPosition.y));
		if (horizontal)
			desiredPoint.add(new Vector2i(length, 0));
		else
			desiredPoint.add(new Vector2i(0, length));

		if (isDirectWayPossilbe(desiredPoint)) {
			gameCharacterTargetPosition.set(desiredPoint.x, desiredPoint.y);
			return true;
		}
		return false;
	}

}