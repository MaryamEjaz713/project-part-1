/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author maryam,pranjal,sanniya
 */
     
import javax.swing.*;/* library file used for  creating GUI*/
import java.awt.*;/*library file used for layout*/

import java.io.*;/*library file used for input output stream*/
import java.net.*;/*library file used for networking(socket)*/
import java.util.*;/*library file used for sorting and searching*/
import java.util.concurrent.*;/*library file used for multithreading*/

public class ChatServer extends JFrame {/*jframe is used to create window*/ 

   /*variable declaration of GUI component*/  
    private  JTextArea chatArea;/*for displaying chat messages*/
    private JTextField inputField;/*for taking user input*/
    private  JButton sendButton;/*for sending messages to client*/
    
    /*variable declaration of networking component*/
    private ServerSocket serverSocket;/*for communication between server and client*/
    private final Set<PrintWriter> clientWriters = ConcurrentHashMap.newKeySet();/*for all connected clients,allowing message broadcast*/
    
    /*constructor(same name as the class)initializes object after creation*/
    public ChatServer() {
        setTitle("Chat Server (Multi-client)");/*set window title*/
        setSize(400,400);/*set window size(length,width)*/
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);/*when window is closed it exits the program*/

        chatArea = new JTextArea();/*For creating text area to display messages*/
        chatArea.setEditable(false);/*text in the chat area cannot be edited*/
        JScrollPane scrollPane = new JScrollPane(chatArea);/*for scrolling chat messages*/ 

        inputField = new JTextField();/*for typing messages*/
        sendButton = new JButton("Send");
       
        /*adding GUI component at specific positions inside the panel using border layout*/
        JPanel panel = new JPanel(new BorderLayout()); 
        panel.add(inputField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);
        add(scrollPane, BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);
        
        /*an action is attached to the button so that the message is sent as soon as the server clicks it*/
        sendButton.addActionListener(e -> sendMessageToAll("Server", inputField.getText()));
        inputField.addActionListener(e -> sendMessageToAll("Server", inputField.getText()));
       
        /*making GUI visible and start connected to the client */
        setVisible(true);

        new Thread(this::startServer).start();
    }

    private void startServer() {/*For starting server to accept multiple clients*/
        try {
            serverSocket = new ServerSocket(12347);/*For creating a server socket on port 12347*/
            appendToChat("Server started. Waiting for clients...\n");

            while (true) {/*while loop is used to accept multiple clients*/ 
                Socket clientSocket = serverSocket.accept();/*accept incoming client 
                connection (repeats for multiple clients*/  
                appendToChat("Client connected: " + clientSocket.getInetAddress() + "\n");

                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);/*create writer to send
                messages to connected client*/ 

                clientWriters.add(writer);/*For add client writer to  the set for broadcasting messages*/   

                new Thread(() -> handleClient(clientSocket, writer)).start();/*handle each 
                client message in a new thread*/
            }
        } catch (IOException e) {/*For handling error if server fails*/
            appendToChat("Server error: " + e.getMessage());
        }
    }
    /* For handling client messages */
    private void handleClient(Socket socket, PrintWriter writer) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String message;
            while ((message = in.readLine()) != null) {/*For reading  messages line by line*/
                /*check for special characters*/
                if (containsSpecialCharacters(message)) {/*if client sends a special character
                    show an error message
                    */
                    writer.println("Your message is incorrect and incomplete");
                    appendToChat("Client (invalid): " + message);
                } else {/*For showing valid client messages and broadcast to all clients*/
                    appendToChat("Client: " + message);
                    
                }
            }
        } catch (IOException e) {/*For displaying "disconnect" message in chat area*/ 
            appendToChat("Client disconnected.");
        } finally {
            clientWriters.remove(writer);/*For removing writer from the set */
            try {
                socket.close();/*For closing the socket*/
            } catch (IOException ignored) {}
        }
    }

    private void sendMessageToAll(String sender, String message) {
        message = message.trim();/*For removing extra spaces*/
        if (!message.isEmpty()) {/*if message is not empty*/
            appendToChat(sender + ": " + message);/*For displaying messages in GUI(chat area)*/
            broadcastMessage(sender, message);/*For sending messages to all clients */
            inputField.setText("");
        }
    }

/* for broadcasting messages to all clients*/
    private void broadcastMessage(String sender, String message) {
        for (PrintWriter writer : clientWriters) {
            writer.println(sender + ": " + message);
        }
    }

/*for appending message to chat area*/
    private void appendToChat(String message) {
        SwingUtilities.invokeLater(() -> chatArea.append(message + "\n"));
    }

    /*check for special characters in message*/ 
    private boolean containsSpecialCharacters(String message) {
        return !message.matches("[a-zA-Z0-9 ,.?!]*");
    }
/*for launching chat server*/ 
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatServer::new);/*for ensuring GUI updates*/ 
    }
}


