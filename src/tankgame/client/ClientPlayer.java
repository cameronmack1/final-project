package tankgame.client;

import tankgame.game.Player;

/**
 *
 * @author Layne
 */
public class ClientPlayer extends Player {
    
    public ClientPlayer(Player toBeCopied){
        super(toBeCopied);
    }
    
    public ClientPlayer(int x, int y, int id){
        super(x, y, id);
    }
}
