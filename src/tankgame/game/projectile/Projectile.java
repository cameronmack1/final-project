package tankgame.game.projectile;

import java.util.UUID;

import tankgame.game.GameHandler;

/**
 *
 * @author layne
 */
public abstract class Projectile {

    public static final int COOLDOWN = 30;
    public static final int DEFAULT_SPEED = 10;
    protected boolean isNew = true;
    protected double x;
    protected double y;
    protected double angle;
    protected double velocity;
    protected final UUID owner;

    public Projectile(double x, double y, double angle, double velocity, UUID owner) {
        this.x = x + Math.cos(angle) * 50;
        this.y = y + Math.sin(angle) * 50;
        this.angle = angle;
        this.velocity = velocity/2 + DEFAULT_SPEED;
        this.owner = owner;
    }

    public double getVel() {
        return velocity;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getAngle() {
        return angle;
    }

    public boolean getIsNew() {
        return isNew;
    }

    public void move(GameHandler gh) {
        if (isNew) {
            isNew = false;
        }
        y += Math.sin(angle) * velocity;
        x += Math.cos(angle) * velocity;
    }
}
