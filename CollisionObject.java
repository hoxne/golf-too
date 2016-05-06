package physicsManager;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class CollisionObject {
	
	public Vector3[] triangles;
	public ArrayList<Vector3> trianglesNormals;
	public BoundingBox boundingBox;
	
	public CollisionObject(Vector3[] triangles) {
		this.triangles = triangles;
		this.boundingBox.set(triangles);
		this.trianglesNormals = findNormals();
	}
	
	private ArrayList<Vector3> findNormals() {
		
		ArrayList<Vector3> normals = new ArrayList<Vector3>();
		
		for(int i = 0; i < triangles.length-2; i+=3) {
			
			Vector3 p1p0 = triangles[i+1].sub(triangles[i]).nor();
			Vector3 p2p0 = triangles[i+2].sub(triangles[i]).nor();
			normals.add(p1p0.crs(p2p0));
		}
		
		return normals;
	}	
}
