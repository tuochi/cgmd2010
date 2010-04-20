package at.ac.tuwien.cg.cgmd.bifth2010.level12.entities;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

import javax.microedition.khronos.opengles.GL10;

import java.lang.System;



public abstract class Tower extends GLObject {
	private float mRadius = -1.0f;
	protected float mShootingInterval = 1.0f; //secs
	protected double mTimeLastProjectileShot = System.currentTimeMillis();
	protected Queue<Projectile> mFlyingProjectiles = new LinkedList<Projectile>();   //any better options regarding performance?
	protected Projectile[] mProjectiles = null;
	protected int mScreenWidth = 800;
	
	
	protected Tower(float xCentr, float yCentr, float radius ){
		this( radius );	
		setXY( xCentr, yCentr );
	}
	
	protected Tower( float radius ){
		mRadius = radius;
		mColor[0] = 0.5f;
		mColor[1] = 0.5f;
		mColor[2] = 0.0f;
		mColor[3] = 1.0f;
	}
	
	
	public void setXY( float xCentr, float yCentr ){
		mX = xCentr;
		mY = yCentr;
		System.out.println("TOWER SETXY: mX: "+mX+" my: "+mY);
		float[] vertices = {
				(mX - mRadius),	(mY - mRadius), 1.0f,
				(mX + mRadius),	(mY - mRadius), 1.0f,
				(mX + mRadius),	(mY + mRadius), 1.0f,
				(mX - mRadius),	(mY + mRadius), 1.0f
		};
		ByteBuffer v = ByteBuffer.allocateDirect( vertices.length * 4 );
		v.order( ByteOrder.nativeOrder() );
		mVerticesBuffer = v.asFloatBuffer();
		mVerticesBuffer.put( vertices );
		mVerticesBuffer.position(0);
		
		short[] indices = {
				0,	1,	2,
				0,	2,	3
		};
		System.out.println("Vertices.length: "+vertices.length+" Indices.length: "+indices.length);
		ByteBuffer i = ByteBuffer.allocateDirect( indices.length * 2 );
		i.order( ByteOrder.nativeOrder() );
		mIndicesBuffer = i.asShortBuffer();
		mIndicesBuffer.put( indices );
		mIndicesBuffer.position(0);	
		mIndicesCounter = indices.length;
		
		float[] colors = { mColor[0], mColor[1], mColor[2], 1.0f,
				mColor[0], mColor[1], mColor[2], 1.0f,
				mColor[0], mColor[1], mColor[2], 1.0f,
				mColor[0], mColor[1], mColor[2], 1.0f};
		ByteBuffer cbb = ByteBuffer.allocateDirect( colors.length * 4 );
		cbb.order( ByteOrder.nativeOrder() );
		mColorBuffer = cbb.asFloatBuffer();
		mColorBuffer.put( colors );
		mColorBuffer.position( 0 );
	}
	
	public void setViewPortLength(int width) {
		mScreenWidth = width;
	}
	
	
	
	@Override
	public void draw( GL10 gl ){
		//new projectile
		double dt =(System.currentTimeMillis() - mTimeLastProjectileShot ) * 0.001;//secs
		if( dt >= mShootingInterval ){
			if( mProjectiles != null ){
				boolean found = false;
				for ( int  i = 0; i < mProjectiles.length && !found; i++){
					if( mProjectiles[i].getActiveState() == false ){
						found = true;
						mProjectiles[i].setActiveState( true );
						mProjectiles[i].setXY( this.getX(), mY);
						System.out.println("Tower: "+mX+" my: "+mY);
						mTimeLastProjectileShot = System.currentTimeMillis();
						mFlyingProjectiles.add( mProjectiles[i] );
						System.out.println("adding projectile #: "+i);
						break;
					}
				}
			}
			Projectile p = mFlyingProjectiles.peek();
			if( p.getX() > mScreenWidth ){
				System.out.println("Removing Projectile over screen edge!!!! ScreenEdge: "+mScreenWidth+" Projectile: "+ p.getX());
				p = mFlyingProjectiles.poll();
				p.reset();
			}
			
			else if( (p.getX() >= p.getCollisionPointX()) && (p.getCollisionPointX() > 0.0f ) ){
				System.out.println("Removing Projectile over collision point!!! CollPoint: "+p.getCollisionPointX()+" Projectile: "+ p.getX());
				p = mFlyingProjectiles.poll();
				p.reset();
			}
		}
		
		//all active sollen gezeichnet werden
		for( int  i = 0; i < mProjectiles.length; i++){
			if(mProjectiles[i].getActiveState()) mProjectiles[i].draw(gl);
		}
		super.draw(gl);
	}
	
	
	public Projectile getProjectile(){
		if (mFlyingProjectiles.isEmpty()) return null;
		else return mFlyingProjectiles.element(); //achtung, kann null zur�ckgeben
	}
	

	
	
}


