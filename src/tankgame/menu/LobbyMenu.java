package tankgame.menu;

import javax.swing.JPanel;
import java.awt.Image;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import tankgame.game.GameFrame;

/**
 * vvvvv that guys a poopyhead
 *
 * @author Layne */
public class LobbyMenu extends JPanel {

    private Image bg;
    private GameFrame gf;
    private int w;
    private int h;
    private int testVariable;
    private int e;

    public LobbyMenu(GameFrame gf) {
        //initialize
        setLayout(null);
        this.gf = gf;
        w = gf.getWidth();
        h = gf.getHeight();
        
        //font
        Font f = new Font("Comic Sans", Font.PLAIN, h/18);

        //load images
        try {
            bg = ImageIO.read(new File("src" + File.separator + "images" + File.separator + "LobbyMenuForest.png")).getScaledInstance(w, h, Image.SCALE_DEFAULT);
        } catch (IOException e) {
            System.out.println("error loading file");
        } catch (NullPointerException e) {
            System.out.println("error file missing wtf did u do");
            e.printStackTrace();
        }

        //create buttons
        JButton startButton = new JButton("Host Game");
        startButton.setBounds(w / 2 - w / 10, 4 * h / 6 - h / 10, w / 5, h / 10);
        add(startButton);
        startButton.setVisible(true);
        startButton.addActionListener(al -> {
            hostGame();
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
        setVisible(true);
    }

    public void hostGame() {

    }

    public void startDebug() {
        gf.startDebug();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bg, 0, 0, this);
    }
}
