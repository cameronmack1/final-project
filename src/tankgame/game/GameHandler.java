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

    private UUID id;
    //server stuff
    private ArrayList<ServerPlayer> players = new ArrayList<>();
    private ArrayList<Projectile> projectiles = new ArrayList<>();
    public boolean isHost;
    public boolean inDebug = false;
    
    private int serverTick;
    
    private int localTick;

    private double tileHeight;
    private double tileWidth;
    private boolean[][] map;

    //local stuff
    boolean[] keys;
    ClientPlayer self;
    private volatile ArrayList<Projectile> localProj = new ArrayList<>();

    public GameHandler(GameFrame gf, boolean isHost) {
        this.gf = gf;
        this.isHost = isHost;
    }

    public void setMap(boolean[][] map) {
        this.map = map;

        //calculate tile sizes in pixels using the number of tiles
        double wallHeight = GameCanvas.WALL_SIZE * ((map.length + 1d) / 2d);
        this.tileHeight = (1080d - wallHeight) / ((map.length) / 2);

        double wallWidth = GameCanvas.WALL_SIZE * ((map[0].length + 1d) / 2d);
        this.tileWidth = (1920d - wallWidth) / ((map[0].length) / 2);
    }

    public void setID(UUID id) {
        this.id = id;
    }

    public void setCanvas(GameCanvas canvas) {
        gc = canvas;
    }

    public ServerPlayer getPlayer(UUID ID) {
        for (ServerPlayer player : players) {
            if (ID.equals(player.getID())) {
                return player;
            }
        }
        return null;
    }

    public void initLocal(int x, int y) {
        self = new ClientPlayer(x, y);
        Snapshot defaultSnapshot = new Snapshot(new ClientPlayer[]{self}, localProj.toArray(Projectile[]::new), System.currentTimeMillis());
        gc.addLocalSnapshot(defaultSnapshot);
        gc.addLocalSnapshot(defaultSnapshot);
        gc.initLocal();
    }

    public void initServer(LobbyPlayer[] lpArray) {
        int count = 0;
        for (LobbyPlayer lp : lpArray) {
            int x = 50;
            int y = 50;
            switch (count) {
                case 0 -> {
                    x = 50;
                    y = 50;
                }

                case 1 -> {
                    x = 50;
                    y = 1080 - 50;
                }

                case 2 -> {
                    x = 1920 - 50;
                    y = 1080 - 50;
                }

                case 3 -> {
                    x = 1920 - 50;
                    y = 50;
                }
            }
            count++;
            players.add(new ServerPlayer(x, y, lp.getID()));
        }
    }

    public String serverTick() {
        String data = null;
        //move all projectiles
        for (Projectile projectile : projectiles) {
            projectile.move(this);
        }
        //move players and check shooting and collision for each player
        for (ServerPlayer player : players) {
            boolean[] keys = player.getKeys(serverTick);
            player.setCooldown(player.getCooldown() - 1);

            player.move(keys, this);
            //player shooting
            if (player.getKeys(serverTick)[4] && player.getCooldown() <= 0) {
                projectiles.add(new NormalProjectile(player.getX(), player.getY(), player.getAngle(), player.getVel(), player.getID()));
                player.setCooldown(Projectile.COOLDOWN);
            }
            //check if player is being hit by projectile and kill if they are
            for (int i = 0; i<projectiles.size();i++) {
                Projectile proj = projectiles.get(i);
                if (checkCollision(player, proj.getX(), proj.getY())) {
                    player.kill();
                    proj.kill();
                    if (player.getID().equals(id)) {
                        self.kill();
                        localProj.get(i).kill();
                    }
                }
            }
        }

        //create snapshot and convert it to base64
        Snapshot ss = new Snapshot(players.toArray(Player[]::new), projectiles.toArray(Projectile[]::new), System.currentTimeMillis());
        gc.addServerSnapshot(ss);
        try {
            data = serialize(ss);
        } catch (IOException e) {
            e.printStackTrace();
        }
        data = "3:" + data;
        serverTick++;
        return data;
    }

    public void localTick() {
        //get keys pressed and move projectiles
        keys = gc.kb.getKeys();
        //move projectile
        for (Projectile proj : localProj) {
            proj.move(this);
        }
        //dont let player move and stuff if they are dead
        if (!self.getIsDead()) {
            //lower projectile cooldowns
            if (self.getCooldown() > 0) {
                self.setCooldown(self.getCooldown() - 1);
            }
            //move self
            self.setKeys(keys, localTick);
            self.move(keys, this);
            //shoot projectile
            if (keys[4] && self.getCooldown() <= 0) {
                localProj.add(new NormalProjectile(self.getX(), self.getY(), self.getAngle(), self.getVel(), id));
                self.setCooldown(Projectile.COOLDOWN);
            }

            //send keys
            if (!isHost) {
                gf.sendKeys(keys, localTick);
            } else if (!inDebug) {
                this.players.get(0).setKeys(keys, localTick);
            }
        }
        
        ClientPlayer[] pArr = new ClientPlayer[]{self};
        gc.addLocalSnapshot(new Snapshot(pArr, localProj.toArray(Projectile[]::new), System.currentTimeMillis()));
        localTick++;
    }

    public boolean[][] getMap() {
        return map;
    }
    
    public void setLocalTick(int newTick){
        localTick = newTick;
    }

    public ServerPlayer[] getPlayers() {
        return players.toArray(ServerPlayer[]::new);
    }

    /**
     * a method to check the collision of projectiles with players
     *
     * @param p the player to check collision with
     * @param x the x coordinate of the point
     * @param y the y coordinate of the point
     * @return true if they are colliding
     */
    public static boolean checkCollision(Player p, double x, double y) {
        //subtract players position from the point then rotate based on players angle
        double a = -p.getAngle();
        double tx = x - p.getX();
        double ty = y - p.getY();
        double nx = (tx * Math.cos(a) - ty * Math.sin(a));
        double ny = (tx * Math.sin(a) + ty * Math.cos(a));

        //return true if it is inside the players bounds
        return (nx < 20 && nx > -35 && ny < 10 && ny > -10);
    }

    /**
     * checks whether a position is inside of a wall on the map
     *
     * @param x the x position to check
     * @param y the y position to check
     * @return true if colliding, false if not
     */
    public boolean checkPos(double x, double y) {
        int gridX = -1;
        int gridY = -1;
        int curX = 0;
        int curY = 0;

        //increase temp x position by tile size or wall size depending on position, and increase grid value until we reach the grid point that the x position is in
        do {
            gridX++;
            curX += (gridX % 2 == 0) ? GameCanvas.WALL_SIZE : tileWidth;
        } while (curX <= x);

        //increase temp y position by tile size or wall size depending on position, and increase grid value until we reach the grid point that the y position is in
        do {
            gridY++;
            curY += (gridY % 2 == 0) ? GameCanvas.WALL_SIZE : tileHeight;
        } while (curY <= y);

        //return collision if out of bounds
        if (gridY >= map.length || gridX >= map[0].length) {
            return true;
        }

        //return if the grid position is a wall or not
        return map[gridY][gridX];
    }

    public int getServerTick(){
        return this.serverTick;
    }
    
    /**
     * converts an object into a base64 encoded serialized string
     *
     * @param o the object to serialize
     * @return base64 encoded string containing the serialized data of this
     * object
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
     *
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
