package tankgame;

import tankgame.game.GameScreen;

import java.util.Scanner;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Cameron
 */
public class Main {

    static int port = 6767;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Scanner kb = new Scanner(System.in);
        char inp = 'a';
        while (inp != 'c' && inp != 's') {
            System.out.println("Client or Server? (c/s)");
            inp = kb.nextLine().charAt(0);
        }
        GameScreen gs = new GameScreen();
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            gs.tick();
        }, 0, 1000/60, TimeUnit.MILLISECONDS);
    }
}
