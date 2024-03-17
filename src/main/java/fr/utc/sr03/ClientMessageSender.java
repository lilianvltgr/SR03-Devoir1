package fr.utc.sr03;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * Classe héritant des caractéristiques des threads permettant
 * au client d'écrire un message sur la console et de l'envoyer au serveur.
 */

public class ClientMessageSender extends Thread{
    private Socket communication;
    private String pseudo;

    //constructor
    public ClientMessageSender(Socket communication, String pseudo){
        this.communication = communication;
        this.pseudo = pseudo;
    }

    @Override
    // Fonction permettant de faire tourner le thread
    public void run(){
            try {
                Scanner sc = new Scanner(System.in);
                DataOutputStream output = new DataOutputStream(communication.getOutputStream());

                while (true) {
                    //lecture du message
                    String message = sc.nextLine();
                    //envoi du message au thread server
                    output.writeUTF(pseudo);
                    output.writeUTF(message);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }

}
