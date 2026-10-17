package serverPackage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int PORT = 5000;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Serveur en attente d'un client sur le port " + PORT + "...");

            try (Socket clientSocket = serverSocket.accept()) {
                System.out.println("Client connecté : " + clientSocket.getInetAddress().getHostAddress());
            }
        } catch (IOException e) {
            System.err.println("Erreur du serveur : " + e.getMessage());
            System.exit(1);
        }
    }
}
