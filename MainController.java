import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;

/**
 * Created by Michael on 13.05.2016.
 */
public class MainController {
    public MainController(Game game) {
        this.game = game;
        gameController = new GameController(this);
        editorController = new EditorController(this);

        gameScreen = new GameScreen(game, this);
        editorScreen = new Editor();
    }

    public void showMainMenu()
    {
        //TODO: show the menu

        boolean isEditor = false;
        if (isEditor) {
            game.setScreen(editorScreen);
        }
        else {

            int playersSelected = 5;
            int ballProtoSelected = 0;
            ArrayList<GolfBall> ballProtos = gameController.getGolfBallProtos();
            for (int curPlayer = 0; curPlayer < playersSelected; curPlayer++) {
                gameController.addPlayer(ballProtos.get(ballProtoSelected));
            }

            if (!gameController.startGame()) {
                // # of players was not specified
            }

            game.setScreen(gameScreen);

        }
    }

    public GameScreen getGameScreen() {
        return gameScreen;
    }

    public GameController getGameController() { return this.gameController; }
    public EditorController getEditorController() { return  this.editorController; }

    private Game game;

    private GameScreen gameScreen;
    private Editor editorScreen;
    private GameController gameController;
    private EditorController editorController;

}
