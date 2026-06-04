package tankgame.server;

import java.util.UUID;
import java.io.Serializable;

/**
 *
 * @author layne
 */
public class LobbyPlayer implements Serializable {
    private static final long serialVersionUID = 69L;
    private String name;
    private UUID ID;

    public LobbyPlayer(String name, UUID ID) {

        this.name = name;
        this.ID = ID;
    }

    public UUID getID() {
        return this.ID;
    }

    public String getName() {
        return this.name;
    }

}
