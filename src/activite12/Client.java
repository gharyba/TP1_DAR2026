package activite12;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final int PORT = 5000;

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, PORT), 3000);
            socket.setSoTimeout(10000);
            try (DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                 DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                 Scanner keyboard = new Scanner(System.in)) {
                System.out.println("Connecté au serveur " + host + ":" + PORT);
                while (true) {
                    System.out.print("Entrez un entier (0 pour arrêter) : ");
                    if (!keyboard.hasNextLine()) {
                        System.out.println("Fin de la saisie ; fermeture de la connexion.");
                        break;
                    }
                    String line = keyboard.nextLine().trim();
                    int x;
                    try {
                        x = Integer.parseInt(line);
                    } catch (NumberFormatException e) {
                        System.out.println("Saisie invalide : entrez un entier sur 32 bits.");
                        continue;
                    }
                    out.writeInt(x);
                    out.flush();
                    if (x == 0) {
                        System.out.println("Arrêt de la communication.");
                        break;
                    }
                    System.out.println("Résultat : " + in.readLong());
                }
            }
            System.out.println("Connexion fermée.");
        } catch (IOException e) {
            System.err.println("Erreur de communication : " + e.getMessage());
            System.exit(1);
        }
    }
}
