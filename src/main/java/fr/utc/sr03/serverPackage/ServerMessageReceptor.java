package fr.utc.sr03.serverPackage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Class inheriting the characteristics of threads, allowing
 * the client to receive a message sent by the server and
 * display it on their personal console.
 */

public class ServerMessageReceptor extends Thread {
    private final Socket client;
    private String pseudo;

    public ServerMessageReceptor(Socket client) {
        this.client = client;
        this.pseudo = "";
    }

    @Override
    /**
     * Method to run the communication thread for receiving messages from the client.
     * Reads messages sent from the client via the input stream and prints them to the console
     * while the connection is still active.
     * If it is an exit message, the client is disconnected ; the thread is closed and
     * the other clients are informed of the departure.
     */
    public void run() {
        boolean activeConnection = true;
        try {
            DataInputStream input = new DataInputStream(client.getInputStream());
            DataOutputStream output = new DataOutputStream(client.getOutputStream());
            // New pseudo is read
            pseudo = input.readUTF();

            //While the pseudo entered by the user already exists, prompt the user to enter it again
            while (Server.isExisting(pseudo)) {
                // The server send "false" to the client because the pseudo is used already
                output.writeBoolean(false);
                pseudo = input.readUTF();
            }
            output.writeBoolean(true);

            // The pseudo is added to the connectedClients array.
            Server.addToConnectedClients(pseudo, output);

            // A message is sent to the other client already connected to inform them about this arrival
            Server.sendMessageToClients("a rejoint la conversation.", pseudo);

            while (activeConnection) {
                // Reads pseudo of the client and the message just sent.
                String pseudo = input.readUTF();
                String message = input.readUTF();

                //If the client wants to leave the chat, he sends "exit"
                if (message.equals("exit")) {

                    //The client pseudo is removed from the hashtable
                    Server.removeFromConnectedClients(pseudo);

                    // The other clients still connected are informed of this departure
                    Server.sendMessageToClients("a quitté la conversation.", pseudo);

                    // The connection between the server and this client is no longer active
                    activeConnection = false;


                } else {
                    // The message is well received
                    System.out.println("message : " + message + "received");

                    // The message is broadcasted to every client currently connected
                    Server.sendMessageToClients(message, pseudo);
                }
            }
        } catch (IOException e) {
            if (!pseudo.isEmpty() && activeConnection) {
                try {
                    Server.removeFromConnectedClients(pseudo);
                    Server.sendMessageToClients("a quitté la conversation de façon imprévue.", pseudo);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        try {
            if (!client.isClosed())
                client.close();
        } catch (IOException exc) {
            // Nothing is done
            System.out.println("Error while closing the socket"); // affiche le message sur la console
        }
    }
}


