/**
 * 
 */
package at.ac.tuwien.cg.cgmd.bifth2010.level88.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import at.ac.tuwien.cg.cgmd.bifth2010.R;
import at.ac.tuwien.cg.cgmd.bifth2010.level88.util.Quad;
import at.ac.tuwien.cg.cgmd.bifth2010.level88.util.Vector2;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author Asperger, Radax
 *
 */
public class Map {
	public class MapCell {
		public MapCell() {
			x = y = -1;
			groundRotation = 0;
			groundTexture = R.drawable.l88_street_none;
			houseTexture = -1;
			isStreetForPolice=false;
			isStreetForBunny=false;
		}

		public int x, y;
		public float translateX, translateY;
		public int groundTexture;
		public int houseTexture;
		public int groundRotation; // Tex-Coords

		public boolean isStreetForPolice;
		public boolean isStreetForBunny;
	}

	private Game game;
	public MapCell[][] cells;
	public Vector2 groundXDir, groundYDir;
	private Quad groundQuad;
	public Quad houseQuad;


	public Map(Game _game) {
		game = _game;

		float norm;
		groundYDir = new Vector2(-229, -169);
		norm = 1.0f / groundYDir.length();
		groundYDir.mult(norm);
		groundXDir = new Vector2(329, -131);
		groundXDir.mult(norm);
		groundQuad = new Quad(new Vector2(), groundXDir, groundYDir);

		Vector2 houseXDir = new Vector2(660, 0);
		houseXDir.mult(norm);
		Vector2 houseYDir = new Vector2(0, -538);
		houseYDir.mult(norm);
		Vector2 houseQuadBase = new Vector2();
		houseQuadBase.x = -groundXDir.x;
		houseQuad = new Quad(houseQuadBase, houseXDir, houseYDir);


		//TODO hier loading machen, Cellgr��e bestimmen
		load();

		/*        
		cells = new MapCell[7][7];
		for(int x=0; x<cells.length; x++) {
			for(int y=0; y<cells[0].length; y++) {
				cells[x][y] = new MapCell();
				cells[x][y].x = x;
				cells[x][y].y = y;
				cells[x][y].translateX = groundXDir.x*x + groundYDir.x*y;
				cells[x][y].translateY = groundXDir.y*x + groundYDir.y*y;
			}
		}
		 */	
		//TODO machen das Defaultlevel
		/*
		for(int x=1; x<cells.length-1; x++) {
			for(int y=1; y<cells[0].length-1; y++) {
				cells[x][y].isStreetForBunny = true;
				cells[x][y].groundTexture = R.drawable.l88_street_junction;
			}
		}
		 */
		// TODO: ersetzen durch File-Loader 
		/*cells[2][2].groundTexture = R.drawable.l88_street_junction;

		cells[0][2].groundTexture = R.drawable.l88_street_tjunction;
		cells[0][2].groundRotation = 0;
		cells[2][0].groundTexture = R.drawable.l88_street_tjunction;
		cells[2][0].groundRotation = 1;
		cells[4][2].groundTexture = R.drawable.l88_street_tjunction;
		cells[4][2].groundRotation = 2;
		cells[2][4].groundTexture = R.drawable.l88_street_tjunction;
		cells[2][4].groundRotation = 3;

		cells[0][0].groundTexture = R.drawable.l88_street_turn;
		cells[0][0].groundRotation = 1;
		cells[4][0].groundTexture = R.drawable.l88_street_turn;
		cells[4][0].groundRotation = 2;
		cells[0][4].groundTexture = R.drawable.l88_street_turn;
		cells[0][4].groundRotation = 0;
		cells[4][4].groundTexture = R.drawable.l88_street_turn;
		cells[4][4].groundRotation = 3;

		cells[1][0].groundTexture = R.drawable.l88_street_straight;
		cells[1][0].groundRotation = 1;
		cells[3][0].groundTexture = R.drawable.l88_street_straight;
		cells[3][0].groundRotation = 1;
		cells[1][2].groundTexture = R.drawable.l88_street_straight;
		cells[1][2].groundRotation = 1;
		cells[3][2].groundTexture = R.drawable.l88_street_straight;
		cells[3][2].groundRotation = 1;
		cells[1][4].groundTexture = R.drawable.l88_street_straight;
		cells[1][4].groundRotation = 1;
		cells[3][4].groundTexture = R.drawable.l88_street_straight;
		cells[3][4].groundRotation = 1;

		cells[0][1].groundTexture = R.drawable.l88_street_straight;
		cells[0][1].groundRotation = 0;
		cells[0][3].groundTexture = R.drawable.l88_street_straight;
		cells[0][3].groundRotation = 0;
		cells[2][1].groundTexture = R.drawable.l88_street_straight;
		cells[2][1].groundRotation = 0;
		cells[2][3].groundTexture = R.drawable.l88_street_straight;
		cells[2][3].groundRotation = 0;
		cells[4][1].groundTexture = R.drawable.l88_street_straight;
		cells[4][1].groundRotation = 0;
		cells[4][3].groundTexture = R.drawable.l88_street_tjunction;
		cells[4][3].groundRotation = 0;

		cells[5][3].groundTexture = R.drawable.l88_street_end;
		cells[5][3].groundRotation = 3;


		cells[1][1].houseTexture = R.drawable.l88_house_block5;
		cells[3][1].houseTexture = R.drawable.l88_house_block2;
		cells[3][3].houseTexture = R.drawable.l88_house_block3;
		cells[1][3].houseTexture = R.drawable.l88_house_block4;*/


	}

	public void update(float elapsedSeconds) {

	}

	public void draw(GL10 gl) {
		groundQuad.vbos.set(gl);
		for(int x=0; x<cells.length; x++) {
			for(int y=0; y<cells[0].length; y++) {
				gl.glMatrixMode(GL10.GL_MODELVIEW);
				gl.glPushMatrix();
				gl.glTranslatef(cells[x][y].translateX, cells[x][y].translateY, 0);
				gl.glMatrixMode(GL10.GL_TEXTURE);
				gl.glPushMatrix();
				gl.glRotatef(cells[x][y].groundRotation*90, 0, 0, 1);

				game.textures.bind(cells[x][y].groundTexture);
				gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4);

				gl.glPopMatrix();
				gl.glMatrixMode(GL10.GL_MODELVIEW);
				gl.glPopMatrix();
			}
		}

		houseQuad.vbos.set(gl);
		for(int x=cells.length-1; x>=0; x--) {
			for(int y=cells[0].length-1; y>=0; y--) {
				if( cells[x][y].houseTexture != -1 ) {
					gl.glPushMatrix();
					gl.glTranslatef(cells[x][y].translateX, cells[x][y].translateY, 0);

					game.textures.bind(cells[x][y].houseTexture);
					gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4);

					gl.glPopMatrix();
				}
			}
		}
	}

	public void load(){
		InputStream is = game.context.getResources().openRawResource(R.raw.l88_level);
		InputStreamReader irs = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(irs);

		ArrayList<String> values = new ArrayList<String>();

		try {
			String zeile = br.readLine();

			while(zeile != null){
				values.add(zeile);
				zeile = br.readLine();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//TODO die Werte
		int h,w;
		h = values.size() + 2;
		w = values.get(0).length() + 2;

		cells = new MapCell[w][h];
		for(int x=0; x<cells.length; x++) {
			for(int y=0; y<cells[0].length; y++) {
				cells[x][y] = new MapCell();
				cells[x][y].x = x;
				cells[x][y].y = y;
				cells[x][y].translateX = groundXDir.x*x + groundYDir.x*y;
				cells[x][y].translateY = groundXDir.y*x + groundYDir.y*y;
			}
		}

		for(int y=0; y<values.size(); y++){
			char[] zeichen = values.get(y).toCharArray();
			for(int x=0; x<values.get(y).length(); x++){

				switch(zeichen[x]){
				case 'X':
				case 'x':
					cells[x+1][values.size()-y].houseTexture = R.drawable.l88_house_block1;
					break;
				case ' ':
					//Stra�e
					break;
				case '1':
					break;
				case '2':
					break;
				case '3':
					break;
				case 'B':
				case 'b':
					break;
				case 'P':
				case 'p':
					break;
						
					
				}

			}


		}//end for




	}


}
