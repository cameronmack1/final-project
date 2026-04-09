package tankgame.game;

import tankgame.server.PlayerHandler;
import tankgame.server.ServerPlayer;
import java.util.UUID;
import java.util.ArrayList;

import tankgame.menu.MainMenu;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.geom.AffineTransform;

import java.io.File;

import tankgame.client.ClientPlayer;

/**
 *
 * @author Cameron
 */
public final class GameScreen extends JFrame {

    KeyHandler kb = new KeyHandler();
    ArrayList<Player> playerList;
    int gameState;
    boolean isHost;
    BufferedImage img;
    private int width;
    private int height;
    public PlayerHandler playerHandler = new PlayerHandler();
    Graphics2D g2d;
    BufferedImage tank;

    public GameScreen() {
        //initialize the JFrame
        setExtendedState(MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        width = getWidth();
        height = getHeight();
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        g2d = img.createGraphics();
        getContentPane().add(new JLabel(new ImageIcon(img)));
        setAlwaysOnTop(true);
        setVisible(true);
        addKeyListener(kb);

        try {
            System.out.println(new File(".").getAbsolutePath());
            tank = resizeImage(60, 80, ImageIO.read(new File(".\\src\\images\\tank.png")));
        } catch (Exception e) {
            System.out.println("error loading file");
        }

        gameState = 10;
        this.initDebug();
    }

    public void initLobby(boolean isHost) {

    }

    public void initMenu() {
        MainMenu menu = new MainMenu();
    }

    public void initDebug() {
        playerList = new ArrayList<>();
        playerList.add(new ClientPlayer());
    }

    public void tick() {
        g2d.setColor(Color.GRAY);
        g2d.fillRect(0, 0, width, height);
        switch (gameState) {
            case 0: {//in menu

                break;
            }
            case 1: {//client in game
                g2d.setColor(Color.WHITE);
                for (ServerPlayer player : playerHandler.getPlayers()) {
                    g2d.fillOval(player.x, player.y, 10, 10);
                }
                break;
            }
            case 2: {//server in game

                break;
            }

            case 10: {//singleplayer debug
                for (Player player : playerList) {
                    player.move(kb.getKeys());
                    drawImageAtRot(tank, player.getX(), player.getY(), player.getX());
                }
                break;
            }
        }
        validate();
        repaint();
    }

    public BufferedImage resizeImage(int newWidth, int newHeight, BufferedImage img) {
        //scale
        Image image2 = img.getScaledInstance(newWidth, newHeight, Image.SCALE_DEFAULT);

        //create new bufferedimage
        BufferedImage rsImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

        //use graphics2d to draw resized image on bufferedimage then dispose
        Graphics2D bg2d = rsImage.createGraphics();
        bg2d.drawImage(image2, 0, 0, null);
        bg2d.dispose();

        return rsImage;
    }

    public void drawImageAtRot(Image img, double x, double y, double angle) {
        //transforms the entire base image, renders new image, and rotates it back
        AffineTransform old = g2d.getTransform();
        g2d.translate(x, y);
        g2d.rotate(angle);
        g2d.drawImage(img, -img.getWidth(null) / 2, -img.getHeight(null)/2, null);
        g2d.setTransform(old);
    }
}
