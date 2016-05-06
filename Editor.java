import java.util.Scanner;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
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

public class Editor implements Screen, InputProcessor {

	private OrthographicCamera cam2d;
	private PerspectiveCamera cam3d;
	private Environment environment;
	private DirectionalLight dirLight;
	private ModelBatch modelBatch;
	private Renderable terrain;
	private Course map;
	private Game game;
	private static float VIEWPORT_HEIGHT = 100;
	
	public Editor(Game game) {

		this.game = game;

		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		cam2d = new OrthographicCamera(VIEWPORT_HEIGHT*(w/h), VIEWPORT_HEIGHT);
	    cam2d.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam2d.position.set(0, 0, 0);
		cam2d.update();

        environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		dirLight = new DirectionalLight().set(0.4f, 0.4f, 0.4f, -0.5f, -1.0f, -0.8f);
		environment.add(dirLight);
		
		modelBatch = new ModelBatch();

		cam3d = new PerspectiveCamera(67, w, h);
        cam3d.position.set(0,0,0);
        cam3d.lookAt(0,0,0);
        cam3d.near = 0.1f;
        cam3d.far = 300f;
        cam3d.update();
		
        // input somehow
		createTerrain(20,20);
	}

	private void createTerrain(int width, int height) {

		map = new Course(width, height);
		map.updateMesh();
		
		terrain = new Renderable();
		terrain.environment = environment;
		terrain.meshPart.mesh = map.mesh;
		terrain.meshPart.primitiveType = GL20.GL_TRIANGLES;
		terrain.meshPart.offset = 0;
		terrain.meshPart.size = map.mesh.getNumIndices();
		terrain.meshPart.update();
		terrain.material = new Material();
		
		cam3d.position.set(map.width/2, Math.max(map.width, map.height), map.height/2); // very hacky. fixy l8rs
        cam3d.lookAt(map.width/2,0,map.height/2);
        
        cam2d.position.set(map.width/2, 1, map.height/2); // very hacky. fixy l8rs
        cam2d.lookAt(map.width/2,0,map.height/2);
        
        
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
		
		//handleInput();
		cam2d.update();
		cam3d.update();
		
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(0f, 0f, 0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		

		modelBatch.begin(cam2d);
        modelBatch.render(terrain);
        modelBatch.end();

	}

	@Override
	public void resize(int width, int height) {
		//cam2d.viewportWidth = VIEWPORT_HEIGHT * width/height;
		//cam2d.viewportHeight = VIEWPORT_HEIGHT;
		//cam2d.update();
		//cam3d.viewportWidth = VIEWPORT_HEIGHT * width/height;
		//cam3d.viewportHeight = VIEWPORT_HEIGHT;
		//cam3d.update();
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
	public boolean touchDown(int arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		return false;
	}

}
