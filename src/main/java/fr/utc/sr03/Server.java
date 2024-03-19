package fr.utc.sr03;

import java.io.IOException;
import java.net.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.*;

/**
 * Classe Server qui exécute une boucle infinie pour accepter les demandes entrantes.
 * Il stocke l'objet socket associé à cette connection dans un tableau clients et
 * lance un thread qui lit le message envoyé à cet objet.
 * Lorsqu'il reçoit une connexion,  message ou une deconnexion d'un des clients,
 * il diffuse l'information à l'ensemble des clients.
 */
public class Server {
    static public ServerSocket connection;
    public static Socket newClient;

    public static int nbPseudosConnectes;
    public volatile static HashMap<String, DataOutputStream> connectedClients;

    private static boolean isExisting(String pseudo) {
        //return the existance of a pseudo in the connectedClients
        if (nbPseudosConnectes == 0)
            return false;
        for (Map.Entry<String, DataOutputStream> entry : connectedClients.entrySet()) {
            if (entry.getKey().equals(pseudo))
                return true;
        }
        return false;
    }

    public static void addToconnectedClients(String pseudo, DataOutputStream outputClient) {
        connectedClients.put(pseudo, outputClient);
        nbPseudosConnectes++;
        System.out.println(pseudo + " added to the hashtable");
    }

    public static void removeFromConnectedClients(String pseudo, DataOutputStream outputClient) {
        boolean removed = connectedClients.remove(pseudo, connectedClients.get(pseudo));
        nbPseudosConnectes--;
        if (removed)
            System.out.println(pseudo + " removed from the hashtable");
        else
            System.out.println(pseudo + " failed to be removed from the hashtable");
    }
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
    protected static void sendArrivalMessageToClients(String message, String newPseudo) throws IOException {
        for (Map.Entry<String, DataOutputStream> entry : connectedClients.entrySet()) {
            String pseudo = entry.getKey(); // getting the pseudo (key of the hashtable)
            if (!newPseudo.equals(pseudo)) {
                //if statement that prevent from display the arrival message to the new client
                DataOutputStream output = entry.getValue(); // getting the outputstream (value of the hashtable)
                output.writeUTF(newPseudo);
                output.writeUTF(message);
            }
        }
    }
    protected static void sendDisconnectionMessageToClients(String message, String disconnectingPseudo) throws IOException {
        for (Map.Entry<String, DataOutputStream> entry : connectedClients.entrySet()) {
            String pseudo = entry.getKey(); // getting the pseudo (key of the hashtable)
            DataOutputStream output = entry.getValue(); // getting the outputstream (value of the hashtable)
            if (!pseudo.equals(disconnectingPseudo)) {
                //if the client is the one who disconnects we don't display the disconnection message
            output.writeUTF(disconnectingPseudo);
            output.writeUTF(message);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        connectedClients = new HashMap<>();
        nbPseudosConnectes = 0;
        connection = new ServerSocket(10080);
        while (true) {
            newClient = connection.accept();
            //lecture du pseudo et vérification qu'il n'est pas dans la liste des pseudos utilisés
            DataInputStream input = new DataInputStream(newClient.getInputStream());
            DataOutputStream output = new DataOutputStream(newClient.getOutputStream());
            String pseudo = input.readUTF();

            if (pseudo.equals("envoiMessage")) {
                String messagePseudo = input.readUTF();
                String message = input.readUTF();
                sendMessagesToClients(message, messagePseudo);
            }
            //if the pseudo is existing already we tell the client to change it
            while (isExisting(pseudo)) {
                output.writeBoolean(false);
                pseudo = input.readUTF();
            }
            //TODO mettre la création du pseudo dans une fonction + thread pour les connexions simultanées

            output.writeBoolean(true);
            // add the pseudo to the hashtable
            addToconnectedClients(pseudo, output);

            String messageArrive = "a rejoint la conversation. ";
            sendArrivalMessageToClients(messageArrive, pseudo);

            ServerMessageReceptor thread = new ServerMessageReceptor(newClient);
            thread.start();
        }
    }
}
