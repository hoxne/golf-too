import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

/**
 * Created by Michael on 13.05.2016.
 */
public class MainController {
    public MainController(Game game) {
        this.game = game;
        gameScreen = new GameScreen(game);
        editorScreen = new Editor();

        ArrayList<Vector3> vertices = gameScreen.getCollisionObjectsVertices();
        ArrayList<CollisionObject> collisionObjects = new ArrayList<>();
        Vector3[] triangles = vertices.toArray(new Vector3[0]);
        CollisionObject object = new CollisionObject(triangles);
        GolfBall golfBall = new GolfBall(new Vector3(0, 0, 0), new Vector3(0, 0, 0), 100, 50);
        ArrayList<GolfBall> golfBalls = new ArrayList<>();
        golfBalls.add(golfBall);

        physicsManager = new PhysicsManager(collisionObjects, golfBalls);
    }

    public void showMainMenu()
    {
        //TODO: show the menu
        boolean isEditor = false;
        if (isEditor) {
            game.setScreen(editorScreen);
        }
        else {
            game.setScreen(gameScreen);
            gameScreen.setMainController(this);
        }
    }

    public void update(float delta) {
    }

    private Game game;
    private PhysicsManager physicsManager;
    private GameScreen gameScreen;
    private Editor editorScreen;
}
