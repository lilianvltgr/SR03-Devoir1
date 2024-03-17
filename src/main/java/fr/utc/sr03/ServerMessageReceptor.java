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
public class ServerMessageReceptor extends Thread{
    private Socket client;
    public ServerMessageReceptor(Socket client){this.client = client;}

    @Override

    // Fonction permettant de faire tourner le thread
    public void run(){
            try {
                DataInputStream input = new DataInputStream(client.getInputStream());
                while(true){

                    String pseudo = input.readUTF(); // lit pseudo envoyé
                    String message = input.readUTF(); // lit message associé
                    System.out.println("message : "+message); // affiche le message sur la console
                    Server.sendMessagesToClients(message, pseudo); // envoie le message à la table des clients

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            }

}
