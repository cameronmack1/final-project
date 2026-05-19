package tankgame.game;

/**
 *
 * @author Layne
 */
public abstract class Player {
    private int rid;
    private double x = 500;
    private double y = 500;
    private double angle;
    private double velocity = 0;
    public static final double BRAKING_POWER = 0.25;
    public static final double ACCELERATION = 0.5;
    
    public Player(){}
    
    public Player(Player toBeCopied){
        this.rid = toBeCopied.getRID();
        this.x = toBeCopied.getX();
        this.y = toBeCopied.getY();
        this.angle = toBeCopied.getAngle();
        this.velocity = toBeCopied.getVel();
    }
    
    public int getRID(){
        return rid;
    }
    
    public double getVel(){
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

    public void move(boolean[] keys) { //up[0], left[1], down[2], right[3] message by Layne Ripley
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
        if (keys[1]) {//pressing left
            angle -= velocity / 50;
        }
        if (keys[3]) {//pressing right
            angle += velocity / 50;
        }
        y += Math.sin(angle) * velocity;
        x += Math.cos(angle) * velocity;

    }

}
