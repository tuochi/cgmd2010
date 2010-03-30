package at.ac.tuwien.cg.cgmd.bifth2010.level33.scene;

import static android.opengl.GLES10.GL_MODELVIEW;
import static android.opengl.GLES10.glLoadIdentity;
import static android.opengl.GLES10.glMatrixMode;
import static android.opengl.GLES10.glMultMatrixf;
import static android.opengl.GLES10.glPopMatrix;
import static android.opengl.GLES10.glPushMatrix;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import at.ac.tuwien.cg.cgmd.bifth2010.level33.GameView;
import at.ac.tuwien.cg.cgmd.bifth2010.level33.model.Cube;
import at.ac.tuwien.cg.cgmd.bifth2010.level33.model.GameCharacter;
import at.ac.tuwien.cg.cgmd.bifth2010.level33.model.Geometry;

public class SceneGraph {

	public static LevelHandler level;

	static Geometry g; // private ArrayList<GeometryGroup> renderables;
	static Geometry c;
	public static Camera camera;
	
	static float deltaTime;
	static long lastFrameStart;
	
	public final static byte GEOMETRY_WALL = 0;
	public final static byte GEOMETRY_WAY = 1;
	public final static byte GEOMETRY_STONE = 2;
	public final static byte GEOMETRY_BARREL = 3;
	public final static byte GEOMETRY_TRASH = 4;
	public final static byte GEOMETRY_MAP = 5;
	public final static byte GEOMETRY_SPRING = 6;
	

	private boolean N;
	private boolean O;
	private boolean S;
	private boolean W;
	

	public static boolean zoomOutView = false; // if false use standard zoom for playing, if true zoom out

	public SceneGraph(LevelHandler level) {
		this.level = level;
	}

	/**
	 * This Method will init the Geometry VBO�s
	 * 
	 * @param gl
	 *            OpenGlHandler
	 */
	public static void init(GL10 gl) {
		SceneGraph.camera = new Camera();
		g = new Cube(gl);
		c = new GameCharacter(gl);
		
	}

	/**
	 * This Method represent the main render loop of the SceneGraph
	 * 
	 * @param gl
	 */
	public void render(GL10 gl) {
		
		// start time mesherment
		long currentFrameStart = System.nanoTime();
		deltaTime = (currentFrameStart-lastFrameStart) / 1000000000.0f;
		lastFrameStart = currentFrameStart;		

		// Clears the screen and depth buffer.
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		// Set the background color to black ( rgba ).
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
		// Enable Smooth Shading, default not really needed.
		gl.glShadeModel(GL10.GL_SMOOTH);
		// Depth buffer setup.
		gl.glClearDepthf(1.0f);
		// Enables depth testing.
		gl.glEnable(GL10.GL_DEPTH_TEST);
		// The type of depth testing to do.
		gl.glDepthFunc(GL10.GL_LEQUAL);
		// Really nice perspective calculations.
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

		// updateLogic
		level.updateLogic();

		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		
		// upadate Camera
		camera.lookAt(gl);

		// now start



		// now render the Scene
		renderScene(gl);

		// ?? sleep rest of time?

	}


	private void renderScene(GL10 gl) {
		//this.level
		
		
		glMatrixMode(GL_MODELVIEW);
		
		
		// render world
		
		// if Caracter is near the end of the world render word twice, quatro
		int viewRange= 5;
		
		
		for(int y=0;y<level.worldDim.y;y++){
			for(int x=0;x<level.worldDim.x;x++){
				//if wall
				int id=y*level.worldDim.x+x;
				if(level.world[id]==level.wall)
				{
				// move the word
				glPushMatrix();
				gl.glTranslatef(-(level.gameCharacterPosition.x-(level.worldDim.x/2)),-((level.worldDim.y/2)-level.gameCharacterPosition.y), 0);
					// move the wall position
					glPushMatrix();
					
						// center world
						gl.glTranslatef(x-(level.worldDim.x/2),(level.worldDim.y/2)-y, 0);
						g.render();	
						
						if(true)//if(camera.zoom==camera.standardZoom)
						{
							
							boolean N = level.gameCharacterTargetPosition.y<viewRange;
							boolean O = level.gameCharacterTargetPosition.x>level.worldDim.x-viewRange;
							boolean S = level.gameCharacterTargetPosition.y>level.worldDim.y-viewRange;
							boolean W = level.gameCharacterTargetPosition.x<viewRange;
							// endless copy position
							
							if(N)
							{
								// N
								glPushMatrix();
								gl.glTranslatef(0,level.worldDim.y, 0);
								g.render();	
								glPopMatrix();
							}
							
							if(N&&O)
							{
							// N-O
							glPushMatrix();
							gl.glTranslatef(level.worldDim.x,level.worldDim.y, 0);
							g.render();	
							glPopMatrix();
							}
							
							if(O)
							{
								// O
								glPushMatrix();
								gl.glTranslatef(level.worldDim.x,0, 0);
								g.render();	
								glPopMatrix();
							}
							
							if(O&&S)
							{
							// O-S
							glPushMatrix();
							gl.glTranslatef(level.worldDim.x,-level.worldDim.y, 0);
							g.render();	
							glPopMatrix();
							}
							
							if(S)
							{
								// S
								glPushMatrix();
								gl.glTranslatef(0,-level.worldDim.y, 0);
								g.render();	
								glPopMatrix();
							}
						
							if(S&&W)
							{
							// S-W
							glPushMatrix();
							gl.glTranslatef(-level.worldDim.x,-level.worldDim.y, 0);
							g.render();	
							glPopMatrix();
							}
							
							if(W)
							{
								// W
								glPushMatrix();
								gl.glTranslatef(-level.worldDim.x,0, 0);
								g.render();	
								glPopMatrix();
							}
							
							if(N&&W)
							{
							// N-W
							glPushMatrix();
							gl.glTranslatef(-level.worldDim.x,level.worldDim.y, 0);
							g.render();	
							glPopMatrix();
							}
							
						}
					
					
									
					glPopMatrix();
				glPopMatrix();
				}
			} 
		}
		
		// render GameCaracter
		glPushMatrix();
		//gl.glTranslatef(level.gameCharacterPosition.x-(level.worldDim.x/2),(level.worldDim.y/2)-level.gameCharacterPosition.y, 0);
		c.render();					
		glPopMatrix();
		
	}

}
