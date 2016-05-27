import java.util.ArrayList;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.collision.BoundingBox;

public class CollisionObject {

	public Vector3[] triangles;
	// NOTE: a normal array would be better here I think, the array never changes and we know the size in advance
	public ArrayList<Vector3> trianglesNormals;
	public BoundingBox boundingBox;

	public CollisionObject(Vector3[] triangles) {
		this.triangles = triangles;
		this.boundingBox = new BoundingBox();
		this.boundingBox.set(triangles);
		this.boundingBox.ext(0.5f, 0.5f, 0.5f);
		this.trianglesNormals = findNormals();
	}

	private ArrayList<Vector3> findNormals() {

		ArrayList<Vector3> normals = new ArrayList<Vector3>();

		for(int i = 0; i < triangles.length-2; i+=3) {

			Vector3 p1p0 = triangles[i+1].cpy().sub(triangles[i]);
			Vector3 p2p0 = triangles[i+2].cpy().sub(triangles[i]);
			normals.add(p1p0.crs(p2p0));
		}

		return normals;
	}

	public Vector3 getNormal(GolfBall ball){
		Vector3[] collidingTriangle = this.collide(ball);
		if (collidingTriangle == null){
			return null;
		}else{
			Vector3 normal = new Vector3(0, 0, 0);
			for (int i = 0; i < collidingTriangle.length - 2 ; i+=3){
				Vector3 p1p0 = collidingTriangle[i+1].cpy().sub(collidingTriangle[i]);
				Vector3 p2p0 = collidingTriangle[i+2].cpy().sub(collidingTriangle[i]);
				normal.add(p1p0.crs(p2p0).nor());
			}
			return normal.nor();
		}

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

	private static boolean isInTriangle(Vector3 p, Vector3 t0, Vector3 t1, Vector3 t2){
		Vector3 p0 = t0.cpy().sub(p).nor();
		Vector3 p1 = t1.cpy().sub(p).nor();
		Vector3 p2 = t2.cpy().sub(p).nor();

		float a = p0.dot(p1);
		float b = p1.dot(p2);
		float c = p2.dot(p0);

		float angle = (float)Math.acos(a) + (float)Math.acos(b) + (float)Math.acos(c);
		return Math.abs(angle - (2*Math.PI)) < 0.01;
	}

	public static float sweptCollideTriangle(Vector3 a, Vector3 b, Vector3 c, GolfBall ball){
		return sweptCollideTriangle(a,b,c,ball,1.0f);
	}


	public static float sweptCollideTriangle(Vector3 a, Vector3 b, Vector3 c, GolfBall ball, float maxT){
		// scale everything so we have a unit sphere
		float invR = 1f/ball.getRadius();
		Vector3 start = ball.getPosition().cpy().scl(invR);
		Vector3 v = ball.getVelocity().cpy().scl(invR);
		Vector3 normV = v.cpy().nor();

		Vector3 p1 = a.cpy().scl(invR);
		Vector3 p2 = b.cpy().scl(invR);
		Vector3 p3 = c.cpy().scl(invR);

		// calculate triangle plane 
		Plane tPlane = new Plane(p1, p2, p3);
		// flip plane if it's pointing away from the ball
		if(tPlane.normal.dot(normV) >= 0.0f){
			tPlane.normal.scl(-1);
			tPlane.d = -tPlane.d;
		}

		// distance to plane
		float planeDist = tPlane.distance(start);
		// System.out.println(planeDist > 0);

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
            // if (Intersector.isPointInTriangle(planeIntersect, p1, p2, p3)) {
            if (isInTriangle(planeIntersect, p1, p2, p3)) {
               	return t0;
            }
        }

        // if we haven't found a collision by now we have to check all points and edges of the triangle
        // float v2 = v.len2();
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

	// NOTE: maybe we should add some 'working memory' vectors to this class 
	//       so we can re-use those instead of allocating new ones every time this function is called
	public static boolean collideTriangle(Vector3 aVector, Vector3 bVector, Vector3 cVector, GolfBall ball) {
		// float t = sweptCollideTriangle(aVector, bVector, cVector, ball);
		// // if(t == Float.POSITIVE_INFINITY)
		// 	// return false;
		// // System.out.println(t);
		// return t <= 1.0f;

		Vector3 A = aVector.cpy().sub(ball.getPosition());
		Vector3 B = bVector.cpy().sub(ball.getPosition());
		Vector3 C = cVector.cpy().sub(ball.getPosition());

		float rr = ball.getRadius()*ball.getRadius();

		// check distance to triangle plane
		Vector3 V = (B.cpy().sub(A)).crs(C.cpy().sub(A));
		float d = A.dot(V);
		float e = V.dot(V);

		if(d*d > rr*e)
			return false;


		// check vertices
		float aa = A.dot(A);
		float ab = A.dot(B);
		float ac = A.dot(C);
		float bb = B.dot(B);
		float bc = B.dot(C);
		float cc = C.dot(C);

		boolean sepA = (aa > rr) && (ab > aa) && (ac > aa);
		boolean sepB = (bb > rr) && (ab > bb) && (bc > bb);
		boolean sepC = (cc > rr) && (ac > cc) && (bc > cc);

		if(sepA || sepB || sepC)
			return false;

		// check edges
		Vector3 AB = B.cpy().sub(A);
		Vector3 BC = C.cpy().sub(B);
		Vector3 CA = A.cpy().sub(C);

		float d1 = ab - aa;
		float d2 = bc - bb;
		float d3 = ac - cc;

		float e1 = AB.dot(AB);
		float e2 = BC.dot(BC);
		float e3 = CA.dot(CA);

		Vector3 Q1 = A.cpy().scl(e1).sub(AB.cpy().scl(d1));
		Vector3 Q2 = B.cpy().scl(e2).sub(BC.cpy().scl(d2));
		Vector3 Q3 = C.cpy().scl(e3).sub(CA.cpy().scl(d3));

		Vector3 QC = C.cpy().scl(e1).sub(Q1);
		Vector3 QA = A.cpy().scl(e2).sub(Q2);
		Vector3 QB = B.cpy().scl(e3).sub(Q3);

		boolean sepAB = (Q1.dot(Q1) > rr * e1 * e1) && (Q1.dot(QC) > 0);
		boolean sepBC = (Q2.dot(Q2) > rr * e2 * e2) && (Q2.dot(QA) > 0);
		boolean sepCA = (Q3.dot(Q3) > rr * e3 * e3) && (Q3.dot(QB) > 0);

		if(sepAB || sepBC || sepCA)
			return false;

		// if all else fails...
		return true;
	}

	// NOTE: update this so it returns all colliding triangles instead of only one
	//       this is important to properly collide with corners
	public Vector3[] collide(GolfBall ball) {
		ArrayList<Vector3> collidingTriangles = new ArrayList<>();
		for (int i = 0; i < this.triangles.length - 2; i += 3) {
			if (collideTriangle(triangles[i], triangles[i + 1], triangles[i + 2], ball)) {
				collidingTriangles.add(triangles[i]);
				collidingTriangles.add(triangles[i + 1]);
				collidingTriangles.add(triangles[i + 2]);
			}
		}

		if(collidingTriangles.size() > 0){
			// System.out.println("Colliding with: " + collidingTriangles);
			return collidingTriangles.toArray(new Vector3[0]);
		}else{
			return null;
		}
	}


	// public static void main(String[] args) {
	// 	Vector3 a = new Vector3(5.4249997f,0.0f,5.65f);
	// 	Vector3 b = new Vector3(5.4249997f,-0.5f,5.65f);
	// 	Vector3 c = new Vector3(5.35f,-0.5f,5.5750003f);

	// 	Vector3 p = new Vector3(4.264173f,0.34265694f,4.264173f);

	// 	GolfBall ball = new GolfBall(p, new Vector3(), 1f, 0.2f);

	// 	boolean colliding = collideTriangle(a, b, c, ball);

	// 	System.out.println(colliding);
	// }

}
