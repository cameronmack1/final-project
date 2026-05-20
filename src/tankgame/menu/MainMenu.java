package tankgame.menu;

import javax.swing.JPanel;
import java.awt.Image;
import javax.swing.*;
import java.awt.*;
import tankgame.game.GameFrame;

/**
 * vvvvv that guys a poopyhead
 *
 * @author Koorosh
 */
public class MainMenu extends JPanel {

    private GameFrame gf;
    int w;
    int h;

    public MainMenu(GameFrame gf) {
        setLayout(null);
        this.gf = gf;
        w = gf.getWidth();
        h = gf.getHeight();

        JButton startButton = new JButton("Start");
        startButton.setBounds(w / 2 - w / 14, 5 * h / 8 - h / 14, w / 14, h / 14);
        add(startButton);
        startButton.setVisible(true);
        startButton.addActionListener(al -> {
            startGame();
        });

        setVisible(true);
    }

    public void startGame() {
        gf.startGame();
    }
}
