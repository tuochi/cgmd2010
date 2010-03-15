package at.ac.tuwien.cg.cgmd.bifth2010.level42;

public class Vector3
{
public float x,y,z;
	
	public Vector3(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3(Vector3 other)
	{
		this(other.x, other.y, other.z);
	}

	public Vector3()
	{
		this(0,0,0);
	}
	
	public Vector3 add(Vector3 other)
	{
		x += other.x;
		y += other.y;
		z += other.z;
		return this;
	}
	
	public Vector3 subtract(Vector3 other)
	{
		x -= other.x;
		y -= other.y;
		z -= other.z;
		return this;
	}
	
	public Vector3 multiply(Vector3 other)
	{
		x *= other.x;
		y *= other.y;
		z *= other.z;
		return this;
	}
	
	public Vector3 multiply(float s)
	{
		x *= s;
		y *= s;
		z *= s;
		return this;
	}
	
	public Vector3 divide(Vector3 other)
	{
		x /= other.x;
		y /= other.y;
		z /= other.z;
		return this;
	}
	
	public Vector3 divide(float s)
	{
		x /= s;
		y /= s;
		z /= s;
		return this;
	}
	
	public Vector3 normalize()
	{
		float length = length();
		if(length != 0 && length != 1)
		{
			x /= length;
			y /= length;
			z /= length;
		}
		return this;
	}
	
	public float length()
	{
		return (float)Math.sqrt(x*x + y*y + z*z);
	}
}
