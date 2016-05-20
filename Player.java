import com.badlogic.gdx.math.Vector3;

/**
 * Created by Michael on 20.05.2016.
 */
public class Player {
    public Player(GolfBall golfBall, GameController gameController) {
        this.golfBall = golfBall;
        this.gameController = gameController;
    }

    public GolfBall getGolfBall() {
        return golfBall;
    }

    public void kick(Vector3 dx) {
        golfBall.kick(dx);
        gameController.ballKicked();
    }

    public boolean getStatus() { return active; }
    public void setStatus(boolean status) { active = status; }

    private GolfBall golfBall;
    private GameController gameController;
    private boolean active = true;
}
