package tankgame.server;

import java.util.UUID;

import java.net.Socket;

import java.io.PrintWriter;

/**
 *
 * @author Cameron
 */
public class ClientObj {

    public Socket socket;
    PrintWriter output;
    public final UUID id;

    public ClientObj(Socket socket, PrintWriter output, UUID id) {
        this.socket = socket;
        this.output = output;
        this.id = id;
    }
    
    public void send(String message){//sends a message to the client
        output.print(message);
    }
    
    public void close() {//closes the socket
        try {
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}