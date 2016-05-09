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

	@Override
	public PhysicsManager clone() {
		ArrayList<GolfBall> ballsDeepCopy = new ArrayList<GolfBall>();
		for(GolfBall b: this.balls)
			ballsDeepCopy.add(b.clone());
		
		return new PhysicsManager(colObjs, ballsDeepCopy);
	}
	
	public Map<GolfBall, ArrayList<CollisionObject>> getBallBoundingBoxCollisions() {
		
		Map<GolfBall, ArrayList<CollisionObject>> bbIntersects = new HashMap<GolfBall, ArrayList<CollisionObject>>();

		for(GolfBall b : this.balls) {
			ArrayList<CollisionObject> list = new ArrayList<>();
			bbIntersects.put(b, list);
			for(CollisionObject co : this.colObjs) {
				if(co.boundingBox.intersects(b.boundingBox)) {
					list.add(co);
				}
			}
		}

		return bbIntersects;
	}
	
	public Map<GolfBall, ArrayList<GolfBall>> getBallBallCollisions() {
		
		Map<GolfBall, ArrayList<GolfBall>> bbIntersects = new HashMap<GolfBall, ArrayList<GolfBall> >();


		for(GolfBall b : this.balls) {
			ArrayList<GolfBall> list = new ArrayList<>();
			bbIntersects.put(b, list);
		}

		for (int i = 0; i < this.balls.size() - 1; i++) {

			for (int k = i + 1; k < this.balls.size(); k++) {
				   
				   GolfBall b1 = balls.get(i);
				   GolfBall b2 = balls.get(k);

				   if(b1.getPosition().dst2(b2.getPosition()) <= (b1.getRadius() + b2.getRadius()) * (b1.getRadius() + b2.getRadius())){
					   bbIntersects.get(b1).add(b2);
					   bbIntersects.get(b2).add(b1);
				   }
			   }
		}
		return bbIntersects;
	}

	public void update(double deltaTime) {
		Map<GolfBall, ArrayList<CollisionObject> > ballVsObjects = getBallBoundingBoxCollisions();
		Map<GolfBall, ArrayList<GolfBall> > ballVsBalls = getBallBallCollisions();

		for (GolfBall ball : balls) {
			ArrayList<CollisionObject> collisionObjects = ballVsObjects.get(ball);
			ArrayList<Vector3> normals = new ArrayList<>();
			for (CollisionObject collisionObject : collisionObjects) {
				Vector3 normal = collisionObject.getNormal(ball);
				if (normal != null)
					normals.add(normal);
			}

			ArrayList<GolfBall> balls = ballVsBalls.get(ball);
			for (GolfBall curBall : balls) {
				Vector3 normal = curBall.getCollisionNormal(ball);
				normals.add(normal);
			}

			ball.bounce(normals);
		}
	}
		
}
