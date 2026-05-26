package tankgame.game;
    
import tankgame.game.Render.GameCanvas;
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
        GameHandler gh = new GameHandler();
        GameCanvas gc = new GameCanvas(this, gh);
        gh.setCanvas(gc);
        add(gc);
        gc.initBuffer();
        gh.initLocal();
        pack();
        
        //30 tps simulate
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                gh.localTick();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 1000 / 30, TimeUnit.MILLISECONDS);
        //144 fps render
        ScheduledExecutorService renderScheduler = Executors.newSingleThreadScheduledExecutor();
        renderScheduler.scheduleAtFixedRate(() -> {
            try {
                gc.renderLoop();
            } catch(Exception e){
                e.printStackTrace();
            }
        }, 0, 1000 / 144, TimeUnit.MILLISECONDS);
    }
}
