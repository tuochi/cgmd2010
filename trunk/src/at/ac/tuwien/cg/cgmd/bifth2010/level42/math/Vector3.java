package at.ac.tuwien.cg.cgmd.bifth2010.level42.math;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import at.ac.tuwien.cg.cgmd.bifth2010.level42.util.Persistable;


public class Vector3 implements Persistable
{
	public float x,y,z;
	
	public Vector3(float xyz)
	{
		copy(xyz,xyz,xyz);
	}
	
	public Vector3(float x, float y, float z)
	{
		copy(x,y,z);
	}
	
	public void persist(DataOutputStream dos) throws IOException
	{
		dos.writeFloat(x);
		dos.writeFloat(y);
		dos.writeFloat(z);
	}
	
	public void restore(DataInputStream dis) throws IOException
	{
		x = dis.readFloat();
		y = dis.readFloat();
		z = dis.readFloat();
	}
	
	public Vector3 copy(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	
	public Vector3 copy(Vector3 other)
	{
		return copy(other.x, other.y, other.z);
	}
	
	public Vector3(Vector3 other)
	{
		this(other.x, other.y, other.z);
	}

	public Vector3()
	{
		this(0,0,0);
	}
	
	public Vector3(float[] arr)
	{
		this(arr[0],arr[1],arr[2]);
	}
	
	public float[] getArray3()
	{
		return new float[] {x,y,z};
	}
	
	public Vector3 invert()
	{
		x = -x;
		y = -y;
		z = -z;
		return this;
	}
	
	public Vector3 add(Vector3 other)
	{
		x += other.x;
		y += other.y;
		z += other.z;
		return this;
	}
	
	public static Vector3 add(Vector3 a, Vector3 b)
	{
		return new Vector3(a).add(b);
	}
	
	public Vector3 subtract(Vector3 other)
	{
		x -= other.x;
		y -= other.y;
		z -= other.z;
		return this;
	}
	
	public static Vector3 subtract(Vector3 a, Vector3 b)
	{
		return new Vector3(a).subtract(b);
	}
	
	public Vector3 multiply(Vector3 other)
	{
		x *= other.x;
		y *= other.y;
		z *= other.z;
		return this;
	}
	
	public static Vector3 multiply(Vector3 a, float b)
	{
		return new Vector3(a).multiply(b);
	}
	
	
	public static Vector3 multiply(Vector3 a, Vector3 b)
	{
		return new Vector3(a).multiply(b);
	}
	
	public static float dotProduct (Vector3 a, Vector3 b)
	{
		return (a.x*b.x + a.y*b.y + a.z*b.z);
	}
	
	public static Vector3 crossProduct (Vector3 a, Vector3 b)
	{
		Vector3 result = new Vector3();
		crossProduct(a, b, result);
		return result;
	}
	
	public static void crossProduct(Vector3 a, Vector3 b, Vector3 result)
	{
		result.x = a.y*b.z - a.z*b.y;
		result.y = a.z*b.x - a.x*b.z;
		result.z = a.x*b.y - a.y*b.x;
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
	
	public static Vector3 divide(Vector3 a, Vector3 b)
	{
		return new Vector3(a).divide(b);
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
	
	public static Vector3 normalize(Vector3 a)
	{
		return new Vector3(a).normalize();
	}
	
	public static float getAngle(Vector3 a, Vector3 b)
	{
		return (float)Math.acos((Vector3.dotProduct(a, b)/(a.length()*b.length())));
	}
	
	public float length()
	{
		return (float)Math.sqrt(x*x + y*y + z*z);
	}
	
	@Override
	public String toString()
	{
		return "(" + x + "," + y + "," + z + ")";
	}
}