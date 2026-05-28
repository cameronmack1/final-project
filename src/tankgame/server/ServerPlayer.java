package tankgame.server;

import tankgame.game.Player;

import java.util.UUID;

/**
 *
 * @author Cameron
 */
public class ServerPlayer extends Player {
    private final UUID id;
    private boolean[] keys;

    public ServerPlayer(double x, double y, int rid, UUID id) {
        super(x, y, rid);
        this.id = id;
    }
    
    public void setKeys(boolean[] keys){
        this.keys = keys;
    }
    
    public UUID getID(){
        return this.id;
    }
}
