package physicsManager;

import java.util.ArrayList;

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
				ballsDeepCopy.add((Ball)b.clone());
		} catch(Exception e) { System.out.println("Copy failed.");}
		
		return new PhysicsManager(colObjs, ballsDeepCopy);
	}
	

	private boolean ballIsInBox(Ball b, CollisionObject co) {
		
		
		
		return false;
	}
	
}
