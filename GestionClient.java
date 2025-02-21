import java.io.*;
import java.net.Socket;
import java.util.List;

class GestionClient implements Runnable {
    private Socket clientSocket;
    private List<PrintWriter> clientWriters;
    private PrintWriter writer;

    public GestionClient(Socket socket, List<PrintWriter> clientWriters) {
        this.clientSocket = socket;
        this.clientWriters = clientWriters;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = clientSocket.getInputStream();
            OutputStream outputStream = clientSocket.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            writer = new PrintWriter(outputStream, true);

            synchronized (clientWriters) {
                clientWriters.add(writer);
            }
            writer.println("Connexion établie avec le serveur.");
            String messageRecu;
            while ((messageRecu = reader.readLine()) != null) {
                System.out.println("Client " + clientSocket.getInetAddress() + ":" + clientSocket.getPort() + " > " + messageRecu);
                broadcastMessage("Client " + clientSocket.getInetAddress() + ":" + clientSocket.getPort() + " > " + messageRecu);
                if (messageRecu.equalsIgnoreCase("quit")) {
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
            synchronized (clientWriters) {
                clientWriters.remove(writer);
            }
            System.out.println("Client " + clientSocket.getInetAddress() + ":" + clientSocket.getPort() + " déconnecté.");
        }
    }

    private void broadcastMessage(String message) {
        synchronized (clientWriters) {
            for (PrintWriter clientWriter : clientWriters) {
                clientWriter.println(message);
            }
        }
    }
}