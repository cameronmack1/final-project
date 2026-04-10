package tankgame.game.Render;

import tankgame.game.Player;
import tankgame.game.projectile.Projectile;

/**
 *
 * @author Cameron
 */
public class Snapshot {

    private Player playerArray[];
    private Projectile projectileArray[];
    private long time;

    public Snapshot(Player playerArray[], Projectile projectileArray[], long time) {
        this.playerArray = playerArray;
        this.projectileArray = projectileArray;
        this.time = time;
    }

    public Projectile[] getProjectileArray() {
        return this.projectileArray;
    }

    public Player[] getPlayerArray() {
        return this.playerArray;
    }
}
