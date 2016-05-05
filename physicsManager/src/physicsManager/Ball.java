package physicsManager;

import java.util.ArrayList;
import com.badlogic.gdx.math.Vector3;

// dummy ball class

public class Ball extends PhysicsManager {
	public double radius = 1.0;
	public Vector3 position = new Vector3();
	// bounding box
	
	public Ball(double radius, Vector3 position) {
		this.radius = radius;
		this.position = position.cpy();		
	}
}
