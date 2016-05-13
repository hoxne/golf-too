import java.util.Scanner;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;

public class Editor implements Screen, InputProcessor {

	private OrthographicCamera cam2d;
	private Environment environment;
	private ModelBatch modelBatch;
	private Renderable terrain;
	private Course map;
	private BoundingBox mapBB;

	public Editor() {

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 1f));

		modelBatch = new ModelBatch();

		Gdx.input.setInputProcessor(this);

		// input somehow
		createTerrain(8, 8);
	}

	private void createTerrain(int width, int height) {

		map = new Course(width, height);
		map.updateMesh();

		int m = Math.max(map.height, map.width);

		cam2d = new OrthographicCamera(m * (Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight()), m);
		cam2d.position.set(0, 0, 0);
		cam2d.update();

		terrain = new Renderable();
		terrain.environment = environment;
		terrain.meshPart.mesh = map.mesh;
		terrain.meshPart.primitiveType = GL20.GL_TRIANGLES;
		terrain.meshPart.offset = 0;
		terrain.meshPart.size = map.mesh.getNumIndices();
		terrain.meshPart.update();
		terrain.material = new Material();

		cam2d.position.set(map.width / 2, 1, map.height / 2);
		cam2d.lookAt(map.width / 2, 0, map.height / 2);

		this.mapBB = map.mesh.calculateBoundingBox();
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(float arg0) {
		// TODO Auto-generated method stub

		// handleInput();
		cam2d.update();

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(0f, 0f, 0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		modelBatch.begin(cam2d);
		modelBatch.render(terrain);
		modelBatch.end();

	}

	@Override
	public void resize(int width, int height) {
		// cam2d.viewportWidth = VIEWPORT_HEIGHT * width/height;
		// cam2d.viewportHeight = VIEWPORT_HEIGHT;
		// cam2d.update();
		// cam3d.viewportWidth = VIEWPORT_HEIGHT * width/height;
		// cam3d.viewportHeight = VIEWPORT_HEIGHT;
		// cam3d.update();
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean keyDown(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(pointer > 0)
			return false;
		
		Ray ray = cam2d.getPickRay(screenX, screenY);
		Vector3 intersection = new Vector3();
		
		if (Intersector.intersectRayBounds(ray, this.mapBB, intersection)) {
			System.out.println(intersection);
			
			if(button == Input.Buttons.LEFT)
				map.raiseCorner(Math.round(intersection.x), Math.round(intersection.z));
			else if(button == Input.Buttons.RIGHT)
				map.lowerCorner(Math.round(intersection.x), Math.round(intersection.z));
			
			map.updateMesh();
			
			return true;
		}

		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		
		// no way to check which button
		touchDown(screenX, screenY, pointer, Input.Buttons.LEFT);		
		return false;
	}

	@Override
	public boolean touchUp(int arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		return false;
	}

}
