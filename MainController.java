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

        int playersSelected = 1;
        int ballProtoSelected = 0;
        ArrayList<GolfBall> ballProtos = gameController.getGolfBallProtos();
        int curPlayer = 0;
        for (; curPlayer < playersSelected; curPlayer++) {
            Player player = new Player(curPlayer, ballProtos.get(ballProtoSelected).clone(), gameController);
            gameController.addPlayer(player);
        }
        // gameController.addPlayer(new AIPlayer(curPlayer, ballProtos.get(ballProtoSelected).clone(), gameController));


        if (!gameController.startGame()) {
            // # of players was not specified
        }

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
