package tankgame.game;

import tankgame.game.Render.GameCanvas;
import java.util.ArrayList;
import tankgame.game.Render.Snapshot;
import tankgame.game.projectile.Projectile;
import tankgame.client.ClientPlayer;

/**
 *
 * @author Cameron
 */
public class GameHandler {

    private GameCanvas gc;

    //local stuff
    int projCooldown;
    boolean[] keys;
    ClientPlayer self;
    private volatile ArrayList<Projectile> localProj = new ArrayList<>();

    public void setCanvas(GameCanvas canvas) {
        gc = canvas;
    }

    public void serverTick() {

    }
    
    public void initLocal(){
        self = new ClientPlayer(0);
        Snapshot defaultSnapshot = new Snapshot(new ClientPlayer[]{self}, localProj.toArray(Projectile[]::new), System.currentTimeMillis());
        gc.addSnapshot(defaultSnapshot);
        gc.addSnapshot(defaultSnapshot);
        gc.initLocal();
    }

    public void localTick() {
        //move self
        keys = gc.kb.getKeys();
        self.move(keys);
        //move projectile
        for (Projectile proj : localProj) {
            proj.move();
        }
        //lower projectile cooldowns
        if (projCooldown > 0) {
            projCooldown--;
        }
        //shoot projectile
        if (keys[4] && projCooldown == 0) {
            localProj.add(new Projectile(self.getX(), self.getY(), self.getAngle(), self.getVel(), self.getRID()));
            projCooldown += Projectile.COOLDOWN;
        }
        ClientPlayer[] pArr = new ClientPlayer[]{new ClientPlayer(self, 0)};
        gc.addSnapshot(new Snapshot(pArr, localProj.toArray(Projectile[]::new), System.currentTimeMillis()));
    }
}
