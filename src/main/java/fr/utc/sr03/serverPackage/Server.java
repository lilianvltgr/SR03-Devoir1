package fr.utc.sr03.serverPackage;

import java.io.IOException;
import java.net.*;
import java.io.DataOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Server class that runs an infinite loop to accept incoming requests.
 * It stores the socket object associated with this connection in a clients array and
 * launches a thread that reads the message sent to this object.
 * When it receives a connection, message, or disconnection from any of the clients,
 * it broadcasts the information to all clients.
 */

public class Server {
    protected static ServerSocket connection;
    protected static Socket newClient;

    // counter of pseudos currently connected
    public static int nbPseudosConnectes;

    // Array containing the pseudos of connected clients
    public volatile static HashMap<String, DataOutputStream> connectedClients;


    /**
     * Function to check if a pseudo exists in the connectedClients array.
     *
     * @param pseudo The pseudo to be checked for existence in the connectedClients array.
     * @return true if the pseudo exists in the connectedClients array, otherwise returns false.
     */
    protected static boolean isExisting(String pseudo) {
        //return the existence of a pseudo in the connectedClients
        if (nbPseudosConnectes == 0)
            return false;
        for (Map.Entry<String, DataOutputStream> entry : connectedClients.entrySet()) {
            if (entry.getKey().equals(pseudo))
                return true;
        }
        return false;
    }

    /**
     * Function to add a new pseudo to the connectedClients array.
     *
     * @param pseudo The pseudo to be added to the connectedClients array.
     * @param outputClient The DataOutputStream associated with the client.
     */
    protected static void addToConnectedClients(String pseudo, DataOutputStream outputClient) {
        connectedClients.put(pseudo, outputClient);
        nbPseudosConnectes++;
        System.out.println(pseudo + " added to the hashtable");
    }

    /**
     * Function to remove e pseudo from the connectedClients array.
     *
     * @param pseudo The pseudo to be removed from the connectedClients array.
     */
    protected static void removeFromConnectedClients(String pseudo) {
        boolean removed = connectedClients.remove(pseudo, connectedClients.get(pseudo));
        nbPseudosConnectes--;
        if (removed)
            System.out.println(pseudo + " removed from the hashtable");
        else
            System.out.println(pseudo + " failed to be removed from the hashtable");
    }

    /**
     * Function to send a message to all the clients contained in the connectedClients array.
     *
     * @param message The message to send to all the clients.
     * @param pseudo The pseudo of the initial client message sender
     *               or the newly added client or the lastly disconnected client
     * @throws IOException If an I/O problem occurs while sending the message.
     */
    protected static void sendMessageToClients(String message, String pseudo) throws IOException {
        String currentTime = java.time.LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")) + " | ";
        for (Map.Entry<String, DataOutputStream> entry : connectedClients.entrySet()) {
            // Getting the entryPseudo (key of the hashtable)
            String entryPseudo = entry.getKey();
            // Getting the output stream (value of the hashtable)
            DataOutputStream output = entry.getValue();

            // If the message is informative : arrival or disconnection of a client
            if (message.equals("a rejoint la conversation.") || message.equals("a quitté la conversation de façon imprévue.") || message.equals("a quitté la conversation.")){
                if (!entryPseudo.equals(pseudo)) {
                    // Concerned pseudo and situation message are written separately to the server's output stream
                    output.writeUTF(currentTime + pseudo);
                    output.writeUTF(message);
                }
            }
            // If the message is sent from another client : retransmission.
            else {
                String finalPseudo = pseudo;
                if (entryPseudo.equals(pseudo)) {
                    // If the current client is the one who wrote the message we display "Moi" instead of the client pseudo
                    finalPseudo = "Moi";
                }
                // Concerned pseudo and retransmitted message are written separately to the server's output stream
                output.writeUTF(currentTime + finalPseudo + ":");
                output.writeUTF(message);
            }
        }
    }

    public static void main(String[] args) throws IOException {

        // Initialize the connectedClients map to store client information,
        // set the count of connected pseudos to 0, and create a ServerSocket on port 10080 for incoming connections.
        connectedClients = new HashMap<>();
        nbPseudosConnectes = 0;
        connection = new ServerSocket(10080);

        // Infinite while running to accept new connections
        while (true) {
            String pseudo = "";
            try {

                // New connection accepted ; a new client will be entering the chat
                newClient = connection.accept();

                // Create a DataInputStream to read text input and a DataOutputStream to write text output
                ServerMessageReceptor thread = new ServerMessageReceptor(newClient);
                thread.start();
            } catch (Exception e) {

                if (pseudo.isEmpty()) {
                    try {
                        if (!newClient.isClosed())
                            newClient.close();
                    } catch (IOException ex) {
                        // Nothing is done
                    }
                }
            }
        }
        }
    }

