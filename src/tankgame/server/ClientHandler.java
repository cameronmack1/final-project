package tankgame.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;

import java.util.Queue;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.UUID;

import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author Cameron
 */
public class ClientHandler {

    private final ArrayList<ClientObj> clients = new ArrayList<>();
    public Queue<String> recieveQueue = new LinkedList<>();
    private int port;
    private ServerSocket ss;
    private boolean isAcceptingClients;
    private final ArrayList<ActionListener> listeners = new ArrayList<>();

    public void addActionListener(ActionListener listener) {
        listeners.add(listener);
    }

    boolean getIsAcceptingClients() {
        return isAcceptingClients;
    }

    public ClientObj[] getClients() {
        return this.clients.toArray(ClientObj[]::new);
    }

    public ClientObj getClient(UUID userID) {
        for (ClientObj client : clients) {
            if (client.id == userID) {
                return client;
            }
        }
        return null;
    }

    public ClientHandler(int port) {
        this.port = port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void close() {
        try {
            if (!ss.isClosed()) {
                ss.close();
            }
            for (ClientObj co : clients) {
                co.close();
            }
        } catch (IOException e) {
            //idk
            e.printStackTrace();
        }
    }

    public void stopAccepting() throws IOException {
        ss.close();
        isAcceptingClients = false;
    }

    public void removeClient(UUID id) {
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).id.equals(id)) {
                try {
                    clients.get(i).close();
                } catch (IOException e) {
                    //i still dont know why it can throw IOExceptions
                    e.printStackTrace();
                }
                clients.remove(i);
            }
        }
    }

    public void initiate() throws SocketException, IOException {
        //create server socket
        ss = new ServerSocket();
        ss.setReuseAddress(true);
        ss.bind(new InetSocketAddress(port));
        isAcceptingClients = true;

        //create thread that loops waiting for clients
        new Thread(() -> {
            while (!ss.isClosed()) {
                try {
                    //block until new connection
                    Socket socket = ss.accept();
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    System.out.println("New connection: " + socket.getRemoteSocketAddress());

                    //create client object with client details
                    UUID id = UUID.randomUUID();
                    ClientObj c = new ClientObj(socket, out, id);
                    c.send("0:" + id.toString());
                    clients.add(c);

                    //create thread to handle each client seperately
                    new Thread(() -> handleClient(c)).start();
                } catch (Exception e) {
                    if(!ss.isClosed()){
                        //ouu shii
                    }
                }
            }
        }).start();
    }

    /**
     * receives messages from clients and puts them into the recieveQueue
     *
     * @param client the client object to handle
     */
    public void handleClient(ClientObj client) {//add messages from client to the queue
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(client.socket.getInputStream()));
            String message;

            //loops and receieves messages, ends when null message is recieved
            //MESSAGE FORMAT
            //messagetype:userid:message
            //message types are 0 for new connection, 1 to tell reset your timeout timer, and 2 to send your inputs every tick
            //new connection message has just name
            //reset timer has nothing
            //inputs has 5 1s or 0s that represent inputs   
            while ((message = in.readLine()) != null) {
                recieveQueue.add(message);
                notifyListeners();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void notifyListeners() {
        //create action event and send to listeners
        ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "MessageRecieved");
        for (ActionListener listener : listeners) {
            listener.actionPerformed(event);
        }
    }
}
