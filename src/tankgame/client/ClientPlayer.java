package tankgame.client;

import tankgame.game.Player;

/**
 *
 * @author Layne
 */
public class ClientPlayer extends Player {
    int rid;
    public ClientPlayer(int id){
        this.rid = id;
    }
}
