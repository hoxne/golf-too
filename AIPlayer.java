import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Michael on 24.05.2016.
 */
public class AIPlayer extends Player {

    public AIPlayer(int playerId, GolfBall golfBall, GameController gameController) {
        super(playerId, golfBall, gameController);
        // marker to find AI ball after deep copy
        this.golfBall.isAi = true;
    }
    @Override
    public void play() {



        for (int attempt = 0; attempt < 100; attempt++) {
            PhysicsManager simulator = gameController.getPhysicsManager().clone();
            if (simulator.getBalls().size() < playerId + 1)
                simulator.addBall(golfBall.clone());
            GolfBall activeBall = getActiveBall(simulator);
            Vector3 dv = new Vector3(attempt, attempt, attempt);
            activeBall.kick(dv);
            simulator.update(10000f);
        }
    }

    private GolfBall getActiveBall(PhysicsManager simulator) {
        ArrayList<GolfBall> balls = simulator.getBalls();
        for (GolfBall curBall : balls) {
            if (curBall.isAi)
                return curBall;
        }

        return null;
    }
}
