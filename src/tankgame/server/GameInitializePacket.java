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
    private final long time;

    public GameInitializePacket(ServerPlayer[] players, long seed, long time) {
        this.players = players;
        this.seed = seed;
        this.time = time;
    }
    
    public long getTime() {
        return time;
    }

    public ServerPlayer[] getPlayers() {
        return players;
    }
    
    public long getSeed() { 
        return seed;
    }
}
