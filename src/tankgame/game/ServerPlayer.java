package tankgame.game;

import java.util.UUID;
/**
 *
 * @author Cameron
 */
public class ServerPlayer {

    public int x;
    public int y;
    public final UUID id;

    public ServerPlayer(int x, int y, UUID id) {
        this.x = x;
        this.y = y;
        this.id = id;
    }

    public void move(int xMove, int yMove) {
        if (xMove > 0) {
            x += 5;
        } else if (xMove < 0) {
            x -= 5;
        }
        if (yMove > 0) {
            y += 5;
        } else if (yMove < 0) {
            y -= 5;
        }
    }
}
