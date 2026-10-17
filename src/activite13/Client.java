package activite13;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

/** Menu et transport des demandes ; les résultats proviennent du serveur. */
public class Client {
    private static final int DEFAULT_PORT = 5000;

    public static void main(String[] args) {
        if (args.length > 2) {
            System.err.println("Usage : java -cp bin activite13.Client [IPv4_serveur] [port]");
            System.exit(1);
        }
        try {
            InetAddress address = ipv4(args.length > 0 ? args[0] : "127.0.0.1");
            int port = port(args.length > 1 ? args[1] : String.valueOf(DEFAULT_PORT));
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(address, port), 3000);
                socket.setSoTimeout(10000);
                try (DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                     DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                     Scanner keyboard = new Scanner(System.in)) {
                    System.out.println("Connecté au serveur " + address.getHostAddress() + ":" + port);
                    while (true) {
                        System.out.println("\n========== CALCULATRICE TCP ==========");
                        System.out.println("1. Addition\n2. Soustraction\n3. Multiplication\n4. Division\n0. Quitter");
                        Integer operation = readInteger(keyboard, "Choix : ");
                        if (operation == null) break;
                        if (operation == 0) {
                            out.writeInt(0);
                            out.flush();
                            System.out.println("Déconnexion...");
                            break;
                        }
                        if (operation < 1 || operation > 4) {
                            System.out.println("Choix invalide : sélectionnez 0 à 4.");
                            continue;
                        }
                        Integer x = readInteger(keyboard, "Entrez x : ");
                        if (x == null) break;
                        out.writeInt(operation);
                        out.writeInt(x);
                        out.flush();
                        int status = in.readUnsignedByte();
                        if (status == 0) {
                            System.out.println("Résultat : " + in.readDouble());
                        } else if (status == 1) {
                            System.out.println("Erreur du serveur : " + in.readUTF());
                        } else {
                            throw new IOException("Réponse inconnue : " + status);
                        }
                    }
                }
                System.out.println("Connexion fermée.");
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Paramètre invalide : " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Erreur de communication : " + e.getMessage());
            System.exit(1);
        }
    }

    private static Integer readInteger(Scanner keyboard, String prompt) {
        while (true) {
            System.out.print(prompt);
            if (!keyboard.hasNextLine()) {
                System.out.println("Fin de la saisie ; fermeture de la connexion.");
                return null;
            }
            try {
                return Integer.parseInt(keyboard.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Saisie invalide : entrez un entier sur 32 bits.");
            }
        }
    }

    private static InetAddress ipv4(String value) throws IOException {
        if (!value.matches("[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+")) {
            throw new IllegalArgumentException("adresse IPv4 attendue");
        }
        InetAddress address = InetAddress.getByName(value);
        if (!(address instanceof Inet4Address)) {
            throw new IllegalArgumentException("adresse IPv4 attendue");
        }
        return address;
    }

    private static int port(String value) {
        try {
            int result = Integer.parseInt(value);
            if (result >= 1 && result <= 65535) return result;
        } catch (NumberFormatException e) {
            // Message commun ci-dessous.
        }
        throw new IllegalArgumentException("port entre 1 et 65535 attendu");
    }
}
