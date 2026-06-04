package tankgame.server;

import java.io.IOException;
import java.util.UUID;

import java.net.Socket;

import java.io.PrintWriter;

/**
 *
 * @author Cameron
 */
public class ClientObj {

    public Socket socket;
    public final PrintWriter output;
    public final UUID id;
    private long lastMessageTime;
    private String name;

    public ClientObj(Socket socket, PrintWriter output, UUID id) {
        this.socket = socket;
        this.output = output;
        this.id = id;
        this.lastMessageTime = System.currentTimeMillis();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String playerName) {
        name = playerName;
    }

    public void updateLastMessageTime() {
        lastMessageTime = System.currentTimeMillis();
    }

    public long getLastMessageTime() {
        return lastMessageTime;
    }

    public void send(String message) {//sends a message to the client
        output.println(message);
        System.out.println("sent message: " + message);
    }

    public void close() throws IOException {//closes the socket
        socket.close();
    }
}
