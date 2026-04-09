package tankgame.game;

/**
 *
 * @author Layne
 */
public abstract class Player {

    private double x = 0;
    private double y = 0;
    private double angle;
    public double acceleration = 0;

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
            if (acceleration > 0 && keys[1]) {
                angle += 0.1;
                acceleration += 0.1;
            }
            if (keys[0] && keys[3]) {
                angle -= 0.1;
                acceleration += 0.1;
            }
            if(keys[2] && keys[1]){
              angle += 0.1;
              acceleration -= 0.1;
          }
            if(keys[2] && keys[3]){
              angle -= 0.1;
              acceleration -= 0.1;
          }
     
        }
        y += Math.sin(angle) * acceleration;
        x += Math.cos(angle) * acceleration;
        
    }

}

