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

	static int MIN_HEIGHT = 0;
	static int MAX_HEIGHT = 15;
	static float HEIGHT_SCALE = 1.0f/(MAX_HEIGHT-MIN_HEIGHT);

	protected String path, name, desc;
	// the width and height are the dimensions between the corners of the tiles
	// there are width-1 by height-1 tiles
	protected int width, height;
	protected Vector2 startpos, holepos;
	protected ArrayList<Obstacle> obstacles;
	// -1 means the tile is outside the terrain
	// [0,15] are the heightvalues
	// > 15 is invalid
	protected int[][] heightmap;

	// mesh stuff
	protected Color color;
	protected Mesh mesh;
	private float[] vertices;
	private short[] indices;
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
		this.heightmap = new int[width][height];
		// set all heights to the avg of min and max height
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				this.heightmap[i][j] = (MIN_HEIGHT+MAX_HEIGHT)/2;
			}
		}


		// mesh stuff
		this.color = new Color(0.4f, 0.8f, 0.2f, 1.0f);
		VertexAttributes attributes = MeshBuilder.createAttributes(Usage.Position | Usage.Normal | Usage.ColorUnpacked);
		this.posPos = attributes.getOffset(Usage.Position, -1);
		this.norPos = attributes.getOffset(Usage.Normal, -1);
		this.colPos = attributes.getOffset(Usage.ColorUnpacked, -1);

		// number of floats per vertex
		this.stride = attributes.vertexSize / 4;

		int numVertices = (width - 1) * (height - 1) * 6;
		int numIndices = (width - 1) * (height - 1) * 6;

		// make new static mesh
		this.mesh = new Mesh(true, numVertices, numIndices, attributes);
		// allocate array to store vertex data
		this.vertices = new float[numVertices * stride];
		// allocate arrar to store indices
		this.indices = new short[numIndices];
	}

	private void setVertex(int index, Vector3 pos, Vector3 normal, Color color){
		index *= stride;
		// position
		vertices[index + posPos + 0] = pos.x;
		vertices[index + posPos + 1] = pos.y;
		vertices[index + posPos + 2] = pos.z;
		// normal
		vertices[index + norPos + 0] = normal.x;
		vertices[index + norPos + 1] = normal.y;
		vertices[index + norPos + 2] = normal.z;
		// color
		vertices[index + colPos + 0] = color.r;
		vertices[index + colPos + 1] = color.g;
		vertices[index + colPos + 2] = color.b;
		vertices[index + colPos + 3] = color.a;
	}
	public void updateMesh(){
		int w = width - 1;
		int h = height - 1;
		// vars to track the position in vertex/index arrays
		int index = 0;
		// buffer variables to store vertex data
		MeshPartBuilder.VertexInfo v00 = new MeshPartBuilder.VertexInfo();
		MeshPartBuilder.VertexInfo v10 = new MeshPartBuilder.VertexInfo();
		MeshPartBuilder.VertexInfo v01 = new MeshPartBuilder.VertexInfo();
		MeshPartBuilder.VertexInfo v11 = new MeshPartBuilder.VertexInfo();
		// temp vector
		Vector3 tmpV = new Vector3();
		// for each tile
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				// get heights at four corners
				int h00 = heightmap[x][y];
				int h10 = heightmap[x+1][y];
				int h01 = heightmap[x][y+1];
				int h11 = heightmap[x+1][y+1];
				// 'normalise' heights tobe either 0 or 1, but remember the offset (minh)
				int minh = Math.min(h00, Math.min(h10, Math.min(h01, h11)));
				h00 -= minh; h10 -= minh; h01 -= minh; h11 -= minh;

				// set positions
				v00.position.set(x,HEIGHT_SCALE*(minh+h00),y);
				v10.position.set(x+1,HEIGHT_SCALE*(minh+h10),y);
				v01.position.set(x,HEIGHT_SCALE*(minh+h01),y+1);
				v11.position.set(x+1,HEIGHT_SCALE*(minh+h11),y+1);

				// set colors
				v00.color.set(this.color).mul(((1.0f*minh+h00)-MIN_HEIGHT)/(MAX_HEIGHT-MIN_HEIGHT));
				v10.color.set(this.color).mul(((1.0f*minh+h10)-MIN_HEIGHT)/(MAX_HEIGHT-MIN_HEIGHT));
				v01.color.set(this.color).mul(((1.0f*minh+h01)-MIN_HEIGHT)/(MAX_HEIGHT-MIN_HEIGHT));
				v11.color.set(this.color).mul(((1.0f*minh+h11)-MIN_HEIGHT)/(MAX_HEIGHT-MIN_HEIGHT));

				// set normals
				// check which direction the diagonal should be
				// this is to make everything look consistent and independent of rotation
				// (same code is used to decide which indices to use)
				int d1 = Math.abs(h00-h11);
				int d2 = Math.abs(h01-h10);
				// if(((h00 > h01 && h00 > h10) || (h11 > h01 && h11 > h10))){
				if(d1 > d2){
				// if(
				// 	(h00 == 1 &&  h10 == 0 && h01 == 0 && h11 == 0) ||
				// 	(h00 == 1 &&  h10 == 1 && h01 == 1 && h11 == 0) ||
				// 	(h00 == 0 &&  h10 == 0 && h01 == 0 && h11 == 1) ||
				// 	(h00 == 1 &&  h10 == 0 && h01 == 0 && h11 == 1) ||
				// 	(h00 == 0 &&  h10 == 1 && h01 == 1 && h11 == 1)
				// ){
					// first triangle (00 - 01 - 10)
					// normals
					v00.normal.set(v00.position).sub(v01.position).nor().crs(tmpV.set(v10.position).sub(v00.position).nor()).scl(-1);
					v10.normal.set(v10.position).sub(v01.position).nor().crs(tmpV.set(v00.position).sub(v10.position).nor());
					v01.normal.set(v01.position).sub(v00.position).nor().crs(tmpV.set(v10.position).sub(v01.position).nor());
					// set vertices
					this.setVertex(6*index+0, v00.position, v00.normal, v00.color);
					this.setVertex(6*index+1, v10.position, v10.normal, v10.color);
					this.setVertex(6*index+2, v01.position, v01.normal, v01.color);
					// set indices
					this.indices[6*index+0] = (short)(6*index+0);
					this.indices[6*index+1] = (short)(6*index+2);
					this.indices[6*index+2] = (short)(6*index+1);

					// second triangle (10 - 01 - 11)
					// normals
					v10.normal.set(v10.position).sub(v01.position).nor().crs(tmpV.set(v11.position).sub(v10.position).nor()).scl(-1);
					v01.normal.set(v01.position).sub(v11.position).nor().crs(tmpV.set(v10.position).sub(v01.position).nor()).scl(-1);
					v11.normal.set(v11.position).sub(v10.position).nor().crs(tmpV.set(v01.position).sub(v11.position).nor()).scl(-1);
					// set vertices
					this.setVertex(6*index+3, v10.position, v10.normal, v10.color);
					this.setVertex(6*index+4, v01.position, v01.normal, v01.color);
					this.setVertex(6*index+5, v11.position, v11.normal, v11.color);
					// set indices
					this.indices[6*index+3] = (short)(6*index+3);
					this.indices[6*index+4] = (short)(6*index+4);
					this.indices[6*index+5] = (short)(6*index+5);
				}else{
					// first triangle (00 - 11 - 10)
					// normals
					v00.normal.set(v00.position).sub(v11.position).nor().crs(tmpV.set(v10.position).sub(v00.position).nor()).scl(-1);
					v10.normal.set(v10.position).sub(v11.position).nor().crs(tmpV.set(v00.position).sub(v10.position).nor());
					v11.normal.set(v11.position).sub(v00.position).nor().crs(tmpV.set(v10.position).sub(v11.position).nor());
					// set vertices
					this.setVertex(6*index+0, v00.position, v00.normal, v00.color);
					this.setVertex(6*index+1, v10.position, v10.normal, v10.color);
					this.setVertex(6*index+2, v11.position, v11.normal, v11.color);
					// set indices
					this.indices[6*index+0] = (short)(6*index+0);
					this.indices[6*index+1] = (short)(6*index+2);
					this.indices[6*index+2] = (short)(6*index+1);

					// second triangle (00 - 01 - 11)
					// normals
					v00.normal.set(v00.position).sub(v01.position).nor().crs(tmpV.set(v00.position).sub(v11.position).nor());
					v01.normal.set(v01.position).sub(v11.position).nor().crs(tmpV.set(v00.position).sub(v01.position).nor()).scl(-1);
					v11.normal.set(v11.position).sub(v00.position).nor().crs(tmpV.set(v01.position).sub(v11.position).nor()).scl(-1);
					// set vertices
					this.setVertex(6*index+3, v00.position, v00.normal, v00.color);
					this.setVertex(6*index+4, v01.position, v01.normal, v01.color);
					this.setVertex(6*index+5, v11.position, v11.normal, v11.color);
					// set indices
					this.indices[6*index+3] = (short)(6*index+3);
					this.indices[6*index+4] = (short)(6*index+4);
					this.indices[6*index+5] = (short)(6*index+5);
				}

				index++;

			}	
		}
		// pass data to mesh
		mesh.setVertices(vertices);
		mesh.setIndices(indices);
	}

	public void raiseTile(int x, int y){
		if(x < 0 || x >= width || y < 0 || y >= height)
			return;

		if(heightmap[x][y] >= MAX_HEIGHT)
			return;

		// raise tile
		heightmap[x][y]++;

		// // recursively raise neighbours if necessary
		// if(x > 0 && heightmap[x][y] - heightmap[x-1][y] > 1){
		// 	raiseTile(x-1, y);
		// }
		// if(x < width-1 && heightmap[x][y] - heightmap[x+1][y] > 1){
		// 	raiseTile(x+1, y);
		// }
		// if(y > 0 && heightmap[x][y] - heightmap[x][y-1] > 1){
		// 	raiseTile(x, y-1);
		// }
		// if(y < height-1 && heightmap[x][y] - heightmap[x][y+1] > 1){
		// 	raiseTile(x, y+1);
		// }
	}

	public void lowerTile(int x, int y){
		if(x < 0 || x >= width || y < 0 || y >= height)
			return;

		if(heightmap[x][y] <= MIN_HEIGHT)
			return;

		// lower tile
		heightmap[x][y]--;

		// // recursively lower neighbours if necessary
		// if(x > 0 && heightmap[x][y] - heightmap[x-1][y] < -1){
		// 	lowerTile(x-1, y);
		// }
		// if(x < width-1 && heightmap[x][y] - heightmap[x+1][y] < -1){
		// 	lowerTile(x+1, y);
		// }
		// if(y > 0 && heightmap[x][y] - heightmap[x][y-1] < -1){
		// 	lowerTile(x, y-1);
		// }
		// if(y < height-1 && heightmap[x][y] - heightmap[x][y+1] < -1){
		// 	lowerTile(x, y+1);
		// }
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
