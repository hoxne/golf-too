import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PhysicsManager {

	private ArrayList<CollisionObject>		colObjs;
	private ArrayList<GolfBall> 			balls;
	
	PhysicsManager(ArrayList<CollisionObject> colObjs) {
		this.colObjs = colObjs;
		balls = new ArrayList<>();
	}

	public void addBall(GolfBall ball) {
		balls.add(ball);
	}

	public void removeBalls(){
		balls.clear();
	}

	public void addBalls(ArrayList<GolfBall> balls) {
		this.balls = balls;
	}

	public void setCollisionObjects(ArrayList<CollisionObject> colObjs){
		this.colObjs = colObjs;
	}

	@Override
	public PhysicsManager clone() {
		ArrayList<GolfBall> ballsDeepCopy = new ArrayList<GolfBall>();
		for(GolfBall b: this.balls)
			ballsDeepCopy.add(b.clone());




		PhysicsManager cloned = new PhysicsManager(colObjs);
		cloned.addBalls(ballsDeepCopy);
		
		return cloned;
	}
	
	public Map<GolfBall, ArrayList<CollisionObject>> getBallBoundingBoxCollisions() {
		
		Map<GolfBall, ArrayList<CollisionObject>> bbIntersects = new HashMap<GolfBall, ArrayList<CollisionObject>>();

		for(GolfBall b : this.balls) {
			ArrayList<CollisionObject> list = new ArrayList<>();
			bbIntersects.put(b, list);
			for(CollisionObject co : this.colObjs) {
				if(b.boundingBox.intersects(co.boundingBox)) {
					// System.out.println("bbouh");
					list.add(co);
				}
			}
		}

		return bbIntersects;
	}
	
	public ArrayList<GolfBall> getBallBallCollisions(float deltaTime) {
		ArrayList<GolfBall> bbIntersects = new ArrayList<GolfBall>();

		for (int i = 0; i < this.balls.size() - 1; i++) {
			for (int k = i + 1; k < this.balls.size(); k++) {
				   
				GolfBall b1 = balls.get(i);
				GolfBall b2 = balls.get(k);

				if(b1.getPosition().dst2(b2.getPosition()) <= (b1.getRadius() + b2.getRadius()) * (b1.getRadius() + b2.getRadius())){
					bbIntersects.add(b1);
					bbIntersects.add(b2);
			    }
			}
		}
		return bbIntersects;
	}

	private void _update(float dt){
		// ball-ball collisions
		ArrayList<GolfBall> ballVsBalls = getBallBallCollisions(dt);
		for (int i = 0; i < ballVsBalls.size(); i+=2) {
			GolfBall b1 = ballVsBalls.get(i);
			GolfBall b2 = ballVsBalls.get(i+1);

			Vector3 dv = b1.getVelocity().cpy().sub(b2.getVelocity());
			Vector3 dx = b1.getPosition().cpy().sub(b2.getPosition());

			b1.getVelocity().sub(dx.cpy().scl(dv.dot(dx) / dx.len2()));
			dv.scl(-1); dx.scl(-1);
			b2.getVelocity().sub(dx.cpy().scl(dv.dot(dx) / dx.len2()));
		}

		// terrain collisions
		Map<GolfBall, ArrayList<CollisionObject>> ballVsObjects = getBallBoundingBoxCollisions();
		for (GolfBall ball : balls) {
			ArrayList<CollisionObject> collisionObjects = ballVsObjects.get(ball);
			ArrayList<Vector3> normals = new ArrayList<>();
			for (CollisionObject collisionObject : collisionObjects) {
				Vector3 normal = collisionObject.getNormal(ball);
				if (normal != null)
					normals.add(normal);
			}

			if (normals.size() > 0) {
				ball.bounce(normals, dt);
			}
		}

		for (GolfBall ball : balls) {
			ball.update(dt);
		}
	}

	public static float FIXED_DT = 1f/600;
	// private static float FIXED_DT = 1f/60;
	private float time = 0.0f;

	public void update(float dt) {
		time += dt;
		while(time > FIXED_DT) {
			time -= FIXED_DT;
			_update(FIXED_DT);
		}
	}

	public ArrayList<GolfBall> getBalls() {
		return balls;
	}
		
}
