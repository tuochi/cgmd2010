package at.ac.tuwien.cg.cgmd.bifth2010.level12.entities;

import at.ac.tuwien.cg.cgmd.bifth2010.R;
import at.ac.tuwien.cg.cgmd.bifth2010.level12.Definitions;
import at.ac.tuwien.cg.cgmd.bifth2010.level12.SoundHandler;

public class AdvancedTower extends Tower {

	public AdvancedTower( ){
		mRadius = (short)Math.floor( (float)Definitions.FIELD_SEGMENT_LENGTH / 100 *  Definitions.ADVANCED_TOWER_RADIUS );
		mColor[0] = 1.0f;
		mColor[1] = 1.0f;
		mColor[2] = 1.0f;
		mColor[3] = 1.0f;
		//mTexture =  R.drawable.l12_advanced_tower;
		mTexture =  R.drawable.l12_bunny3;
		mSound = R.raw.l12_advanced_tower_shooting_sound;
		mPrice = Definitions.ADVANCED_TOWER_IRON_NEED;
		initProjectiles();
	}
	
	
	public void initProjectiles(){
		if( mProjectiles == null){
			mProjectiles = new AdvancedProjectile[ Definitions.ADVANCED_PROJECTILE_POOL ];
			for( int i = 0; i < mProjectiles.length; i++){
				mProjectiles[i] = new AdvancedProjectile();	
			}
		}
	}
}
