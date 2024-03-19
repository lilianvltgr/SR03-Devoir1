package fr.utc.sr03;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import static java.lang.Thread.sleep;


/**
 Client class that enables a connection
 to the server and launches the two communication threads:
 ClientMessageReceptor to intercept messages coming from the server,
 and ClientMessageSender to retrieve messages entered
 by the user and transmit them to the server.
 */
public class Client {
    static public Socket communication;
    static volatile boolean connectionActive = true;
    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner sc = new Scanner(System.in); // scanner pour entrée console

        // Connexion serveur
        communication = new Socket("localhost", 10080); // socket créée
        // Instanciations des pipes
        DataInputStream input = new DataInputStream(communication.getInputStream()); // pipe entrée texte
        DataOutputStream output = new DataOutputStream(communication.getOutputStream()); // pipe sortie texte

        // Création du pseudo + connection au chat
        String pseudo;
        do {
            System.out.println("Ecrivez votre pseudo");
        } while ((pseudo = sc.next()) == null);
        output.writeUTF(pseudo); // ecriture pipe sortie pseudo créé
        while (!input.readBoolean()) {
            do {
                System.out.println("Le pseudo est indisponible, choisissez en un autre");
            } while ((pseudo = sc.next()) == null);
            output.writeUTF(pseudo);
        }
        System.out.println("Vous êtes connectés");

        //New message sender thread
        ClientMessageSender threadMessageSender = new ClientMessageSender(communication, pseudo);
        threadMessageSender.start();

        //New message receptor thread
        ClientMessageReceptor threadMessageReceptor = new ClientMessageReceptor(communication);
        threadMessageReceptor.start();
        while (connectionActive) {
            /* while the communication is active, the main programm waits */
        }
        System.out.println("Déconnection en cours");
        sleep(2000);
        communication.close();
    }
}