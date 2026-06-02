package tankgame.server;

import java.util.UUID;

/**
 *
 * @author layne
 */
public class LobbyPlayer {

    private String name;
    private UUID ID;

    public LobbyPlayer(String name, UUID ID) {

        this.name = name;
        this.ID = ID;
    }

    public UUID getID() {
        return this.ID;
    }

}
