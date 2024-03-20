package fr.utc.sr03;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

/**
 * Class inheriting the characteristics of threads, allowing
 * the client to receive a message sent by the server and
 * display it on their personal console.
 */

public class ServerMessageReceptor extends Thread {
    private Socket client;
    private String pseudo;
//TODO voir l'interet de mettre les attributs en final
public ServerMessageReceptor(Socket client, String pseudo) {
        this.client = client;
    this.pseudo = pseudo;
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

            // Create a DataInputStream to read input from the client's input stream
            DataInputStream input = new DataInputStream(client.getInputStream());

            while (activeConnection) {
                // Reads pseudo of the client and the message just sent.
                String pseudo = input.readUTF();
                String message = input.readUTF();

                //If the client wants to leave the chat, he sends "exit"
                if (message.equals("exit")) {

                    //The client pseudo is removed from the hashtable
                    Server.removeFromConnectedClients(pseudo);

                    // The other clients still connected are informed of this departure
                    message = "a quitté la conversation.";
                    Server.sendDisconnectionMessageToClients(message, pseudo);

                    // The connection between the server and this client is no longer active
                    activeConnection = false;
                    System.out.println("Connexion active  : " + activeConnection);

                } else {
                    // The message is well received
                    System.out.println("message : " + message);

                    // The message is broadcasted to every client currently connected
                    Server.sendMessagesToClients(message, pseudo);

                    // The connection between the server and this client is still active
                    System.out.println("Connexion active  : " + activeConnection);
                }
            }
        } catch (IOException e) {
            if (activeConnection) {
                System.out.println(client.isClosed());
                try {
                    Server.removeFromConnectedClients(pseudo);
                    Server.sendDisconnectionMessageToClients("a quitté la conversation de façon imprévue", pseudo);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        try {
            if (!client.isClosed())
                client.close();
            } catch (IOException e) {
                // Nothing is done
            System.out.println("Erreur en fermant le socket"); // affiche le message sur la console
            }
        }
    }

