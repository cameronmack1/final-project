package tankgame.game;

import tankgame.game.Render.GameCanvas;
import javax.swing.JFrame;
import tankgame.menu.MainMenu;
import tankgame.menu.LobbyMenu;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import tankgame.server.UDPListener;
import tankgame.server.ClientHandler;
import tankgame.server.LobbyPlayer;
import tankgame.server.ClientObj;

import java.net.BindException;
import java.net.SocketException;

import java.io.IOException;

import java.util.UUID;
import java.util.ArrayList;

/**
 *
 * @author Cameron
 */
public class GameFrame extends JFrame {

    private MainMenu mm;
    private LobbyMenu lm;
    private UDPListener udpListener;
    private ClientHandler ch;
    private GameHandler gh;
    private GameCanvas gc;
    private boolean gameStarted = false;
    private int port;
    private int width;
    private int height;
    ArrayList<LobbyPlayer> lobbyPlayers;

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
        //create lobby menu panel and remove main menu
        this.remove(mm);
        lm = new LobbyMenu(this);
        this.add(lm);
        setVisible(true);
        lobbyPlayers = new ArrayList<>();
        gh = new GameHandler(this);

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
            messageUUID = UUID.fromString(message.substring(0, 36));
            message = message.substring(37);
            ch.getClient(messageUUID).updateLastMessageTime();
            switch (type) {
                //new connection
                case 0 -> {
                    //if in lobby
                    if (!gameStarted) {
                        //send all players to the new guy
                        try {
                            String data = "0:" + GameHandler.serialize(lobbyPlayers);
                            ch.getClient(messageUUID).send(data);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        
                        //add new guy to the lobby
                        LobbyPlayer lp = new LobbyPlayer(message, messageUUID);
                        lobbyPlayers.add(lp);
                        lm.addPlayer(lp);

                        //send new player to everyone
                        String data = "1:" + messageUUID + ":" + message;
                        ch.broadcast(data);
                    }
                }
                //reset timeout

                case 1 -> {
                    //literally do nothing lmao cuz the timeout is reset earlier lmao
                }

                //in game
                //should be players sending inputs
                case 2 -> {
                    if (gameStarted) {
                        boolean[] keys = new boolean[5];
                        for (int i = 0; i < 5; i++) {
                            keys[i] = '1' == message.charAt(i);
                        }
                        gh.getPlayer(messageUUID).setKeys(keys);
                    }
                }

                //player leave lobby
                case 9 -> {
                    removePlayer(messageUUID);
                }

            }
        }
        );
    }

    public void removePlayer(UUID id) {
        ch.removeClient(id);
        if (!gameStarted) {
            lm.removePlayer(id);
            for (int i = 0; i < lobbyPlayers.size(); i++) {
                if (lobbyPlayers.get(i).getID() == id) {
                    lobbyPlayers.remove(i);
                }
            }
            String sendMessage = "2:" + id;
            ch.broadcast(sendMessage);
        }
    }

    public void initServer() {
        //close sockets
        udpListener.close();
        try {
            ch.stopAccepting();
        } catch (IOException e) {
            //idk why this can throw
            e.printStackTrace();
        }
        gameStarted = true;
        //canvas
        gc = new GameCanvas(this, gh);
        gh.setCanvas(gc);
        add(gc);

        //start loops
        initLocal();
        //30 tps simulate
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            //check timeout and kick player if last message time is > 10 seconds
            for (ClientObj co : ch.getClients()) {
                if (co.getLastMessageTime() - System.currentTimeMillis() > 10_000) {
                    ch.removeClient(co.id);
                }
            }

            //tick
            try {
                //local prediction then server prediction and send game state to clients
                gh.localTick();
                String message = gh.serverTick();
                ch.broadcast(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 1000 / 30, TimeUnit.MILLISECONDS);

    }

    public void initLocal() {
        gh = new GameHandler(this);
        gc = new GameCanvas(this, gh);
        gh.setCanvas(gc);
        add(gc);
        gc.initBuffer();
        gh.initDebug();
        pack();

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

    public void startDebug() {
        remove(mm);
        initLocal();
        //30 tps simulate
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                gh.localTick();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 1000 / 30, TimeUnit.MILLISECONDS);
    }
}
