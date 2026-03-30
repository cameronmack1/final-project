package tankgame.game;

import java.util.UUID;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.ImageIcon;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Color;

/**
 *
 * @author Cameron
 */
public class GameScreen extends JFrame {

    int gameState;
    boolean isHost;
    BufferedImage img;
    private int width;
    private int height;
    public PlayerHandler playerHandler = new PlayerHandler();
    Graphics2D g2d;

    public GameScreen(boolean isHost) {
        this.isHost = isHost;
        setExtendedState(MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        width = getWidth();
        height = getHeight();
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        g2d = img.createGraphics();
        getContentPane().add(new JLabel(new ImageIcon(img)));
    }

    public void tick() {
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, width, height);
        
        g2d.setColor(Color.WHITE);
        for(Player player : playerHandler.getPlayers()){
            g2d.fillOval(player.x, player.y, 10, 10);
        }
        
        validate();
        repaint();
    }
}
