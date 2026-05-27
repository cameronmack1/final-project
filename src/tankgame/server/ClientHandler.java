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

/**
 *
 * @author Cameron
 */
public class ClientHandler {

    public ArrayList<ClientObj> clients = new ArrayList<>();
    public Queue<String> recieveQueue = new LinkedList<>();
    private int port;
    ServerSocket ss;

    public ClientHandler(int port) {
        this.port = port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void close() throws IOException {
        ss.close();
    }

    public void removeClient(UUID id) {
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).id.equals(id)) {
                clients.get(i).close();
                clients.remove(i);
            }
        }
    }

    public void initiate() throws SocketException, IOException {
        //create server socket
        ss = new ServerSocket();
        ss.setReuseAddress(true);
        ss.bind(new InetSocketAddress(port));
        //create thread that loops waiting for clients
        new Thread(() -> {
            while (!ss.isClosed()) {
                try {
                    //block until new connection
                    Socket socket = ss.accept();
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    System.out.println("New connection: " + socket.getRemoteSocketAddress());
                    
                    //create client object with client details
                    ClientObj c = new ClientObj(socket, out, UUID.randomUUID());
                    clients.add(c);
                    
                    //create thread to handle each client seperately
                    new Thread(() -> handleClient(c)).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void handleClient(ClientObj client) {//add messages from client to the queue
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(client.socket.getInputStream()));
            String message;
            
            //loops and receieves messages, ends when null message is recieved
            while ((message = in.readLine()) != null) {
                recieveQueue.add(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
