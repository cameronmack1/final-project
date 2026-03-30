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

    public void initiate() {//starts listening for broadcasts
        if (!isListening) {
            isListening = true;
            new Thread(() -> {
                try {
                    socket = new DatagramSocket(port);//create socket
                    while (isListening) {
                        byte[] receiveData = new byte[1024];//create receive data variable
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

                        socket.receive(receivePacket);//block until packet recieved

                        InetAddress clientAddress = receivePacket.getAddress();//create response message with client ip and port
                        int clientPort = receivePacket.getPort();
                        byte[] sendData = String.valueOf(port).getBytes();

                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);//send response packet
                        socket.send(sendPacket);
                    }
                } catch (Exception e) {
                    if (!isListening) {//dont print stack trace on normal shutdown
                        return;
                    }
                    e.printStackTrace();//adjust error handling later
                }
            }).start();
        }
    }
}
