package tankgame.server;

import tankgame.game.Player;
import tankgame.game.projectile.Projectile;
import tankgame.game.Render.Snapshot;

/**
 *
 * @author Cameron
 */
public class ServerSimulate {

    public static Snapshot simulate(Snapshot state) {
        for (Player player : state.getPlayerArray()) {
            //move every player using their respective pressed keys
        }
        for (Projectile projectile : state.getProjectileArray()) {
            projectile.move();
        }
        //check collision
        return state;
    }
}
