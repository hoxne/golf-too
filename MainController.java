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

            int playersSelected = 1;
            int ballProtoSelected = 0;
            ArrayList<GolfBall> ballProtos = gameController.getGolfBallProtos();
            int curPlayer = 0;
            for (; curPlayer < playersSelected; curPlayer++) {
                Player player = new Player(curPlayer, ballProtos.get(ballProtoSelected).clone(), gameController);
                gameController.addPlayer(player);
            }
            gameController.addPlayer(new AIPlayer(curPlayer, ballProtos.get(ballProtoSelected).clone(), gameController));


            if (!gameController.startGame()) {
                // # of players was not specified
            }

            game.setScreen(gameScreen);

        }
    }

    public void gameOver() {
        gameScreen.setTextToShow("Game Over");
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
