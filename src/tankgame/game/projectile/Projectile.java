package tankgame.game.projectile;

/**
 *
 * @author layne
 */
public class Projectile {

    public static final int COOLDOWN = 30;
    public static final int DEFAULT_SPEED = 10;
    private boolean isNew = true;
    private double x;
    private double y;
    private double angle;
    private double velocity;
    private final int rid;

    public Projectile(double x, double y, double angle, double velocity, int rid) {
        this.x = x + Math.cos(angle) * 50;
        this.y = y + Math.sin(angle) * 50;
        this.angle = angle;
        this.velocity = velocity/2 + DEFAULT_SPEED;
        this.rid = rid;
    }

    public double getRID() {
        return rid;
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

    public void move() {
        if (isNew) {
            isNew = false;
        }
        y += Math.sin(angle) * velocity;
        x += Math.cos(angle) * velocity;
    }
}
