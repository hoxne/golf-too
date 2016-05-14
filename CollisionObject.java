import java.util.ArrayList;

import com.badlogic.gdx.math.Vector3;
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
		this.trianglesNormals = findNormals();
	}

	private ArrayList<Vector3> findNormals() {

		ArrayList<Vector3> normals = new ArrayList<Vector3>();

		for(int i = 0; i < triangles.length-2; i+=3) {

			Vector3 p1p0 = triangles[i+1].cpy().sub(triangles[i]).nor();
			Vector3 p2p0 = triangles[i+2].cpy().sub(triangles[i]).nor();
			normals.add(p1p0.crs(p2p0));
		}

		return normals;
	}

	public Vector3 getNormal(GolfBall ball){
		Vector3[] collidingTriangle = this.collide(ball);
		if (collidingTriangle == null)
			return null;

		Vector3 p1p0 = collidingTriangle[1].sub(collidingTriangle[0]).nor();
		Vector3 p2p0 = collidingTriangle[2].sub(collidingTriangle[0]).nor();
		return p1p0.crs(p2p0);
	}

	// NOTE: maybe we should add some 'working memory' vectors to this class 
	//       so we can re-use those instead of allocating new ones every time this function is called
	public boolean collideTriangle(Vector3 aVector, Vector3 bVector, Vector3 cVector, GolfBall ball) {
		Vector3 A = aVector.cpy();
		Vector3 B = bVector.cpy();
		Vector3 C = bVector.cpy();
		A.sub(ball.getPosition());
		B.sub(ball.getPosition());
		C.sub(ball.getPosition());
		float rr = ball.getRadius()*ball.getRadius();

		Vector3 V = B.cpy();
		Vector3 c = C.cpy();
		V = (V.sub(A)).crs(c.sub(A));

		float d = A.dot(V);
		float e = V.dot(V);
		boolean sep1 = d*d > rr*e;

		float aa = A.dot(A);
		float ab = A.dot(B);
		float ac = A.dot(C);
		float bb = B.dot(B);
		float bc = B.dot(C);
		float cc = C.dot(C);

		boolean sep2 = (aa > rr) && (ab > aa) && (ac > aa);
		boolean sep3 = (bb > rr) && (ab > bb) && (bc > bb);
		boolean sep4 = (cc > rr) && (ac > cc) && (bc > cc);

		Vector3 AB = B.cpy();
		Vector3 BC = C.cpy();
		Vector3 CA = C.cpy();
		AB.sub(A);
		BC.sub(B);
		CA.sub(A);

		float d1 = ab - aa;
		float d2 = bc - bb;
		float d3 = ac - cc;

		float e1 = AB.dot(AB);
		float e2 = BC.dot(BC);
		float e3 = CA.dot(CA);

		Vector3 a = A.cpy();
		Vector3 aB = AB.cpy();
		Vector3 Q1 = (a.scl(e1)).sub(aB.scl(d1));
		Vector3 b = B.cpy();
		Vector3 bC = BC.cpy();
		Vector3 Q2 = (b.scl(e2)).sub(bC.scl(d2));
		c = C.cpy();
		Vector3 ca = CA.cpy();
		Vector3 Q3 = (c.scl(e3)).sub(ca.scl(d3));

		Vector3 QC = (C.scl(e1)).sub(Q1);
		Vector3 QA = (A.scl(e2)).sub(Q2);
		Vector3 QB = (B.scl(e3)).sub(Q3);

		boolean sep5 = (Q1.dot(Q1) > rr * e1 * e1) && (Q1.dot(QC) > 0);
		boolean sep6 = (Q2.dot(Q2) > rr * e2 * e2) && (Q2.dot(QA) > 0);
		boolean sep7 = (Q3.dot(Q3) > rr * e3 * e3) && (Q3.dot(QB) > 0);

		boolean separated = !(sep1 || sep2 || sep3 || sep4 || sep5 || sep6 || sep7);

		return separated;
	}

	// NOTE: update this so it returns all colliding triangles instead of only one
	//       this is important to properly collide with corners
	public Vector3[] collide(GolfBall ball) {
		for (int i = 0; i < this.triangles.length - 2; i += 3) {
			if (collideTriangle(triangles[i], triangles[i + 1], triangles[i + 2], ball)) {
				Vector3[] triangle = new Vector3[3];
				triangle[0] = triangles[i];
				triangle[1] = triangles[i + 1];
				triangle[2] = triangles[i + 2];
				return triangle;
			}
		}

		return null;
	}

}
