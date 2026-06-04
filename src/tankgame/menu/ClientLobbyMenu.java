package tankgame.menu;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import tankgame.game.GameFrame;
import tankgame.server.LobbyPlayer;
import java.awt.Color;

/**
 *
 * @author Cameron
 */
public class ClientLobbyMenu extends JPanel {

    private ArrayList<LobbyPlayer> playerList = new ArrayList<>();
    private JLabel[] playerLabels;

    private Image bg;
    private GameFrame gf;
    private int w;
    private int h;
    private Font f;

    public ClientLobbyMenu(GameFrame gf) {
        //initialize
        setLayout(null);
        this.gf = gf;
        w = gf.getWidth();
        h = gf.getHeight();

        //font
        f = new Font("Comic Sans", Font.PLAIN, h / 18);

        //load images
        try {
            bg = ImageIO.read(new File("src" + File.separator + "images" + File.separator + "LobbyMenuForest.png")).getScaledInstance(w, h, Image.SCALE_DEFAULT);
        } catch (IOException e) {
            System.out.println("error loading file");
        } catch (NullPointerException e) {
            System.out.println("error file missing wtf did u do");
            e.printStackTrace();
        }
        
        //buttons
        JButton leaveButton = new JButton("Leave Game");
        leaveButton.setBounds(0, 0, w / 5, h / 10);
        add(leaveButton);
        leaveButton.setVisible(true);
        leaveButton.addActionListener(al -> {
            gf.leaveGame();
        });
        leaveButton.setFont(f);
    }

    public void refreshLabels() {
        playerLabels = new JLabel[playerList.size()];
        for (int i = 0; i < playerLabels.length; i++) {
            JLabel label = new JLabel(playerList.get(i).getName());
            label.setBounds(w / 8, h / 8 * (i + 1), w / 3, h / 10);
            add(label);
            label.setVisible(true);
            label.setFont(f);
            label.setForeground(Color.WHITE);
        }
    }

    public void startGame() {
        gf.initServer();
    }

    public void startDebug() {
        gf.startDebug();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bg, 0, 0, this);
    }

    public void addPlayer(LobbyPlayer player) {
        playerList.add(player);
        refreshLabels();
    }

    public void removePlayer(UUID id) {
        for (int i = 0; i < playerList.size(); i++) {
            if (playerList.get(i).getID() == id) {
                playerList.remove(i);
                break;
            }
        }
        refreshLabels();
    }
}
