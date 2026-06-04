package tankgame.game;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import tankgame.game.Render.GameCanvas;
import tankgame.game.Render.Snapshot;
import tankgame.game.projectile.*;
import tankgame.client.ClientPlayer;
import tankgame.server.*;
import java.util.UUID;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

/**
 *
 * @author Cameronx
 */
public class GameHandler {

    private GameFrame gf;
    private GameCanvas gc;
    
    //server stuff
    private ArrayList<ServerPlayer> players = new ArrayList<>();
    private ArrayList<Projectile> projectiles = new ArrayList<>();
    private boolean isHost;

    //local stuff
    boolean[] keys;
    ClientPlayer self;
    private volatile ArrayList<Projectile> localProj = new ArrayList<>();

    public GameHandler(GameFrame gf, boolean isHost) {
        this.gf = gf;
    }

    public void setCanvas(GameCanvas canvas) {
        gc = canvas;
    }

    public ServerPlayer getPlayer(UUID ID) {
        for (ServerPlayer player : players) {
            if (ID == player.getID()) {
                return player;
            }
        }
        return null;
    }
    
    public void initLocal(){
        self = new ClientPlayer(0, 0, 0);
        Snapshot defaultSnapshot = new Snapshot(new ClientPlayer[]{self}, localProj.toArray(Projectile[]::new), System.currentTimeMillis());
        gc.addLocalSnapshot(defaultSnapshot);
        gc.addLocalSnapshot(defaultSnapshot);
        gc.initLocal();
    }

    public void initServer(LobbyPlayer[] lpArray) {
        int ridCount = 0;
        for(LobbyPlayer lp : lpArray){
            players.add(new ServerPlayer(0, 0, ridCount, lp.getID()));
        }
    }

    public String serverTick() {
        String data = null;
        //move all projectiles
        for (Projectile projectile : projectiles) {
            projectile.move();
        }
        //move players and check shooting for each player
        for (ServerPlayer player : players) {
            player.setCooldown(player.getCooldown() - 1);

            player.move(player.getKeys());
            //player shooting
            if (player.getKeys()[4] && player.getCooldown() <= 0) {
                projectiles.add(new NormalProjectile(player.getX(), player.getY(), player.getAngle(), player.getVel(), player.getRID()));
                player.setCooldown(Projectile.COOLDOWN);
            }
        }
        //check collision

        //create snapshot and convert it to base64
        Snapshot ss = new Snapshot(players.toArray(Player[]::new), projectiles.toArray(Projectile[]::new), System.currentTimeMillis());
        try {
            data = serialize(ss);
        } catch (IOException e) {
            e.printStackTrace();
        }
        data = "3:" + data;
        return data;
    }

    public void initDebug() {
        self = new ClientPlayer(500, 500, 0);
        Snapshot defaultSnapshot = new Snapshot(new ClientPlayer[]{self}, localProj.toArray(Projectile[]::new), System.currentTimeMillis());
        gc.addLocalSnapshot(defaultSnapshot);
        gc.addLocalSnapshot(defaultSnapshot);
        gc.initLocal();
    }

    public void localTick() {
        keys = gc.kb.getKeys();
        //move projectile
        for (Projectile proj : localProj) {
            proj.move();
        }
        //lower projectile cooldowns
        if (self.getCooldown() > 0) {
            self.setCooldown(self.getCooldown() - 1);
        }
        //shoot projectile
        if (keys[4] && self.getCooldown() <= 0) {
            localProj.add(new NormalProjectile(self.getX(), self.getY(), self.getAngle(), self.getVel(), self.getRID()));
            self.setCooldown(Projectile.COOLDOWN);
        }
        //move self
        self.setKeys(keys);
        self.move(keys);
        ClientPlayer[] pArr = new ClientPlayer[]{new ClientPlayer(self)};
        gc.addLocalSnapshot(new Snapshot(pArr, localProj.toArray(Projectile[]::new), System.currentTimeMillis()));
        
        if(!isHost){
            gf.sendKeys(keys);
        }
    }
    
    
      /**
     * converts an object into a base64 encoded serialized string
     * @param o the object to serialize
     * @return base64 encoded string containing the serialized data of this object
     * @throws IOException sometimes
     */
    public static String serialize(Object o) throws IOException {
        //create output stream
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);

        //write object to output stream
        out.writeObject(o);
        out.flush();

        //convert to bytes then to base64 string
        byte[] bytes = bos.toByteArray();
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * deserialized a string into a snapshot object
     * @param data the string data
     * @return the deserialized object
     * @throws IOException sometimes
     * @throws ClassNotFoundException sometimes
     */
    public static Object deserialize(String data) throws IOException, ClassNotFoundException {
        //base 64 string to bytes
        byte[] bytes = Base64.getDecoder().decode(data);

        //put through input stream
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream in = new ObjectInputStream(bis);

        //return as snapshot
        return in.readObject();
    }
}
