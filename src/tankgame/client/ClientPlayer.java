package tankgame.client;

import tankgame.game.Player;

/**
 *
 * @author Layne
 */
public class ClientPlayer extends Player {
    int rid;
    
    public ClientPlayer(Player toBeCopied, int id){
        super(toBeCopied);
        this.rid = id;
    }
    
    public ClientPlayer(int x, int y, int id){
        super(x, y);
        this.rid = id;
    }
}
