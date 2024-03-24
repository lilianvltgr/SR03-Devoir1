package fr.utc.sr03.clientPackage;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * Class inheriting the characteristics of threads, enabling
 * the client to receive a message sent by the server and
 * display it on their personal console.
 */

public class ClientMessageSender extends Thread {
    public Socket communication;
    private String pseudo;

    //constructor
    public ClientMessageSender(Socket communication, String pseudo) {
        this.communication = communication;
        this.pseudo = pseudo;
    }

    @Override
    /**
     * Method to run the communication thread for sending messages to the server.
     * Reads messages from the input stream and writes them to the output stream.
     * This method runs in a loop until the connection is active, facilitating continuous communication.
     */

    public void run() {
        try {
            // Create a Scanner object to read input from the standard input (keyboard)
            Scanner sc = new Scanner(System.in);

            // Create a DataOutputStream to write text output to the server's communication socket
            DataOutputStream output = new DataOutputStream(communication.getOutputStream());

            // While the connection between the server and the client is indeed active
            while (Client.activeConnection) {

                // Read the line typed by the user
                String message = sc.nextLine();

                // if the message written equals "exit", the connection between
                // the server and this client is no longer active
                if ("exit".equalsIgnoreCase(message.trim())) {
                    Client.activeConnection = false;
                    message = "exit";
                }

                // The message entered by the user is written to the output stream along with his pseudo
                output.writeUTF(pseudo);
                output.writeUTF(message);
            }
        } catch (IOException e) {
            if (Client.activeConnection)
                throw new RuntimeException(e);
        }
    }
}
