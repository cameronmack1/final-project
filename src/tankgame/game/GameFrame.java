package tankgame.game;

import tankgame.game.Render.*;
import tankgame.menu.*;
import tankgame.client.ServerObject;
import tankgame.client.TCPHandler;
import tankgame.client.UDPScanner;

import java.net.UnknownHostException;

import javax.swing.JFrame;
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

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.SwingUtilities;

/**
 *
 * @author Cameron
 */
public class GameFrame extends JFrame {

    private FindLobby fl;
    private MainMenu mm;
    private LobbyMenu lm;
    private UDPListener udpListener;
    private ClientHandler ch;
    private TCPHandler th;
    private GameHandler gh;
    private GameCanvas gc;
    private boolean gameStarted = false;
    private int port;
    private ArrayList<LobbyPlayer> lobbyPlayers;
    private boolean isHost = false;
    private UUID id;
    private String username;

    public GameFrame() {
        //initialize the JFrame
        setExtendedState(MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        SwingUtilities.invokeLater(() -> {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            mm = new MainMenu(this, (int) screenSize.getWidth(), (int) screenSize.getHeight());
            this.add(mm);
            setVisible(true);
        });
    }

    //starts the server
    public void initServerLobby() {
        //create lobby menu panel and remove main menu
        isHost = true;
        this.remove(mm);
        lm = new LobbyMenu(this/*, isHost*/);
        this.add(lm);
        setVisible(true);
        lobbyPlayers = new ArrayList<>();
        lobbyPlayers.add(new LobbyPlayer(username, null));
        gh = new GameHandler(this, isHost);

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
                udpListener = new UDPListener(6767);
                udpListener.initiate(mm.getUsername());
                portFound = true;
            } catch (BindException e) {
                //port already in use
                port++;
                ch.setPort(port);
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
                            ArrayList<LobbyPlayer> tempLP = new ArrayList<>();
                            System.out.println("size:" + lobbyPlayers.size());
                            for (LobbyPlayer lp : lobbyPlayers) {
                                tempLP.add(new LobbyPlayer(lp.getName(), null));
                            }
                            String data = "1:" + GameHandler.serialize(tempLP);
                            ch.getClient(messageUUID).send(data);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //add new guy to the lobby
                        LobbyPlayer lp = new LobbyPlayer(message, messageUUID);
                        lobbyPlayers.add(lp);
                        lm.addPlayer(lp);

                        //send new player to everyone
                        String data = "2:" + message;
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
        });
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
        remove(lm);
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

    public void openScanMenu() {
        ServerObject[] sos;
        try {
            sos = UDPScanner.scan();
            System.out.println(sos.length);
            fl = new FindLobby(this, sos);
            this.add(fl);
            remove(mm);
            setVisible(true);
        } catch (IOException e) {
            //yeah idk still
        }
    }

    public void joinServer(ServerObject so) {
        remove(fl);
        lm = new LobbyMenu(this/*, isHost*/);
        add(lm);
        setVisible(true);
        try {
            th = new TCPHandler(so.getIP(), so.getPort());
        } catch (UnknownHostException e) {
            //failed to connect to server (no longer exists)
        } catch (IOException e) {
            //error
            e.printStackTrace();
        }
        th.initiate();

        //message recieved
        th.addActionListener(al -> {
            String message = th.recieveQueue.poll();
            System.out.println(message);
            //get type
            int type = Character.getNumericValue(message.charAt(0));
            message = message.substring(2);
            switch (type) {
                //initialize your own UUID and shi
                case 0 -> {
                    this.id = UUID.fromString(message);
                    th.send("0:" + this.id + ":" + this.username);
                    System.out.println("0:" + this.id + ":" + this.username);
                }
                //just connected, message will have arraylist of players
                case 1 -> {
                    try {
                        lobbyPlayers = (ArrayList<LobbyPlayer>) GameHandler.deserialize(message);
                        System.out.println("size:" + lobbyPlayers.size());
                    } catch (IOException e) {
                        //idk
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        //this should NOT happen
                    }
                }

                //new player joined
                case 2 -> {
                    //create lobby player from message and add them
                    LobbyPlayer lp = new LobbyPlayer(message, null);
                    lobbyPlayers.add(lp);
                    lm.addPlayer(lp);
                }

                //tick
                case 3 -> {
                    try {
                        gc.addServerSnapshot((Snapshot) GameHandler.deserialize(message));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void initLocal() {
        gh = new GameHandler(this, isHost);
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

    public void sendKeys(boolean[] keys) {
        String message;
    }

    public void startDebug() {
        remove(mm);
        initLocal();
        gc.inDebug = true;
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

    public void setUsername(String name) {
        this.username = name;
    }
}
