package tankgame.client;

import java.awt.event.ActionListener;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Cameron
 */
public class TCPHandler {

    Socket socket;
    public Queue<String> recieveQueue = new LinkedList<>();
    private final ArrayList<ActionListener> listeners = new ArrayList<>();
    
    public TCPHandler(String ip, int port) {
       // socket = new Socket(ip, port);
    }
    
    public void addActionListener(ActionListener listener) {
        listeners.add(listener);
    }
}
