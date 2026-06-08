package tankgame.game;

import java.io.Serializable;

import java.util.HashMap;

/**
 *
 * @author Layne
 */
public abstract class Player implements Serializable {

    private static final long serialVersionUID = 8008L;

    private boolean[] keys = new boolean[]{false, false, false, false, false};
    private double x = 500;
    private double y = 500;
    private double angle;
    private double velocity = 0;
    private int projCooldown;
    public static final double BRAKING_POWER = 0.25;
    public static final double ACCELERATION = 0.5;
    public static final double TURN_SPEED = 1.0 / 67.0;
    public static final double WALL_THRESHOLD = 0.3;
    private boolean isDead = false;

    private HashMap<Integer, Boolean[]> inputMap = new HashMap<>();

    public Player(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Player(Player toBeCopied) {
        this.x = toBeCopied.getX();
        this.y = toBeCopied.getY();
        this.angle = toBeCopied.getAngle();
        this.velocity = toBeCopied.getVel();
    }

    public void setCooldown(int cooldown) {
        this.projCooldown = cooldown;
    }

    public int getCooldown() {
        return projCooldown;
    }

    public boolean[] getKeys(int tick) {
        try {
            Boolean[] old = new Boolean[5];
            for (int i = 0; i <= 10; i++) {
                 old = inputMap.get(tick - i);
                if (old != null) {
                    break;
                }
            }
            boolean[] inp = new boolean[old.length];
            for (int i = 0; i < inp.length; i++) {
                inp[i] = old[i];
            }
            return inp;
        } catch (NullPointerException e) {
            return new boolean[5];
        }
    }

    public void setKeys(boolean[] keys, int tick) {
        Boolean[] inp = new Boolean[keys.length];
        for (int i = 0; i < inp.length; i++) {
            inp[i] = keys[i];
        }
        inputMap.put(tick, inp);
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

    public void kill() {
        this.isDead = true;
    }

    public boolean getIsDead() {
        return this.isDead;
    }

    public void move(boolean[] keys, GameHandler gh) { //up[0], left[1], down[2], right[3] message by Layne Ripley
        if (keys[0] ^ keys[2]) {//if only one of forwards or backwards pressed
            if (keys[0]) {
                velocity += ACCELERATION;
            } else {
                velocity -= ACCELERATION;
            }
        } else if (Math.abs(velocity) > BRAKING_POWER) {//lower velocity by the braking power
            velocity -= Math.signum(velocity) * BRAKING_POWER;
        } else {
            velocity = 0;
        }
        if (velocity > 5) {
            velocity = 5;
        }
        if (velocity < -5) {
            velocity = -5;
        }
        double da = 0;
        if (keys[1]) {//pressing left
            da = -velocity * TURN_SPEED;
        }
        if (keys[3]) {//pressing right
            da = velocity * TURN_SPEED;
        }
        //change in x and y including the change in angle
        double dya = Math.sin(angle + da) * velocity;
        double dxa = Math.cos(angle + da) * velocity;

        //change in x and y excluding the change in angle
        double dy = Math.sin(angle) * velocity;
        double dx = Math.cos(angle) * velocity;

        //points for each corner of the tank with rotation
        //front right
        double dx1a = (20 * Math.cos(angle + da)) - (10 * Math.sin(angle + da)) + x;
        double dy1a = (20 * Math.sin(angle + da)) + (10 * Math.cos(angle + da)) + y;

        //back right
        double dx2a = (-35 * Math.cos(angle + da)) - (10 * Math.sin(angle + da)) + x;
        double dy2a = (-35 * Math.sin(angle + da)) + (10 * Math.cos(angle + da)) + y;

        //front left
        double dx3a = (20 * Math.cos(angle + da)) - (-10 * Math.sin(angle + da)) + x;
        double dy3a = (20 * Math.sin(angle + da)) + (-10 * Math.cos(angle + da)) + y;

        //back left
        double dx4a = (-35 * Math.cos(angle + da)) - (-10 * Math.sin(angle + da)) + x;
        double dy4a = (-35 * Math.sin(angle + da)) + (-10 * Math.cos(angle + da)) + y;

        //front middle
        double dx5a = (20 * Math.cos(angle + da)) + x;
        double dy5a = (20 * Math.sin(angle + da)) + y;

        //back middle
        double dx6a = (-35 * Math.cos(angle + da)) + x;
        double dy6a = (-35 * Math.sin(angle + da)) + y;

        //if the tank cannot be moved and rotated, move but do not rotate
        //points for each corner of the tank without rotation
        //front right
        double dx1 = (20 * Math.cos(angle)) - (10 * Math.sin(angle)) + x;
        double dy1 = (20 * Math.sin(angle)) + (10 * Math.cos(angle)) + y;

        //back right
        double dx2 = (-35 * Math.cos(angle)) - (10 * Math.sin(angle)) + x;
        double dy2 = (-35 * Math.sin(angle)) + (10 * Math.cos(angle)) + y;

        //front left
        double dx3 = (20 * Math.cos(angle)) - (-10 * Math.sin(angle)) + x;
        double dy3 = (20 * Math.sin(angle)) + (-10 * Math.cos(angle)) + y;

        //back left
        double dx4 = (-35 * Math.cos(angle)) - (-10 * Math.sin(angle)) + x;
        double dy4 = (-35 * Math.sin(angle)) + (-10 * Math.cos(angle)) + y;

        //front middle
        double dx5 = (20 * Math.cos(angle)) + x;
        double dy5 = (20 * Math.sin(angle)) + y;

        //back middle
        double dx6 = (-35 * Math.cos(angle)) + x;
        double dy6 = (-35 * Math.sin(angle)) + y;

        boolean mx = false;
        boolean my = false;

        //move in x direction if all points are able to
        if (!(gh.checkPos(dx1a + dxa, dy1a) || gh.checkPos(dx2a + dxa, dy2a) || gh.checkPos(dx3a + dxa, dy3a) || gh.checkPos(dx4a + dxa, dy4a) || gh.checkPos(dx5a + dxa, dy5a) || gh.checkPos(dx6a + dxa, dy6a))) {
            x += dxa;
            dx1a += dxa;
            dx2a += dxa;
            dx3a += dxa;
            dx4a += dxa;
            dx5a += dxa;
            dx6a += dxa;
            angle += da / 2;
            mx = true;
        }
        //move in y direction if possible
        if (!(gh.checkPos(dx1a, dy1a + dya) || gh.checkPos(dx2a, dy2a + dya) || gh.checkPos(dx3a, dy3a + dya) || gh.checkPos(dx4a, dy4a + dya) || gh.checkPos(dx5a, dy5a + dya) || gh.checkPos(dx6a, dy6a + dya))) {
            y += dya;
            angle += da / 2;
            my = true;
        }

        //move in x direction if all points are able to
        if (!(mx || gh.checkPos(dx1 + dx, dy1) || gh.checkPos(dx2 + dx, dy2) || gh.checkPos(dx3 + dx, dy3) || gh.checkPos(dx4 + dx, dy4) || gh.checkPos(dx5 + dx, dy5) || gh.checkPos(dx6 + dx, dy6))) {
            x += dx;
            dx1 += dx;
            dx2 += dx;
            dx3 += dx;
            dx4 += dx;
            dx5 += dx;
            dx6 += dx;
            mx = true;
        }
        //move in y direction if possible
        if (!(my || gh.checkPos(dx1, dy1 + dy) || gh.checkPos(dx2, dy2 + dy) || gh.checkPos(dx3, dy3 + dy) || gh.checkPos(dx4, dy4 + dy) || gh.checkPos(dx5, dy5 + dy) || gh.checkPos(dx6, dy6 + dy))) {
            y += dy;
            my = true;
        }

        //if hitting wall and almost perpendicular, lower velocity for a faster turnaround
        //horizontal wall
        if (mx && !my && Math.abs(dx) < WALL_THRESHOLD * Math.abs(velocity)) {
            velocity *= 0.2;
        }

        //vertical wall
        if (my && !mx && Math.abs(dy) < WALL_THRESHOLD * velocity) {
            velocity *= 0.2;
        }
    }
}
