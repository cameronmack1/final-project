package tankgame;

import tankgame.server.UDPListener;
import tankgame.server.ClientHandler;
import tankgame.game.GameScreen;


import java.util.Scanner;

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
        if (inp == 's') {
            ClientHandler server = new ClientHandler(6767);
            try {
                server.initiate();
            } catch (Exception e) {
                server.setPort(port += 1);
            }
            UDPListener listener = new UDPListener(port);
            listener.initiate();
            GameScreen gs = new GameScreen(true);
            while (true) {
                gs.tick();
            }
        }
        if (inp == 'c') {

        }
    }
}
