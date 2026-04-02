package tankgame.server;

import java.util.ArrayList;
import java.util.UUID;

/**
 *
 * @author Cameron
 */
public class PlayerHandler {

    final private ArrayList<ServerPlayer> players = new ArrayList<>();

    public PlayerHandler() {
    }
    
    public ArrayList<ServerPlayer> getPlayers(){
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

    public void addPlayer(ServerPlayer player) {
        players.add(player);
    }
}
