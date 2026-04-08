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
  
  public void move(boolean[] arr){ //up[0], left[1], down[2], right[3] message by Layne Ripley
  
      if (arr[0] && !arr[2]){
          y -= 5;
      }
       if (arr[2] && !arr[0]){
          y += 5;
      }
       if (arr[1] && !arr[3]){
          x -= 5;
      }
        if (arr[3] && !arr[1]){
          y += 5;
      }
      
  }   
}

