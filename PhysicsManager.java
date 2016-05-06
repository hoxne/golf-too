import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PhysicsManager {

	private ArrayList<CollisionObject>		colObjs;
	private ArrayList<GolfBall> 			balls;
	
	PhysicsManager(ArrayList<CollisionObject> colObjs, ArrayList<GolfBall> balls) {
		this.colObjs = colObjs;
		this.balls = balls;
	}
	
	public PhysicsManager copy() {
		ArrayList<GolfBall> ballsDeepCopy = new ArrayList<GolfBall>();
		try{ // TODO is this needed
			// NOTE: Nope
			for(GolfBall b: this.balls)
				// NOTE: should probably be b.copy() instead of new GolfBall(b)
				//       so wee need a copy function in GolfBall...
				ballsDeepCopy.add(new GolfBall(b)); // TODO check if this works
		} catch(Exception e) { System.out.println("Copy failed.");}
		
		return new PhysicsManager(colObjs, ballsDeepCopy);
	}
	
	public Map<GolfBall, CollisionObject> getBallBoundingBoxCollisions() {
		
		Map<GolfBall, CollisionObject> bbIntersects = new HashMap<GolfBall, CollisionObject>();

		for(GolfBall b : this.balls) {
			for(CollisionObject co : this.colObjs) {
				// this might not need both checks
				// NOTE: indeed, it doesn't, it only needs the first one :)
				if(co.boundingBox.intersects(b.boundingBox) ||
						co.boundingBox.contains(b.boundingBox)) {
					bbIntersects.put(b, co);
				}
			}
		}
		return bbIntersects;
	}
	
	public Map<GolfBall, GolfBall> getBallBallCollisions() {
		
		Map<GolfBall, GolfBall> bbIntersects = new HashMap<GolfBall, GolfBall>();
		
		// NOTE: keep an eye out for off-by-one errors here... but I *think* it's fine
		for (int i = 0; i < this.balls.size()-1; i++) {
			   for (int k = i+1; k < this.balls.size(); k++) {
				   
				   GolfBall b1 = balls.get(i);
				   GolfBall b2 = balls.get(k);

				   // only works with equally sized balls
				   
				   // NOTE: should be b1.r+b2.r and not b1.r+b1.r
				   if(b1.getPosition().dst2(b2.getPosition()) <= (b1.getRadius()+b1.getRadius())*(b1.getRadius()+b1.getRadius()))
					   bbIntersects.put(b1, b2);
			   }
		}
		return bbIntersects;
	}

	public void update(double deltaTime) {
		// wtf m8 how do i even
		Map<GolfBall, CollisionObject> ballVsObjects = getBallBoundingBoxCollisions();
		Map<GolfBall, GolfBall> ballVsBall = getBallBallCollisions();

		for (GolfBall ball : balls) {
			// NOTE: this would only check one of all the colliding bounding boxes for any given ball
			//       instead of iterating over the balls and then retrieving the colliding BB
			//       you should probably just iterate over all the elements in ballVsObjects
			//       and keep track of all the normals for the balls
		
			// TODO: also check the ball-ball collisions before calling the bounce function
			CollisionObject collisionObject = ballVsObjects.get(ball);
			if (collisionObject != null) {
				ArrayList<Vector3> normals = collisionObject.trianglesNormals;
				ball.bounce(collisionObject.getNormal(ball));
			}
		}

	}
		
}
