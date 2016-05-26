import com.badlogic.gdx.math.Vector3;

/**
 * Created by Michael on 20.05.2016.
 */
public class Player {
    protected Course map;
    protected boolean hasKicked;

    public Player(int playerId, GolfBall golfBall, Course map) {
        this.map = map;
        this.playerId = playerId;
        this.golfBall = golfBall;
        this.hasKicked = false;
    }

    public void play(PhysicsManager physx) {}

    public GolfBall getGolfBall() {
        return golfBall;
    }

    public void setGolfBall(GolfBall golfBall) { this.golfBall = golfBall; }

    public void kick(Vector3 dx) {
        golfBall.kick(dx);
        hasKicked = true;
    }

    public boolean hasKicked(){
        return hasKicked;
    }
    public void noYouHaventKickedYet(){
        hasKicked = false;
    }

    public boolean getStatus() { return active; }
    public void setStatus(boolean status) { active = status; }

    protected GolfBall golfBall;
    protected GameController gameController;
    protected boolean active = true;
    protected int playerId;
}
