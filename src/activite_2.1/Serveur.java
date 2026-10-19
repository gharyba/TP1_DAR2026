
import java.io.*;
import java.net.*;
import java.util.regex.*;

public class Serveur {
    public static void main(String[] args) {

        try (ServerSocket serveur = new ServerSocket(5000)) {
            System.out.println("Serveur en attente...");

            try (Socket socket = serveur.accept();
                 BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(
                     socket.getOutputStream(), true)) {

                System.out.println("Client connecte !");

                String expression;

                Pattern pattern = Pattern.compile(
                    "(-?\\d+)\\s*([+*/-])\\s*(-?\\d+)"
                );

                while ((expression = in.readLine()) != null) {

                    if (expression.equalsIgnoreCase("exit")) {
                        break;
                    }

                    Matcher matcher = pattern.matcher(expression.trim());
                    String resultat;

                    if (!matcher.matches()) {
                        resultat = "Erreur : expression invalide";
                    } else {
                        try {
                            int a = Integer.parseInt(matcher.group(1));
                            String op = matcher.group(2);
                            int b = Integer.parseInt(matcher.group(3));

                            switch (op) {
                                case "+":
                                    resultat = String.valueOf(a + b);
                                    break;

                                case "-":
                                    resultat = String.valueOf(a - b);
                                    break;

                                case "*":
                                    resultat = String.valueOf(a * b);
                                    break;

                                case "/":
                                    resultat = (b == 0)
                                        ? "Erreur : division par zero"
                                        : String.valueOf((double) a / b);
                                    break;

                                default:
                                    resultat = "Operateur invalide";
                            }

                        } catch (NumberFormatException e) {
                            resultat = "Erreur : nombre invalide";
                        }
                    }

                    System.out.println(expression + " = " + resultat);
                    out.println(resultat);
                }
            }

            System.out.println("Connexion fermee.");

        } catch (IOException e) {
            System.err.println("Erreur reseau : " + e.getMessage());
        }
    }
}
