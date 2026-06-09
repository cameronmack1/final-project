package tankgame.game;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 *
 * @author Cameron
 */
public class KeyHandler extends KeyAdapter {

    private boolean[] keys = {false, false, false, false, false};

    public boolean[] getKeys() {
        return keys;
    }
    private boolean paused = false;
    private boolean iQUIT = false;

    public boolean isPaused() {
        return paused;
    }

    public boolean getiQUIT() {
        return iQUIT;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W: {
                keys[0] = true;
                break;
            }
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A: {
                keys[1] = true;
                break;
            }
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S: {
                keys[2] = true;
                break;
            }
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D: {
                keys[3] = true;
                break;
            }
            case KeyEvent.VK_SPACE: {
                keys[4] = true;
                break;
            }
            case KeyEvent.VK_ESCAPE: {
                paused = !paused;
                break;
            }
            case KeyEvent.VK_Q: {
               //double quit = you LEAVE
                if (paused) {
                    iQUIT = true;
                }
                break;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W: {
                keys[0] = false;
                break;
            }
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A: {
                keys[1] = false;
                break;
            }
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S: {
                keys[2] = false;
                break;
            }
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D: {
                keys[3] = false;
                break;
            }
            case KeyEvent.VK_SPACE: {
                keys[4] = false;
                break;
            }
        }
    }
}
