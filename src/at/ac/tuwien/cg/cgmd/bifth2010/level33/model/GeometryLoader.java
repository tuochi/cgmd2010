package at.ac.tuwien.cg.cgmd.bifth2010.level33.model;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.microedition.khronos.opengles.GL10;

public class GeometryLoader {

	public static Geometry loadObj(GL10 gl, InputStream is,InputStream isImage) {
		String line = "";

		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			StringBuffer b = new StringBuffer();
			String l = reader.readLine();
			while (l != null) {
				b.append(l);
				b.append("\n");
				l = reader.readLine();
			}

			line = b.toString();
			reader.close();
		} catch (Exception ex) {
			throw new RuntimeException(
					"couldn't load obj from input stream");
		}
		return loadObjFromString(gl, line, isImage);

	}

	public static Geometry loadObjFromString(GL10 gl, String obj, InputStream isImage) {
		String[] lines = obj.split("\n");
		float[] vertices = new float[lines.length * 3];
		float[] normals = new float[lines.length * 3];
		float[] uv = new float[lines.length * 3];

		int numVertices = 0;
		int numNormals = 0;
		int numUV = 0;
		int numFaces = 0;

		int[] facesVerts = new int[lines.length * 3];
		int[] facesNormals = new int[lines.length * 3];
		int[] facesUV = new int[lines.length * 3];
		int vertexIndex = 0;
		int normalIndex = 0;
		int uvIndex = 0;
		int faceIndex = 0;

		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if (line.startsWith("v ")) {
				String[] tokens = line.split(" ");
				vertices[vertexIndex] = Float.parseFloat(tokens[1]);
				vertices[vertexIndex + 1] = Float.parseFloat(tokens[2]);
				vertices[vertexIndex + 2] = Float.parseFloat(tokens[3]);
				vertexIndex += 3;
				numVertices++;
				continue;
			}

			if (line.startsWith("vn ")) {
				String[] tokens = line.split(" ");
				normals[normalIndex] = Float.parseFloat(tokens[1]);
				normals[normalIndex + 1] = Float.parseFloat(tokens[2]);
				normals[normalIndex + 2] = Float.parseFloat(tokens[3]);
				normalIndex += 3;
				numNormals++;
				continue;
			}

			if (line.startsWith("vt")) {
				String[] tokens = line.split(" ");
				uv[uvIndex] = Float.parseFloat(tokens[1]);
				uv[uvIndex + 1] = 1-Float.parseFloat(tokens[2]);
				uvIndex += 2;
				numUV++;
				continue;
			}

			if (line.startsWith("f ")) {
				String[] tokens = line.split(" ");

				String[] parts = tokens[1].split("/");
				facesVerts[faceIndex] = getIndex(parts[0], numVertices);
				facesNormals[faceIndex] = getIndex(parts[2], numNormals);
				facesUV[faceIndex] = getIndex(parts[1], numUV);
				faceIndex++;

				parts = tokens[2].split("/");
				facesVerts[faceIndex] = getIndex(parts[0], numVertices);
				facesNormals[faceIndex] = getIndex(parts[2], numNormals);
				facesUV[faceIndex] = getIndex(parts[1], numUV);
				faceIndex++;

				parts = tokens[3].split("/");
				facesVerts[faceIndex] = getIndex(parts[0], numVertices);
				facesNormals[faceIndex] = getIndex(parts[2], numNormals);
				facesUV[faceIndex] = getIndex(parts[1], numUV);
				faceIndex++;
				numFaces++;
				continue;
			}
		}

		Geometry geometry = new Geometry(gl, Geometry.Type.Triangles, numFaces * 3,
				false, numUV > 0, numNormals > 0,isImage);

		for (int i = 0; i < numFaces * 3; i++) {
			if (numNormals > 0) {
				int normalIdx = facesNormals[i] * 3;
				geometry.normal(normals[normalIdx], normals[normalIdx + 1],
						normals[normalIdx + 2]);
			}
			if (numUV > 0) {
				int uvIdx = facesUV[i] * 2;
				geometry.texCoord(uv[uvIdx], uv[uvIdx + 1]);
			}

			int vertexIdx = facesVerts[i] * 3;
			geometry.vertex(vertices[vertexIdx], vertices[vertexIdx + 1],
					vertices[vertexIdx + 2]);
		}

		return geometry;
	}

	private static int getIndex(String index, int size) {
		if (index == null || index.length() == 0)
			return 0;
		int idx = Integer.parseInt(index);
		if (idx < 0)
			return size + idx;
		else
			return idx - 1;
	}

}
