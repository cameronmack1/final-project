package tankgame.server;

import java.io.Serializable;

/**
 *
 * @author Cameron
 */
public class GameInitializePacket implements Serializable {

    private static final long serialVersionUID = 42069L;

    private final ServerPlayer[] players;
    private final long seed;

    public GameInitializePacket(ServerPlayer[] players, long seed) {
        this.players = players;
        this.seed = seed;
    }

    public ServerPlayer[] getPlayers() {
        return players;
    }
    
    public long getSeed() { 
        return seed;
    }
}
