package fr.utc.sr03.clientPackage;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Class inheriting the characteristics of threads, allowing
 * the client to read a message sent by the server.
 */

public class ClientMessageReceptor extends Thread {

    @Override
    /**
     * Method to run the communication thread for receiving messages from the server.
     * Reads messages sent from the server via the input stream and prints them to the console.
     * This method runs in a loop until the connection is active, facilitating continuous communication.
     */
    public void run() {
        try {
            // Create a DataInputStream to read input from the server's communication socket
            DataInputStream input = new DataInputStream(Client.communication.getInputStream());

            // While the connection between the server and this client is indeed active
            while (Client.activeConnection) {

                // Client pseudo and message sent are read separately
                String pseudo = input.readUTF();
                String message = input.readUTF();

                // The full message is displayed along with the pseudo on the console
                System.out.println(pseudo + " " + message);
            }
        } catch (IOException e) {
            if (Client.activeConnection) {
                Client.activeConnection = false;
                System.out.println("Le serveur a un problème, retentez de vous connecter plus tard");
            }
        }
    }
}
