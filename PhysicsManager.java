package physicsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PhysicsManager {

	private ArrayList<CollisionObject>		colObjs;
	private ArrayList<Ball> 				balls;
	
	PhysicsManager(ArrayList<CollisionObject> colObjs, ArrayList<Ball> balls) {

		this.colObjs = colObjs;
		this.balls = balls;
	}
	
	public PhysicsManager copy() {
		
		ArrayList<Ball> ballsDeepCopy = new ArrayList<Ball>();
		try{ // TODO is this needed
			for(Ball b: this.balls)
				ballsDeepCopy.add(new Ball(b)); // TODO check if this works
		} catch(Exception e) { System.out.println("Copy failed.");}
		
		return new PhysicsManager(colObjs, ballsDeepCopy);
	}
	
	public Map<Ball, CollisionObject> getBallBoundingBoxCollisions() {
		
		Map<Ball, CollisionObject> bbIntersects = new HashMap<Ball, CollisionObject>();

		for(Ball b : this.balls) {
			for(CollisionObject co : this.colObjs) {
				// this might not need both checks
				if(co.boundingBox.intersects(b.boundingBox) ||
						co.boundingBox.contains(b.boundingBox)) {
					bbIntersects.put(b, co);
				}
			}
		}
		return bbIntersects;
	}
	
	public Map<Ball, Ball> getBallBallCollisions() {
		
		Map<Ball, Ball> bbIntersects = new HashMap<Ball, Ball>();
		
		for (int i = 0; i < this.balls.size()-1; i++) {
			   for (int k = i+1; k < this.balls.size(); k++) {
				   
				   Ball b1 = balls.get(i);
				   Ball b2 = balls.get(k);

				   // only works with equally sized balls
				   
				   if(b1.position.dst2(b2.position) <= (b1.radius+b1.radius)*(b1.radius+b1.radius))
					   bbIntersects.put(b1, b2);
			   }
		}
		return bbIntersects;
	}

	public void update(double deltaTime) {
		// wtf m8 how do i even
		
	}
		
}
