package fr.utc.sr03;

import java.io.IOException;
import java.net.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.*;

/**
 * Server class that runs an infinite loop to accept incoming requests.
 * It stores the socket object associated with this connection in a clients array and
 * launches a thread that reads the message sent to this object.
 * When it receives a connection, message, or disconnection from any of the clients,
 * it broadcasts the information to all clients.
 */

public class Server {
    static public ServerSocket connection;
    public static Socket newClient;

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
    private static boolean isExisting(String pseudo) {
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
    public static void addToConnectedClients(String pseudo, DataOutputStream outputClient) {
        connectedClients.put(pseudo, outputClient);
        nbPseudosConnectes++;
        System.out.println(pseudo + " added to the hashtable");
    }

    /**
     * Function to remove e pseudo from the connectedClients array.
     *
     * @param pseudo The pseudo to be removed from the connectedClients array.
     */
    public static void removeFromConnectedClients(String pseudo) {
        boolean removed = connectedClients.remove(pseudo, connectedClients.get(pseudo));
        nbPseudosConnectes--;
        if (removed)
            System.out.println(pseudo + " removed from the hashtable");
        else
            System.out.println(pseudo + " failed to be removed from the hashtable");
    }

    /**
     * Function to send a message received from a client to all the clients
     * contained in the connectedClients array.
     *
     * @param message The message to send to all the clients.
     * @param messagePseudo The pseudo of the initial client message sender.
     * @throws IOException If an I/O problem occurs while sending the message.
     */
    protected static void sendMessagesToClients(String message, String messagePseudo) throws IOException {
        for (Map.Entry<String, DataOutputStream> entry : connectedClients.entrySet()) {
            String pseudo = entry.getKey();
            DataOutputStream output = entry.getValue();
            System.out.println("Message envoyé au client " + pseudo);
            String finalPseudo = messagePseudo;
            if (pseudo.equals(messagePseudo)) {
                // If the current client is the one who wrote the message we display "Moi" instead of the client pseudo
                finalPseudo = "Moi";
            }
            output.writeUTF(finalPseudo + ":");
            output.writeUTF(message);
        }
    }

    /**
     * Function to send a message to inform the clients in the connectedClients array
     * that a new client has joined the chat.
     *
     * @param message The message to send to all the clients.
     * @param newPseudo The pseudo of the newly added client.
     * @throws IOException If an I/O problem occurs while sending the message.
     */
    protected static void sendArrivalMessageToClients(String message, String newPseudo) throws IOException {
        for (Map.Entry<String, DataOutputStream> entry : connectedClients.entrySet()) {
            // Getting the pseudo (key of the hashtable)
            String pseudo = entry.getKey();

            //If statement that prevents from displaying the arrival message to the new client
            if (!newPseudo.equals(pseudo)) {

                // Getting the outputstream (value of the hashtable)
                DataOutputStream output = entry.getValue();

                // NewPseudo and arrival message are written separately to the server's output stream
                output.writeUTF(newPseudo);
                output.writeUTF(message);

            }
        }
    }

    /**
     * Function to send a message to inform the clients still connected to the chat
     * that one client has left the chat.
     *
     * @param message The message to send to clients contained in the connectedClients array.
     * @param disconnectingPseudo The pseudo of the client who is leaving the chat.
     * @throws IOException If an I/O problem occurs while sending the message.
     */
    protected static void sendDisconnectionMessageToClients(String message, String disconnectingPseudo) throws IOException {
        for (Map.Entry<String, DataOutputStream> entry : connectedClients.entrySet()) {

            // Getting the pseudo (key of the hashtable)
            String pseudo = entry.getKey();
            // Getting the outputstream (value of the hashtable)
            DataOutputStream output = entry.getValue();

            //If the client is the one who disconnects we don't display the disconnection message
            if (!pseudo.equals(disconnectingPseudo)) {

                // DisconnectingPseudo and arrival message are written separately to the server's output stream
                output.writeUTF(disconnectingPseudo);
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
                DataInputStream input = new DataInputStream(newClient.getInputStream());
                DataOutputStream output = new DataOutputStream(newClient.getOutputStream());

                // New pseudo is read
                pseudo = input.readUTF();

                // Case where a new message is sent from a client to the server
                if (pseudo.equals("envoiMessage")) {
                    String messagePseudo = input.readUTF();
                    String message = input.readUTF();
                    sendMessagesToClients(message, messagePseudo);
                }
                //While the pseudo entered by the user already exists, prompt the user to enter it again
                while (isExisting(pseudo)) {
                    // The server send "false" to the client beacause the pseudo is used already
                    output.writeBoolean(false);
                    pseudo = input.readUTF();
                }
                //TODO mettre la création du pseudo dans une fonction + thread pour les connexions simultanées
                output.writeBoolean(true);

                // The pseudo is added to the connectedClients array.
                addToConnectedClients(pseudo, output);

                // A message is sent to the other client already connected to inform them about this arrival
                String messageArrive = "a rejoint la conversation. ";
                sendArrivalMessageToClients(messageArrive, pseudo);

                ServerMessageReceptor thread = new ServerMessageReceptor(newClient, pseudo);
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

