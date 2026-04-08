package tankgame.game;

import tankgame.server.PlayerHandler;
import tankgame.server.ServerPlayer;
import java.util.UUID;
import java.util.ArrayList;

import tankgame.menu.MainMenu;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.ImageIcon;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Color;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

    public GameScreen() {
        setExtendedState(MAXIMIZED_BOTH);//initialize the screen
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        width = getWidth();
        height = getHeight();
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        g2d = img.createGraphics();
        getContentPane().add(new JLabel(new ImageIcon(img)));
        setVisible(true);
        addKeyListener(kb);

        gameState = 10;
        this.initDebug();

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            this.tick();
        }, 0, 1000 / 60, TimeUnit.MILLISECONDS);
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
        g2d.setColor(Color.BLACK);
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
                g2d.setColor(Color.WHITE);
                for (Player player : playerList) {
                    player.move(kb.getKeys());
                    g2d.drawOval((int) player.getX(), (int) player.getY(), 25, 25);
                }
                break;
            }
        }
        validate();
        repaint();
    }
}
