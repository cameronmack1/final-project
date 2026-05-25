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
    MainMenu mm;
    private int width;
    private int height;
    public GameFrame() {
        //initialize the JFrame
        setExtendedState(MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        
        setVisible(true);
        width = getWidth();
        height = getHeight();
        mm = new MainMenu(this);
        this.add(mm);
        setVisible(true);
    }
    
    public void startGame(){
        remove(mm);
        GamePanel gp = new GamePanel(this);
        add(gp);
        gp.initBuffer();
        gp.initDebug();
        pack();
        
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
            } catch(Exception e){
                e.printStackTrace();
            }
        }, 0, 1000 / 144, TimeUnit.MILLISECONDS);
    }
}
