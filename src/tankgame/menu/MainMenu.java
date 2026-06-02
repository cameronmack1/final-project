package tankgame.menu;

import tankgame.client.UDPScanner;
import tankgame.client.ServerObject;
import javax.swing.JPanel;
import java.awt.Image;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import tankgame.game.GameFrame;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * vvvvv that guys a poopyhead
 *
 * @author Layne
 */
public class MainMenu extends JPanel {

    private Image bg;
    private GameFrame gf;
    private int w;
    private int h;
    private JTextField username;

    public MainMenu(GameFrame gf, int width, int height) {
        //initialize
        setLayout(null);
        this.gf = gf;
        w = width;
        h = height;

        //font
        Font f = new Font("Comic Sans", Font.PLAIN, h / 18);

        //load images
        try {
            bg = ImageIO.read(new File("src" + File.separator + "images" + File.separator + "mainMenu.png")).getScaledInstance(w, h, Image.SCALE_DEFAULT);

        } catch (IOException e) {
            System.out.println("error loading file");
        } catch (NullPointerException e) {
            System.out.println("error file missing wtf did u do");
            e.printStackTrace();
        }
        JOptionPane enterName = new JOptionPane("Please enter a valid username yo.");
        this.add(enterName);
        enterName.setBounds(0, h - h / 11, w / 7, h / 10);
        enterName.setVisible(false);
        //create buttons
        username = new JTextField("Username");
        username.setBounds(w / 2 - w / 10, 4 * h / 6 - h / 7, w / 5, h / 10);
        add(username);
        username.setFont(f);
        username.setHorizontalAlignment(JTextField.CENTER);
        username.setVisible(true);
        username.setForeground(Color.GRAY);
        setVisible(true);

        username.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                if (username.getText().equals("Username")) {
                    username.setText("");
                    username.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (username.getText().trim().isEmpty()) {
                    username.setText("Username");
                    username.setForeground(Color.GRAY);
                }
            }
        });

        JButton startButton = new JButton("Host Game");
        startButton.setBounds(w / 2 - w / 10, 4 * h / 5 - h / 9, w / 5, h / 10);
        add(startButton);
        startButton.setVisible(true);
        startButton.addActionListener(al -> {
            String name = username.getText().trim();

            if (name.isEmpty() || name.equalsIgnoreCase("Username")) {
                JOptionPane.showMessageDialog(this, "Please enter a valid username yo", "yo", JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            enterName.setVisible(false); // hide error

            hostGame();
        });
        startButton.setFont(f);

        JButton debug = new JButton("Debug");
        debug.setBounds(0, h - h / 11, w / 7, h / 10);
        add(debug);
        debug.setVisible(true);
        debug.addActionListener(al -> {
            startDebug();
        });
        debug.setFont(f);

        JButton scan = new JButton("Find Lobby");
        scan.setBounds(w / 2 - w / 10, 4 * h / 5, w / 5, h / 10);
        add(scan);
        scan.setVisible(true);
        scan.addActionListener(al -> {
            String name = username.getText().trim();

            if (name.isEmpty() || name.equalsIgnoreCase("Username")) {
                JOptionPane.showMessageDialog(this, "Please enter a valid username yo", "yo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            gf.openScanMenu();
        });
        scan.setFont(f);

    }

    public String getUsername() {
        return username.getText();
    }

    public void hostGame() {
        gf.initServerLobby();
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
