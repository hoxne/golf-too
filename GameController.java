import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

/**
 * Created by Michael on 13.05.2016.
 */
public class GameController {
    public GameController(MainController mainController) {
        this.mainController = mainController;

        players = new ArrayList<Player>();

        map = mainController.getMap();
        map.updateMesh();

        ArrayList<CollisionObject> collisionObjects = new ArrayList<>();
        ArrayList<CollisionObject> mapCollisionObjects = map.getCollisionObjects();
        collisionObjects.addAll(mapCollisionObjects);
        physicsManager = new PhysicsManager(collisionObjects);
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public boolean startGame() {
        if (players.size() > 0) {
            updateScreenText();
            physicsManager.addBall(getCurrentPlayer().getGolfBall());
            getCurrentPlayer().play();
            return true;
        }

        return false;
    }

    public ArrayList<GolfBall> getGolfBallProtos() {
        GolfBall golfBall = new GolfBall(new Vector3(3, 0.15f, 3), new Vector3(0, 0, 0), 100, 0.1f);
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

    public PhysicsManager getPhysicsManager() { return physicsManager; }

    public void update(float delta) {
        physicsManager.update(delta);

        boolean isBallStopped = isBallStopped();
        boolean isBallOutOfGame = isBallOutOfGame();

        if (hasPlayerKicked && (isBallStopped || isBallOutOfGame))
        {
            hasPlayerKicked = false;

            if (isBallStopped && isBallInTheHole()) {
                mainController.getGameScreen().toggleInput(false);
                mainController.gameIsWon();
            }
            else if (!nextPlayer())
                mainController.gameOver();
        }

    }

    public void ballKicked() {
        hasPlayerKicked = true;
    }

    private boolean nextPlayer() {
        int checkOutPlayers = 0;
        do  {
            currentPlayerId++;
            currentPlayerId = currentPlayerId % players.size();
            checkOutPlayers++;

            if (checkOutPlayers > players.size())
                return false;
        } while (!players.get(currentPlayerId).getStatus());

        getCurrentPlayer().play();
        updateScreenText();


        if (physicsManager.getBalls().size() < currentPlayerId + 1)
            physicsManager.addBall(getCurrentPlayer().getGolfBall());

        return true;
    }

    private void updateScreenText() {
        String text;
        text = "Player " + (currentPlayerId + 1);

        mainController.getGameScreen().setTextToShow(text);
    }

    private boolean isBallInTheHole() {
        float xHole = getMap().getHoleInWorld().x;
        float zHole = getMap().getHoleInWorld().z;
        float xBall = getCurrentPlayer().getGolfBall().getPosition().x;
        float zBall = getCurrentPlayer().getGolfBall().getPosition().z;
        float yBall = getCurrentPlayer().getGolfBall().getPosition().y;
        float yHole = getCurrentPlayer().getGolfBall().getPosition().y;
        float holeRadius = getMap().getRadius();
        float ballRadius = getCurrentPlayer().getGolfBall().getRadius();

        if (xHole - holeRadius < xBall && xBall < xHole + holeRadius
                && zHole - holeRadius < zBall && zBall < zHole +holeRadius
                && yHole >= yBall - ballRadius)
            return true;

        return  false;
    }

    private boolean isBallStopped() {
        // probably ball stopped
        Vector3 activeVelocity = getCurrentPlayer().getGolfBall().getVelocity();
        if (activeVelocity.len() < 0.05) {
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
        if (activePosition.y < -10) {
            players.get(currentPlayerId).setStatus(false);
            return true;
        }

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
