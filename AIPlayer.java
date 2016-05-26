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

        Vector3 bestKick = new Vector3(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
        float closestDistance = Float.MAX_VALUE;

        for (int attempt = 0; attempt < 100; attempt++) {
            PhysicsManager simulator = gameController.getPhysicsManager().clone();
            if (simulator.getBalls().size() < playerId + 1)
                simulator.addBall(golfBall.clone());
            GolfBall activeBall = getActiveBall(simulator);
            Vector3 dv = new Vector3(0, 0, 0);

            Vector3 holePos = gameController.getMap().getHoleInWorld();
            Vector3 ballPos = activeBall.getPosition();
            float distToHoleLen = holePos.dst(ballPos);

            dv = applyHeuristics(dv, holePos, ballPos, distToHoleLen);

            activeBall.kick(dv);
            simulator.update(100f);


            if (distToHoleLen < closestDistance) {
                bestKick = dv;
                closestDistance = distToHoleLen;
            }
        }

        kick(bestKick);
    }

    private Vector3 applyHeuristics(Vector3 dv, Vector3 holePos, Vector3 ballPos, float distToHoleLen) {

        float min = -0.1f;
        float max = 0.1f;

        Random rand = new Random();
        dv.x = holePos.x + rand.nextFloat() * (max - min) + min;
        dv.y = holePos.y + rand.nextFloat() * (max - min) + min;
        dv.z = holePos.z + rand.nextFloat() * (max - min) + min;

        dv = holePos;


        return dv;
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
