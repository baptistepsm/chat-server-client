import java.io.*;
import java.net.*;

public class Client {
	private static final String Serveur_IP = "127.0.0.1";
	private static final int Serveur_Port = 6600;

	public static void main(String[] args) {
		String serveurIp = Serveur_IP;
		int serveurPort = Serveur_Port;
		if (args.length == 2) {
			try {
				serveurIp = args[0];
				serveurPort = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				System.out.println("Erreur réseau : " + e.getMessage());
				System.exit(1);
			}
		}
		try (Socket clientSocket = new Socket(serveurIp, serveurPort);
			 BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
			 PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
			 BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

			System.out.println("Client > Connecté au serveur " + serveurIp + ":" + serveurPort);
			System.out.println("Client > Message du serveur: " + reader.readLine());
			String message;
			while (true) {
				System.out.print("Client > ");
				message = userInput.readLine();
				if (message.equalsIgnoreCase("q")) {
					break;
				}
				writer.println(message);
				System.out.println("Client > Réponse du serveur: " + reader.readLine());
			}
		} catch (IOException e) {
			System.err.println("Client > Erreur : " + e.getMessage());
		}
		System.out.println("Client > Connexion fermée.");
	}
}