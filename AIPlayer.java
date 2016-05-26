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

        int MAX_ATTEMPTS = 100;
        System.out.print("Calculating shot...");
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            System.out.print("\rCalculating shot... " + (100f*attempt/MAX_ATTEMPTS) + "%");
            PhysicsManager simulator = physx.clone();
            if (simulator.getBalls().size() < playerId + 1)
                simulator.addBall(golfBall.clone());
            GolfBall activeBall = getActiveBall(simulator);

            Vector3 holePos = map.getHoleInWorld();

            // simulate random shot
            Vector3 dv = getRandomShot();
            activeBall.kick(dv);
            int stopCounter = 0;
            while(stopCounter < 5 && activeBall.getPosition().y > -10){
                if(activeBall.getVelocity().len() < 0.05)
                    stopCounter++;
                else
                    stopCounter = 0;

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
        System.out.println("\rCalculating shot... 100%");

        kick(bestKick);
    }

    private Vector3 getRandomShot(){
        Random rand = new Random();

        Vector3 dv = new Vector3(1,0,0);
        float angle = rand.nextFloat() * (float)Math.PI * 2;
        dv.rotateRad(angle, 0,1,0);

        float min = 0.1f;
        float max = 5.0f;
        float force = rand.nextFloat() * (max - min) + min;

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
