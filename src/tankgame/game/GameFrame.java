package tankgame.game;

import tankgame.game.Render.GameCanvas;
import javax.swing.JFrame;
import tankgame.menu.MainMenu;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import tankgame.server.UDPListener;
import tankgame.server.ClientHandler;

import java.net.BindException;
import java.net.SocketException;

import java.io.IOException;

import java.util.UUID;

/**
 *
 * @author Cameron
 */
public class GameFrame extends JFrame {

    private final MainMenu mm;
    private UDPListener udpListener;
    private ClientHandler ch;
    private boolean gameStarted = false;
    private int port;
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

    //starts the server
    public void initServerLobby() {
        port = 6767;
        boolean portFound = false;
        //create client handler
        ch = new ClientHandler(port);
        //keep trying to make tcp server with new port until it doesnt give bindException
        //basically if port is already taken then retry with new one
        do {
            try {
                //initiate them all
                //only ch.initiate gives errors so once that goes through then we can start the udp server
                ch.initiate();
                udpListener = new UDPListener(port);
                udpListener.initiate();
                portFound = true;
            } catch (BindException e) {
                //port already in use
                port++;
            } catch (SocketException e) {
                //connection reset
                e.printStackTrace();
            } catch (IOException e) {
                //idk why it throws this but be scared if it does
                e.printStackTrace();
            }
        } while (!portFound);

        //add action listener for when a message is recieved
        ch.addActionListener(al -> {
            String message = ch.recieveQueue.poll();
            UUID messageUUID;
            //get type
            int type = Character.getNumericValue(message.charAt(0));
            message = message.substring(2);
            //get uuid
            if (type != 0) {
                messageUUID = UUID.fromString(message.substring(0, 36));
                message = message.substring(37);
                ch.getClient(messageUUID).updateLastMessageTime();
            }
            switch (type) {
                //new connection
                case 0 -> {
                    if (!gameStarted) {
                        
                    }
                }
                //lobby timeout
                case 1 -> {
                    if (!gameStarted) {
                        
                    }
                }

                //in game tick
                //should be players sending inputs
                case 2 -> {
                    if (gameStarted) {
                        
                    }
                }
            }
        });
    }

    public void initServer() {
        udpListener.close();
        try {
            ch.stopAccepting();
        } catch (IOException e) {
            //idk why this can throw
            e.printStackTrace();
        }
        gameStarted = true;
    }

    public void startDebug() {
        remove(mm);
        GameHandler gh = new GameHandler();
        GameCanvas gc = new GameCanvas(this, gh);
        gh.setCanvas(gc);
        add(gc);
        gc.initBuffer();
        gh.initDebug();
        pack();

        //30 tps simulate
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                gh.debugTick();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 1000 / 30, TimeUnit.MILLISECONDS);
        //144 fps render
        ScheduledExecutorService renderScheduler = Executors.newSingleThreadScheduledExecutor();
        renderScheduler.scheduleAtFixedRate(() -> {
            try {
                gc.renderLoop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 1000 / 144, TimeUnit.MILLISECONDS);
    }
}
