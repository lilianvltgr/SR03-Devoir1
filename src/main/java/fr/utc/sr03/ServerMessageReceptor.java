package fr.utc.sr03;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

/**
 * Classe héritant des caractéristiques des threads permettant
 * au client de recevoir un message envoyé par le serveur et
 * de l'afficher sur sa console personnelle.
 */
public class ServerMessageReceptor extends Thread {
    private Socket client;
//TODO voir l'interet de mettre les attributs en final
    public ServerMessageReceptor(Socket client) {
        this.client = client;
    }

    @Override

    // Fonction permettant de faire tourner le thread
    public void run() {
        try {
            DataInputStream input = new DataInputStream(client.getInputStream());
            DataOutputStream output = new DataOutputStream(client.getOutputStream());
            boolean connectionActive = true;

            while (connectionActive && !client.isClosed()) {
                String pseudo = input.readUTF(); // lit pseudo envoyé
                String message = input.readUTF(); // lit message associé

                //Si l'utilisateur veut quitter la conversation, il a entré exit dans la console
                if (message.equals("exit")) {
                    // client supprimé de la table de hashage
                    Server.removeFromConnectedClients(pseudo, output);
                    // envoie message autres utilisateurs
                    message = "a quitté la conversation.";
                    Server.sendDisconnectionMessageToClients(message, pseudo);
                    connectionActive = false;
                    System.out.println("Connexion active  : " + connectionActive);

                } else {
                    // si c'est un autre message il s'affiche
                    System.out.println("message : " + message); // affiche le message sur la console
                    Server.sendMessagesToClients(message, pseudo); // envoie le message à la table des clients
                    System.out.println("Connexion active  : " + connectionActive);
                }
            }
        } catch (IOException e) {
            System.out.println("erreur dans la boucle while"); // affiche le message sur la console
            //throw new RuntimeException(e);
        }
            try {
                client.close();
            } catch (IOException e) {
                //we do nothing
                System.out.println("erreur en fermant le socket"); // affiche le message sur la console

            }
        }
    }

