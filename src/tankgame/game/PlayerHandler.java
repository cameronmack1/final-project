package tankgame.game;

import java.util.ArrayList;
import java.util.UUID;

/**
 *
 * @author Cameron
 */
public class PlayerHandler {

    final private ArrayList<Player> players = new ArrayList<>();

    public PlayerHandler() {
    }
    
    public ArrayList<Player> getPlayers(){
        return players;
    }

    public void removePlayer(UUID id) {
        for (int i = 0; i < players.size(); i++) {
            if (id == players.get(i).id) {
                players.remove(i);
                break;
            }
        }
    }

    public void addPlayer(Player player) {
        players.add(player);
    }
}
