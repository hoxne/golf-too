import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

/**
 * Created by Michael on 13.05.2016.
 */
public class GameController {
    public GameController(MainController mainController) {
        this.mainController = mainController;

        players = new ArrayList<Player>();

        map = new Course(20,20);
        // map.heightmap[2][2]=10;
        //map.setTileInMap(3,4,false);
        for (int i = 0; i < 100; i++) {
            map.lowerCorner(4,4);
        }
        for (int i = 0; i < 100; i++) {
            map.lowerCorner(4,5);
        }
        for (int i = 0; i < 100; i++) {
            map.lowerCorner(5, 5);
        }
        for (int i = 0; i < 100; i++) {
            map.lowerCorner(5, 6);
        }
        for (int i = 0; i < 100; i++) {
            map.lowerCorner(6, 6);
        }
        for (int i = 0; i < 100; i++) {
            map.lowerCorner(6, 7);
        }
        for (int i = 0; i < 100; i++) {
            map.lowerCorner(7,8);
        }
        for (int i = 0; i < 100; i++) {
            map.lowerCorner(7,7);
        }
        map.updateMesh();

        ArrayList<CollisionObject> collisionObjects = new ArrayList<>();
        ArrayList<CollisionObject> mapCollisionObjects = map.getCollisionObjects();
        collisionObjects.addAll(mapCollisionObjects);
        physicsManager = new PhysicsManager(collisionObjects);
    }

    public void addPlayer(GolfBall golfBall) {
        GolfBall golfBallCopy = golfBall.clone();
        Player player = new Player(golfBallCopy, this);
        players.add(player);
    }

    public boolean startGame() {
        if (players.size() > 0) {
            physicsManager.addBall(getCurrentPlayer().getGolfBall());
            return true;
        }

        return false;
    }

    public ArrayList<GolfBall> getGolfBallProtos() {
        GolfBall golfBall = new GolfBall(new Vector3(3, 1, 3), new Vector3(0, 0, 0), 100, 0.2f);
        ArrayList<GolfBall> golfBallProtos = new ArrayList<>();
        golfBallProtos.add(golfBall);

        return golfBallProtos;

    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerId);
    }

    public ArrayList<GolfBall> getBalls() {
        return physicsManager.getBalls();
    }

    public Course getMap() { return  map; }

    public void update(float delta) {
        physicsManager.update(delta);

        if (hasPlayerKicked && (isBallStopped() || isBallOutOfGame()))
        {
            nextPlayer();
            hasPlayerKicked = false;
        }

    }

    public void ballKicked() {
        hasPlayerKicked = true;
    }

    private void nextPlayer() {
        currentPlayerId++;
        currentPlayerId = currentPlayerId % players.size();

        if (physicsManager.getBalls().size() < currentPlayerId + 1)
            physicsManager.addBall(getCurrentPlayer().getGolfBall());
    }

    private boolean isBallStopped() {
        // probably ball stopped
        Vector3 activeVelocity = getCurrentPlayer().getGolfBall().getVelocity();
        if (activeVelocity.len() < 0.01) {
            if (probablyBallStopped) {
                ballStoppedIteration++;
                if (ballStoppedIteration > 10)
                    return true;
            }
            else {
                probablyBallStopped = true;
                ballStoppedIteration = 0;
            }
        }

        return  false;
    }

    private boolean isBallOutOfGame() {
        Vector3 activePosition = getCurrentPlayer().getGolfBall().getPosition();
        if (activePosition.y < -10)
            return true;

        return false;
    }

    private MainController mainController;
    private PhysicsManager physicsManager;
    private ArrayList<Player> players;
    private Course map;
    private int currentPlayerId;
    private boolean probablyBallStopped = false;
    private int ballStoppedIteration = 0;
    private boolean hasPlayerKicked = false;
}
