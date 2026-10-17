
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 5000);
             BufferedReader in = new BufferedReader(
                 new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(
                 socket.getOutputStream(), true);
             Scanner clavier = new Scanner(System.in)) {

            System.out.println("Connecte au serveur !");

            while (true) {
                System.out.print("Operation : ");
                String operation = clavier.nextLine().trim();

                if (operation.equalsIgnoreCase("exit")) {
                    out.println("exit");
                    break;
                }

                if (!operation.matches("-?\\d+\\s*[+*/-]\\s*-?\\d+")) {
                    System.out.println("Format invalide.");
                    continue;
                }

                out.println(operation);

                String resultat = in.readLine();
                if (resultat == null) {
                    System.out.println("Serveur deconnecte.");
                    break;
                }

                System.out.println("Resultat : " + resultat);
            }

        } catch (IOException e) {
            System.err.println("Erreur reseau : " + e.getMessage());
        }
    }
}
