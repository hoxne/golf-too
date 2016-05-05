import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.io.*;
import java.net.*;
import java.lang.Math;

public class GameScreen implements Screen, InputProcessor {
	private Game game;

	private OrthographicCamera cam2d;
	private static float VIEWPORT_HEIGHT = 100;

	private PerspectiveCamera cam3d;
	private CameraInputController cam3dController;
	private DirectionalLight dirLight;
	private Environment environment;
	// private Model model;
	// private ModelInstance instance;
	private Course map;
	private Renderable terrain;

	private ShapeRenderer shapeRenderer;
	private SpriteBatch spriteBatch;
	private ModelBatch modelBatch;
	private BitmapFont font;

	private Model ball;
	private ModelInstance ballInstance;
	private Vector3 ballPos = new Vector3(0,0,0);
	
	public GameScreen(Game game) {
		this.game = game;

		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		// Constructs a new OrthographicCamera, using the given viewport width and height
		// Height is multiplied by aspect ratio.
		cam2d = new OrthographicCamera(VIEWPORT_HEIGHT*(w/h), VIEWPORT_HEIGHT);

		cam2d.position.set(cam2d.viewportWidth / 2f, cam2d.viewportHeight / 2f, 0);
		cam2d.update();

		shapeRenderer = new ShapeRenderer();
		spriteBatch = new SpriteBatch();
		font = new BitmapFont(new FileHandle("./warnock.fnt"));

		// 3d cam
		cam3d = new PerspectiveCamera(67, w, h);
        cam3d.position.set(10f, 10f, 10f);
        cam3d.lookAt(0,0,0);
        cam3d.near = 0.1f;
        cam3d.far = 300f;
        cam3d.update();

        environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		dirLight = new DirectionalLight().set(0.4f, 0.4f, 0.4f, -0.5f, -1.0f, -0.8f);
		environment.add(dirLight);
		
		modelBatch = new ModelBatch();

		InputMultiplexer multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(this);
 		cam3dController = new CameraInputController(cam3d);
		multiplexer.addProcessor(cam3dController);
		Gdx.input.setInputProcessor(multiplexer);


		map = new Course(9,9);
		// map.heightmap[2][2]=10;
		for (int i = 0; i < 1; i++) {
			map.raiseTile(2,2);
		}
		map.updateMesh();

		new Thread(new Runnable() {
			@Override
			public void run() {
				Scanner in = new Scanner(System.in);
				while(true){
					int x = in.nextInt();
					int y = in.nextInt();
					// for(int i = 0; i < 15; i++)
						map.raiseTile(x,y);
					Gdx.app.postRunnable(new Runnable() {
						@Override
						public void run() {
							map.updateMesh();
						}
					});
				}
			}
		}).start();


		terrain = new Renderable();
		terrain.environment = environment;
		terrain.meshPart.mesh = map.mesh;
		terrain.meshPart.primitiveType = GL20.GL_TRIANGLES;
		terrain.meshPart.offset = 0;
		terrain.meshPart.size = map.mesh.getNumIndices();
		terrain.meshPart.update();
		terrain.material = new Material();


        ModelBuilder modelBuilder = new ModelBuilder();
        // modelBuilder.begin();
        ball = modelBuilder.createSphere(0.5f, 0.5f, 0.5f, 20, 20, new Material(ColorAttribute.createDiffuse(Color.RED)), Usage.Position | Usage.Normal);
        // ball = modelBuilder.end();
        ballInstance = new ModelInstance(ball);

	}

	private void updateCamera(){
		// cam2d.zoom = MathUtils.clamp(cam2d.zoom, 0.1f, 100/cam2d.viewportWidth*2);
		if(cam2d.zoom < 0.1f){
			cam2d.zoom = 0.1f;
		}

		float effectiveViewportWidth = cam2d.viewportWidth * cam2d.zoom;
		float effectiveViewportHeight = cam2d.viewportHeight * cam2d.zoom;

		// cam2d.position.x = MathUtils.clamp(cam2d.position.x, effectiveViewportWidth / 2f, 100 - effectiveViewportWidth / 2f);
		// cam2d.position.y = MathUtils.clamp(cam2d.position.y, effectiveViewportHeight / 2f, 100 - effectiveViewportHeight / 2f);
		cam2d.update();
	}

	@Override
	public void render(float delta) {

		// rotate light
		dirLight.direction.rotate(0.3f, 0,1,0);

		cam2d.update();
		cam3d.update();

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(0f, 0f, 0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		// 3d
		modelBatch.begin(cam3d);

		// terrain
        modelBatch.render(terrain);
        // ball
        // ballInstance.transform.idt();
        // float y = map.getHeightAt(ballPos.x, ballPos.z);
		// ballInstance.transform.setTranslation(ballPos.x, y+0.25f, ballPos.z);
		// modelBatch.render(ballInstance, environment);

        modelBatch.end();


		// 2d overlay
		shapeRenderer.setProjectionMatrix(cam2d.combined);

		shapeRenderer.begin(ShapeType.Line);
		// border
		shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1);
		shapeRenderer.rect(0, 0, (float)cam2d.viewportWidth, (float)cam2d.viewportHeight);
		shapeRenderer.end();

		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		spriteBatch.begin();
		font.setColor(1,0,1,1);
		font.getData().setScale(1f);
		font.draw(spriteBatch, "Test Text", 0, h-font.getCapHeight(), w, Align.center, false);
		spriteBatch.end();


	}

	@Override
	public void resize(int width, int height) {
		cam2d.viewportWidth = VIEWPORT_HEIGHT * width/height;
		cam2d.viewportHeight = VIEWPORT_HEIGHT;
		cam2d.update();
	}
	
	// INPUT
	public boolean keyDown(int key){
		float ds = 0.5f;
		if (key == Input.Keys.LEFT || key == Input.Keys.A) {
			ballPos.z -= ds;
		}
		if (key == Input.Keys.RIGHT || key == Input.Keys.D) {
			ballPos.z += ds;
		}
		if (key == Input.Keys.DOWN || key == Input.Keys.S) {
			ballPos.x -= ds;
		}
		if (key == Input.Keys.UP || key == Input.Keys.W) {
			ballPos.x += ds;
		}

		// if (key == Input.Keys.LEFT || key == Input.Keys.A) {
		// 	cam2d.translate(-3, 0, 0);
		// }
		// if (key == Input.Keys.RIGHT || key == Input.Keys.D) {
		// 	cam2d.translate(3, 0, 0);
		// }
		// if (key == Input.Keys.DOWN || key == Input.Keys.S) {
		// 	cam2d.translate(0, -3, 0);
		// }
		// if (key == Input.Keys.UP || key == Input.Keys.W) {
		// 	cam2d.translate(0, 3, 0);
		// }

		updateCamera();

		return false;
	}

	public boolean keyTyped(char character){
		return false;
	}

	public boolean keyUp(int key){
		return false;
	}

	public boolean mouseMoved(int screenX, int screenY){
		return false;
	}

	public boolean scrolled(int amount){
		// cam2d.zoom += 0.05*amount;
		// updateCamera();
		return false;
	}

	public boolean touchDown(int screenX, int screenY, int pointer, int button){
		return false;
	}

	public boolean touchDragged(int screenX, int screenY, int pointer){
		return false;
	}

	public boolean touchUp(int screenX, int screenY, int pointer, int button){
		return false;
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		modelBatch.dispose();
		map.dispose();
	}

	@Override
	public void pause() {
	}

	@Override
	public void hide(){
	}

	@Override
	public void show(){
	}
}
