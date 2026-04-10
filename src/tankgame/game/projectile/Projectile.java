package tankgame.game.projectile;

/**
 *
 * @author layne
 */
public abstract class Projectile {

    private double x;
    private double y;
    double angle;
    private double velocity;
    
    public void move(){
        y += Math.sin(angle) * velocity;
        x += Math.cos(angle) * velocity;
    }
}
