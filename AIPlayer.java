import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.Random;
import java.lang.Math;

/**
 * Created by Michael on 24.05.2016.
 */
public class AIPlayer extends Player {

    public AIPlayer(int playerId, GolfBall golfBall, Course map) {
        super(playerId, golfBall, map);
        // marker to find AI ball after deep copy
        this.golfBall.isAi = true;
    }
    @Override
    public void play(PhysicsManager physx) {

        Vector3 bestKick = new Vector3(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
        float bestScore = Float.MAX_VALUE;

        for (int attempt = 0; attempt < 100; attempt++) {
            PhysicsManager simulator = physx.clone();
            if (simulator.getBalls().size() < playerId + 1)
                simulator.addBall(golfBall.clone());
            GolfBall activeBall = getActiveBall(simulator);

            Vector3 holePos = map.getHoleInWorld();

            // simulate random shot
            Vector3 dv = getRandomShot();
            activeBall.kick(dv);
            while(activeBall.getVelocity().len() > 0.1 && activeBall.getPosition().y > -10){
                System.out.println(activeBall.getVelocity().len());
                simulator.update(1f);
            }

            Vector3 ballPos = activeBall.getPosition();

            float score = applyHeuristics(holePos, ballPos);
            // minimise score
            if (score < bestScore) {
                bestKick = dv;
                bestScore = score;
            }
        }

        kick(bestKick);
    }

    private Vector3 getRandomShot(){
        Random rand = new Random();

        Vector3 dv = new Vector3(1,0,0);
        float angle = rand.nextFloat() * (float)Math.PI * 2;
        dv.rotateRad(angle, 0,1,0);

        float min = 1f;
        float max = 10.0f;
        float force = rand.nextFloat() * (max - min) + min;
        System.out.println(dv);

        dv.scl(force);

        return dv;
    }

    private float applyHeuristics(Vector3 holePos, Vector3 ballPos) {
        float distToHoleLen = holePos.dst2(ballPos);
        return distToHoleLen;
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
