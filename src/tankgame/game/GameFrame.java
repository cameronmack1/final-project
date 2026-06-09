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
import tankgame.server.GameInitializePacket;
import tankgame.server.ServerPlayer;

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
    private HostLobbyMenu hlm;
    private ClientLobbyMenu clm;
    private GameOverMenu gom;
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
    ScheduledExecutorService scheduler;

    private int w;
    private int h;

    private int readyPlayers = 0;

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
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        w = (int) screenSize.getWidth();
        h = (int) screenSize.getHeight();
    }

    //starts the server
    public void initServerLobby() {
        scheduler = Executors.newScheduledThreadPool(2);
        //create lobby menu panel and remove main menu
        this.id = UUID.randomUUID();
        isHost = true;
        this.remove(mm);
        hlm = new HostLobbyMenu(this);
        this.add(hlm);
        setVisible(true);
        lobbyPlayers = new ArrayList<>();
        lobbyPlayers.add(new LobbyPlayer(username, id));
        hlm.addPlayer(lobbyPlayers.get(0));

        port = 6767;
        boolean portFound = false;
        //create client handler
        ch = new ClientHandler(port);
        //keep trying to make tcp server with new port until it doesnt give bindException
        //basically if port is already taken then retry with new one
        do {
            try {
                //initiate them all
                //only ch.initiate can give  errors so once that goes through then we can start the udp server
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
            if (type != 9) {
                message = message.substring(37);
                ch.getClient(messageUUID).updateLastMessageTime();
            }
            switch (type) {
                //new connection
                case 0 -> {
                    //if in lobby
                    if (!gameStarted) {
                        //send all players to the new guy
                        try {
                            String data = "1:" + GameHandler.serialize(lobbyPlayers);
                            ch.getClient(messageUUID).send(data);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //add new guy to the lobby
                        LobbyPlayer lp = new LobbyPlayer(message, messageUUID);
                        lobbyPlayers.add(lp);
                        hlm.addPlayer(lp);

                        //send new player to everyone
                        String data = "2:" + messageUUID + ":" + message;
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
                        int sentTick = Integer.parseInt(message.substring(6));
                        gh.getPlayer(messageUUID).setKeys(keys, sentTick);
                    }
                }

                //players responding after getting ready
                case 8 -> {
                    readyPlayers++;
                    if (readyPlayers >= lobbyPlayers.size()) {
                        initServer();
                    }
                }

                //player leave lobby
                case 9 -> {
                    System.out.println("removed player: " + messageUUID);
                    removePlayer(messageUUID);
                }
            }
        });
    }

    public void removePlayer(UUID id) {
        ch.removeClient(id);
        if (!gameStarted) {
            hlm.removePlayer(id);
            for (int i = 0; i < lobbyPlayers.size(); i++) {
                if (id.equals(lobbyPlayers.get(i).getID())) {
                    lobbyPlayers.remove(i);
                }
            }
            String sendMessage = "9:" + id;
            ch.broadcast(sendMessage);
        }
    }

    public void quitToMenu() {
        //kill the game loops
        scheduler.shutdownNow();
        //only one of th/ch exists so one of these throws, ignore it (same as createGameOver does)
        try {
            th.send("9:" + id);
            th.close();
        } catch (Exception e) {
            ch.close();
            ch.broadcast("8:host has left the game");
        }
        remove(gc);
        mm = new MainMenu(this, w, h);
        add(mm);
        setVisible(true);
    }

    public void initClient(GameInitializePacket gp) {
        gameStarted = true;
        initLocal(gp);
        gh.setLocalTick((int) (System.currentTimeMillis() / 33 - gp.getTime() / 33) + 10);

        scheduler.scheduleAtFixedRate(() -> {
            //tick
            try {
                //local prediction
                gh.localTick();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 1000 / 30, TimeUnit.MILLISECONDS);
    }

    public void halfInitServer() {
        //close sockets
        udpListener.close();
        try {
            ch.stopAccepting();
        } catch (IOException e) {
            //idk why this can throw
            e.printStackTrace();
        }
    }

    public void initServer() {
        long seed = (long) (Math.random() * 1000000);
        gameStarted = true;
        initLocal(seed);
        gh.initServer(lobbyPlayers.toArray(LobbyPlayer[]::new));
        remove(hlm);

        //send init packet
        GameInitializePacket startPacket = new GameInitializePacket(gh.getPlayers(), seed, System.currentTimeMillis());
        try {
            String message = "4:" + GameHandler.serialize(startPacket);
            ch.broadcast(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //start loops
        //30 tps simulate
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
                if (message.charAt(0) == '7') {
                    createGameOver(message.substring(2));
                    System.out.println("game over");
                }
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
        scheduler = Executors.newScheduledThreadPool(2);
        remove(fl);
        clm = new ClientLobbyMenu(this);
        add(clm);
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
        isHost = false;

        //message recieved
        th.addActionListener(al -> {
            String message = th.recieveQueue.poll();
            //get type
            int type = Character.getNumericValue(message.charAt(0));
            message = message.substring(2);
            switch (type) {
                //initialize your own UUID and shi
                case 0 -> {
                    this.id = UUID.fromString(message);
                    th.send("0:" + this.id + ":" + this.username);
                }
                //just connected, message will have arraylist of players
                case 1 -> {
                    try {
                        lobbyPlayers = new ArrayList<>();
                        ArrayList<?> list = (ArrayList<?>) GameHandler.deserialize(message);
                        for (Object o : list) {
                            if (o instanceof LobbyPlayer) {
                                lobbyPlayers.add((LobbyPlayer) o);
                            }
                        }
                        for (LobbyPlayer lp : lobbyPlayers) {
                            clm.addPlayer(lp);
                        }
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
                    UUID newID = UUID.fromString(message.substring(0, 36));
                    message = message.substring(37);
                    LobbyPlayer lp = new LobbyPlayer(message, newID);
                    lobbyPlayers.add(lp);
                    clm.addPlayer(lp);
                }

                //tick
                case 3 -> {
                    try {
                        //load new snapshot
                        Snapshot newSnapshot = (Snapshot) GameHandler.deserialize(message);
                        //check if youre dead in the snapshot
                        for (Player player : newSnapshot.getPlayerArray()) {
                            if (player instanceof ServerPlayer) {
                                ServerPlayer sp = (ServerPlayer) player;
                                if (sp.getID().equals(id) && sp.getIsDead()) {
                                    gh.self.kill();
                                }
                            }
                        }
                        gc.addServerSnapshot(newSnapshot);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //init game
                //will contain seed and players
                case 4 -> {
                    try {
                        Object o = GameHandler.deserialize(message);
                        if (o instanceof GameInitializePacket) {
                            GameInitializePacket startPacket = (GameInitializePacket) o;
                            initClient(startPacket);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }

                //get ready for init
                case 8 -> {
                    halfInitLocal();
                    String nmessage = "8:" + id;
                    th.send(nmessage);
                }

                //player leave
                case 9 -> {
                    UUID newID = UUID.fromString(message.substring(0, 36));
                    clm.removePlayer(newID);
                }

                //game over
                case 7 -> {
                    createGameOver(message);
                }
            }
        });
    }

    public void halfInitLocal() {
        isHost = false;
        remove(clm);
        gh = new GameHandler(this, isHost);
        gc = new GameCanvas(this, gh, id);
        gh.setCanvas(gc);
        gh.setID(id);
    }

    public void initLocal(GameInitializePacket packet) {
        halfInitLocal();
        boolean[][] map = new MapGenerate().generate(packet.getSeed());
        gh.setMap(map);
        gc.setMap(map);
        add(gc);
        gc.initBuffer();
        int x = 50;
        int y = 50;
        for (ServerPlayer sp : packet.getPlayers()) {
            if (sp.getID().equals(id)) {
                x = (int) sp.getX();
                y = (int) sp.getY();
            }
        }
        gh.initLocal(x, y);
        pack();

        //144 fps render
        scheduler.scheduleAtFixedRate(() -> {
            try {
                gc.renderLoop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 1000 / 144, TimeUnit.MILLISECONDS);

    }

    public void initLocal(long seed) {
        gh = new GameHandler(this, isHost);
        gc = new GameCanvas(this, gh, id);
        gh.setCanvas(gc);
        gh.setID(id);
        boolean[][] map = new MapGenerate().generate(seed);
        gh.setMap(map);
        gc.setMap(map);
        add(gc);
        gc.initBuffer();
        gh.initLocal(50, 50);
        pack();

        //144 fps render
        scheduler.scheduleAtFixedRate(() -> {
            try {
                gc.renderLoop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 1000 / 144, TimeUnit.MILLISECONDS);

    }

    public void sendKeys(boolean[] keys, int tick) {
        String message = "2:" + id;
        message = message + ":";
        //1s and 0s for true anid false
        for (int i = 0; i < 5; i++) {
            message = message + (keys[i] ? 1 : 0);
        }
        message = message + ":" + tick;
        th.send(message);
    }

    public void startDebug() {
        scheduler = Executors.newScheduledThreadPool(2);
        this.username = mm.getUsername();
        remove(mm);
        this.isHost = true;
        initLocal((long) (Math.random() * 10000));
        gc.inDebug = true;
        gh.inDebug = true;
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

    public void leaveGame() {
        th.send("9:" + id);
        try {
            th.close();
        } catch (IOException e) {
            //already closed
            e.printStackTrace();
        }
        remove(clm);
        mm = new MainMenu(this, this.w, this.h);
        add(mm);
        setVisible(true);
        this.username = "";
    }

    public void createGameOver(String winnerName) {
        try {
            //wait a second to terminate
            if (!scheduler.awaitTermination(100, TimeUnit.MILLISECONDS)) {
                //force kill if it didnt shut down
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            //force shutdown if interrupted
            scheduler.shutdownNow();
        }
        try {
            th.close();
        } catch (Exception e) {
            ch.close();
        }
        remove(gc);
        gom = new GameOverMenu(this, winnerName);
        add(gom);
        setVisible(true);
    }

    public void exitToMenu() {
        mm = new MainMenu(this, w, h);
        remove(gom);
        add(mm);
        setVisible(true);
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String name) {
        this.username = name;
    }
}
