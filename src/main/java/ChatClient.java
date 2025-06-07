/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author  maryam,pranjal,sanniya
 */
 import javax.swing.*;/*library file used for creating  GUI*/
import java.awt.*;/*library file used for layout*/
import java.io.*;/*library file used for input output stream*/
import java.net.*;/*library file used for networking such as socket*/

public class ChatClient extends JFrame {/*jframe is used for creating window*/ 

/*variable declaration of GUI component*/
    private JTextArea chatArea;/*for displaying chat messages*/
    private JTextField inputField;/*for taking user input*/
    private JButton sendButton;/*for sending messages to server*/
   
    /*variable declaration of networking component*/
    private Socket socket;/*for communication between server and client*/
    private BufferedReader in;/*for recieving messages from server*/
    private PrintWriter out;/*for sending messages to server*/

    private volatile boolean connected = false;/*for checking connection status*/

     /*constructor(same name as the class)initializes object after creation*/
    public ChatClient() {
        /*for setting up the GUI*/
        setTitle("Chat Client");/* For setting window title*/
        setSize(450,450);/*For setting window size(length,width)*/
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);/*when window is closed it exits the program*/

        chatArea = new JTextArea();/* For creating text area to display messages*/
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

         /*an action is attached to the button so that the message is sent as soon as the client clicks it*/
        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());

        /*For making GUI visible and start connecting to the server*/ 
        setVisible(true);

        new Thread(this::connectToServer).start();/*For starting a new thread to connect to a server*/
    }
/*for connecting to server and handling incoming messages */
    private void connectToServer() {
        while (true) {/*infinite loop is used to keep retrying connection*/
            try {
                chatArea.append("Trying to connect to server...\n");
                socket = new Socket("127.0.0.1", 12347);/*clients will connect through  this IP and port to connect to a server*/
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));/*for recieving messages from server*/
                out = new PrintWriter(socket.getOutputStream(), true);/*for sending messages to server*/
                
               //*for updating about "connection established"*/
                chatArea.append("Connected to server!\n");/*notify client that connection is successful*/
                connected = true;

                String message;
                while ((message = in.readLine()) != null) {/* for reading messages line by line*/
                    chatArea.append(message + "\n");
                }
            } catch (IOException e) {
                
                /* for updating about "connection lost" and attempting to reconnect*/ 
                chatArea.append("Connection lost. Retrying in 3 seconds...\n");
                connected = false;
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        }
    }

    /*for sending message to the server*/ 
    private void sendMessage() {
        /*for getting message from input field and for trimming it*/ 
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {/*if message is not empty*/
            if (connected) {
              /*for checking server connection*/ 
                out.println(message);/*For sending message to server*/ 
                chatArea.append("You: " + message + "\n");/*show it in chat area*/ 
                inputField.setText("");/*For clearing input field*/ 
            } else {
                chatArea.append("Cannot send message. Not connected to server.\n");/*if not connected show error in chat area*/
            }
        }
    }

/*for launching the chat client*/ 
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatClient::new);
    }
}

 


