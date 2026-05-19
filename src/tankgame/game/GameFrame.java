package tankgame.game;

import javax.swing.JFrame;
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

        //30 tps simulate
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            gp.tick();
        }, 0, 1000 / 30, TimeUnit.MILLISECONDS);
        //144 fps render
        ScheduledExecutorService renderScheduler = Executors.newSingleThreadScheduledExecutor();
        renderScheduler.scheduleAtFixedRate(() -> {
            gp.renderLoop();
        }, 0, 1000 / 60, TimeUnit.MILLISECONDS);
    }
}
