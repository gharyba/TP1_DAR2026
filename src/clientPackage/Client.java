package clientPackage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {
    private static final int PORT = 5000;

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        System.out.println("Connexion au serveur " + host + ":" + PORT + "...");

        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, PORT));
            System.out.println("Connexion au serveur réussie !");
        } catch (IOException e) {
            System.err.println("Erreur de connexion : " + e.getMessage());
            System.exit(1);
        }
    }
}
