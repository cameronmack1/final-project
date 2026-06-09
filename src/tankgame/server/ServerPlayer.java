package tankgame.server;

import tankgame.game.Player;

import java.util.UUID;

/**
 *
 * @author Cameron
 */
public class ServerPlayer extends Player {
    
    private final UUID id;
    private final String name;

    public ServerPlayer(double x, double y, UUID id, String name) {
        super(x, y);
        this.id = id;
        this.name = name;
    }
    
    public String getName(){
        return this.name;
    }
    
    public UUID getID(){
        return this.id;
    }
}
