import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

/**
 * Created by Michael on 13.05.2016.
 */
public class MainController {
    public MainController(Main main) {
        this.main = main;

        ArrayList<Vector3> vertices = main.getGameScreen().getCollisionObjectsVertices();
        ArrayList<CollisionObject> collisionObjects = new ArrayList<>();
        CollisionObject object = new CollisionObject((Vector3[])vertices.toArray());
        GolfBall golfBall = new GolfBall(new Vector3(0, 0, 0), new Vector3(0, 0, 0), 100, 50);
        ArrayList<GolfBall> golfBalls = new ArrayList<>();
        golfBalls.add(golfBall);

        physicsManager = new PhysicsManager(collisionObjects, golfBalls);
    }

    public void showMainMenu()
    {
        //TODO: show the menu
        boolean isEditor = true;
        if (isEditor) {
            main.setScreen(main.getEditorScreen());
        }
        else {
            main.setScreen(main.getGameScreen());
            main.getGameScreen().setMainController(this);
        }
    }

    public void update(float delta) {

    }

    private Main main;
    private PhysicsManager physicsManager;

}
