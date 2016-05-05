package physicsManager;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class CollisionObject {
	
	public ArrayList<Vector3[]> triangles;
	public BoundingBox boundingBox;
	
	public CollisionObject(ArrayList<Vector3[]> triangles) {
		this.triangles = triangles;
	}
	
	
	
}
