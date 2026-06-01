package tankgame.server;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 *
 * @author Cameron
 */
public class UDPListener {

    private final int port;
    private volatile boolean isListening = false;
    private DatagramSocket socket;

    public UDPListener(int port) {
        this.port = port;
    }
    
    public boolean isListening() {//return if the socket is currently listening
        return isListening;
    }

    public void close() {//pretty obvious what this does it closes the socket
        isListening = false;
        if (socket != null && !socket.isClosed()) {//check if it is open before closing
            socket.close();
        }
    }

    public void initiate(String name) {//starts listening for broadcasts
        if (!isListening) {
            isListening = true;
            new Thread(() -> {
                try {
                    //create socket
                    socket = new DatagramSocket(6767);
                    while (isListening) {
                        //create receive data variable
                        byte[] receiveData = new byte[1024];
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        
                        //block until packet recieved
                        socket.receive(receivePacket);
                        
                        //create response message using client ip and port
                        InetAddress clientAddress = receivePacket.getAddress();
                        int clientPort = receivePacket.getPort();
                        byte[] sendData = String.valueOf(port+":"+name).getBytes();

                        //send message
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);//send response packet
                        socket.send(sendPacket);
                    }
                } catch (Exception e) {
                    //dont print stack trace on normal shutdown
                    if (!isListening) {
                        return;
                    }
                    e.printStackTrace();//adjust error handling later
                }
            }).start();
        }
    }
}
