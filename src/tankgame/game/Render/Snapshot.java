package tankgame.game.Render;

import tankgame.game.Player;
import tankgame.game.projectile.Projectile;

import java.io.Serializable;

/**
 *
 * @author Cameron
 */
public class Snapshot implements Serializable {

    private static final long serialVersionUID = 67L;

    private final Player playerArray[];
    private final Projectile projectileArray[];
    private final long time;

    public Snapshot(Player playerArray[], Projectile projectileArray[], long time) {
        this.playerArray = playerArray;
        this.projectileArray = projectileArray;
        this.time = time;
    }

    public long getTime() {
        return this.time;
    }

    public Projectile[] getProjectileArray() {
        return this.projectileArray;
    }

    public Player[] getPlayerArray() {
        return this.playerArray;
    }
}
