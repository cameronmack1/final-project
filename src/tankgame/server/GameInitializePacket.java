package tankgame.server;

import java.io.Serializable;

/**
 *
 * @author Cameron
 */
public class GameInitializePacket implements Serializable {

    private static final long serialVersionUID = 42069L;

    private ServerPlayer[] players;
    private int width;
    private int height;
    private long seed;

    public GameInitializePacket(ServerPlayer[] players, int width, int height, long seed) {
        this.players = players;
        this.width = width;
        this.height = height;
        this.seed = seed;
    }

    public ServerPlayer[] getPlayers() {
        return players;
    }
    
    public int getWidth() { 
        return width;
    }
    
    public int getHeight() { 
        return height;
    }
    
    public long getSeed() { 
        return seed;
    }
}
