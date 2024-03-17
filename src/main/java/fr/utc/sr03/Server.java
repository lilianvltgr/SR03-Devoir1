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
    public static final int MAX_ELEM = 20;
    public static Socket newClient;
    public static String[] pseudosConnectes;
    public static int nbPseudosConnectes = 0;
    public static HashMap<String, DataOutputStream> hashtable;

    private static boolean isExisting(String pseudo){
        //return the existance of a pseudo in the array
        if(nbPseudosConnectes == 0)
            return false;
        for(String element : pseudosConnectes){
            if (Objects.equals(element, pseudo))
                return true;
        }
        return false;
    }
    private static void addToPseudosConnectes(String pseudo){
        pseudosConnectes[nbPseudosConnectes] = pseudo;
        nbPseudosConnectes++;
    }
    private static void addToHashtable(String pseudo, DataOutputStream outputClient){
        hashtable.put(pseudo, outputClient);
        nbPseudosConnectes++;
    }
    protected static void sendMessagesToClients(String message, String messagePseudo) throws IOException {
        for(Map.Entry<String, DataOutputStream> entry : hashtable.entrySet()) {
            String pseudo = entry.getKey();
            DataOutputStream output = entry.getValue();
            System.out.println("pseudo de la hashtable : "+pseudo);
            String finalPseudo = messagePseudo;
            if (pseudo.equals(messagePseudo)){
                // If the current client is the one who wrote the message we display "Moi" instead of the client pseudo
                finalPseudo = "Moi";
            }
            output.writeUTF(finalPseudo + ":");
            output.writeUTF(message);
        }
    }
    protected static void sendArrivalMessageToClients(String message, String newPseudo) throws IOException {
        for(Map.Entry<String, DataOutputStream> entry : hashtable.entrySet()) {
            String pseudo = entry.getKey(); // récupère clé table hashage
            DataOutputStream output = entry.getValue(); // prend valeur sortie
            System.out.println("pseudo de la hashtable : " + pseudo); // affiche pseudo
            output.writeUTF(newPseudo);
            output.writeUTF(message);
        }
    }
    public static void main(String[] args) throws IOException {
        hashtable = new HashMap<>();
        pseudosConnectes = new String[MAX_ELEM];
        nbPseudosConnectes = 0;
        connection = new ServerSocket(10080);


        while (true) {
            newClient = connection.accept();
            //lecture du pseudo et vérification qu'il n'est pas dans la liste des pseudos utilisés
            DataInputStream input = new DataInputStream(newClient.getInputStream());
            String pseudo = input.readUTF();

            if (pseudo.equals("envoiMessage")){
                String messagePseudo = input.readUTF();
                String message = input.readUTF();
                sendMessagesToClients(message, messagePseudo);
            }
            if (isExisting(pseudo)){
                //envoi d'un message d'erreur car le pseudo est déjà utilisé
            } else {
                DataOutputStream output = new DataOutputStream(newClient.getOutputStream());
                // add the pseudo to the array
                addToPseudosConnectes(pseudo);
                addToHashtable(pseudo, output);
                System.out.println("Pseudo "+pseudo+" added to the array");
                String messageArrive = "a rejoint la conversation. ";
                sendArrivalMessageToClients(messageArrive, pseudo);
            }
            ServerMessageReceptor thread = new ServerMessageReceptor(newClient);
            thread.start();

        }
    }
}
