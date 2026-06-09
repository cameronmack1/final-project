package tankgame.menu;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import tankgame.game.GameFrame;
import javax.swing.JPanel;
import tankgame.client.ServerObject;
import javax.swing.JButton;
import tankgame.client.UDPScanner;

/**
 *
 * @author layne
 */
public class FindLobby extends JPanel {

    private Image tuffBG;
    private GameFrame gf;
    private int w;
    private int h;
    private int e;
    private JButton[] buttons;
    private ServerObject[] servers;

    public FindLobby(GameFrame gf, ServerObject[] servers) {
        //initialize
        setLayout(null);
        this.gf = gf;
        w = gf.getWidth();
        h = gf.getHeight();
        this.servers = servers;
        
        //font
        Font f = new Font("Comic Sans", Font.PLAIN, h / 18);
       JButton refresh = new JButton("refresh");
         refresh.setBounds(0, h - h / 11, w / 7, h / 10);
         add(refresh);
         refresh.setVisible(true);
        try {
            tuffBG =ImageIO.read(ClassLoader.getSystemResource("images/TuffTuffTuffBackground.png")).getScaledInstance(w, h, Image.SCALE_DEFAULT);

        } catch (IOException e) {
            System.out.println("error loading file");
        } catch (NullPointerException e) {
            System.out.println("error file missing wtf did u do");
            e.printStackTrace();
        }

        for (int i = 0; i < servers.length; i++) {
            final int index = i;
            JButton lobby = new JButton(servers[i].getName() + "'s server");
            lobby.setBounds(w / 2 - w / 10, h / 8 * (i + 1), w / 3, h / 10);
            add(lobby);
            lobby.setVisible(true);
            lobby.addActionListener(al -> {
                gf.joinServer(servers[index]);
            });
            lobby.setFont(f);
        }
  
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(tuffBG, 0, 0, this);
    }
    
    private void refresh(){
        
    } 

}
