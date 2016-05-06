/**
 * Created by Michael on 05.05.2016.
 */

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.BoundingBox;

public class GolfBall {
    private static float FRICTION_COEFFICIENT = 10;

    private Vector3 position;
    private Vector3 velocity;
    private float radius;
    private float mass;
    public BoundingBox boundingBox;
    private Vector3 gravity;

    public GolfBall(Vector3 startPos, Vector3 velocity, float mass, float radius) {
        this.position = startPos;
        this.velocity = velocity;
        this.mass = mass;
        this.radius = radius;

	    this.position = startPos.cpy();
		
	    Vector3 min = startPos.cpy();
	    Vector3 max = startPos.cpy();
	    min.add(-radius);
	    max.add(radius);
		
	    this.boundingBox = new BoundingBox(min, max);

        gravity = new Vector3(0, -1, 0);
    }

    public GolfBall(GolfBall b) {
        this(b.position, b.velocity, b.mass, b.radius);
    }

    public void update(float deltaTime){
        this.position.set(new Vector3(this.position.add(this.velocity.scl(deltaTime))));
        applyFriction(deltaTime);
        applyGravity(deltaTime);
    }

    private void applyGravity(float deltaTime) {
        this.gravity.scl((float)0.95);
        this.velocity.add(gravity);
    }

    private void applyFriction(float deltaTime) {
        // friction
        if(velocity.len() > 0){
            double friction = FRICTION_COEFFICIENT * mass;
            Vector3 frictionForce = velocity.cpy();
            frictionForce.scl(-1);
            frictionForce.nor();
            frictionForce.scl((float)friction);
            Vector3 dv = frictionForce.cpy();
            dv.scl(deltaTime / mass);
            velocity.add(dv);
        }
    }

    public void bounce(Vector3 normal){
        Vector3 componentA = normal.scl((velocity.dot(normal)));
        Vector3 componentB = velocity.cpy();
        componentB.sub(componentA);
        this.velocity = componentB.sub(componentA);
    }

    public void kick(Vector3 dv) {
        this.velocity.add(dv);
    }

    public Vector3 getPosition() {
        return this.position;
    }

    public void setPosition(Vector3 newPos) {
        this.position = newPos;
    }

    public float getRadius() {
        return this.radius;
    }

    public void setRadius(float newRadius) {
        this.radius = newRadius;
    }

}
