package tankgame.menu;

import javax.swing.JPanel;
import java.awt.Image;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import tankgame.game.GameFrame;
import tankgame.server.LobbyPlayer;
import java.util.UUID;
import java.util.ArrayList;
import javax.sound.sampled.*;           // imports sounds

/**
 * shoutout @wwavely
 *
 * @author Koorosh
 */
public class HostLobbyMenu extends JPanel {

    private ArrayList<LobbyPlayer> playerList = new ArrayList<>();

    private Image bg;
    private GameFrame gf;
    private int w;
    private int h;
    private JLabel[] playerLabels;
    private Font f;
    private  int RNG = (int)(Math.random() * 100);

    public HostLobbyMenu(GameFrame gf) {
     

        //initialize
        setLayout(null);
        this.gf = gf;
         String uname = gf.getUsername().toLowerCase();
        w = gf.getWidth();
        h = gf.getHeight();
           if (uname.contains("sahur")) {
            playSound("src/sounds/sahur.wav");
        } else if (uname.contains("tung")) {
            playSound("src/sounds/sahur.wav");
        } else if (RNG == 67) {
            playSound("src/sounds/sahur.wav");
        } else if (RNG == 41 || RNG == 61) {
            playSound("src/sounds/tiki.wav");
        } else {
            playSound("src/sounds/lobby.wav");
        }

        //font
        f = new Font("Comic Sans", Font.PLAIN, h / 18);

        //load images
        try {
            bg = ImageIO.read(ClassLoader.getSystemResource("images/LobbyMenuForest.png")).getScaledInstance(w, h, Image.SCALE_DEFAULT);
        } catch (IOException e) {
            System.out.println("error loading file");
        } catch (NullPointerException e) {
            System.out.println("error file missing wtf did u do");
            e.printStackTrace();
        }

        //create buttons
        JButton startButton = new JButton("Start Game");
        startButton.setBounds(w / 2 - w / 10, 4 * h / 6 - h / 10, w / 5, h / 10);
        add(startButton);
        startButton.setVisible(true);
        startButton.addActionListener(al -> {
            startGame();
        });
        startButton.setFont(f);

        JButton debug = new JButton("Debug");
        debug.setBounds(0, h - h / 10, w / 7, h / 10);
        add(debug);
        debug.setVisible(true);
        debug.addActionListener(al -> {
            startDebug();
        });
        debug.setFont(f);

        playerLabels = new JLabel[0];
        setVisible(true);
    }

    public void startGame() {
        gf.initServer();
    }

    public void startDebug() {
        gf.startDebug();
    }

    public LobbyPlayer[] getPlayerList(){
        return playerList.toArray(LobbyPlayer[]::new);
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

    public void playSound(String filename) { //play sound effects
        try {
            InputStream audioSrc = getClass().getResourceAsStream(filename);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioSrc);
            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(audioStream);
            clip.start(); //play the sound
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void removePlayer(UUID id) {
        for (int i = 0; i < playerList.size(); i++) {
            if (id.equals(playerList.get(i).getID())) {
                System.out.println(id + " " + i + " " + playerList.get(i).getName());
                playerList.remove(i);
                refreshLabels();
                break;
            }
        }
        System.out.println(playerList.size());
    }

    public void refreshLabels() {
        if (playerLabels.length != 0) {
            for (JLabel label : playerLabels) {
                remove(label);
            }
        }
        playerLabels = new JLabel[playerList.size()];
        for (int i = 0; i < playerLabels.length; i++) {
            playerLabels[i] = new JLabel(playerList.get(i).getName());
            playerLabels[i].setBounds(w / 8, h / 8 * (i + 1), w / 3, h / 10);
            add(playerLabels[i]);
            playerLabels[i].setVisible(true);
            playerLabels[i].setFont(f);
            playerLabels[i].setForeground(Color.WHITE);
        }
        revalidate();
        repaint();
    }
}
