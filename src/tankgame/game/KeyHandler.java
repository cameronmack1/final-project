package tankgame.game;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 *
 * @author Cameron
 */
public class KeyHandler extends KeyAdapter {
    private boolean[] keys = {false, false, false, false, false};
    
    public boolean[] getKeys(){
        return keys;
    }
    
    @Override
    public void keyPressed(KeyEvent e){
        switch(e.getKeyCode()){
            case KeyEvent.VK_W:{
                keys[0] = true;
                break;
            }
            case KeyEvent.VK_A:{
                keys[1] = true;
                break;
            }
            case KeyEvent.VK_S:{
                keys[2] = true;
                break;
            }
            case KeyEvent.VK_D:{
                keys[3] = true;
                break;
            }
            case KeyEvent.VK_SPACE:{
                keys[4] = true;
                break;
            }
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e){
        switch(e.getKeyCode()){
            case KeyEvent.VK_W:{
                keys[0] = false;
                break;
            }
            case KeyEvent.VK_A:{
                keys[1] = false;
                break;
            }
            case KeyEvent.VK_S:{
                keys[2] = false;
                break;
            }
            case KeyEvent.VK_D:{
                keys[3] = false;
                break;
            }
            case KeyEvent.VK_SPACE:{
                keys[4] = false;
                break;
            }
        }
    }
}
