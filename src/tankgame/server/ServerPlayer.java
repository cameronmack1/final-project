package tankgame.server;

import tankgame.game.Player;

import java.util.UUID;
import java.io.Serializable;

/**
 *
 * @author Cameron
 */
public class ServerPlayer extends Player {
    
    private final UUID id;

    public ServerPlayer(double x, double y, UUID id) {
        super(x, y);
        this.id = id;
    }
    
    public UUID getID(){
        return this.id;
    }
}
