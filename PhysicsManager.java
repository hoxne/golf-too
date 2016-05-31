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
	
	public ArrayList<CollisionObject> getBallBoundingBoxCollisions(GolfBall b) {
		ArrayList<CollisionObject> intersects = new ArrayList<>();
		for(CollisionObject co : this.colObjs) {
			if(b.boundingBox.intersects(co.boundingBox)) {
				intersects.add(co);
			}
		}
		// System.out.println(intersects.size() + " / " + colObjs.size());
		return intersects;
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
		for (GolfBall ball : balls) {
			float processedT = 0.0f;
			while(processedT < 1.0f){
				float t = 1.0f - processedT;
				float a = 1.0f - processedT;
				Vector3 normal = null;
				ArrayList<CollisionObject> collisionObjects = getBallBoundingBoxCollisions(ball);

				for (CollisionObject collisionObject : collisionObjects) {
					Vector3 n = new Vector3();
					float newT = collisionObject.getCollision(ball, t, n);
					if (newT < t){
						t = newT;
						normal = n;
					}
				}
				if(normal != null){
					ArrayList<Vector3> normals = new ArrayList<>();
					normals.add(normal);
					ball.bounce(normals);
				}

				ball.update(dt*t*1.0f);
       			// System.out.println(velocity.len());
				if(t > a){
					System.out.println("EVERYITNHG IS BORKED");
				}

				processedT += t;
				// System.out.println(processedT);
			}
			// System.out.println(processedT);
		}
	}

	// public static float FIXED_DT = 1f/600;
	public static float FIXED_DT = 1f/600;
	public static float SUPER_HOT = 1.0f;
	private float time = 0.0f;

	public void update(float dt) {
		time += dt*SUPER_HOT;
		while(time > FIXED_DT) {
			time -= FIXED_DT;
			_update(FIXED_DT);
		}
	}

	public ArrayList<GolfBall> getBalls() {
		return balls;
	}
		
}
