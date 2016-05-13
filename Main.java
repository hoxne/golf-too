import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

public class Main extends Game {
	private FPSLogger fps;

	@Override
	public void create() {
		fps = new FPSLogger();
		gameScreen = new GameScreen(this);
		editorScreen = new Editor();
		MainController mainController = new MainController(this);
		mainController.showMainMenu();
	}

	@Override
	public void render() {
		super.render();
		fps.log();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void pause() {
	}

	public void setGameScreen(GameScreen gameScreen) {
		this.gameScreen = gameScreen;
	}

	public void setEditorScreen(Editor editor) {
		this.editorScreen = editor;
	}

	public GameScreen getGameScreen() {
		return  gameScreen;
	}

	public Editor getEditorScreen() {
		return editorScreen;
	}

	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1280;
		config.height = 720;
		config.x = -1; //center
		config.y = -1; // center
		config.title = "Crazy Golf!";
		config.samples = 4;

		new LwjglApplication(new Main(), config);
	}

	private GameScreen gameScreen;
	private Editor editorScreen;

}
