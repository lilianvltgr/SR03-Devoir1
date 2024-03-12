package fr.utc.sr03;
import java.io.IOException;
import java.net.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Arrays;
import java.util.Objects;

public class Serveur {
    static public ServerSocket connection;
    public static final int MAX_ELEM = 20;
    public static Socket newClient;
    public static String[] pseudosConnectes;
    public static int nbPseudosConnectes = 0;

    public Serveur(){
        String[] pseudosConnectés = new String[MAX_ELEM];
    }

    private static boolean isExisting(String pseudo){
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
    public static void main(String[] args) throws IOException {
        pseudosConnectes = new String[MAX_ELEM];
        nbPseudosConnectes = 0;
        connection = new ServerSocket(10080);
        while (true) {
            newClient = connection.accept();
            //lecture du pseudo et vérification qu'il n'est pas dans la liste des pseudos utilisés
            DataInputStream input = new DataInputStream(newClient.getInputStream());
            String pseudo = input.readUTF(); // récupération du pseudo

            if (isExisting(pseudo)){
                //envoi d'un message d'erreur car le pseudo est déjà utilisé
            } else {
                // add the pseudo to the array
                addToPseudosConnectes(pseudo);
                System.out.println("Pseudo "+pseudo+" added to the array");
            }
            MessageReceptor thread = new MessageReceptor(newClient);
            thread.start();
        }
    }
}
