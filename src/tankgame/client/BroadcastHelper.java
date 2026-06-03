package tankgame.client;

import java.net.Inet4Address;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;
import java.net.InetAddress;

/**
 *
 * @author Cameron
 */
public class BroadcastHelper {

    public static String getBroadcastAddress() {
        try {
            //get all network interfaces
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            //loop thru em all
            for (NetworkInterface ni : Collections.list(interfaces)) {
                //skip the bad ones (disabled, loopback, virtual)
                if (ni.isLoopback() || !ni.isUp()) {
                    continue;
                }
                //go thru all the addesses on the interface
                for (InterfaceAddress interfaceAddress : ni.getInterfaceAddresses()) {
                    
                    //check if it is an IPv4 address
                    if (interfaceAddress.getAddress() instanceof Inet4Address) {
                        InetAddress broadcast = interfaceAddress.getBroadcast();

                        if (broadcast != null) {
                           return broadcast.getHostAddress();
                        }
                        
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
