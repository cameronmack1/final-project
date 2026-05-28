package tankgame.game;

import java.util.ArrayList;
import java.util.HashSet;

import tankgame.game.Render.GameCanvas;
import tankgame.game.Render.Snapshot;
import tankgame.game.projectile.Projectile;
import tankgame.client.ClientPlayer;
import tankgame.server.*;
import java.util.UUID;

import java.io.IOException;

/**
 *
 * @author Cameron
 */
public class GameHandler {

    private GameFrame gf;
    private GameCanvas gc;
    //server stuff
    private ArrayList<ServerPlayer> players = new ArrayList<>();
    private ArrayList<Projectile> projectiles = new ArrayList<>();

    //local stuff
    boolean[] keys;
    ClientPlayer self;
    private volatile ArrayList<Projectile> localProj = new ArrayList<>();

    public GameHandler(GameFrame gf) {
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

    public void initServer() {

    }

    public String serverTick() {
        String data = null;
        for (Projectile projectile : projectiles) {
            projectile.move();
        }
        for (ServerPlayer player : players) {
            player.setCooldown(player.getCooldown() - 1);

            player.move(player.getKeys());
            //player shooting
            if (player.getKeys()[4] && player.getCooldown() <= 0) {
                projectiles.add(new Projectile(player.getX(), player.getY(), player.getAngle(), player.getVel(), player.getRID()));
                player.setCooldown(Projectile.COOLDOWN);
            }
        }

        //create snapshot and convert it to base64
        Snapshot ss = new Snapshot(players.toArray(Player[]::new), projectiles.toArray(Projectile[]::new), System.currentTimeMillis());
        try {
            data = ss.serialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
        data = "2:" + data;
        return data;
    }

    public void initDebug() {
        self = new ClientPlayer(500, 500, 0);
        Snapshot defaultSnapshot = new Snapshot(new ClientPlayer[]{self}, localProj.toArray(Projectile[]::new), System.currentTimeMillis());
        gc.addSnapshot(defaultSnapshot);
        gc.addSnapshot(defaultSnapshot);
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
            localProj.add(new Projectile(self.getX(), self.getY(), self.getAngle(), self.getVel(), self.getRID()));
            self.setCooldown(Projectile.COOLDOWN);
        }
        //move self
        self.setKeys(keys);
        self.move(keys);
        ClientPlayer[] pArr = new ClientPlayer[]{new ClientPlayer(self)};
        gc.addSnapshot(new Snapshot(pArr, localProj.toArray(Projectile[]::new), System.currentTimeMillis()));
    }
}
