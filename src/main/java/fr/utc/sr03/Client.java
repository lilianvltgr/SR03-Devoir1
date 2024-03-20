package fr.utc.sr03;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import static java.lang.Thread.sleep;


/**
 * Client class for establishing a connection
 * with the server and launching two communication threads:
 * ClientMessageReceptor to intercept messages from the server
 * and ClientMessageSender to retrieve messages entered
 * by the user and transmit them to the server.
 */

public class Client {
    static public Socket communication;
    static volatile boolean activeConnection = true;
    public static void main(String[] args) throws IOException, InterruptedException {

        // Create a Scanner object to read input from the standard input (keyboard)
        Scanner sc = new Scanner(System.in);

        // Establishes a connection to the server running on localhost at port 10080
        communication = new Socket("localhost", 10080);

        // Create a DataInputStream to read text input and a DataOutputStream to write text output
        DataInputStream input = new DataInputStream(communication.getInputStream());
        DataOutputStream output = new DataOutputStream(communication.getOutputStream());

        // Create a pseudo and connect the client to the chat.
        String pseudo;

        // While the pseudo entered by the user is empty, prompt the user to enter it again
        do {
            System.out.println("Ecrivez votre pseudo");
        } while ((pseudo = sc.next()) == null);

        // The pseudo is written on the client's output stream
        output.writeUTF(pseudo);

        // While the pseudo entered by the user already exists, prompt the user to enter it again
        while (!input.readBoolean()) {
            do {
                System.out.println("Le pseudo est indisponible, choisissez en un autre");
            } while ((pseudo = sc.next()) == null);
            output.writeUTF(pseudo);
        }

        // Confirmation message to inform the newly connected client that he entered the chat
        System.out.println("Vous êtes connectés");

        // Create a new message sender thread and start it.
        // This thread is responsible for sending messages to the server using the communication channel.
        ClientMessageSender threadMessageSender = new ClientMessageSender(communication, pseudo);
        threadMessageSender.start();

        // Create a new message receptor thread and start it.
        // This thread is responsible for receiving messages from the server using the communication channel.
        ClientMessageReceptor threadMessageReceptor = new ClientMessageReceptor(communication);
        threadMessageReceptor.start();

        while (activeConnection) {
            /* while the communication is active, the main programm waits */
        }

        // Process of disconnection; closes the communication socket after 2 seconds
        System.out.println("Déconnection en cours");
        sleep(2000);
        communication.close();
    }
}