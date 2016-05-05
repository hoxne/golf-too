// import javax.json.*;

import java.io.*;
import java.lang.Math;
import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;


public class Course implements Disposable {

	static final int MIN_HEIGHT = -3;
	static final int MAX_HEIGHT = 4;
	static final float HEIGHT_SCALE = 1.0f/(MAX_HEIGHT-MIN_HEIGHT);

	protected String path, name, desc;
	// the width and height are how many tiles there are
	// so, there are width+1 by height+1 vertices
	protected int width, height;
	protected Vector2 startpos, holepos;
	protected ArrayList<Obstacle> obstacles;
	// array to store the height values
	// 0 is the default ground level
	protected int[][] heightmap;
	// boolean array, determining if a tile is part of the map or not
	// i.e. this determines the shape of the map
	protected boolean[][] isOutside;

	// mesh stuff
	protected Color color;
	protected Mesh mesh;
	private float[] vertices;
	private short[] indices;
	// keeps track of the position in vertex/index arrays
	private int index;
	private int stride;
	private int posPos;
	private int norPos;
	private int colPos;

	Course(int width, int height){
		this.width = width;
		this.height = height;
		this.startpos = new Vector2(1, 1);
		this.holepos = new Vector2(7, 7);
		this.obstacles = new ArrayList<>();
		this.heightmap = new int[width+1][height+1];
		this.isOutside = new boolean[width][height];

		// mesh stuff
		// set default color (grass-green)
		this.color = new Color(0.4f, 0.8f, 0.2f, 1.0f);
		// attributes stored at each vertex
		VertexAttributes attributes = MeshBuilder.createAttributes(Usage.Position | Usage.Normal | Usage.ColorUnpacked);
		// offsets for the atributes, needed to see what's what in the vertex array
		this.posPos = attributes.getOffset(Usage.Position, -1);
		this.norPos = attributes.getOffset(Usage.Normal, -1);
		this.colPos = attributes.getOffset(Usage.ColorUnpacked, -1);

		// number of floats per vertex
		this.stride = attributes.vertexSize / 4;

		int numVertices = 6 * width*height;
		int numIndices = 6 * width*height;

		// make new static mesh
		this.mesh = new Mesh(true, numVertices, numIndices, attributes);
		// allocate array to store vertex data
		this.vertices = new float[numVertices * stride];
		// allocate arrar to store indices
		this.indices = new short[numIndices];
	}

	public void setCorner(int x, int y, int h){
		if(x >= 0 && x < width+1 && y >= 0 && y < height+1 && heightmap[x][y] < MAX_HEIGHT)
			heightmap[x][y] = (h > MAX_HEIGHT) ? MAX_HEIGHT : (h < MIN_HEIGHT ? MIN_HEIGHT : h);
	}

	public void raiseCorner(int x, int y){
		if(x >= 0 && x < width+1 && y >= 0 && y < height+1 && heightmap[x][y] < MAX_HEIGHT)
			heightmap[x][y]++;
	}

	public void lowerCorner(int x, int y){
		if(x >= 0 && x < width+1 && y >= 0 && y < height+1 && heightmap[x][y] > MIN_HEIGHT)
			heightmap[x][y]--;
	}

	public void setTileInMap(int x, int y, boolean isInMap){
		if(x >= 0 && x < width && y >= 0 && y < height)
			isOutside[x][y] = !isInMap;
	}

	public void setStartPosition(Vector2 pos){
		if(pos.x >= 0 && pos.x < width && pos.y >= 0 && pos.y < height)
			this.startpos = pos;
	}
	public void setHolePosition(Vector2 pos){
		if(pos.x >= 0 && pos.x < width && pos.y >= 0 && pos.y < height)
			this.holepos = pos;
	}



	// v v   MESH STUFF   v v

	// adds a vertex to the vertex and index array, given a MeshPartBuilder.VertexInfo
	private void addVertex(MeshPartBuilder.VertexInfo v){
		// add the vertex to the index array
		this.indices[index] = (short)(index);

		// add the vertex to the vertex array
		int vindex = index*stride;
		// position
		vertices[vindex + posPos + 0] = v.position.x;
		vertices[vindex + posPos + 1] = v.position.y;
		vertices[vindex + posPos + 2] = v.position.z;
		// normal
		vertices[vindex + norPos + 0] = v.normal.x;
		vertices[vindex + norPos + 1] = v.normal.y;
		vertices[vindex + norPos + 2] = v.normal.z;
		// color
		vertices[vindex + colPos + 0] = v.color.r;
		vertices[vindex + colPos + 1] = v.color.g;
		vertices[vindex + colPos + 2] = v.color.b;
		vertices[vindex + colPos + 3] = v.color.a;

		index++;
	}

	// function to generate a triangle from 3 vertices and add it to the vertex/index arrays
	// it assumes the position of the 3 vertices is already set
	// note: keep winding order in mind
	private void generateTriangle(MeshPartBuilder.VertexInfo v0, MeshPartBuilder.VertexInfo v1, MeshPartBuilder.VertexInfo v2){
		// temp vector, to avoid excessive allocations
		Vector3 tmpV = new Vector3();
		// calculate normals
		v0.normal.set(v0.position).sub(v1.position).nor().crs(tmpV.set(v0.position).sub(v2.position).nor());//.scl(-1);
		v1.normal.set(v1.position).sub(v2.position).nor().crs(tmpV.set(v1.position).sub(v0.position).nor());//.scl(-1);
		v2.normal.set(v2.position).sub(v0.position).nor().crs(tmpV.set(v2.position).sub(v1.position).nor());//.scl(-1);
		// add the vertices to the array
		this.addVertex(v0);
		this.addVertex(v1);
		this.addVertex(v2);
	}

	// updates the whole mesh, based on the heightmap and isTerrain array
	public void updateMesh(){
		// reset index to overwrite the arrays
		this.index = 0;
		// buffer variables to store temporary vertex data
		// to avoid excessive allocations
		MeshPartBuilder.VertexInfo v00 = new MeshPartBuilder.VertexInfo();
		MeshPartBuilder.VertexInfo v10 = new MeshPartBuilder.VertexInfo();
		MeshPartBuilder.VertexInfo v01 = new MeshPartBuilder.VertexInfo();
		MeshPartBuilder.VertexInfo v11 = new MeshPartBuilder.VertexInfo();

		// for each tile
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				// skip tile if it's outise of the map
				if(isOutside[x][y])
					continue;

				// get heights at four corners
				int h00 = heightmap[x][y];
				int h10 = heightmap[x+1][y];
				int h01 = heightmap[x][y+1];
				int h11 = heightmap[x+1][y+1];

				// set positions
				v00.position.set(x,HEIGHT_SCALE*h00,y);
				v10.position.set(x+1,HEIGHT_SCALE*h10,y);
				v01.position.set(x,HEIGHT_SCALE*h01,y+1);
				v11.position.set(x+1,HEIGHT_SCALE*h11,y+1);

				// set colors
				// lower vertices are darker, higher ones are lighter
				float dark = 0.2f;
				float light = 0.5f;
				v00.color.set(this.color).mul((1f*h00-MIN_HEIGHT)/(MAX_HEIGHT-MIN_HEIGHT)*(light-dark)+dark);
				v10.color.set(this.color).mul((1f*h10-MIN_HEIGHT)/(MAX_HEIGHT-MIN_HEIGHT)*(light-dark)+dark);
				v01.color.set(this.color).mul((1f*h01-MIN_HEIGHT)/(MAX_HEIGHT-MIN_HEIGHT)*(light-dark)+dark);
				v11.color.set(this.color).mul((1f*h11-MIN_HEIGHT)/(MAX_HEIGHT-MIN_HEIGHT)*(light-dark)+dark);

				// check which direction the diagonal should be
				// this is to make everything look consistent and independent of rotation

				// calculate the slope for both diagonals (i.e. the difference in height)
				int d1 = Math.abs(h00-h11);
				int d2 = Math.abs(h01-h10);
				// pick the digonal with the highest slope
				if(d1 > d2){
					// first triangle (00 - 01 - 10)
					generateTriangle(v00,v01,v10);
					// second triangle (10 - 01 - 11)
					generateTriangle(v10,v01,v11);
				}else{
					// first triangle (00 - 11 - 10)
					generateTriangle(v00,v11,v10);
					// second triangle (00 - 01 - 11)
					generateTriangle(v00,v01,v11);
				}
			}	
		}
		// pass data to mesh
		mesh.setVertices(vertices);
		mesh.setIndices(indices);
	}

	// public void load(String path){
	// 	this.path = path;
	// 	try {
	// 		JsonReader jsonReader = Json.createReader(new FileReader(path));
	// 		JsonObject obj = jsonReader.readObject();

	// 		this.name = obj.getJsonString("name").getString();
	// 		this.desc = obj.getJsonString("desc").getString();
	// 		this.width = obj.getJsonNumber("width").doubleValue();
	// 		this.height = obj.getJsonNumber("height").doubleValue();

	// 		JsonArray start = obj.getJsonArray("start");
	// 		double startX = start.getJsonNumber(0).doubleValue();
	// 		double startY = start.getJsonNumber(1).doubleValue();
	// 		this.startpos = new Vector3D(startX, startY);

	// 		JsonArray hole = obj.getJsonArray("hole");
	// 		double holeX = hole.getJsonNumber(0).doubleValue();
	// 		double holeY = hole.getJsonNumber(1).doubleValue();
	// 		double radius = hole.getJsonNumber(2).doubleValue();
	// 		this.holepos = new Vector3D(holeX, holeY);
	// 		this.holeRadius = radius;

	// 		JsonArray arr = obj.getJsonArray("walls");
	// 		this.walls = new ArrayList<Double>();
	// 		for(int i = 0; i < arr.size(); i++){
	// 			JsonArray segment = arr.getJsonArray(i);
	// 			if(segment.size() != 4){
	// 				throw new Exception("Invalid wall format");
	// 			}
	// 			for(int j = 0; j < segment.size(); j++){
	// 				double d = segment.getJsonNumber(j).doubleValue();
	// 				walls.add(d);
	// 			}
	// 		}
	// 		jsonReader.close();
	// 	} catch(Exception e){
	// 		System.out.println("something went wrong while reading map: " + path);
	// 		e.printStackTrace();
	// 	}
	// }

	// public void store(String path){
	// 	this.path = path;

	// 	// start pos
	// 	JsonArrayBuilder start = Json.createArrayBuilder();
	// 	start.add(startpos.x);
	// 	start.add(startpos.y);

	// 	// hole
	// 	JsonArrayBuilder hole = Json.createArrayBuilder();
	// 	hole.add(holepos.x);
	// 	hole.add(holepos.y);
	// 	hole.add(holeRadius);

	// 	// walls
	// 	JsonArrayBuilder arr = Json.createArrayBuilder();
	// 	for (int i = 0; i < this.walls.size()/4; i++) {
	// 		JsonArrayBuilder segment = Json.createArrayBuilder();
	// 		segment.add(this.walls.get(4*i+0));
	// 		segment.add(this.walls.get(4*i+1));
	// 		segment.add(this.walls.get(4*i+2));
	// 		segment.add(this.walls.get(4*i+3));
		
	// 		arr.add(segment.build());
	// 	}

	// 	// Map<String, Object> properties = new HashMap<String, Object>(1);
	// 	// properties.put(JsonGenerator.PRETTY_PRINTING, true);
	// 	// JsonGeneratorFactory jgf = Json.createGeneratorFactory(properties);
	// 	// JsonGenerator jg = jgf.createGenerator(System.out);

	// 	JsonObject obj = Json.createObjectBuilder()
	// 		.add("name", this.name)
	// 		.add("desc", this.desc)
	// 		.add("width", this.width)
	// 		.add("height", this.height)
	// 		.add("start", start.build())
	// 		.add("hole", hole.build())
	// 		.add("walls", arr.build())
	// 		.build();

	// 	// System.out.println(obj);
		
	// 	try {
	// 		JsonWriter writer = Json.createWriter(new FileWriter(path));
	// 		writer.writeObject(obj);
	// 		writer.close();
	// 	} catch(IOException e){
	// 		System.out.println("couldn't write file: " + path);
	// 	}


	// }

	// public String getName(){ return name; }
	// public String getDescription(){ return desc; }
	// public double getWidth(){ return width; }
	// public double getHeight(){ return height; }
	// public Vector3D getStartPosition(){ return startpos; }
	// public Vector3D getHolePosition(){ return holepos; }
	// public double getHoleRadius(){ return holeRadius; }
	// public ArrayList<Double> getWalls(){ return walls; }

	// public void setName(String name){ this.name = name; }
	// public void setDescription(String desc){ this.desc = desc; }
	// public void setWidth(double width){ this.width = width; }
	// public void setHeight(double height){ this.height = height; }
	// public void setStartPosition(Vector3D pos){ this.startpos = pos; }
	// public void setHolePosition(Vector3D pos){ this.holepos = pos; }
	// public void setHoleRadius(double r){ this.holeRadius = r; }
	// public void addWall(double x1, double y1, double x2, double y2){ 
	// 	this.walls.add(x1);
	// 	this.walls.add(y1);
	// 	this.walls.add(x2);
	// 	this.walls.add(y2);
	// }

	// @Override
	// public String toString(){
	// 	String r = "";
	// 	r += "name = " + name + "\n";
	// 	r += "desc = " + desc + "\n";
	// 	r += "width = " + width + "\n";
	// 	r += "height = " + height + "\n";
	// 	r += "start = " + startpos.x + ", " + startpos.y + "\n";
	// 	r += "hole = " + holepos.x + ", " + holepos.y + " (r=" + holeRadius + ")\n";
	// 	r += "walls = [\n";
	// 	for (int i = 0; i < walls.size()/4; i++) {
	// 		r += "    [";
	// 		r += walls.get(4*i+0) + ", ";
	// 		r += walls.get(4*i+1) + ",  ";
	// 		r += walls.get(4*i+2) + ", ";
	// 		r += walls.get(4*i+3);
	// 		r += "]\n";
	// 	}
	// 	r += "]";
	// 	return r;
	// }

	@Override
	public void dispose () {
		mesh.dispose();
	}

	public static void main(String[] args) {
		// Map m = new Map("test.json");
		// System.out.println(m);

		// Map newMap = new Map();
		// newMap.setName("writeTest");
		// newMap.setDescription("something something ...");
		// newMap.setStartPosition(new Vector3D(20, 10));
		// newMap.setHolePosition(new Vector3D(80, 60));
		// newMap.setHoleRadius(2);
		// newMap.addWall(0, 0, 100, 100);
		// newMap.addWall(0, 0, 100, 0);
		// newMap.addWall(100, 0, 100, 100);

		// newMap.store("new.json");
	}
}
