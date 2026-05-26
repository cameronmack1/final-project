package tankgame.server;

import tankgame.game.Player;

import java.util.UUID;

/**
 *
 * @author Cameron
 */
public class ServerPlayer extends Player {
    public final UUID id;

    public ServerPlayer(double x, double y, UUID id) {
        super(x, y);
        this.id = id;
    }
}
