package tankgame.client;

import java.net.SocketException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import java.net.UnknownHostException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 *
 * @author Cameron
 */
public class TCPHandler {

    Socket socket;
    public Queue<String> recieveQueue = new LinkedList<>();
    private final ArrayList<ActionListener> listeners = new ArrayList<>();
    PrintWriter out;

    public TCPHandler(String ip, int port) throws UnknownHostException, IOException {
        //creat socket
        socket = new Socket(ip, port);

        //create output stream (sending messages)
        out = new PrintWriter(socket.getOutputStream(), true);

        //create input stream (recieving) and add action listener
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String message;

        try {
            while ((message = in.readLine()) != null) {
                recieveQueue.add(message);
                notifyListeners();
            }
        } catch (SocketException e) {
            //server disconnects u or fail to connect
            
        }
    }

    private void notifyListeners() {
        //create action event and send to listeners
        ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "MessageRecieved");
        for (ActionListener listener : listeners) {
            listener.actionPerformed(event);
        }
    }

    /**
     * send message to server
     *
     * @param message message to send
     */
    public void send(String message) {
        out.print(message);
    }

    public void close() throws IOException {
        socket.close();
    }

    public void addActionListener(ActionListener listener) {
        listeners.add(listener);
    }
}
