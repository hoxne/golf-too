package physicsManager;

import java.util.ArrayList;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class CollisionObject {
	
	public ArrayList<Vector3[]> triangles;
	public ArrayList<Vector3> trianglesNormals;
	public BoundingBox boundingBox;
	
	public CollisionObject(ArrayList<Vector3[]> triangles) {
		this.triangles = triangles;
		this.boundingBox = findBoundingBox();
		this.trianglesNormals = findNormals();
	}
	
	private BoundingBox findBoundingBox() {
		
		float xmin = Float.POSITIVE_INFINITY;
		float ymin = Float.POSITIVE_INFINITY;
		float zmin = Float.POSITIVE_INFINITY;
		
		float xmax = Float.NEGATIVE_INFINITY;
		float ymax = Float.NEGATIVE_INFINITY;
		float zmax = Float.NEGATIVE_INFINITY;
		
		for(Vector3[] t: triangles) {
			for(Vector3 p: t) {
				if(p.x < xmin )
					xmin = p.x;
				
				if(p.y < ymin )
					ymin = p.y;

				if(p.z < zmin )
					zmin = p.z;
				
				if(p.x > xmax )
					xmax = p.x;

				if(p.y > ymax )
					ymax = p.y;

				if(p.z > zmax )
					zmax = p.z;
			}
		}
		
		Vector3 min = new Vector3(xmin, ymin, zmin);
		Vector3 max = new Vector3(xmax, ymax, zmax);
		
		return new BoundingBox(min, max);
	}
	
	private ArrayList<Vector3> findNormals() {
		
		ArrayList<Vector3> normals = new ArrayList<Vector3>();
		
		for(Vector3[] t : triangles) {
			
			Vector3 p1p0 = t[1].sub(t[0]).nor();
			Vector3 p2p0 = t[2].sub(t[0]).nor();
			normals.add(p1p0.crs(p2p0));
		}
		
		return normals;
	}
}
