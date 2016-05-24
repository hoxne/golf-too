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

	// NOTE: maybe we should add some 'working memory' vectors to this class 
	//       so we can re-use those instead of allocating new ones every time this function is called
	public static boolean collideTriangle(Vector3 aVector, Vector3 bVector, Vector3 cVector, GolfBall ball) {
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
