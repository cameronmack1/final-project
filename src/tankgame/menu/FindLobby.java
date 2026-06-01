package tankgame.menu;

import java.awt.Font;
import java.awt.Image;
import tankgame.game.GameFrame;
import javax.swing.JPanel;
import tankgame.client.ServerObject;
/**
 *
 * @author layne
 */
public class FindLobby extends JPanel {
    
     private Image bg;
    private GameFrame gf;
    private int w;
    private int h;
    private int e;
    
    public FindLobby(GameFrame gf, ServerObject[] servers) {
        //initialize
        setLayout(null);
        this.gf = gf;
        w = gf.getWidth();
        h = gf.getHeight();

        //font
        Font f = new Font("Comic Sans", Font.PLAIN, h / 18);
        
    }
    
}
