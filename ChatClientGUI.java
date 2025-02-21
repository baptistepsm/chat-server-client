import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.Socket;

public class ChatClientGUI extends JFrame {
    private JTextArea serverMessages;
    private JTextArea clientMessages;
    private JTextField messageInput;
    private JButton sendButton;
    private JButton quitButton;
    private JButton newClientButton;
    private PrintWriter writer;
    private BufferedReader reader;

    public ChatClientGUI(String serverIp, int serverPort) {
        setTitle("Chat Client");
        setSize(600, 400);
        setLayout(new BorderLayout());

        serverMessages = new JTextArea();
        serverMessages.setEditable(false);
        clientMessages = new JTextArea();
        clientMessages.setEditable(false);
        messageInput = new JTextField();
        sendButton = new JButton("Send");
        quitButton = new JButton("Quit");
        newClientButton = new JButton("New Client");

        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.add(messageInput, BorderLayout.CENTER);
        messagePanel.add(sendButton, BorderLayout.EAST);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(quitButton);
        buttonPanel.add(newClientButton);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(serverMessages), new JScrollPane(clientMessages));
        splitPane.setDividerLocation(200);

        add(splitPane, BorderLayout.CENTER);
        add(messagePanel, BorderLayout.SOUTH);
        add(buttonPanel, BorderLayout.NORTH);

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        messageInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });

        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        newClientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ChatClientGUI(serverIp, serverPort).setVisible(true);
            }
        });

        connectToServer(serverIp, serverPort);
    }

    private void connectToServer(String serverIp, int serverPort) {
        try {
            Socket clientSocket = new Socket(serverIp, serverPort);
            writer = new PrintWriter(clientSocket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String serverMessage;
                        while ((serverMessage = reader.readLine()) != null) {
                            serverMessages.append("Server: " + serverMessage + "\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String message = messageInput.getText();
        if (!message.isEmpty()) {
            writer.println(message);
            clientMessages.append("Client: " + message + "\n");
            messageInput.setText("");
        }
    }

    public static void main(String[] args) {
        String serverIp = "127.0.0.1";
        int serverPort = 6600;
        if (args.length == 2) {
            serverIp = args[0];
            serverPort = Integer.parseInt(args[1]);
        }
        String finalServerIp = serverIp;
        int finalServerPort = serverPort;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ChatClientGUI(finalServerIp, finalServerPort).setVisible(true);
            }
        });
    }
}