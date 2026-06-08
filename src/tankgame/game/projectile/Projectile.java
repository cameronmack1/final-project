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
        this.x = x + Math.cos(angle) * 15;
        this.y = y + Math.sin(angle) * 15;
        this.angle = angle;
        this.velocity = velocity / 2 + DEFAULT_SPEED;
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
        double dy = Math.sin(angle) * velocity;
        double dx = Math.cos(angle) * velocity;

        //check collision after moving in x direction, then move
        if (gh.checkPos(x + dx, y) || gh.checkPos(x + dx / 2, y)) {
            dx = -dx;
        }
        x += dx;

        //check collision after moving in y direction, then move
        if (gh.checkPos(x, y + dy) || gh.checkPos(x, y + dy / 2)) {
            dy = -dy;
        }
        y += dy;

        //set angle (will be the same if no collision)
        this.angle = Math.atan2(dy, dx);
    }
}
