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
    private boolean chatOpen = false;
    private boolean justOpened = false;
    private StringBuilder chatText = new StringBuilder();
    private String outgoing = null;

    public boolean isChatOpen() {
        return chatOpen;
    }

    public String getChatText() {
        return chatText.toString();
    }

    public String pollOutgoing() {
        String temp = outgoing;
        outgoing = null;
        return temp;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (chatOpen) {
            if (justOpened) {
                justOpened = false;
                return;
            }
            char c = e.getKeyChar();
            if (!Character.isISOControl(c)) {
                chatText.append(c);
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (chatOpen) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                outgoing = chatText.toString();
                chatText.setLength(0);
                chatOpen = false;
            } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE && chatText.length() > 0) {
                chatText.deleteCharAt(chatText.length() - 1);
            } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                chatText.setLength(0);
                chatOpen = false;
            }
            return;
        }
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
            case KeyEvent.VK_T: {
                //opens chat
                chatOpen = true;
                justOpened = true;
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
