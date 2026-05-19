package tankgame.game;

import javax.swing.JFrame;
import tankgame.menu.MainMenu;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Cameron
 */
public class GameFrame extends JFrame {

    KeyHandler kb = new KeyHandler();

    public GameFrame() {
        //initialize the JFrame
        setExtendedState(MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        addKeyListener(kb);
        GamePanel gp = new GamePanel(this);
        add(gp);
        pack();

        gp.initDebug();
        
        //30 tps simulate
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                gp.tick();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 1000 / 30, TimeUnit.MILLISECONDS);
        //144 fps render
        ScheduledExecutorService renderScheduler = Executors.newSingleThreadScheduledExecutor();
        renderScheduler.scheduleAtFixedRate(() -> {
            try {
                gp.renderLoop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 1000 / 60, TimeUnit.MILLISECONDS);
    }
}
