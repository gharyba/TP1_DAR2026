package activite12;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
    private static final int PORT = 5000;

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Serveur en attente sur le port " + PORT + "...");
            try (Socket socket = server.accept();
                 DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                 DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()))) {
                System.out.println("Client connecté : " + socket.getInetAddress().getHostAddress());
                while (true) {
                    int x;
                    try {
                        x = in.readInt();
                    } catch (EOFException e) {
                        System.out.println("Client déconnecté sans envoyer 0.");
                        break;
                    }
                    if (x == 0) {
                        System.out.println("Arrêt demandé par le client.");
                        break;
                    }
                    long result = (long) x * 5;
                    out.writeLong(result);
                    out.flush();
                    System.out.println(x + " * 5 = " + result);
                }
            }
            System.out.println("Connexion fermée.");
        } catch (IOException e) {
            System.err.println("Erreur du serveur : " + e.getMessage());
            System.exit(1);
        }
    }
}
