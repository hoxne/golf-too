import com.badlogic.gdx.math.Vector3;

/**
 * Created by Michael on 20.05.2016.
 */
public class Player {
    public Player(int playerId, GolfBall golfBall, GameController gameController) {
        this.playerId = playerId;
        this.golfBall = golfBall;
        this.gameController = gameController;
    }

    public void play() {}

    public GolfBall getGolfBall() {
        return golfBall;
    }

    public void setGolfBall(GolfBall golfBall) { this.golfBall = golfBall; }
    public void setGameController(GameController gameController) { this.gameController = gameController; }

    public void kick(Vector3 dx) {
        golfBall.kick(dx);
        gameController.ballKicked();
    }

    public boolean getStatus() { return active; }
    public void setStatus(boolean status) { active = status; }

    protected GolfBall golfBall;
    protected GameController gameController;
    protected boolean active = true;
    protected int playerId;
}
