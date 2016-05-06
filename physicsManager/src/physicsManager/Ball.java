package physicsManager;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

// dummy ball class

public class Ball {
	public double radius;
	public Vector3 position;
	public BoundingBox boundingBox;
	
	public Ball(double radius, Vector3 position) {
		this.radius = radius;
		this.position = position.cpy();
		
		Vector3 min = position.cpy();
		Vector3 max = position.cpy();
		min.add((float) (-radius/2));
		max.add((float) (radius/2));
		
		this.boundingBox = new BoundingBox(min, max);
	}
	
	public Ball(Ball b) {
		this(b.radius, b.position);
	}
}