package tankgame.client;

/**
 *
 * @author expLayne
 */
public class ServerObject {

    final int port;
    final String ip;
    final String name;

    public ServerObject(int port, String ip, String name) {
        this.port = port;
        this.ip = ip;
        this.name = name;
    }

    public String getIP() {
        return this.ip;
    }

    public int getPort() {
        return this.port;
    }

    public String getName() {
        return name;
    }
}
