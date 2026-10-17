package activite13;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/** Calculatrice TCP : toutes les opérations sont réalisées côté serveur. */
public class Server {
    private static final int DEFAULT_PORT = 5000;
    private static final int OPERAND = 5;

    public static void main(String[] args) {
        if (args.length > 2) {
            System.err.println("Usage : java -cp bin activite13.Server [IPv4_locale] [port]");
            System.exit(1);
        }
        try {
            InetAddress address = ipv4(args.length > 0 ? args[0] : "127.0.0.1");
            int port = port(args.length > 1 ? args[1] : String.valueOf(DEFAULT_PORT));
            try (ServerSocket server = new ServerSocket()) {
                server.bind(new InetSocketAddress(address, port));
                System.out.println("Serveur en attente sur " + address.getHostAddress() + ":" + port + "...");
                try (Socket socket = server.accept();
                     DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                     DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()))) {
                    System.out.println("Client connecté : " + socket.getInetAddress().getHostAddress());
                    while (true) {
                        int operation;
                        try {
                            operation = in.readInt();
                        } catch (EOFException e) {
                            System.out.println("Client déconnecté.");
                            break;
                        }
                        if (operation == 0) {
                            System.out.println("Arrêt demandé par le client.");
                            break;
                        }
                        int x;
                        try {
                            x = in.readInt();
                        } catch (EOFException e) {
                            System.out.println("Client déconnecté pendant une demande.");
                            break;
                        }
                        if (operation < 1 || operation > 4) {
                            out.writeByte(1);
                            out.writeUTF("Opération inconnue : " + operation);
                            System.out.println("Opération inconnue : " + operation);
                        } else {
                            double result = calculate(operation, x);
                            out.writeByte(0);
                            out.writeDouble(result);
                            System.out.println("Opération " + operation + ", x = " + x + ", résultat = " + result);
                        }
                        out.flush();
                    }
                }
                System.out.println("Connexion fermée.");
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Paramètre invalide : " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Erreur du serveur : " + e.getMessage());
            System.exit(1);
        }
    }

    private static double calculate(int operation, int x) {
        switch (operation) {
            case 1: return (long) x + OPERAND;
            case 2: return (long) x - OPERAND;
            case 3: return (long) x * OPERAND;
            case 4: return x / (double) OPERAND;
            default: throw new IllegalArgumentException("Opération inconnue");
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
            // Le message commun ci-dessous décrit aussi les valeurs non numériques.
        }
        throw new IllegalArgumentException("port entre 1 et 65535 attendu");
    }
}
