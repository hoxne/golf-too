/**
 * Created by Michael on 05.05.2016.
 */

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.BoundingBox;
import org.lwjgl.Sys;

import java.util.ArrayList;

public class GolfBall {
    private static float FRICTION_COEFFICIENT = 0.5f;
    private static float BOUNCINESS = 0.30f;

    private Vector3 position;
    private Vector3 velocity;
    private float radius;
    private float mass;
    public BoundingBox boundingBox;
    private Vector3 gravity;
    public boolean isAi = false;

    public GolfBall(Vector3 startPos, Vector3 velocity, float mass, float radius) {
        this.position = startPos.cpy();
        this.velocity = velocity.cpy();
        this.mass = mass;
        this.radius = radius;
        this.boundingBox = new BoundingBox();
        updateBoundingBox();

        gravity = new Vector3(0, -5, 0);

    }

    public void updateBoundingBox() {
        // reset
        boundingBox.set(position, position);
        // add current position
        boundingBox.ext(position, radius);
        // add next position
        Vector3 nextPos = velocity.cpy().scl(PhysicsManager.FIXED_DT).add(position);   
        boundingBox.ext(nextPos, radius);
    }

    @Override
    public GolfBall clone() {
        GolfBall cloned = new GolfBall(this.position, this.velocity, this.mass, this.radius);
        cloned.isAi = isAi;
        return cloned;
    }

    public void update(float deltaTime){
        position.add(velocity.cpy().scl(deltaTime));
        updateBoundingBox();

        // TODO: fix this
        // the ball keeps bouncing higher because gravity gets applied right before the bounce every time (because of the new system)
        applyGravity(deltaTime);
        updateBoundingBox();
    }

    public void setVelocity(Vector3 velocity) { this.velocity = velocity.cpy(); }

    public float getMass() { return mass; }

    private void applyGravity(float deltaTime) {
        // if (velocity.len() > 0.01)
            this.velocity.add(gravity.cpy().scl(deltaTime));
            // System.out.println("g " + gravity.cpy().scl(deltaTime));
        // else
            // velocity = new Vector3(0,0,0);
    }

    private void applyFriction(Vector3 normal) {
        Vector3 vUp = normal.cpy().scl((velocity.dot(normal)));
        Vector3 vSide = velocity.cpy().sub(vUp);

        Vector3 friction = vSide.cpy().nor().scl(-1 * FRICTION_COEFFICIENT);
        friction.scl(vUp.len());
        // System.out.println(dv);
        // if(vSide.len2() > friction.len2()){
            velocity.add(friction);
        // }else{
            // velocity.sub(vSide);
        // }
    }

    public void bounce(ArrayList<Vector3> normals){
        Vector3 normal = new Vector3();

        for (Vector3 vect : normals) {
            normal.add(vect);
        }
        normal.nor();

        // System.out.println("v " + velocity);
        Vector3 componentA = normal.cpy().scl((velocity.dot(normal)));
        Vector3 componentB = velocity.cpy().sub(componentA);
        componentA.scl(BOUNCINESS);
        this.velocity = componentB.cpy().sub(componentA);
        // System.out.println("v " + velocity);


        // friction
        applyFriction(normal);

    }

    public void kick(Vector3 dv) {
        float l = dv.len();
        if(l > 5.0f){
            dv.scl(5.0f/l);
        }
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

    public Vector3 getVelocity() { return velocity; }

    public Vector3 getCollisionNormal(GolfBall ball) {
        Vector3 start = this.position;
        Vector3 normal = ball.position.cpy();
        normal.sub(start).nor();

        return  normal;
    }
}
