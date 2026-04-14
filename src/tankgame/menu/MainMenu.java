package tankgame.menu;

import javax.swing.JPanel;
import java.awt.Image;
import javax.swing.*;
import java.awt.*;

/**
 *
 * @author Koorosh
 */
public class MainMenu extends JPanel {

    //test 
    private JButton playButton;
    private JButton settingsButton;
    private JButton quitButton;
    private JTextField lobbyCode;
    private JTextField cheatCode;

    public MainMenu() {

        //make buttons stack
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        //create the buttons
        playButton = new JButton("Play");
        settingsButton = new JButton("Settings");
        quitButton = new JButton("Quit");
        lobbyCode = new JTextField("Lobby Code");
        cheatCode = new JTextField("Cheat Code");
        JLabel lobbyLabel = new JLabel();
        JLabel cheatLabel = new JLabel();
        addPlaceholder(lobbyCode, "Lobby Code");
        addPlaceholder(cheatCode, "Cheat Code");
        lobbyCode.setMaximumSize(new Dimension(200, 30));
        cheatCode.setMaximumSize(new Dimension(200, 30));
        lobbyCode.setAlignmentX(Component.CENTER_ALIGNMENT);
        cheatCode.setAlignmentX(Component.CENTER_ALIGNMENT);

        //style the buttons
        styleButton(playButton);
        styleButton(settingsButton);
        styleButton(quitButton);

        quitButton.addActionListener(e -> System.exit(0));

        //push buttons to center
        add(Box.createVerticalGlue());
        add(lobbyLabel);
        add(lobbyCode);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(cheatLabel);
        add(cheatCode);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(playButton);
        add(Box.createRigidArea(new Dimension(0, 15))); //gap between the buttons
        add(settingsButton);
        add(Box.createRigidArea(new Dimension(0, 15)));
        add(quitButton);
        add(Box.createVerticalGlue());
    }

    private void styleButton(JButton button) {

        button.setPreferredSize(new Dimension(200, 50));
        button.setMaximumSize(new Dimension(200, 50));
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setBackground(Color.DARK_GRAY);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT); // centers horizontally

    }

    
    //placeholder text for the text fields
    private void addPlaceholder(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(Color.GRAY);

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);

                }
            }
        });
    }

}
