package fr.utc.sr03;
import java.io.IOException;
import java.net.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Arrays;
public class Serveur {
    static public ServerSocket connection;
    public static final int MAX_ELEM = 20;
    public static Socket newClient;
    public static String[] pseudosConnectes;

    //constructeur ??
//    public Serveur(){
//        String[] pseudosConnectés = new String[MAX_ELEM];
//    }
    public static void main(String[] args) throws IOException {
        connection = new ServerSocket(10080);
        while (true) {
            newClient = connection.accept();
            //lecture du pseudo et vérification qu'il n'est pas dans la liste des pseudos utilisés
            DataInputStream input = new DataInputStream(newClient.getInputStream());
            String pseudo = input.readUTF(); // récupération du pseudo

//            if (!Arrays.asList(pseudosConnectes).isEmpty() && Arrays.asList(pseudosConnectes).contains(pseudo)) {
//                //using arrays from java utils
//                //envoi d'un message d'erreur car le pseudo est déjà utilisé
//            } else {
//                // add the pseudo to the array
//            }
            MessageReceptor thread = new MessageReceptor(newClient);
            thread.start();
        }
    }
}
