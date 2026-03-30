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

    public void setPort(int port){
        this.port = port;
    }
    
    public void close() throws IOException{
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

    public void initiate() throws SocketException, IOException{
            ss =  new ServerSocket();//create server socket
            ss.setReuseAddress(true);
            ss.bind(new InetSocketAddress(port));
            new Thread(() -> {//create thread that loops waiting for clients
                while (!ss.isClosed()) {
                    try {
                        Socket socket = ss.accept();//block until new connection
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        System.out.println("New connection: " + socket.getRemoteSocketAddress());
                        ClientObj c = new ClientObj(socket, out, UUID.randomUUID());//create client object with client details
                        clients.add(c);
                        new Thread(() -> handleClient(c)).start();//create thread to handle each client seperately
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
            while ((message = in.readLine()) != null) {//loops and receieves messages, ends when null message is recieved
                recieveQueue.add(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
