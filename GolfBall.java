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
    private static float FRICTION_COEFFICIENT = 10f;
    private static float BOUNCINESS = 0.05f;

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
        GolfBall cloned = new GolfBall(this.position, this.velocity, this.mass, this.radius);
        cloned.isAi = isAi;
        return cloned;
    }

    public void update(float deltaTime){
        //this.position.set(new Vector3(this.position.add(this.velocity.scl(deltaTime))));

        applyGravity(deltaTime);

        this.position.add(this.velocity.cpy().scl(deltaTime));

        updateBoundingBox();
    }

    public void setVelocity(Vector3 velocity) { this.velocity = velocity.cpy(); }

    public float getMass() { return mass; }

    private void applyGravity(float deltaTime) {
        // if (velocity.len() > 0.01)
            this.velocity.add(gravity.cpy().scl(deltaTime));
        // else
            // velocity = new Vector3(0,0,0);
    }

    private void applyFriction(Vector3 normal, float dt) {
        if(velocity.len() > 0.01){
            double friction = FRICTION_COEFFICIENT;
            Vector3 frictionForce = velocity.cpy();
            Vector3 velocityUp = normal.cpy().scl((velocity.dot(normal)));
            frictionForce.scl(-1);
            frictionForce.nor();
            frictionForce.scl((float)friction);
            frictionForce.scl(velocityUp.len());
            Vector3 dv = frictionForce.cpy();
            dv.scl(dt*10);
            // dv.scl(dt);
            // System.out.println(dv);
            velocity.add(dv);
            
            // Vector3 velocityUp = normal.cpy().scl((velocity.dot(normal)));
            // Vector3 velocityTan = velocity.cpy().sub(velocityUp);
            // Vector3 friction = velocityUp.cpy();//.scl(BOUNCINESS);
            // Vector3 constantF = velocityUp.cpy().nor().scl(0.1f);
            // friction.add(constantF);
            // // velocityUp
            // Vector3 newV = velocity.cpy().sub(friction);
            // if(newV.len2() < velocity.len2())
            //     velocity = newV;
        }else{
            velocity = new Vector3(0,0,0);
        }
    }

    public void bounce(ArrayList<Vector3> normals, float deltaTime){
        Vector3 normal = new Vector3();

        // System.out.println("Position: " + this.position);
        // System.out.println("Velocity before bounce: " + this.velocity.toString());

        for (Vector3 vect : normals) {
            normal.add(vect);
            // System.out.println("\t" + vect.toString());
        }
        normal.nor();

        // System.out.println("Bouncing normal: " + normal.toString());

        Vector3 oldV = velocity.cpy();

        Vector3 componentA = normal.cpy().scl((velocity.dot(normal)));
        Vector3 componentB = velocity.cpy().sub(componentA);
        // componentA.scl(BOUNCINESS);
        this.velocity = componentB.cpy().sub(componentA);

        // this.position.add(oldV.cpy().scl(-deltaTime));
        this.position.add(this.velocity.cpy().scl(deltaTime));

        // friction
        applyFriction(normal, deltaTime);

        // System.out.println("Velocity after bounce: " + this.velocity.toString());
        // System.out.println();
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
