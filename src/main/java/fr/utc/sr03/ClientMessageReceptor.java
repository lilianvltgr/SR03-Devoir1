package fr.utc.sr03;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * Classe héritant des caractéristiques des threads permettant
 * au client de lire un message envoyé par le serveur.
 */
public class ClientMessageReceptor extends Thread{
    private Socket communication;


    //constructor
    public ClientMessageReceptor(Socket communication){
        this.communication = communication;
    }

    @Override
    // Fonction permettant de faire tourner le thread
    public void run(){
            try {
                DataInputStream intput = new DataInputStream(communication.getInputStream());

                while (true) {
                    //lecture du message
                    String pseudo = intput.readUTF();
                    String message = intput.readUTF();
                    System.out.println(pseudo + " " + message);

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }

}