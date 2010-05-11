package at.ac.tuwien.cg.cgmd.bifth2010.level42.orbit;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import at.ac.tuwien.cg.cgmd.bifth2010.level42.math.Matrix44;
import at.ac.tuwien.cg.cgmd.bifth2010.level42.math.Vector3;
import at.ac.tuwien.cg.cgmd.bifth2010.level42.util.Config;

/**
 * The Class DirectionalMotion represents a directed linear motion.
 *
 * @author Alex Druml
 * @author Lukas Roessler
 */
public class DirectionalPlanetMotion extends Motion
{
	
	/** The direction of the movement */
	private final Vector3 directionVec;
	
	/** The current position of the object */
	public final Vector3 position;
	
	private final Vector3 tempDirectionVec;
	
	/** The motion speed. */
	private float speed;
	
	/** The satellite transformation */
	private SatelliteTransformation satTrans;
	
	/** The transformation matrix */
	private Matrix44 transform;
	
	/** The basic orientation of the object */
	private Matrix44 basicOrientation;
	
	/**
	 * Instantiates a new directional motion.
	 *
	 * @param startPos the initial position of the motion
	 * @param directionVec the direction of the movement
	 * @param speed the motion speed
	 * @param basicOrientation the basic orientation of the object
	 */
	public DirectionalPlanetMotion(Vector3 startPos,Vector3 directionVec,float speed,Matrix44 basicOrientation)
	{
		//init
		this();
		this.directionVec.set(directionVec.normalize());
		this.position.set(startPos);
		this.speed = speed;
		
		if(basicOrientation!=null)
			this.basicOrientation.copy(basicOrientation);
	}
	
	/**
	 * Instantiates a new directional motion.
	 */
	protected DirectionalPlanetMotion()
	{
		//init
		this.transform = new Matrix44();
		this.basicOrientation = new Matrix44();
		this.directionVec = new Vector3();
		this.position = new Vector3();
		this.tempDirectionVec = new Vector3();
	}
	
	/* (non-Javadoc)
	 * @see at.ac.tuwien.cg.cgmd.bifth2010.level42.orbit.Motion#update(float)
	 */
	public void update(float dt)
	{
		tempDirectionVec.set(directionVec).normalize();
		tempDirectionVec.multiply(dt*speed);
		
		position.add(tempDirectionVec);
	
		transform.setIdentity();
		transform.mult(basicOrientation);
		
		if(satTrans!=null){
			satTrans.update(dt);
			transform.mult(satTrans.getTransform());
		}
		
		transform.addTranslate(position.x, position.y, position.z);
	}

	
	/* (non-Javadoc)
	 * @see at.ac.tuwien.cg.cgmd.bifth2010.level42.orbit.Motion#getTransform()
	 */
	public Matrix44 getTransform() {
		return transform;
	}

	
	/* (non-Javadoc)
	 * @see at.ac.tuwien.cg.cgmd.bifth2010.level42.orbit.Motion#setSatTrans(at.ac.tuwien.cg.cgmd.bifth2010.level42.orbit.SatelliteTransformation)
	 */
	public void setSatTrans(SatelliteTransformation satTrans) {
		this.satTrans = satTrans;
	}

	/* (non-Javadoc)
	 * @see at.ac.tuwien.cg.cgmd.bifth2010.level42.orbit.Motion#persist(java.io.DataOutputStream)
	 */
	@Override
	public void persist(DataOutputStream dos) throws IOException
	{
		this.directionVec.persist(dos);
		this.position.persist(dos);
		dos.writeFloat(this.speed);
		
		if(satTrans != null){
			dos.writeBoolean(true);
			dos.writeUTF(satTrans.getClass().getName());
			this.satTrans.persist(dos);
		}else
			dos.writeBoolean(false);
	}

	/* (non-Javadoc)
	 * @see at.ac.tuwien.cg.cgmd.bifth2010.level42.orbit.Motion#restore(java.io.DataInputStream)
	 */
	@Override
	public void restore(DataInputStream dis) throws IOException
	{
		this.directionVec.restore(dis);
		this.position.restore(dis);
		this.speed = dis.readFloat();
		
		if(dis.readBoolean()){
			String className = dis.readUTF();
			this.satTrans = SatelliteTransformation.restore(dis,className);
		}
	}
	
	/* (non-Javadoc)
	 * @see at.ac.tuwien.cg.cgmd.bifth2010.level42.orbit.Motion#getSatTrans()
	 */
	@Override
	public SatelliteTransformation getSatTrans() {
		return satTrans;
	}

	/* (non-Javadoc)
	 * @see at.ac.tuwien.cg.cgmd.bifth2010.level42.orbit.Motion#getCurrDirectionVec()
	 */
	@Override
	public Vector3 getCurrDirectionVec() {
		return directionVec;
	}

	/* (non-Javadoc)
	 * @see at.ac.tuwien.cg.cgmd.bifth2010.level42.orbit.Motion#setTransform(at.ac.tuwien.cg.cgmd.bifth2010.level42.math.Matrix44)
	 */
	@Override
	public void setTransform(Matrix44 transform) {
		this.transform = transform;
	}

	/* (non-Javadoc)
	 * @see at.ac.tuwien.cg.cgmd.bifth2010.level42.orbit.Motion#getSpeed()
	 */
	@Override
	public float getSpeed() {
		return this.speed;
	}

	/* (non-Javadoc)
	 * @see at.ac.tuwien.cg.cgmd.bifth2010.level42.orbit.Motion#morph(at.ac.tuwien.cg.cgmd.bifth2010.level42.math.Vector3)
	 */
	@Override
	public void morph(Vector3 pushVec) {
		this.directionVec.multiply(speed).add(pushVec);
		
		this.speed = this.directionVec.length();
		if(this.speed>Config.UNIVERSE_SPEED_LIMIT)
			this.speed = Config.UNIVERSE_SPEED_LIMIT;
		
		this.directionVec.normalize();
	}
	
	@Override
	public Matrix44 getBasicOrientation() {
		return basicOrientation;
	}
}