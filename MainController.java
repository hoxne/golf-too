import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;

/**
 * Created by Michael on 13.05.2016.
 */
public class MainController {
    private Game game;

    private GameScreen gameScreen;
    private Editor editorScreen;
    private MenuScreen menuScreen;

    private GameController gameController;
    private EditorController editorController;


    public MainController(Game game) {
        this.game = game;
        gameController = new GameController(this);
        editorController = new EditorController(this);

        menuScreen = new MenuScreen(this);
        gameScreen = new GameScreen(this);
        editorScreen = new Editor(this);

        this.showMainMenu();

        int playersSelected = 5;
        int ballProtoSelected = 0;
        ArrayList<GolfBall> ballProtos = gameController.getGolfBallProtos();
        for (int curPlayer = 0; curPlayer < playersSelected; curPlayer++) {
            gameController.addPlayer(ballProtos.get(ballProtoSelected));
        }

        if (!gameController.startGame()) {
            // # of players was not specified
        }
    }

    public void showMainMenu()
    {
        game.setScreen(menuScreen);
    }
    public void showGameScreen()
    {
        game.setScreen(gameScreen);
    }
    public void showEditor()
    {
        game.setScreen(editorScreen);
    }

    public void gameOver() {
        gameScreen.setTextToShow("Game Over");
    }

    public GameScreen getGameScreen() {
        return gameScreen;
    }

    public GameController getGameController() { return this.gameController; }
    public EditorController getEditorController() { return  this.editorController; }
}
