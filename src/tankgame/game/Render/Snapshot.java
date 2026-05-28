package tankgame.game.Render;

import tankgame.game.Player;
import tankgame.game.projectile.Projectile;

import java.io.Serializable;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

import java.util.Base64;

/**
 *
 * @author Cameron
 */
public class Snapshot implements Serializable {

    private static final long serialVersionUID = 67L;

    private final Player playerArray[];
    private final Projectile projectileArray[];
    private final long time;

    public Snapshot(Player playerArray[], Projectile projectileArray[], long time) {
        this.playerArray = playerArray;
        this.projectileArray = projectileArray;
        this.time = time;
    }

    public long getTime() {
        return this.time;
    }

    public Projectile[] getProjectileArray() {
        return this.projectileArray;
    }

    public Player[] getPlayerArray() {
        return this.playerArray;
    }

    /**
     * converts this Snapshot into a base64 encoded serialized string
     * @return base64 encoded string containing the serialized data of this object
     * @throws IOException sometimes
     */
    public String serialize() throws IOException {
        //create output stream
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);

        //write object to output stream
        out.writeObject(this);
        out.flush();

        //convert to bytes then to base64 string
        byte[] bytes = bos.toByteArray();
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * deserialized a string into a snapshot object
     * @param data the string data
     * @return the deserialized snapshot
     * @throws IOException sometimes
     * @throws ClassNotFoundException sometimes
     */
    public static Snapshot deserialize(String data) throws IOException, ClassNotFoundException {
        //base 64 string to bytes
        byte[] bytes = Base64.getDecoder().decode(data);

        //put through input stream
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream in = new ObjectInputStream(bis);

        //return as snapshot
        return (Snapshot) in.readObject();
    }
}
