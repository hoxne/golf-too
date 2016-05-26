import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;

/**
 * Created by Michael on 13.05.2016.
 */
public class MainController {
    private Game game;

    private Course map;

    private GameScreen gameScreen;
    private Editor editorScreen;
    private MenuScreen menuScreen;

    private GameController gameController;
    private EditorController editorController;


    public MainController(Game game) {
        this.game = game;

        this.map = new Course(19, 19);

        gameController = new GameController(this);
        editorController = new EditorController(this);

        menuScreen = new MenuScreen(this);
        gameScreen = new GameScreen(this);
        editorScreen = new Editor(this);

        this.showMainMenu();

        gameController.reset();

    }

    public Course getMap(){
        return this.map;
    }

    public void showMainMenu()
    {
        game.setScreen(menuScreen);
    }
    public void showGameScreen()
    {
        gameController.reset();
        if (!gameController.startGame()) {
            // # of players was not specified
        }
        game.setScreen(gameScreen);
        gameController.getPhysicsManager().setCollisionObjects(map.getCollisionObjects());
    }
    public void showEditor()
    {
        game.setScreen(editorScreen);
    }

    public void gameOver() {
        gameScreen.setTextToShow("Game Over");
    }

    public void gameIsWon() { gameScreen.setTextToShow("Success!"); }

    public GameScreen getGameScreen() {
        return gameScreen;
    }

    public GameController getGameController() { return this.gameController; }
    public EditorController getEditorController() { return  this.editorController; }
}
