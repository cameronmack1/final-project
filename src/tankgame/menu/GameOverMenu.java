package tankgame.menu;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import tankgame.game.GameFrame;
import java.awt.Color;

/**
 *
 * @author Koorosh
 */
public class GameOverMenu extends JPanel {

    Image bg;
    Image tank;
    private int w;
    private int h;

    public GameOverMenu(GameFrame gf, String winnerName) {
        //load images
        setLayout(null);
        w = gf.getWidth();
        h = gf.getHeight();

        System.out.println(w + " " + h);

        //font
        Font f = new Font("Comic Sans", Font.PLAIN, h / 18);

        try {
            bg = ImageIO.read(new File("src" + File.separator + "images" + File.separator + "mainMenu.png")).getScaledInstance(w, h, Image.SCALE_DEFAULT);
            tank = ImageIO.read(new File("src" + File.separator + "images" + File.separator + "tank.png")).getScaledInstance(w / 7, 2*h / 5, Image.SCALE_DEFAULT);
        } catch (IOException e) {
            System.out.println("error loading file");
        } catch (NullPointerException e) {
            System.out.println("error file missing wtf did u do");
            e.printStackTrace();
        }

        //buttons n labels
        JLabel nameLabel = new JLabel(winnerName+" has won the game!");
        nameLabel.setForeground(Color.BLACK);
        nameLabel.setBounds(w / 2 - w / 6, h / 3, w, h / 5);
        nameLabel.setFont(f);
        add(nameLabel);
        nameLabel.setVisible(true);

        JButton exit = new JButton("Exit to Menu");
        exit.setBounds(w / 2 - w / 10, 4 * h / 5, w / 5, h / 10);
        add(exit);
        exit.addActionListener(al -> {
            gf.exitToMenu();
        });
        exit.setFont(f);
        exit.setVisible(true);
    }

    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bg, 0, 0, this);
        Graphics2D g2d = (Graphics2D) g.create();
        
        g2d.translate(w/2+2/10, h/2+h/7);
        g2d.rotate(Math.PI/4);
        g2d.drawImage(tank, -w/10, -h/5, this);
        g2d.dispose();
    }
}
