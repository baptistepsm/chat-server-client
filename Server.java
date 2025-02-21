import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Server {
    private static final int Port_Serveur = 6600;
    private static final int Nb_Clients = 10;

    public static void main(String[] args) {
        int portServeur = Port_Serveur;
        if (args.length == 1) {
            try {
                portServeur = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Erreur réseau : " + e.getMessage());
                System.exit(1);
            }
        }
        System.out.println("Serveur > Démarrage du serveur sur le port " + portServeur);
        ExecutorService clients = Executors.newFixedThreadPool(Nb_Clients);
        try (ServerSocket serveurSocket = new ServerSocket(portServeur)) {
            while (true) {
                Socket clientSocket = serveurSocket.accept();
                System.out.println("Serveur > Nouvelle connexion du client : " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
                clients.execute(new GestionClient(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Serveur > Erreur : " + e.getMessage());
        } finally {
            clients.shutdown();
        }
    }
}