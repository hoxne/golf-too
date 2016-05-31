import java.util.ArrayList;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.collision.BoundingBox;

public class CollisionObject {

	public Vector3[] triangles;
	public BoundingBox boundingBox;

	public CollisionObject(Vector3[] triangles) {
		this.triangles = triangles;
		this.boundingBox = new BoundingBox();
		this.boundingBox.set(triangles);
		// this.boundingBox.ext(0.1f, 0.1f, 0.1f);
	}

	// normal is a reference in which this funtion will store the result
	public float getCollision(GolfBall ball, float maxT, Vector3 normal){
		float t = maxT;
		normal.set(0,0,0);

		for (int i = 0; i < this.triangles.length - 2; i += 3) {
			float newT = sweptCollideTriangle(triangles[i], triangles[i + 1], triangles[i + 2], ball, t);
			if (newT < t) {
				t = newT;
				Vector3 p1p0 = triangles[i+1].cpy().sub(triangles[i]);
				Vector3 p2p0 = triangles[i+2].cpy().sub(triangles[i]);
				normal.set(p1p0.crs(p2p0).nor());
			}
		}
		return t;
	}

	// sources:
	// http://www.peroxide.dk/papers/collision/collision.pdf
	// https://gist.github.com/toji/2802287
	private static float getLowestRoot(float a, float b, float c, float max){
		float det = b*b-4*a*c;
		if(det < 0){
			return Float.NaN;
		}
		float sqrtdet = (float)Math.sqrt(det);
		float r1 = (-b-sqrtdet) / (2*a);
		float r2 = (-b+sqrtdet) / (2*a);
		if(r1 > r2){
			float temp = r2; r2 = r1; r1 = temp;
		}
		if(r1 > 0 && r1 < max){
			return r1;
		}
		if(r2 > 0 && r2 < max){
			return r2;
		}
		return Float.NaN;

	}
	private static float testVertex(Vector3 p, float t, Vector3 start, Vector3 v){
		Vector3 dx = start.cpy().sub(p);
		float a = v.len2();
		float b = 2*v.dot(dx);
		float c = dx.len2()-1;
		float r = getLowestRoot(a,b,c,t);
		return (!Float.isNaN(r)) ? r : t;
	}

	private static float testEdge(Vector3 pa, Vector3 pb, float t, Vector3 start, Vector3 v){
		Vector3 edge = pb.cpy().sub(pa);
		Vector3 dx = pa.cpy().sub(start);

		float edgelen2 = edge.len2();
		float vlen2 = v.len2();
		float edgedotv = edge.dot(v);
		float edgedotdx = edge.dot(dx);

		float a = -edgelen2*vlen2 + edgedotv*edgedotv;
		float b = edgelen2*(2*v.dot(dx))-2*edgedotv*edgedotdx;
		float c = edgelen2*(1-dx.len2())+edgedotdx*edgedotdx;

		// check against infinite edge
		float r = getLowestRoot(a,b,c, t);
		if(!Float.isNaN(r) && r < t){
			// check against line segment
			float f = (edgedotv*r - edgedotdx) / edgelen2;
			if(f >= 0 && f <= 1){
				return r;
			}
		}
		return t;
	}

	public static float sweptCollideTriangle(Vector3 a, Vector3 b, Vector3 c, GolfBall ball){
		return sweptCollideTriangle(a,b,c,ball,1.0f);
	}

	public static float sweptCollideTriangle(Vector3 a, Vector3 b, Vector3 c, GolfBall ball, float maxT){
		// scale everything so we have a unit sphere
		float invR = 1f/ball.getRadius();
		Vector3 start = ball.getPosition().cpy().scl(invR);
		Vector3 v = ball.getVelocity().cpy().scl(invR);
		v.scl(PhysicsManager.FIXED_DT);
		Vector3 normV = v.cpy().nor();

		Vector3 p1 = a.cpy().scl(invR);
		Vector3 p2 = b.cpy().scl(invR);
		Vector3 p3 = c.cpy().scl(invR);

		// calculate triangle plane 
		Plane tPlane = new Plane(p1, p2, p3);
		if(tPlane.normal.dot(normV) >= 0.0f){
			// // flip plane if it's pointing away from the ball
			// tPlane.normal.scl(-1);
			// tPlane.d = -tPlane.d;

			// stop if the triangle is facing away from the ball
			return Float.POSITIVE_INFINITY;
		}

		// distance to plane
		float planeDist = tPlane.distance(start);

		float t0, t1;
		boolean inPlane = false;

		float normdotV = tPlane.normal.dot(v);

		// is sphere travelling parallel to plane?
		if(normdotV == 0.0f){
			// is sphere inside of the plane?
			if(Math.abs(planeDist) >= 1.0f){
				// no collision possible
				return Float.POSITIVE_INFINITY;
			}else{
				// sphere is embedded in plane
				inPlane = true;
				t0 = 0.0f;
				t1 = 1.0f;
			}
		}else{
			// // Calculate intersection interval
			t0 = (-1-planeDist)/normdotV;
			t1 = ( 1-planeDist)/normdotV;
			// Swap so t0 < t1
            if (t0 > t1) {
                float temp = t1; t1 = t0; t0 = temp;
            }
            // Check that at least one result is within range:
            if (t0 > 1.0f || t1 < 0.0f) {
                // No collision possible
                return Float.POSITIVE_INFINITY;
            }
            // clamp to [0,1]
            if (t0 < 0.0f) t0 = 0.0f;
            if (t1 < 0.0f) t1 = 0.0f;
            if (t0 > 1.0f) t0 = 1.0f;
            if (t1 > 1.0f) t1 = 1.0f;
		}

		// if the closest possible collision point is further away
        // than maxT  then there's no point in testing further.
        if(t0 >= maxT)
        	return Float.POSITIVE_INFINITY;

        // check for collision againt the triangle face:
        if (!inPlane) {
            // Calculate the intersection point with the plane
            Vector3 planeIntersect = start.cpy().sub(tPlane.normal).add(v.cpy().scl(t0));


            // Is that point inside the triangle?
            if (Intersector.isPointInTriangle(planeIntersect, p1, p2, p3)) {
            // if (isInTriangle(planeIntersect, p1, p2, p3)) {
               	return t0;
            }
        }

        // if we haven't found a collision by now we have to check all points and edges of the triangle
        float t = Float.POSITIVE_INFINITY;

        // check vertices
        t = testVertex(p1, t, start, v);
        t = testVertex(p2, t, start, v);
        t = testVertex(p3, t, start, v);

        t = testEdge(p1, p2, t, start, v);
        t = testEdge(p2, p3, t, start, v);
        t = testEdge(p3, p1, t, start, v);

        return t;
	}

}
