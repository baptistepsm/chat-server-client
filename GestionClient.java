import java.io.*;
import java.net.Socket;

//Seconde classe pour gérer les clients
class GestionClient implements Runnable {
    private Socket clientSocket;
    public GestionClient(Socket socket) {
        this.clientSocket = socket;
    }
    @Override
    public void run() {
        try (
                InputStream inputStream = clientSocket.getInputStream();
                OutputStream outputStream = clientSocket.getOutputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                PrintWriter writer = new PrintWriter(outputStream, true)
        ) {
            writer.println("Connexion établie avec le serveur.");
            String message_recu;
            while ((message_recu = reader.readLine()) != null) {
                System.out.println("Client " + clientSocket.getInetAddress() + ":" + clientSocket.getPort() + " > " + message_recu);
                writer.println("Message reçu : " + message_recu);
                if (message_recu.equalsIgnoreCase("quit")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur avec le client " + clientSocket.getInetAddress() + ": " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Erreur lors de la fermeture du socket client : " + e.getMessage());
            }
            System.out.println("Client " + clientSocket.getInetAddress() + ":" + clientSocket.getPort() + " déconnecté.");
        }
    }
}