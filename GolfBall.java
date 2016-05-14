/**
 * Created by Michael on 05.05.2016.
 */

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.BoundingBox;
import org.lwjgl.Sys;

import java.util.ArrayList;

public class GolfBall {
    private static float FRICTION_COEFFICIENT = 10;

    private Vector3 position;
    private Vector3 velocity;
    private float radius;
    private float mass;
    public BoundingBox boundingBox;
    private Vector3 gravity;

    public GolfBall(Vector3 startPos, Vector3 velocity, float mass, float radius) {
        this.position = startPos.cpy();
        this.velocity = velocity.cpy();
        this.mass = mass;
        this.radius = radius;
        updateBoundingBox();

        gravity = new Vector3(0, -5, 0);
    }

    public void updateBoundingBox() {
        Vector3 min = position.cpy();
        Vector3 max = position.cpy();
        min.add(-radius);
        max.add(radius);
       // max.add(radius);


        this.boundingBox = new BoundingBox(min, max);
       // System.out.println("Ball position: " + this.getPosition());
        //System.out.println("Bounding box positon: " + this.boundingBox.toString());

    }

    @Override
    public GolfBall clone() {
        return new GolfBall(this.position, this.velocity, this.mass, this.radius);
    }

    public void update(float deltaTime){
        //this.position.set(new Vector3(this.position.add(this.velocity.scl(deltaTime))));

        this.position.add(this.velocity.cpy().scl(deltaTime));
        updateBoundingBox();
        //applyFriction(deltaTime);
        applyGravity(deltaTime);
    }

    private void applyGravity(float deltaTime) {
        if (velocity.len() > 0.01)
            this.velocity.add(gravity.cpy().scl(deltaTime));

        else
            velocity = new Vector3(0,0,0);
    }

    private void applyFriction(float deltaTime) {
        // NOTE: this will apply a constant force on the pass in the opposite direction of the velocity vector
        //       no matter where it is, in the air or on the ground

        if(velocity.len() > 0.01){
            double friction = FRICTION_COEFFICIENT * mass;
            Vector3 frictionForce = velocity.cpy();
            frictionForce.scl(-1);
            frictionForce.nor();
            frictionForce.scl((float)friction);
            Vector3 dv = frictionForce.cpy();
            dv.scl(deltaTime / mass);
            velocity.add(dv);
        }

        else
            velocity = new Vector3(0,0,0);
    }

    public void bounce(ArrayList<Vector3> normals, float deltaTime){
        applyFriction(deltaTime);
        Vector3 normal = new Vector3();

        System.out.println("Velocity before bounce: " + this.velocity.toString());

        for (Vector3 vect : normals) {
            normal.add(vect);
        }
        normal.nor();

        System.out.println("Bouncing normal: " + normal.toString());

        Vector3 componentA = normal.scl((velocity.dot(normal)));
        Vector3 componentB = velocity.cpy();
        componentB.sub(componentA);
        this.velocity = componentB.sub(componentA);

        System.out.println("Velocity after bounce: " + this.velocity.toString());
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

    public Vector3 getCollisionNormal(GolfBall ball) {
        Vector3 start = this.position;
        Vector3 normal = ball.position.cpy();
        normal.sub(start).nor();

        return  normal;
    }

}
