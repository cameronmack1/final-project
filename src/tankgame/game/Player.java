package tankgame.game;
/**
 *
 * @author Layne
 */
public abstract class Player {
   private double x = 0;
   private double y = 0;
   private double angle;
    
  public double getX(){
      return x;
  }
  public double getY(){
      return y;
  }
  
  public void move(boolean up, boolean left, boolean down, boolean right){
  
      if (up && !down){
          y -= 5;
      }
       if (down && !up){
          y += 5;
      }
       if (left && !right){
          x -= 5;
      }
        if (right && !left){
          y += 5;
      }
      
  }   
}

