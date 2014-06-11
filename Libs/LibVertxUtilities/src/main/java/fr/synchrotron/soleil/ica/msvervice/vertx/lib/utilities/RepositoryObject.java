package fr.synchrotron.soleil.ica.msvervice.vertx.lib.utilities;

/**
 * @author Gregory Boissinot
 */
public class RepositoryObject {

    private String host;
    private int port;
    private String uri;

    public RepositoryObject(String host, int port, String uri) {
        this.host = host;
        this.port = port;
        this.uri = uri;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUri() {
        return uri;
    }

    @Override
    public String toString() {
        return "RepositoryObject{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", uri='" + uri + '\'' +
                '}';
    }
}