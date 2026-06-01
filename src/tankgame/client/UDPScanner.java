package tankgame.client;

import java.net.DatagramPacket;
import java.net.SocketException;
import java.net.DatagramSocket;
import java.net.UnknownHostException;
import java.net.SocketTimeoutException;
import java.net.InetAddress;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Cameron
 */
public class UDPScanner {
    public static ServerObject[] scan() throws SocketException, UnknownHostException {
        //create shi
        DatagramSocket UDPSocket = new DatagramSocket();
        UDPSocket.setBroadcast(true);
        ArrayList<ServerObject> servers = new ArrayList<>();

        //make the packet on port 6767
        InetAddress broadcastAddress = InetAddress.getByName("255.255.255.255");
        byte[] data = "six seven".getBytes();
        DatagramPacket broadcastPacket = new DatagramPacket(data, data.length, broadcastAddress, 6767);

        Thread listenerThread = new Thread(() -> {
            try {
                while (true) {
                    //recieve packet
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    UDPSocket.receive(packet);

                    //read message into strings
                    String message = new String(packet.getData(), 0, packet.getLength());
                    String senderIp = packet.getAddress().getHostAddress();

                    //create server object and add to the list from the message
                    servers.add(new ServerObject(Integer.parseInt(message.substring(0, 4)), senderIp, message.substring(5)));
                }
            } catch (SocketTimeoutException e) {
                //socket timed out (good)
            } catch (IOException e) {
                //ouuu shii
            }
        });
        listenerThread.start();
        //time out listener after 5 seconds
        UDPSocket.setSoTimeout(5000);

        //send packet
        try {
            UDPSocket.send(broadcastPacket);
            //wait for listener thread to end
            listenerThread.join(5000);
        } catch (IOException e) {
            //thats not what i woulda done
        } catch (InterruptedException e){
            //thing lowkey got interrupted
        }

        return servers.toArray(ServerObject[]::new);
    }
}
